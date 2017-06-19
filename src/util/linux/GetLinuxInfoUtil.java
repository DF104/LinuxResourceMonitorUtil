package util.linux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import util.DcitsUtil;
import util.consts.CommandConstant;
import util.consts.LinuxConstant;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import entiy.LinuxInfo;

/**
 * 解析通过主机命令返回的信息的相关工具方法
 * @author xuwangcheng
 * @version 2017.2.19
 * 
 *
 */

public class GetLinuxInfoUtil {		
	
	/**
	 * 获取指定主机的连接
	 * @param host
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static void getConnection(LinuxInfo info) throws Exception {			
		Connection conn = new Connection(info.getHost(), Integer.valueOf(info.getPort()));
		conn.connect(null, 3000, 2500);	
		boolean flag = conn.authenticateWithPassword(info.getUsername(), info.getPassword()); 
		if (flag) {
			if (info.getUanme() == "") {
				info.setUanme(execCommand(conn, CommandConstant.LINUX_COMMAND_MAP.get(CommandConstant.GET_UNAME), 1, null));
				
				switch (info.getUanme()) {
				case "Linux":
					info.setCommandMap(CommandConstant.LINUX_COMMAND_MAP);
					break;
				case "HP-UX":
					info.setCommandMap(CommandConstant.HP_COMMAND_MAP);
					break;
				case "SunOS":
					info.setCommandMap(CommandConstant.SUN_COMMAND_MAP);
					break;
				default:
					info.setCommandMap(CommandConstant.LINUX_COMMAND_MAP);
					break;
				}
				
				info.setCpuInfo(execCommand(conn, info.getCommandMap().get(CommandConstant.GET_CPU_INFO), 1, null));	
				info.setMemInfo(execCommand(conn, info.getCommandMap().get(CommandConstant.GET_MEMORY_INFO), 1, null));
				String str = execCommand(conn, info.getCommandMap().get(CommandConstant.GET_NETWORK_CARD), 1, null);
				info.setNewWorkInfo(str.split(","));
			}			
			info.setConn(conn);
		}
	}
	
	/**
	 * 获取session
	 * @param conn
	 * @return
	 * @throws IOException
	 */
	public static Session getSession(Connection conn) throws IOException {
		if (conn != null) {
			return conn.openSession();
		}
		return null;
	}
	
	/**
	 * 关闭session
	 * @param session
	 */
	public static void closeSession(Session session) {
		if (session != null) {
			session.close();
		}
	}
	
	/**
	 * 关闭连接
	 * @param conn
	 */
	public static void closeConnection(Connection conn){
		if (conn != null) {
			conn.close();
		}
	}
	
	/**
	 * 执行一次命令
	 * <br>返回结果
	 * <br>命令一定要是一次性执行完毕的命令(非交互性命令)
	 * 
	 * @param conn
	 * @param command
	 * @param count 读取多少行,从第一行开始读取
	 * @param context 从context中读取是否中断命令的flag
	 * @return
	 * @throws IOException 
	 */
	public static String execCommand(Connection conn, String command, int count, ServletContext context) throws IOException {
		
		String str = "";
		
		if (conn != null) {
			Session session = conn.openSession();
			InputStream is = new StreamGobbler(session.getStdout());
			BufferedReader brStat = new BufferedReader(new InputStreamReader(is));
			session.execCommand(command);

			String readLine = null;
			int i = 0;
			while (i < count && (readLine = brStat.readLine()) != null) {
				str += readLine;
				
				i++;
				
				if (i != count ) {
					str += "\n";
				}
				
				if (context != null && "true".equals(context.getAttribute(LinuxConstant.STOP_EXEC_COMMAND_FLAG_ATTRIBUTE))) {
					context.setAttribute(LinuxConstant.STOP_EXEC_COMMAND_FLAG_ATTRIBUTE, "false");
					break;
				}
			}
												
			brStat.close();
			is.close();	
			session.close();
			
		}
		
		return str;
	}
	
	/**
	 * 持续命令的执行
	 * <br>返回输出流
	 * @param conn
	 * @param command
	 * @return
	 * @throws IOException
	 */
	public static BufferedReader execLoopCommand(Connection conn, String command) throws IOException {
		Session session = conn.openSession();
		InputStream is = new StreamGobbler(session.getStdout());		
		BufferedReader brStat = new BufferedReader(new InputStreamReader(is));
		session.execCommand(command);		
		return brStat;
	}
	
	
	
	/**
	 * 解析从前台传过来的主机信息列表
	 * @param str
	 * @param infoId
	 * @return 
	 */
	public static List<LinuxInfo> parseServerListStr(String str,int infoId) {
		List<LinuxInfo> servers = new ArrayList<LinuxInfo>();
		
		String[] infos = str.split("\\n");
		String[] info = null;
		String mark;
		String port;
		for (String s:infos) {
			info = s.split(",");
			String[] ipport = info[0].split(":");
			mark = "";
			port = "22";
			if (info.length > 3) {
				mark = info[3].trim();
			}
			if (ipport.length == 2) {
				port = ipport[1];
			}
			try {
				servers.add(new LinuxInfo(++infoId, port, ipport[0], info[1], info[2], mark, DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN)));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}			
		}					
		return servers;
	}
	
	/**
	 * 检查命令是否被禁止执行<br>
	 * @param command
	 * @return false 可以执行  true 禁止执行
	 */
	public static boolean checkCommand(String command) {
		for (String s:CommandConstant.FORBID_EXEC_COMMANDS) {
			if (command.startsWith(s)) {
				return true;
			}
		}				
		return false;
	}
	
}

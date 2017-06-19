package util.linux;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;

import util.consts.CommandConstant;
import entiy.LinuxInfo;
import entiy.RealTimeInfo;

/**
 * 执行相关命令返回linux资源情况
 * 使用到的命令：sar netstat awk vmstat df iostat
 * 获取返回的信息
 * @author xuwangcheng
 * @version 20170224
 *
 */
public class GetLinuxInfo implements Runnable {
	
	/**
	 * 执行命令的时间间隔 默认3s
	 */
	private static final String INTERVAL_TIME = "3";

	private LinuxInfo info;
	private ServletContext context;
	
	private String str = "";
	
	@Override
	public void run() {
		// TODO Auto-generated method stub	
		String intervalTime = INTERVAL_TIME;		
		RealTimeInfo realTimeInfo = new RealTimeInfo();
		
		realTimeInfo.setInfoId(info.getInfoId());
		
		if (this.info.getOptions().get("intervalTime") != null) {
			intervalTime = this.info.getOptions().get("intervalTime");
		}
		
		
		//实例化不同类型主机的信息解析类实例
		ParseInfo parseUtil = ParseInfoFactory.getInstance(info.getUanme());
		
		try {
			Map<String, String> commandMap = info.getCommandMap();
			
			BufferedReader vmstatBrStat = GetLinuxInfoUtil.execLoopCommand(info.getConn(), commandMap.get(CommandConstant.VMSTAT) + " " + intervalTime);
			/*BufferedReader netstatBrStat = GetLinuxInfoUtil.execLoopCommand(info.getConn()
					, "while :;do " + commandMap.get(CommandConstant.GET_TCP_PORT_COUNT) + ";sleep " + intervalTime + "; done");					
			BufferedReader networkBrStat = GetLinuxInfoUtil.execLoopCommand(info.getConn()
					, "while :;do " + commandMap.get(CommandConstant.GET_NETWORK_INFO) + ";sleep " + intervalTime + ";done");
			//BufferedReader ioBrStat = GetLinuxInfoUtil.execLoopCommand(info.getConn(), "iostat -d -x -k " + intervalTime);
			//while :;do df -h|grep -w  '/root$\|/$';sleep 3;done
			BufferedReader diskBrStat = GetLinuxInfoUtil.execLoopCommand(info.getConn()
					,"while :;do " + commandMap.get(CommandConstant.GET_DISK_INFO) + ";sleep " + intervalTime + ";done");*/
			//vmstat前三行不读
			vmstatBrStat.readLine();	
			vmstatBrStat.readLine();	
			vmstatBrStat.readLine();
			
			//io前一行不读
			//ioBrStat.readLine();
			
			while (!this.info.isStopFlag()) {
				
				//vmstat读取cpu和内存
				str = vmstatBrStat.readLine();
				
				while (str == null || str.contains("memory") || str.contains("free") || str.isEmpty()) {
					str = vmstatBrStat.readLine();
				}								

				parseUtil.parseInfo(str, realTimeInfo, info);				
				

				//处理tcp端口
				parseUtil.parseTcpInfo(GetLinuxInfoUtil.execCommand(info.getConn()
						, commandMap.get(CommandConstant.GET_TCP_PORT_COUNT), 1, context), realTimeInfo);
				
				//处理网络带宽
				parseUtil.parseNetworkInfo(GetLinuxInfoUtil.execCommand(info.getConn()
						, commandMap.get(CommandConstant.GET_NETWORK_INFO), 1, context), realTimeInfo);
				
				//处理磁盘空间使用信息 匹配 /和/username挂载的磁盘
				//while ((str = diskBrStat.readLine()).isEmpty()) {}	
				parseUtil.parseDiskInfo(GetLinuxInfoUtil.execCommand(info.getConn()
						, commandMap.get(CommandConstant.GET_DISK_INFO), 1, context), realTimeInfo);
				
				//处理磁盘io																																	
				
				//保存信息
				context.setAttribute(String.valueOf(this.info.getInfoId()), realTimeInfo);
			}
			
			this.info.setStopFlag(false);;
			
			
			if (vmstatBrStat != null) {
				vmstatBrStat.close();
			}
			
			/*if (netstatBrStat != null) {
				netstatBrStat.close();
			}
			
			if (networkBrStat != null) {
				networkBrStat.close();
			}
			
			if (diskBrStat != null) {
				diskBrStat.close();
			}			*/	
			
			/*if (ioBrStat != null) {
				networkBrStat.close();
			}	*/		
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public GetLinuxInfo(LinuxInfo info, ServletContext context) {
		super();
		this.info = info;
		this.context = context;
	}

}

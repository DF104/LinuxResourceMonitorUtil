package entiy;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import util.DcitsUtil;
import util.linux.GetLinuxInfo;
import util.linux.GetLinuxInfoUtil;
import ch.ethz.ssh2.Connection;

/**
 * linux主机信息
 * @author xuwangcheng
 * @version 20170224
 *
 */
public class LinuxInfo extends ServerInfo {
	
	private Integer infoId;
	
	/**
	 * 传入自定义的参数
	 * 由于目前只使用了vmstat命令
	 * 所以能传入的参数只有  intervalTime 不传的话默认为3 即每两秒执行一次vmstat
	 */
	private Map<String, String> options = new HashMap<String, String>();

	/**
	 * 与指定主机建立的连接
	 */
	private Connection conn;
	
	/**
	 * 该主机每次获取远程返回的结果时
	 * 都会检查该值
	 * 如果为true 将会关闭session和connection
	 */
	private boolean stopFlag = false;
	
	/**
	 * 主机类型
	 * 例如 Linux HP-UX SUNOS
	 */
	private String uanme = "";
	/**
	 * Cpu信息 cpu核数
	 */
	private String cpuInfo = "0";
	
	/**
	 * 内存信息
	 */
	private String memInfo = "0";
	
	/**
	 * 网卡信息
	 */
	private String[] newWorkInfo;
	
	/**
	 * 可使用的命令
	 */
	private Map<String, String> commandMap;
	
	
	/**
	 * 启动远程查询
	 * 线程独立
	 * @param context
	 * @throws Exception
	 */
	public void start(ServletContext context) throws Exception {
		Thread t = new Thread(new GetLinuxInfo(this, context));
		t.start();
	}
	
	/**
	 * 初始化连接时的报错信息
	 */
	private String errorInfo = "";
	
	public void stop() throws Exception {
		stopFlag = true;
	}
	
	public void conect() throws Exception  {
		try {
			GetLinuxInfoUtil.getConnection(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.errorInfo = e.getMessage();
			throw e;
		}
		
		if (this.conn == null) {
			this.errorInfo = "用户名密码凭证不正确!";
			throw new Exception();			
		}
	}
	
	public void disconect() {
		GetLinuxInfoUtil.closeConnection(conn);
	}
	
	public LinuxInfo(String host, String port, String username, String password, String time) {
		super();
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.time = time;
	}

	public LinuxInfo(Integer infoId, String port, String host, String username, String password, String mark, String time) {
		super();
		this.infoId = infoId;
		this.port = port;
		this.host = host;
		this.username = username;
		this.password = password;
		this.mark = mark;
		this.time = time;
		
	}
	
	
	public LinuxInfo(ServerInfo serverInfo) {
		super();
		this.host = serverInfo.host;
		this.port = serverInfo.port;
		this.username = serverInfo.username;
		this.password = serverInfo.password;
		this.mark = serverInfo.mark;
		this.time = DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN);
	}
	
	public LinuxInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Map<String, String> getCommandMap() {
		return commandMap;
	}

	public void setCommandMap(Map<String, String> commandMap) {
		this.commandMap = commandMap;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public String[] getNewWorkInfo() {
		return newWorkInfo;
	}


	public void setNewWorkInfo(String[] newWorkInfo) {
		this.newWorkInfo = newWorkInfo;
	}


	public Integer getInfoId() {
		return infoId;
	}

	public void setInfoId(Integer infoId) {
		this.infoId = infoId;
	}

	public Connection getConn() {
		return conn;
	}
	
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	

	public Map<String, String> getOptions() {
		return options;
	}

	public void setOptions(Map<String, String> options) {
		this.options = options;
	}

	public boolean isStopFlag() {
		return stopFlag;
	}

	public void setStopFlag(boolean stopFlag) {
		this.stopFlag = stopFlag;
	}


	public String getUanme() {
		return uanme;
	}


	public void setUanme(String uanme) {
		this.uanme = uanme;
	}


	public String getCpuInfo() {
		return cpuInfo;
	}


	public void setCpuInfo(String cpuInfo) {
		this.cpuInfo = cpuInfo;
	}


	public String getMemInfo() {
		return memInfo;
	}


	public void setMemInfo(String memInfo) {
		this.memInfo = memInfo;
	}

	
}

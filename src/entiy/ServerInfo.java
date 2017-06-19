package entiy;




/**
 * 服务器信息基类
 * @author xuwangcheng
 * @version 20170317
 *
 */
public class ServerInfo {
	
	/**
	 * ip地址:端口
	 * 以及登录用户名密码
	 */
	protected Integer id;
	
	protected String host;
	
	protected String port;
	protected String username;
	protected String password;
	
	/**
	 * 备注
	 * 如果是通过端口转发的,则再次备注真实监控的主机地址
	 */
	protected String mark;
	
	/**
	 * 加入时间
	 */
	protected String time;
	

	public ServerInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ServerInfo [id=" + id + ", host=" + host + ", port=" + port
				+ ", username=" + username + ", password=" + password
				+ ", mark=" + mark + ", time=" + time + "]";
	}
	
	
}

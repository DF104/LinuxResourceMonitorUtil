package entiy;

/**
 * 常用信息类
 * @version 1.0.0.0,20170426
 * @author Administrator
 *
 */
public class CommonInfo {
	
	private Integer infoId;
	
	/**
	 * 信息类型<br>
	 * 目前 ：<br>
	 * 0-主机信息  包括主机 基本硬件信息  以及账号密码
	 * 1-常见主机命令信息  包括linux/window等 各种操作系统、软件的命令
	 * 2-查询知识  包括各种好的知识文章等
	 * 3-测试项目 做完的各种项目
	 * 4-数据库信息   TNS、各种常用表结构、账号密码
	 * 5-常用shell脚本
	 * 6-常用数据库SQL命令
	 */
	private String infoType;
	
	/**
	 * 信息标题
	 */
	private String infoName;
	
	/**
	 * 详细内容
	 */
	private String infoDetail;
	
	/**
	 * 文件路径
	 * 逗号分隔,多个地址
	 */
	private String filePath;
	
	/**
	 * 标签,方便查找
	 * <br>类似    linux,vim
	 */
	private String infoTag;
	
	/**
	 * 加入时间
	 */
	private String joinTime;
	
	/**
	 * 加入用户名
	 */
	private String joinUserName;		 
	
	public CommonInfo(Integer infoId, String infoType, String infoName,
			String infoDetail, String infoTag, String joinTime,
			String joinUserName) {
		super();
		this.infoId = infoId;
		this.infoType = infoType;
		this.infoName = infoName;
		this.infoDetail = infoDetail;
		this.infoTag = infoTag;
		this.joinTime = joinTime;
		this.joinUserName = joinUserName;
	}

	public CommonInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getInfoId() {
		return infoId;
	}

	public void setInfoId(Integer infoId) {
		this.infoId = infoId;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getInfoType() {
		return infoType;
	}

	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}

	public String getInfoName() {
		return infoName;
	}

	public void setInfoName(String infoName) {
		this.infoName = infoName;
	}

	public String getInfoDetail() {
		return infoDetail;
	}

	public void setInfoDetail(String infoDetail) {
		this.infoDetail = infoDetail;
	}

	public String getInfoTag() {
		return infoTag;
	}

	public void setInfoTag(String infoTag) {
		this.infoTag = infoTag;
	}

	public String getJoinTime() {
		return joinTime;
	}

	public void setJoinTime(String joinTime) {
		this.joinTime = joinTime;
	}

	public String getJoinUserName() {
		return joinUserName;
	}

	public void setJoinUserName(String joinUserName) {
		this.joinUserName = joinUserName;
	}
	
	
	
}

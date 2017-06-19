package entiy;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * 主机的实时信息
 * @author xuwangcheng
 * @version 20170224
 *
 */
public class RealTimeInfo {
	
	private Integer id;
	/**
	 * 对应的主机id
	 */
	private Integer infoId;
	/**
	 * 空闲cpu
	 */
	private String freeCpu;
	/**
	 * 空闲内存
	 */
	private String freeMem;
	
	/**
	 * tcp端口占用情况
	 */
	private Map<String, String> tcpInfo = new HashMap<String, String>();
	
	/**
	 * io情况
	 */
	private Map<String, String> ioInfo = new HashMap<String, String>();
	
	/**
	 * 网络带宽情况
	 */
	private Map<String, String> networkInfo = new HashMap<String, String>();
	
	/**
	 * 磁盘文件系统情况
	 */
	private Map<String, String> diskInfo = new HashMap<String, String>();
	
	/**
	 * 当前主机上运行的weblogic实例
	 */
	private String weblogicServerCount = "0"; 
	
	private String time;
	
	

	public RealTimeInfo(Integer infoId, String freeCpu, String freeMem, String time) {
		super();
		this.infoId = infoId;
		this.freeCpu = freeCpu;
		this.freeMem = freeMem;
		this.time = time;				
	}

	public RealTimeInfo() {
		super();
		// TODO Auto-generated constructor stub
		tcpInfo.put("ESTABLISHED", "0");
		tcpInfo.put("LISTEN", "0");
		tcpInfo.put("CLOSE_WAIT", "0");
		tcpInfo.put("TIME_WAIT", "0");
		
		networkInfo.put("rx", "0");
		networkInfo.put("tx", "0");
		
		diskInfo.put("rootDisk", "0");
		diskInfo.put("userDisk", "0");
	}

	

	public Map<String, String> getDiskInfo() {
		return diskInfo;
	}

	public void setDiskInfo(Map<String, String> diskInfo) {
		this.diskInfo = diskInfo;
	}

	public String getWeblogicServerCount() {
		return weblogicServerCount;
	}

	public void setWeblogicServerCount(String weblogicServerCount) {
		this.weblogicServerCount = weblogicServerCount;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getInfoId() {
		return infoId;
	}

	public void setInfoId(Integer infoId) {
		this.infoId = infoId;
	}

	public String getFreeCpu() {
		return freeCpu;
	}

	public void setFreeCpu(String freeCpu) {
		this.freeCpu = freeCpu;
	}

	public String getFreeMem() {
		return freeMem;
	}

	public void setFreeMem(String freeMem) {
		this.freeMem = freeMem;
	}

	

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	
	
	
	
	public Map<String, String> getTcpInfo() {
		return tcpInfo;
	}

	public void setTcpInfo(Map<String, String> tcpInfo) {
		this.tcpInfo = tcpInfo;
	}

	public Map<String, String> getIoInfo() {
		return ioInfo;
	}

	public void setIoInfo(Map<String, String> ioInfo) {
		this.ioInfo = ioInfo;
	}

	public Map<String, String> getNetworkInfo() {
		return networkInfo;
	}

	public void setNetworkInfo(Map<String, String> networkInfo) {
		this.networkInfo = networkInfo;
	}

	@Override
	public String toString() {
		return "RealTimeInfo [id=" + id + ", infoId=" + infoId + ", freeCpu=" + freeCpu + ", freeMem=" + freeMem
				+ ", time=" + time + "]";
	}
	
	
	
}

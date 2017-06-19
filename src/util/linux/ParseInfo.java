package util.linux;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;

import util.DcitsUtil;
import entiy.LinuxInfo;
import entiy.RealTimeInfo;

/**
 * 不同类型的主机需要不同信息解析规则
 * @author xuwangcheng
 * @version 20170317
 *
 */
public abstract class ParseInfo {
	/**
	 * 解析vmstat返回的信息<br>
	 * 不同类型的主机可能都可以执行vmstat,但是返回的格式可能不相同
	 * @param info
	 * @param realTimeInfo
	 * @return null
	 */
	public void parseInfo(String info, RealTimeInfo realTimeInfo, LinuxInfo linuxInfo){
		if ("".equals(info)) {
			return;
		}
		
		String[] infos = info.trim().split("(\\s)+");
		
		DecimalFormat formater = new DecimalFormat();  
		formater.setMaximumFractionDigits(1);  
		formater.setRoundingMode(RoundingMode.UP); 
		Double freeMem = 0.0;
		String idle = "";		
		
		if (linuxInfo.getUanme().equals("SunOS") || linuxInfo.getUanme().equals("HU-UX")) {
			freeMem = Double.parseDouble(infos[4]);
			idle = infos[infos.length - 1];
		} else {
			freeMem = ((Double.valueOf(infos[3]) 
					+ Double.valueOf(infos[4]) 
					+ Double.valueOf(infos[5])));
			idle = infos[infos.length - 3];
		}
		
		realTimeInfo.setFreeCpu(idle);
		realTimeInfo.setFreeMem(formater.format((freeMem / Double.parseDouble(linuxInfo.getMemInfo())) * 100));
		realTimeInfo.setTime(DcitsUtil.getCurrentTime(DcitsUtil.DEFAULT_DATE_PATTERN));
	};
	
	/**
	 * 解析返回的tcp端口信息<br>
	 * @param info
	 * @return
	 */
	public void parseTcpInfo(String info, RealTimeInfo realTimeInfo) {
		Map<String, String> map = realTimeInfo.getTcpInfo();		
		
		if (info != null && !info.isEmpty()) {
			String[] strs = info.split(",");
			String[] ss = null;
			for (String s:strs) {
				ss = s.trim().split("\\s+");
				map.put(ss[1], ss[0]);
			}
						
		}
	};
	
	/**
	 * 解析返回的网络带宽信息<br>
	 * @param info
	 * @param realTimeInfo
	 */
	public void parseNetworkInfo(String info, RealTimeInfo realTimeInfo) {
		
	};
	
	/**
	 * 解析返回的磁盘空间信息<br>
	 * @param info
	 * @param realTimeInfo
	 */
	public void parseDiskInfo(String info, RealTimeInfo realTimeInfo) {
		Map<String, String> map = realTimeInfo.getDiskInfo();
		if (info != null && !info.isEmpty()) {
			String[] strs = info.split(",");
			String[] ss = null;
			
			ss = strs[0].trim().split("\\s+");
			String percent = ss[4].substring(0, ss[4].length() - 1);
			
			if (!ss[4].contains("%")) {
				percent = ss[3].substring(0, ss[3].length() - 1);
			} 
			
			map.put("rootDisk", percent);
			
			if (strs.length > 1) {
				ss = strs[1].trim().split("\\s+");
				
				String percent2 = ss[4].substring(0, ss[4].length() - 1);
				
				if (!ss[4].contains("%")) {
					percent2 = ss[3].substring(0, ss[3].length() - 1);
				}
				
				map.put("userDisk", percent2);
			}

		}
	};
	
	
}

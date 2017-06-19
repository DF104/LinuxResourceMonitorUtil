package util.linux;

import java.util.Map;

import entiy.RealTimeInfo;

public class ParseInfoLinux extends ParseInfo{

	@Override
	public void parseNetworkInfo(String info, RealTimeInfo realTimeInfo) {
		// TODO Auto-generated method stub
		Map<String, String> map = realTimeInfo.getNetworkInfo();
		
		if (info != null && !info.isEmpty()) {
			double rx = 0, tx = 0;
			String[] strs = info.split(",");
			String[] ss = null;
			for (String s:strs) {
				ss = s.trim().split("\\s+");
				rx += Double.parseDouble(ss[0]);
				tx += Double.parseDouble(ss[1]);
			}
			map.put("rx", String.valueOf(rx));
			map.put("tx", String.valueOf(tx));
			
		}
	}

}

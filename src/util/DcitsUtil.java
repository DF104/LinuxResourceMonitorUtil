package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import entiy.WeblogicInfo;

/**
 * 工具类
 * @author xuwangcheng
 * @version 20170217
 *
 */
public class DcitsUtil {
	
	//public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";   	
	public static final String DEFAULT_DATE_PATTERN = "HH:mm:ss"; 
	public static final String FULL_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	public static String dataFormat(Date date, String format) {
		DateFormat dateFormat = new SimpleDateFormat(format); 
		return dateFormat.format(date);
	}
	
	/**
	 * 当前日期的String返回
	 * @return
	 */
	public static String getCurrentTime(String format) {
		return dataFormat(new Date(), format);
	}
	
	/**
	 * 从context中的weblogicInfo的列表中查找指定id的weblogicInfo
	 * @param weblogicId
	 * @param list
	 * @return
	 */
	public static WeblogicInfo findByWeblogicId(Integer weblogicId, List<WeblogicInfo> list) {		
		for (WeblogicInfo info:list) {
			if (info.getWeblogicId().equals(weblogicId)) {
				return info;
			}
		}
		return null;
	}
	
	
}

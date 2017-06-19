package util.weblogic;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import util.DcitsUtil;
import weblogic.health.HealthState;
import entiy.WeblogicInfo;
import entiy.WeblogicRealTimeInfo;


/**
 * jmx获取weblogic信息的工具类<br>
 * 参考 JmxWeblogicDemo.java
 * @author xuwangcheng
 * @version 20170310
 *
 */
public class WeblogicJmxUtil {
	
	private static final String RUNTIMESERVICEMBEAN = "com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean";
	
	private static final String PROTOCOL = "t3";
	
	private static final String JINDIROOT = "/jndi/";
	
	private static final String MSERVER = "weblogic.management.mbeanservers.runtime";	
	
	
	/**
	 * 初始化jmx连接
	 * @param weblogic
	 * @throws MalformedObjectNameException
	 * @throws IOException
	 */
	public static void getConnection(WeblogicInfo weblogic) throws MalformedObjectNameException, IOException, Exception {
		
		Integer portInteger = Integer.valueOf(weblogic.getPort());
		int port = portInteger.intValue();
		JMXServiceURL serviceURL = new JMXServiceURL(PROTOCOL, weblogic.getHost(), port, JINDIROOT + MSERVER);
		
		Hashtable<String, String> h = new Hashtable<String, String>();
		h.put(Context.SECURITY_PRINCIPAL, weblogic.getUsername());
	    h.put(Context.SECURITY_CREDENTIALS, weblogic.getPassword());
	    h.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES,
	            "weblogic.management.remote");
	    	    
	    weblogic.setConnection(JMXConnectorFactory.connect(serviceURL, h).getMBeanServerConnection());
	    
	    ObjectName runtimeService = new ObjectName(RUNTIMESERVICEMBEAN);
	    
	    //运行状态
	    ObjectName serverRuntime = getAttribute(runtimeService, "ServerRuntime", weblogic.getConnection());	    
	    weblogic.setServerRuntime(serverRuntime);
	    //JVM状态
	    ObjectName jvmRuntime = getAttribute(serverRuntime, "JVMRuntime", weblogic.getConnection());
	    weblogic.setJvmRuntime(jvmRuntime);
	    
	    //线程状态
	    ObjectName threadPoolRuntime = getAttribute(serverRuntime, "ThreadPoolRuntime", weblogic.getConnection());
	    weblogic.setThreadPoolRuntime(threadPoolRuntime);
	}
	
	/**
	 * 获取weblogic信息
	 * 前台调用一次weblogic/getInfo?weblogicId=?就执行一次该方法
	 * 
	 * @param weblogic
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 */
	public static void getWeblogicInfo(WeblogicInfo weblogic) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, Exception {
		if (weblogic.getConnection() == null) {
			return;
		}
		//getServerRuntime 获取运行信息
		WeblogicRealTimeInfo info = weblogic.getInfo();
		MBeanServerConnection conn = weblogic.getConnection();
		
		//运行状态情况
		ObjectName serverRuntime = weblogic.getServerRuntime();
		//jvm情况
	    ObjectName JVMRuntime = weblogic.getJvmRuntime();
		
	    //线程队列情况
	    ObjectName threadPoolRuntime = weblogic.getThreadPoolRuntime();
		
		//是否首次获取或者是否重新获取新的固定信息？
		if (weblogic.isFirstGetInfo()) {
			//server的name
			String serverName = getAttribute(serverRuntime, "Name", conn);
			
			//server的激活时间
			Long activationTime = getAttribute(serverRuntime, "ActivationTime",  conn);
		    Date date = new Date(activationTime);
		    String time = formatDate(date, "yyyy/MM/dd HH:mm:ss");
		    
		    //运行状态
		    String state = getAttribute(serverRuntime, "State", conn);
		    
		    //健康状态
		    HealthState healthState = (HealthState) conn.getAttribute(serverRuntime, "HealthState");
		    String health = healthState.getState() == 0 ? "Health" : "Not Health";
		    
		    //最大堆内存大小
		    Long heapSizeMax = getAttribute(JVMRuntime, "HeapSizeMax", conn);
		    String maxJvm = byteToMB(heapSizeMax);
		    
		    info.setServerName(serverName);
		    info.setHealth(health);
		    info.setStatus(state);
		    info.setStartTime(time);
		    info.setMaxJvm(maxJvm);
		    
		    weblogic.setFirstGetInfo(false);
		}
		
	    //空闲内存百分比
	    Integer heapFreePercent = getAttribute(JVMRuntime, "HeapFreePercent", conn);
	    String freePercent = String.valueOf(heapFreePercent);
	    
	    //当前已使用的堆的总空间
	    Long heapSizeCurrent = getAttribute(JVMRuntime, "HeapSizeCurrent", conn);
	    String currentSize = byteToMB(heapSizeCurrent);
	    
	    // 当前堆 空闲 HeapFreeCurrent
	    Long heapFreeCurrent = getAttribute(JVMRuntime, "HeapFreeCurrent", conn);
	    String freeSize = byteToMB(heapFreeCurrent);
	    
	    // 执行线程总数 
        Integer executeThreadTotalCount = getAttribute(threadPoolRuntime, "ExecuteThreadTotalCount", conn);
        String maxThreadCount = String.valueOf(executeThreadTotalCount);
	        	    
	    //空闲线程数
	    Integer executeThreadCurrentIdleCount = getAttribute(
	    		threadPoolRuntime, "ExecuteThreadIdleCount", conn);
	    String idleCount = String.valueOf(executeThreadCurrentIdleCount);
	    
	    //暂挂请求数
	    Integer pendingRequestCurrentCount = getAttribute(
	    		threadPoolRuntime, "PendingUserRequestCount", conn);
	    String pendingCount = String.valueOf(pendingRequestCurrentCount);
	         
	    
	    info.getJvmInfo().put(WeblogicRealTimeInfo.JVM_CURRENT_USE_SIZE, currentSize);
	    info.getJvmInfo().put(WeblogicRealTimeInfo.JVM_FREE_PERCENT, freePercent);
	    info.getJvmInfo().put(WeblogicRealTimeInfo.JVM_FREE_SIZE, freeSize);
	    
	    info.getQueueInfo().put(WeblogicRealTimeInfo.THREAD_IDLE_COUNT, idleCount);
	    info.getQueueInfo().put(WeblogicRealTimeInfo.THREAD_REQUEST_PENDING_COUNT, pendingCount);
	    info.getQueueInfo().put(WeblogicRealTimeInfo.THREAD_TOTAL_COUNT, maxThreadCount);
	    
	    info.setTime(DcitsUtil.getCurrentTime(DcitsUtil.DEFAULT_DATE_PATTERN));
	    
	}
	
	
	
	/**
	 * 获取weblogic属性参数
	 * 
	 * @param objectName
	 * @param name
	 * @param connection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAttribute(ObjectName objectName, String name, MBeanServerConnection connection) {
	    Object obj = null;
	    try {
	        obj = connection.getAttribute(objectName, name);
	    } catch (Exception e) {
	        // TODO
	        e.printStackTrace();
	    }
	    return (T) obj;
	}
	
	/**
	 * 日期格式转换
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatDate(Date date, String format) {
	    DateFormat df = new SimpleDateFormat(format);
	    return df.format(date);
	}
	
	
	/**
	 * 字节转换成MB
	 * 
	 * @param bytes
	 * @return
	 */
	public static String byteToMB(long bytes) {
	    double mb = (double) bytes / 1024 / 1024;
	    DecimalFormat df = new DecimalFormat("#.00");
	    return df.format(mb);
	}
	
	/**
	 * 解析批量保存weblogicServer的信息字符串
	 * @param str
	 * @param weblogcId
	 * @return
	 */
	public static List<WeblogicInfo> parseServerListStr(String str, int weblogcId) {
		List<WeblogicInfo> servers = new ArrayList<WeblogicInfo>();				
		
		String[] infos = str.split("\\n");
		String[] info = null;		
		for (String s:infos) {
			info = s.split(",");
			String[] ipport = info[0].split(":");	
			String mark = "";
			if (info.length > 3) {
				mark = info[3].trim();
			}
			try {				
				servers.add(new WeblogicInfo(++weblogcId, ipport[0].trim(), ipport[1].trim(), info[1].trim(), info[2].trim(), mark, DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN)));				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				continue;
			}
		}		
		return servers;
	}
	
}

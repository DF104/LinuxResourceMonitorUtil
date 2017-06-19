package listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import util.consts.LinuxConstant;
import util.consts.WeblogicConstant;
import dao.CommonInfoDao;
import dao.ServerDao;
import entiy.LinuxInfo;
import entiy.WeblogicInfo;

/**
 * 自定义监听器
 * 
 * @author xuwangcheng
 * @version 20170224
 */
public class InitWebListener implements ServletContextListener {
	
	/**
	 * 容器销毁时断开所有主机连接
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		ServletContext context = arg0.getServletContext();
		Map<String, LinuxInfo> serverList = (Map<String, LinuxInfo>)context.getAttribute(LinuxConstant.LINUX_INFO_LIST_ATTRIBUTE);
		for (LinuxInfo info:serverList.values()) {
			try {
				info.stop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			info.disconect();
		}
		
	}
	
	/**
	 * 启动时
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {	
		arg0.getServletContext().setAttribute(LinuxConstant.LINUX_INFO_LIST_ATTRIBUTE, new HashMap<String, LinuxInfo>());
		arg0.getServletContext().setAttribute(WeblogicConstant.WEBLOGIC_INFO_LIST_ATTRIBUTE, new ArrayList<WeblogicInfo>());
		
		ServerDao dao = new ServerDao();
		CommonInfoDao dao1 = new CommonInfoDao();
		try {
			dao.createTable();
			dao1.createTable();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import util.DcitsUtil;
import util.consts.LinuxConstant;
import util.consts.WeblogicConstant;
import util.weblogic.WeblogicJmxUtil;
import dao.ServerDao;
import entiy.WeblogicInfo;

public class WeblogicServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	private static int weblogic_id = 0;
	
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        
		PrintWriter out = response.getWriter();
		
        String uri = request.getRequestURI();      
        uri = uri.substring(uri.lastIndexOf("/") + 1);       
        ServletContext context = getServletContext();
        
        Map<String, Object> data = new HashMap<String, Object>();;
        
        List<WeblogicInfo> list = (List<WeblogicInfo>) context.getAttribute(WeblogicConstant.WEBLOGIC_INFO_LIST_ATTRIBUTE);
        
        if (WeblogicConstant.GET_WEBLOGIC_LIST.equals(uri)) {
        	for (WeblogicInfo w:list) {
        		w.setInfo();
        	}
        	data.put("data", list);
        	out.println(JSONObject.fromObject(data));  
        	
        } else if (WeblogicConstant.REGET_WEBLOGIC_BASE_INFO.equals(uri)) {
        	Integer weblogicId = null;
        	try {
        		weblogicId = Integer.parseInt(request.getParameter("weblogicId"));
			} catch (Exception e) {
				e.printStackTrace();
				data.put("returnCode", LinuxConstant.ERROR_RETURN_CODE);
				data.put("msg", "缺少weblogicId参数");
				out.println(JSONObject.fromObject(data)); 
			}
        	
        	WeblogicInfo info = DcitsUtil.findByWeblogicId(weblogicId, list);
        	
        	if (info != null) {
        		info.setFirstGetInfo(true);
        	}
        	data.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
        	out.println(JSONObject.fromObject(data)); 
        	
        } else if (WeblogicConstant.RECONNECT_WEBLOGIC_SERVER.equals(uri)) {
        	
        	Integer weblogicId = null;
        	try {
        		weblogicId = Integer.parseInt(request.getParameter("weblogicId"));
			} catch (Exception e) {
				e.printStackTrace();
				data.put("returnCode", LinuxConstant.ERROR_RETURN_CODE);
				data.put("msg", "缺少weblogicId参数");
				out.println(JSONObject.fromObject(data)); 
			}
        	
        	WeblogicInfo info = DcitsUtil.findByWeblogicId(weblogicId, list);
        	
        	if (info != null) {
        		info.connect();
        	}
        	data.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
        	out.println(JSONObject.fromObject(data)); 
        	
        } else if (WeblogicConstant.DEL_WEBLOGIC_SERVER.equals(uri)) {
        	
        	Integer weblogicId = null;
        	try {
        		weblogicId = Integer.parseInt(request.getParameter("weblogicId"));
			} catch (Exception e) {
				e.printStackTrace();
				data.put("returnCode", LinuxConstant.ERROR_RETURN_CODE);
				data.put("msg", "缺少weblogicId参数");
				out.println(JSONObject.fromObject(data)); 
			}
        	
        	WeblogicInfo info = DcitsUtil.findByWeblogicId(weblogicId, list);
        	
        	if (info != null) {
        		list.remove(info);       		
        	}
        	
        	data.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
        	out.println(JSONObject.fromObject(data)); 
        	
        } else if (WeblogicConstant.DEL_ALL_WEBLOGIC_SERVER.equals(uri)) {
        	context.setAttribute(WeblogicConstant.WEBLOGIC_INFO_LIST_ATTRIBUTE, new ArrayList<WeblogicInfo>());
        	System.gc();
        	
        	data.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
        	out.println(JSONObject.fromObject(data)); 
        	
        } else if (WeblogicConstant.SAVE_WEBLOGIC_INFO.equals(uri)) {
        	
        	String mode = request.getParameter("mode");
        	
        	data.put("returnCode", LinuxConstant.ERROR_RETURN_CODE);
        	data.put("msg", "");
        	ServerDao dao = new ServerDao();
        	//单条保存
        	if (LinuxConstant.SAVE_MODE_SINGLE.equals(mode)) {
        		
        		String host = request.getParameter("host");
            	String username = request.getParameter("username");
            	String password = request.getParameter("password");  
            	String mark = request.getParameter("mark"); 
            	
            	if (host != null && !host.isEmpty() && username !=null 
            			&& !username.isEmpty() && password != null && !password.isEmpty()) {
            		
            		String[] ipport = host.split(":");
            		WeblogicInfo info = null;
            		
            		try {
            			info = new WeblogicInfo(++weblogic_id, ipport[0], ipport[1], username, password, mark, DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN)); 
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						data.put("msg", "数据错误,可能没有加上端口号,请检查!");
						out.println(JSONObject.fromObject(data)); 
						return;
					}
            		
            		info.connect();
        			
        			if (!"true".equals(info.getConnectStatus())) {
        				
        				data.put("msg", "尝试进行连接失败,请检查，原因：" + info.getConnectStatus());
						out.println(JSONObject.fromObject(data));
						return;
        			}
            		//测试连接成功，为可用的信息,保存该信息到数据库 
        			try {
						dao.saveServerAfter(info);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			      			
            		list.add(info);            		            		
            		data.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);  			
            	} else {            		
            		data.put("msg", "参数不完整!");  
            	}    
        	}  
        	
        	//批量保存
        	if (LinuxConstant.SAVE_MODE_BATCH.equals(mode)) {
        		
        		String serversStr = request.getParameter("serverList");
        		
        		if (serversStr != null && !serversStr.isEmpty()) {
        			
        			List<WeblogicInfo> infos = WeblogicJmxUtil.parseServerListStr(serversStr, this.weblogic_id);      			
        			
        			if (infos.size() != 0) {
        				int successCount = 0;
        				weblogic_id += infos.size();
        				
        				for (WeblogicInfo info:infos) {
        					
        					info.connect();
        					
        					if ("true".equals(info.getConnectStatus())) {
        						
        						try {
									dao.saveServerAfter(info);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
        						
        						list.add(info);
        						successCount++; 
        					}       					
        				}
        			    				
        				data.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE); 
        				data.put("count", successCount); 
        			} else {
        				data.put("msg", "输入格式有误!"); 
        			}
        		} else {
        			data.put("msg", "输入信息不完整!");  
        		}       		
        	}       	
        	out.println(JSONObject.fromObject(data));       	
        } else if (WeblogicConstant.USE_HISTORY_SERVER_INFO.equals(uri)) {
        	ServerDao dao = new ServerDao();       	
    		String id = request.getParameter("id");
    		String[] ids = id.split(",");
    		
    		if (ids.length > 0) {
    			int successCount = 0;
    			for (String s:ids) {
    				try {
    					WeblogicInfo info = new WeblogicInfo(dao.findById(Integer.parseInt(s)));
                		info.setWeblogicId(++weblogic_id);
                		info.connect();
                		
                		if (!"true".equals(info.getConnectStatus())) {
                			continue;
                		}
                		
                		list.add(info);
                		successCount++;
                		dao.updateTime(Integer.parseInt(s)); 
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}   				
    			}
    			data.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);  	
    			data.put("count", successCount);
    		} else {
    			data.put("returnCode", LinuxConstant.ERROR_RETURN_CODE);	
    		}    		      		    		        		
        	out.println(JSONObject.fromObject(data));
        }        
	} 
}

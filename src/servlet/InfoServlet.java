package servlet;

import java.io.IOException;
import java.io.PrintWriter;
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
import util.linux.GetLinuxInfoUtil;
import dao.ServerDao;
import entiy.LinuxInfo;
import entiy.RealTimeInfo;
import entiy.ServerInfo;

/**
 * 操作servlet
 * @author xuwangcheng
 * @version 20170224
 *
 */
public class InfoServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static int info_id = 0;
	
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        
		PrintWriter out = response.getWriter();
		
        String uri = request.getRequestURI();      
        uri = uri.substring(uri.lastIndexOf("/") + 1);
        
        ServletContext context = getServletContext();
        
        Map<String, Object> ajaxMap = new HashMap<String, Object>();
        
        Map<String, LinuxInfo> serverList = (Map<String, LinuxInfo>)context.getAttribute(LinuxConstant.LINUX_INFO_LIST_ATTRIBUTE);
        //获取当前所有主机的实时资源情况
        if (LinuxConstant.GET_REAL_TIME_INFO_URL.equals(uri)) {
        	Map<String, RealTimeInfo> map = new HashMap<String, RealTimeInfo>();
        	
        	try {
        		for (String infoId:serverList.keySet()) {
        			map.put(infoId, (RealTimeInfo)context.getAttribute(infoId));
        		}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
    		JSONObject obj = JSONObject.fromObject(map);       		
    		out.println(obj.toString());  
    	//获取当前主机列表	
        } else if (LinuxConstant.GET_SERVER_INFO_LIST.equals(uri)) { 
        	String[] ids = new String[serverList.size()];
        	int i = serverList.size() - 1;
        	for (String key:serverList.keySet()) {
        		ids[i] = key;
        		i--;
        	}       	
        	ajaxMap.put("length", ids);
        	ajaxMap.put("list", serverList);
        	out.println(JSONObject.fromObject(ajaxMap));  
        //增加主机	
        } else if (LinuxConstant.ADD_LINUX_SERVER_URL.equals(uri)) {
        	
        	String mode = request.getParameter("mode");
        	
    		ajaxMap.put("returnCode", LinuxConstant.ERROR_RETURN_CODE);
    		
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
            		
            		LinuxInfo info = new LinuxInfo(ipport[0].trim(), "22", username.trim(), password.trim(), DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN));
            		
            		if (ipport.length == 2) {
            			info.setPort(ipport[1]);
            		}
            		
            		info.setInfoId(++info_id);
            		info.setMark(mark);
            		try {
    					info.conect();
    					info.start(context);
    				} catch (Exception e) {
    					// TODO Auto-generated catch block
    					//e.printStackTrace();
    					ajaxMap.put("msg", "启动连接查询出现错误,请稍后再试,错误信息：" + info.getErrorInfo()); 
    					out.println(JSONObject.fromObject(ajaxMap).toString());
    					return;
    				}
            		
            		try {
						dao.saveServerAfter(info);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		
            		serverList.put(String.valueOf(info.getInfoId()), info);            		
            		
            		ajaxMap.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);  			
            	} else {
            		
            		ajaxMap.put("msg", "参数不完整!");  
            	}
        	}  
        	
        	//批量保存
        	if (LinuxConstant.SAVE_MODE_BATCH.equals(mode)) {
        		String serversStr = request.getParameter("serverList");
        		if (serversStr != null && !serversStr.isEmpty()) {
        			List<LinuxInfo> infos = GetLinuxInfoUtil.parseServerListStr(serversStr, this.info_id);
        			if (infos.size() != 0) {
        				int successCount = 0;
        				info_id += infos.size();
        				
        				for (LinuxInfo info:infos) {
        					try {
        						info.conect();
            					info.start(context);            					          					
    						} catch (Exception e) {
    							// TODO: handle exception
    							e.printStackTrace();
    							continue;
    						}
        					try {
								dao.saveServerAfter(info);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
        					serverList.put(String.valueOf(info.getInfoId()), info); 
        					successCount++; 
        				}
        			    				
        				ajaxMap.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE); 
        				ajaxMap.put("count", successCount); 
        			} else {
        				ajaxMap.put("msg", "输入格式有误!"); 
        			}
        		} else {
        			ajaxMap.put("msg", "输入信息不完整!");  
        		}       		
        	}
        	
        	//context.setAttribute(LinuxConstant.LINUX_INFO_LIST_ATTRIBUTE, serverList); 
        	out.println(JSONObject.fromObject(ajaxMap));
        //删除主机信息	
        } else if (LinuxConstant.DEL_LINUX_SERVER_URL.equals(uri)) {
        	
        	String infoId = request.getParameter("infoId");
        	
        	ajaxMap.put("returnCode", LinuxConstant.ERROR_RETURN_CODE);
        	
        	if (infoId != null && !infoId.isEmpty()) {
        		LinuxInfo info = serverList.get(infoId);
        		
        		if (info != null) {
        			try {
						info.stop();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			info.disconect();
        			serverList.remove(String.valueOf(info.getInfoId()));
        			
        			//context.setAttribute(LinuxConstant.LINUX_INFO_LIST_ATTRIBUTE, serverList);
        			ajaxMap.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
        			
        		} else {
        			ajaxMap.put("msg", "没有该服务器主机信息!");
        		}
        	} else {
        		ajaxMap.put("msg", "infoId不能为空!");
        	}
        	
        	out.println(JSONObject.fromObject(ajaxMap)); 
        	//重连主机
        } else if (LinuxConstant.RECONNECT_LINUX_SERVER_URL.equals(uri)) {
        	String infoId = request.getParameter("infoId");
        	
        	ajaxMap.put("returnCode", LinuxConstant.ERROR_RETURN_CODE);
        	if (infoId != null && !infoId.isEmpty()) {
        		LinuxInfo info = serverList.get(infoId);
        		
        		if (info != null) {
        			try {
						info.stop();
						info.disconect();
						info.conect();
						info.start(context);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						ajaxMap.put("msg", "重连操作失败,请稍后尝试重新操作!"); 
						out.println(JSONObject.fromObject(ajaxMap).toString());
						return;
					}        			      			
        			ajaxMap.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);        			
        		} else {
        			ajaxMap.put("msg", "没有该服务器主机信息!");
        		}
        	} else {
        		ajaxMap.put("msg", "infoId不能为空!");
        	}  
        	out.println(JSONObject.fromObject(ajaxMap)); 
        	//删除所有主机
        } else if (LinuxConstant.CLEAR_ALL_LINUX_SERVER_URL.equals(uri)) {
        	
        	ajaxMap.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
        	
        	for (LinuxInfo info:serverList.values()) {
    			try {
    				info.stop();
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			info.disconect();
    		}       	
        	context.setAttribute(LinuxConstant.LINUX_INFO_LIST_ATTRIBUTE, new HashMap<String, LinuxInfo>());
        	System.gc();
        	out.println(JSONObject.fromObject(ajaxMap));
        //历史记录	
        } else if (LinuxConstant.GET_HISTORY_SERVER_INFO_URL.equals(uri)) {
        	
        	ServerDao dao = new ServerDao();
        	try {
				List<ServerInfo> data = dao.findByType(request.getParameter("type"));
				ajaxMap.put("data", data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	out.println(JSONObject.fromObject(ajaxMap));
        	//使用保存的历史数据
        } else if (LinuxConstant.USE_HISTORY_SERVER_INFO.equals(uri)) {
        	ServerDao dao = new ServerDao();       	
    		String id = request.getParameter("id");
    		String[] ids = id.split(",");
    		
    		if (ids.length > 0) {
    			int successCount = 0;
    			for (String s:ids) {
    				try {
    					LinuxInfo info = new LinuxInfo(dao.findById(Integer.parseInt(s)));
                		info.setInfoId(++info_id);
                		info.conect();
                		info.start(context);   
                		
                		serverList.put(String.valueOf(info.getInfoId()), info); 
                		successCount++;
                		dao.updateTime(Integer.parseInt(s)); 
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}   				
    			}
    			ajaxMap.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);  	
    			ajaxMap.put("count", successCount);
    		} else {
    			ajaxMap.put("returnCode", LinuxConstant.ERROR_RETURN_CODE);	
    		}    		      		    		        		
        	out.println(JSONObject.fromObject(ajaxMap));
        //删除历史记录	
        } else if(LinuxConstant.DEL_HISTORY_SERVER_INFO_URL.equals(uri)) {
        	ServerDao dao = new ServerDao();
        	try {
				dao.delServer(Integer.parseInt(request.getParameter("id")));
				ajaxMap.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);	
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				ajaxMap.put("returnCode", LinuxConstant.ERROR_RETURN_CODE);	
				ajaxMap.put("msg", "删除失败,请稍后再试!");	
			}
        	out.println(JSONObject.fromObject(ajaxMap));
        //执行命令	
        } else if (LinuxConstant.EXEC_COMMAND_URL.equals(uri)) {
        	ajaxMap.put("returnCode", LinuxConstant.ERROR_RETURN_CODE);	
        	ajaxMap.put("msg", "");
        	LinuxInfo info = serverList.get(request.getParameter("infoId"));
        	
        	if (info != null) {
        		String command = request.getParameter("command");
        		
        		if (GetLinuxInfoUtil.checkCommand(command)) {
        			ajaxMap.put("msg", "此命令禁止从该页面执行!");	
        			out.println(JSONObject.fromObject(ajaxMap));
        			return;
        		}
        		
        		context.setAttribute(LinuxConstant.STOP_EXEC_COMMAND_FLAG_ATTRIBUTE, "false");
        		//最多读取1000行
        		String returnStr = GetLinuxInfoUtil.execCommand(info.getConn(), command, 1000, context);
        		
        		if (returnStr != null && !returnStr.isEmpty()) {
        			ajaxMap.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);	
        			ajaxMap.put("msg", returnStr);
        		}        		
        	}        	
        	out.println(JSONObject.fromObject(ajaxMap));
        	//中断正在执行的命令
        } else if (LinuxConstant.STOP_EXEC_COMMAND_URL.equals(uri)) {
        	context.setAttribute(LinuxConstant.STOP_EXEC_COMMAND_FLAG_ATTRIBUTE, "true");
        	ajaxMap.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);	
        	out.println(JSONObject.fromObject(ajaxMap));
        }
	}
	
}

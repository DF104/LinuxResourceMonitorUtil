package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import util.consts.CommonInfoConstant;
import util.consts.LinuxConstant;
import dao.CommonInfoDao;
import entiy.CommonInfo;

public class CommonInfoServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void service (HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        
		PrintWriter out = response.getWriter();
		
        String uri = request.getRequestURI();      
        uri = uri.substring(uri.lastIndexOf("/") + 1);
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        CommonInfoDao dao = new CommonInfoDao();
    	
    	data.put("returnCode", LinuxConstant.ERROR_RETURN_CODE);
    	data.put("msg", "");
        
        if (CommonInfoConstant.LIST_COMMON_INFO.equalsIgnoreCase(uri)) {
        	String infoType = request.getParameter("infoType");
        	List<CommonInfo> infos = null;
        	try {
				infos = dao.list(infoType);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				data.put("msg", "查询数据出错");
				out.println(JSONObject.fromObject(data)); 
				return;
			}
        	data.put("data", infos);
        	data.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
        	out.println(JSONObject.fromObject(data)); 
        	
        } else if (CommonInfoConstant.GET_COMMON_INFO.equalsIgnoreCase(uri)) {
        	Integer infoId = Integer.parseInt(request.getParameter("infoId"));       	
        	CommonInfo info = null;
        	try {
				info = dao.get(infoId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				data.put("msg", "查询数据出错");
				out.println(JSONObject.fromObject(data)); 
				return;
			}
        	
        	data.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
        	data.put("info", info);
        	out.println(JSONObject.fromObject(data)); 
        	
        } else if (CommonInfoConstant.DEL_COMMON_INFO.equalsIgnoreCase(uri)) {
        	Integer infoId = Integer.parseInt(request.getParameter("infoId")); 
        	try {
				dao.del(infoId);
			} catch (Exception e) {
				e.printStackTrace();
				data.put("msg", "删除数据出错");
				out.println(JSONObject.fromObject(data)); 
				return;
			}
        	
        	data.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
        	out.println(JSONObject.fromObject(data)); 
        	
        } else if(CommonInfoConstant.EDIT_COMMON_INFO.equalsIgnoreCase(uri)) {
        	
        	String infoId_s = request.getParameter("infoId");             	
        	Integer infoId = (infoId_s == null || infoId_s.isEmpty()) ? null : Integer.parseInt(infoId_s); 
        	
        	String infoType = request.getParameter("infoType");
        	String infoName = request.getParameter("infoName");
        	String infoDetail = request.getParameter("infoDetail");
        	String infoTag = request.getParameter("infoTag");
        	String joinUserName = request.getParameter("joinUserName");
        	String filePath = request.getParameter("filePath");
        	CommonInfo info = new CommonInfo(infoId, infoType, infoName, infoDetail, infoTag, null, joinUserName);
        	info.setFilePath(filePath);
        	try {
				dao.edit(info);       		
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				data.put("msg", "操作数据库出错!");
				out.println(JSONObject.fromObject(data)); 
				return;
			}
        	
        	
        	data.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
        	out.println(JSONObject.fromObject(data)); 
        	
        } else if (CommonInfoConstant.BATCH_DEL_COMMON_INFO.equalsIgnoreCase(uri)) {
        	String ids = request.getParameter("ids");
        	int delCount = 0;
        	boolean allDelFlag = true;
        	
        	for (String s:ids.split(",")) {
        		if (!s.isEmpty()) {
        			try {
						dao.del(Integer.parseInt(s));
						delCount++;
					}  catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						allDelFlag = false;
					}
        		}
        	}
        	if (allDelFlag) {
        		data.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
        	}
        	data.put("delCount", delCount);
        	out.println(JSONObject.fromObject(data)); 
        }
	}
	
}

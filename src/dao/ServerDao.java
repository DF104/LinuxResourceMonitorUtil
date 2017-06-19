package dao;

import java.util.List;

import mapper.ServerInfoMapper;
import util.DcitsUtil;
import entiy.ServerInfo;
import entiy.WeblogicInfo;

/**
 * 记录linuxInfo和weblogicInfo的历史记录<br>
 * <br>type = 0 LinuxInfo
 * <br>type = 1 weblogicInfo
 * <br>
 * ServerInfo (id integer primary key, host text,port text,username text,password text,mark text,time text,type text)
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,20170317
 *
 */

public class ServerDao {
	
	private static ServerInfoMapper mapper = new ServerInfoMapper();
	
	/**
	 * 创建表
	 * @throws Exception 
	 */
	public void createTable() throws Exception {
		String sql = "create table if not exists ServerInfo (id integer primary key autoincrement, host text,port text,username text,password text,mark text,time text,type text)";
		try {
			mapper.execSQL(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 保存服务器信息
	 * @param info
	 * @throws Exception 
	 */
	public int saveServer(ServerInfo info) throws Exception {
		
		String type = "0";		
		if (info instanceof WeblogicInfo) {
			type = "1";
		}
		String sql = "insert into ServerInfo values(null,?,?,?,?,?,?,?)";
		
		try {
			mapper.execSQL(sql, new Object[]{info.getHost(), info.getPort()
							, info.getUsername(), info.getPassword(), info.getMark()
							, DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN), type});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getEffectRowCount();
	}
	
	/**
	 * 根据id查找信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ServerInfo findById(Integer id) throws Exception {
		
		String sql = "select * from ServerInfo where id=?";
		try {
			mapper.execSQL(sql, id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getObject();
		
	}
	
	/**
	 * 根据type查找指定的历史记录
	 * @param type
	 * @return
	 * @throws Exception 
	 */
	public List<ServerInfo> findByType (String type) throws Exception {
		
		String sql = "select * from ServerInfo where type=?";
		try {
			mapper.execSQL(sql, type);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getObjectList();
		
	}
	
	/**
	 * 更新最近使用时间
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public int updateTime(Integer id) throws Exception {
		
		String sql = "update ServerInfo set time=? where id=?";
		
		try {
			mapper.execSQL(sql, DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN), id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getEffectRowCount();
		
	}
	
	/**
	 * 查找新添加的服务器信息是否已经在数据库中
	 * @param info
	 * @return
	 * @throws Exception 
	 */
	public Integer checkExistServerInDB(ServerInfo info) throws Exception  {
		String sql = "select * from ServerInfo where host=? and port=? and username=? and password=?";
		
		try {
			mapper.execSQL(sql, info.getHost(), info.getPort(), info.getUsername(), info.getPassword());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		if (mapper.getQueryCount() > 0) {
			return mapper.getObject().getId();
		}
		
		return null;	
	}
	
	/**
	 * 根据id删除
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public int delServer(Integer id) throws Exception  {
		String sql = "delete from ServerInfo where id=?";
		
		try {
			mapper.execSQL(sql, id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getEffectRowCount();
				
	}
	
	/**
	 * 保存服务器信息之后是否添加进库还是更新时间
	 * @param info
	 * @throws Exception 
	 */
	public void saveServerAfter(ServerInfo info) throws Exception {
		Integer id = checkExistServerInDB(info);
		if (id == null) {
			//添加
			saveServer(info);
		} else {
			//更新
			updateTime(id);
		}
	}
}

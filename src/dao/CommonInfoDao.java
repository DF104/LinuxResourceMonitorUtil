package dao;

import java.util.List;

import mapper.CommonInfoMapper;
import util.DcitsUtil;
import entiy.CommonInfo;

/**
 * create table if not exists CommonInfo{info_id integer primary key autoincrement,info_type text,info_name text,info_detail text,info_tag text,join_time text,join_user_name text};
 * @author xuwangcheng
 * @version 1.0.0.0,20170426
 * 
 */
public class CommonInfoDao {
	
	private static CommonInfoMapper mapper = new CommonInfoMapper();
	
	/**
	 * 创建表
	 * @throws Exception
	 */
	public void createTable() throws Exception {
		String sql = "create table if not exists CommonInfo(info_id integer primary key autoincrement,info_type text,file_path text,info_name text,info_detail text,info_tag text,join_time text,join_user_name text)";
		
		try {
			mapper.execSQL(sql);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 查找全部
	 * @return
	 * @throws Exception
	 */
	public List<CommonInfo> list(String infoType) throws Exception {
		String sql = "select info_id,info_type,file_path,info_name,info_tag,join_time,join_user_name from CommonInfo";
		
		if (infoType != null && !"11".equals(infoType)) {
			sql += " where info_type=?";
		}
		
		try {
			mapper.execSQL(sql, infoType);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		
		
		
		return mapper.getObjectList();
	}
	
	/**
	 * 查找指定信息
	 * @param infoId
	 * @return
	 * @throws Exception 
	 */
	public CommonInfo get(Integer infoId) throws Exception {
		String sql = "select * from CommonInfo where info_Id=?";
		
		try {
			mapper.execSQL(sql, infoId);			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getObject();
	}
	
	/**
	 * 删除指定信息
	 * @param infoId
	 * @return
	 * @throws Exception
	 */
	public int del(Integer infoId) throws Exception {
		String sql = "delete from CommonInfo where info_id=?";
		
		try {
			mapper.execSQL(sql, infoId);			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return mapper.getEffectRowCount();
	}
	
	/**
	 * 编辑和新增
	 * @param info
	 * @return
	 * @throws Exception
	 */
	public int edit(CommonInfo info) throws Exception {
		Integer infoId = info.getInfoId();
		String sql = "";
		try {
			if (infoId == null) {
				sql = "insert into CommonInfo (info_id,info_type,info_name,info_detail,file_path,info_tag,join_time,join_user_name) values(null,?,?,?,?,?,?,?)";
				mapper.execSQL(sql, info.getInfoType(), info.getInfoName(), info.getInfoDetail(), info.getFilePath(), info.getInfoTag()
						, DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN), info.getJoinUserName());
			} else {
				sql = "update CommonInfo set info_type=?,info_name=?,info_detail=?,file_path=?,info_tag=? where info_id=?";
				mapper.execSQL(sql, info.getInfoType(), info.getInfoName(), info.getInfoDetail(), info.getFilePath(), info.getInfoTag(), info.getInfoId());
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		
		return mapper.getEffectRowCount();
	}
}

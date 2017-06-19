package util;

import java.util.List;

import mapper.ServerInfoMapper;

import org.junit.Test;

import dao.ServerDao;
import entiy.ServerInfo;
import entiy.WeblogicInfo;

public class TestUtil {
	
/*	@Test
	public void newT() throws Exception {
		LinuxInfo info = new LinuxInfo("115.159.34.224","root","xUWANGCHENG14", DcitsUtil.getCurrentTime());
		info.conect();
		BufferedReader reader = GetLinuxInfoUtil.execLoopCommand(info.getConn(), "iostat -d -x -k 3");
		String str = "";
		reader.readLine();
		while ((str = reader.readLine()) != "") {
			System.out.println(str);
		}
	}*/
	
	@Test
	public void testWeblogicUtil() throws InterruptedException {
		WeblogicInfo weblogic = new WeblogicInfo(1, "127.0.0.1", "7001", "weblogic", "11111111", "", DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN));
		
		int i = 0;
		weblogic.connect();
		while (i < 1000) {
			long start = System.currentTimeMillis();				
			weblogic.setInfo();
			long end = System.currentTimeMillis();
			System.out.println(end - start);
			System.out.println(weblogic.toString());
			Thread.sleep(4000);
			i++;		
		}	
	}
	
	@Test
	public void testDBUtil() throws Exception {
		WeblogicInfo weblogic = new WeblogicInfo(null, "127.0.0.1", "7001", "weblogic", "11111111", "",DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN));
		
		ServerDao dao = new ServerDao();
		dao.createTable();
		
		dao.saveServer(weblogic);
		
		List<ServerInfo> infos = dao.findByType("1");
		
		WeblogicInfo w1 = new WeblogicInfo(infos.get(0));
		
		System.out.println(w1.toString());
	}
	
	@Test
	public void testDateUtil() {
		System.out.println(DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN));
	}
	
	@Test
	public void testMyUtil() throws Exception {
		String sql = "insert into serverInfo values(null,?,?,?,?,?,?,?)";
		Object[] objs = new Object[]{"aa", "aa", "aa", "aa", "aa", "aa", "aa"};
		
		String sql1 = "select count(*) from serverInfo";
		
		String sql2 = "select * from serverInfo";
		ServerInfoMapper mapper = new ServerInfoMapper();
		mapper.execSQL(sql2);
		System.out.println(mapper.getObjectList().toString());
		mapper.execSQL(sql, objs);
		mapper.execSQL(sql2);
		System.out.println(mapper.getObjectList().toString());
	}
	
}

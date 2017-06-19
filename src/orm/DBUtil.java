package orm;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 管理数据库连接的工具类
 * 可以获取数据库的连接对象
 * 以及关闭数据库连接
 * @author xuwangcheng
 * @version 20170217
 *
 */
public class DBUtil {
	
	private static String CLASS_NAME;
	private static String CONNECTION_URL;
	private static String USERNAME;
	private static String PASSWORD;
	
	private static Properties props = new Properties();
	
	static {
		try {
			props.load(DBUtil.class.getClassLoader()
				       .getResourceAsStream("db.properties"));
			CLASS_NAME = props.getProperty("db.classname");
			CONNECTION_URL = props.getProperty("db.url");
			USERNAME = props.getProperty("db.username");
			PASSWORD = props.getProperty("db.password");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() throws Exception {
		Connection con = null;
		try {
			Class.forName(CLASS_NAME);
			con = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);		
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return con;
	}
	public static void close(Connection con) throws SQLException {
		if(con != null){
			try {
				con.close();
			} catch (SQLException e) {				
				e.printStackTrace();
				throw e;
			}
    	 }
     }
}


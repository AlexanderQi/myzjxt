package zjxt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class zjxt_OraHelper {
    public static String cfg_host = "127.0.0.1";
    public static String username = "softcore";
    public static String pw = "admin";
    public static List<String> sqlList = new ArrayList<String>();
    
    public Connection connection = null;
    public Statement statement = null;
    
    private static Lock lock = new ReentrantLock();
	private static zjxt_OraHelper instance=null;
	private zjxt_OraHelper(){
		
	}
	public static zjxt_OraHelper Instance(){
		lock.lock();
		if(instance == null){
			instance = new zjxt_OraHelper();
		}
		lock.unlock();
		return instance;
	}
	public void ConnectDB()throws Exception{
		throw new Exception("没有实现oracle DAL");
//		String url = "jdbc:oracle:thin:@" + cfg_host + ":1521:orcl";
//        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
//        connection = DriverManager.getConnection(url, username, pw);
//        statement = connection.createStatement();
	}
	
	
	

}

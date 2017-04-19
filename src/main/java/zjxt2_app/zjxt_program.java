package zjxt2_app;

import java.io.Console;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

import org.apache.log4j.PropertyConfigurator;

import com.drools.zjxt.kernellib.zjxt_ConnectionPool;
import com.drools.zjxt.kernellib.zjxt_Measure;
import com.drools.zjxt.kernellib.zjxt_kernel;

public class zjxt_program {

	public static void main(String[] args) {
		String osn = "";
		try {
			osn =  System.getProperty("os.name").toLowerCase();
			if(osn.indexOf("win") < 0)
				Runtime.getRuntime().exec("sh zjxtled.sh");

			zjxt_kernel.IniLog4j(null);
			zjxt_kernel.ZJXT_DRL = "pw.drl";// "rules/pw.drl";
			clearCmd();
			zjxt_kernel.launch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 专家系统运行之前清除命令表中的数据
	//	同时探测数据库能否连接,否则退出运行.并显示故障信号灯.
	public static void clearCmd() throws IOException {
		Connection conn = null;
		Statement state = null;
		String dbinfo = "";
		try {
			dbinfo = zjxt_ConnectionPool.Instance().getDbInfo();
			conn = zjxt_ConnectionPool.Instance().getConnection();
			state = conn.createStatement();
			String sql = "delete from tblcommand";
			state.execute(sql);
		} catch (Exception e) {			
			System.out.print("\n\n连接数据库异常.\nConnecting DB failed.\n"+dbinfo+'\n');
			String osn =  System.getProperty("os.name").toLowerCase();
			if(osn.indexOf("win") < 0)
				Runtime.getRuntime().exec("sh zjxterrled.sh");
			//e.printStackTrace();
			System.exit(0);
			
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (state != null) {
					state.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

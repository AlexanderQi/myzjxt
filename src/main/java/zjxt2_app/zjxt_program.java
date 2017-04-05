package zjxt2_app;

import java.sql.Connection;
import java.sql.Statement;

import org.apache.log4j.PropertyConfigurator;

import com.drools.zjxt.kernellib.zjxt_ConnectionPool;
import com.drools.zjxt.kernellib.zjxt_Measure;
import com.drools.zjxt.kernellib.zjxt_kernel;

public class zjxt_program {

	public static void main(String[] args) {
		zjxt_kernel.IniLog4j(null);
		zjxt_kernel.ZJXT_DRL = "rules/pw.drl";
		clearCmd();
		zjxt_kernel.launch();
	}
	
	//专家系统运行之前清除命令表中的数据
	public static void clearCmd() {
		Connection conn = null;
		Statement state = null;
		try {
			conn = zjxt_ConnectionPool.Instance().getConnection();
			state = conn.createStatement();
			String sql = "delete from tblcommand";
			state.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
				if(state != null) {
					state.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	
}

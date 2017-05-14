package ZjxtTest;

import static org.junit.Assert.*;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;
import com.drools.zjxt.*;
import com.drools.zjxt.kernellib.zjxt_Cmd;
import com.drools.zjxt.kernellib.zjxt_ConnectionPool;

import zjxt.*;
import java.sql.*;
import java.util.Date;

public class FuncTest1 {

	@Test
	public void test() {
		
		try {
			PropertyConfigurator.configure("zjxt2-log4j.ini");

			Connection conn = zjxt_ConnectionPool.Instance().getConnection();
			Statement st = conn.createStatement();
			String sql = "select now();";
			ResultSet rs = st.executeQuery(sql);
			rs.first();
			java.sql.Timestamp d = rs.getTimestamp(1);
			java.util.Date ud = new Date(d.getTime());
			st.close();
			conn.close();
			
			System.out.println('\n');
			System.out.println(new java.util.Date().toString());
			System.out.println(ud.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			fail("Not yet implemented");
			e.printStackTrace();
		}
		
	}

}

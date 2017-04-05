package com.drools.zjxt.kernellib;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import zjxt.zjxt_msg;
import zjxt2_app.zjxt_build;

import com.softcore.cim.entity.PowerSystemResource;

//import org.antlr.grammar.v3.ANTLRv3Parser.action_return;

public class Limit {
	public static float up_v220 = 250.000f; // 合法上限
	public static float lo_v220 = 180.000f; // 合法下限

	public static float diff_v220 = 35.000f; // 三相最大电压差
	public static float blance_v220 = 10.000f; // 三相允许电压差
	public static float blacne_v10000 = 2000.000f; // 10kV三相电压允许差
	public static float v220 = 220.000f; // 标准相电压
	public static float v380 = 380.000f; // 标准线电压

	public static float upper_v220 = 235.4f; // 220 +7%
	public static float lower_v220 = 200.000f; // 220 -10% 198
	public static float upper_v380 = 400.9f;
	public static float lower_v380 = 379.9f;
	public static float upper_v10000 = 10800.0f;
	public static float lower_v10000 = 10000.0f;

	public static float upper_cos = 1.000f;
	public static float lower_cos = 0.89f;

	public static float upper_tyqpc = 200f;
	public static float lower_tyqpc = 100f;

	public static int ActNum = 40;

	public static java.util.Date ChangeTime = new java.util.Date();

	// public static int dbProvider = 2; //1 oracle; 2 mysql;

	private static List<LimitValue> CommonLimits = new ArrayList<Limit.LimitValue>();

	public static void Refresh() {
		try {
			Connection conn = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = conn.createStatement();
			String sql = "SELECT * FROM tblelementlimit WHERE ELEMENTID=0 or ELEMENTID is null";
			ResultSet rSet = stat.executeQuery(sql);
			CommonLimits.clear();
			while (rSet.next()) {
				String limitname = rSet.getString("LIMITNAME");
				String limittype = rSet.getString("LIMITKIND");
				zjxt_msg.showwarn("载入默认限值:" + limitname + " " + limittype);

				LimitValue lv = new LimitValue();
				lv.IsTemp = false;
				lv.IsValid = true;
				lv.IsCommon = true;

				float lo = rSet.getFloat("L_LIMIT");
				float lolo = rSet.getFloat("LL_LIMIT");
				float up = rSet.getFloat("H_LIMIT");
				float upup = rSet.getFloat("HH_LIMIT");

				lv.LimitType = limittype;
				lv.lolo = lolo;
				lv.lo = lo;
				lv.up = up;
				lv.upup = upup;
				CommonLimits.add(lv);
			}
			conn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			zjxt_msg.showwarn("Limit.Refresh->", e);
		}
	}

	// As using the MySql,all of SQL about type of DateTime in the oracle has to
	// be modified .

	public static LimitValue GetSpecLimit(String elementId, String LimitType) {
		LimitValue lv = null;
		try {
			PowerSystemResource psr = zjxt_CimBuild.GetById(elementId);
			if (psr == null) {
				zjxt_msg.showwarn("GetSpecLimit()->" + elementId + " 该设备id无效!");
				return null;
			}
			Connection conn = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = conn.createStatement();
			String ts = zjxt_msg.GetTime();
			String sql = null;
			int dbProvider = zjxt_ConnectionPool.dbProvider; // 1 oracle; 2
																// mysql;
			if (dbProvider == 1) {
				sql = "select t.* from tblvaluelimit t " + "where elementid='"
						+ elementId + "' and LimitType='" + LimitType
						+ "' and '" + ts
						+ "' > to_char(periodBegin,'hh24:mi:ss') and '" + ts
						+ "'< to_char(periodEnd,'hh24:mi:ss')"
						+ "order by t.istemp*-1,t.periodend-t.periodbegin";

			} else if (dbProvider == 2) {
				sql = "select t.* from tblelementlimit t "
						+ "where elementid='" + elementId + "' and LimitKind='"
						+ LimitType
						+ "' and now() between t.PERIODBEGIN and t.PERIODEND "
						+ "order by t.istemp*-1,t.periodend-t.periodbegin";
			}

			ResultSet rSet = stat.executeQuery(sql);
			while (rSet.next()) {
				boolean tmp = rSet.getInt("ISTEMP") == 1 ? true : false;
				if (tmp) {
					Date begin = rSet.getDate("periodBegin");
					Date end = rSet.getDate("periodEnd");
					Date now = new Date();
					long nowlong = now.getTime();
					if (nowlong < begin.getTime() || nowlong > end.getTime()) // 判断当前时间是否在临时限值时段中，如不在则跳过记录。
						continue;
				}
				lv = new LimitValue();
				lv.IsTemp = tmp;
				lv.IsValid = true;
				lv.IsCommon = false;

				float lo = rSet.getFloat("L_LIMIT");
				float lolo = rSet.getFloat("LL_LIMIT");
				float up = rSet.getFloat("H_LIMIT");
				float upup = rSet.getFloat("HH_LIMIT");

				lv.LimitType = LimitType;
				lv.lolo = lolo;
				lv.lo = lo;
				lv.up = up;
				lv.upup = upup;

				zjxt_msg.showwarn(psr.getName() + "匹配时段限值成功，" + LimitType
						+ " 上限:" + up + " 下限:" + lo + " 是否临时限值:" + tmp + " 时间:"
						+ ts);
				break;
			}
			conn.close();
			return lv;
			// if(lv!=null)
			// return lv;
			// else {
			// zjxt_msg.showwarn(psr.getName()+
			// LimitType+"限值在当前时间无时段限值,将采用通用限值." );
			// return GetLimit(elementId);
			// }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			zjxt_msg.showwarn("Limit.GetSpecLimit()->", e);
			return lv;
		}
	}

	public static LimitValue GetVolLimit(String elementId) {
		return GetSpecLimit(elementId, "电压");
	}

	public static LimitValue GetActNumLimit(String elementId) {
		return GetSpecLimit(elementId, "动作次数");
	}

	public static LimitValue GetActNumLimitEx(String elementId) {
		LimitValue lv = GetSpecLimit(elementId, "动作次数");
		if (lv == null) {
			lv = GetLimitByNameNoTime("开关通用动作次数");
		}
		return lv;
	}

	public static LimitValue GetCosLimit(String elementId) {
		return GetSpecLimit(elementId, "功率因数");
	}

	//获取设备时段限值
	public static LimitValue getLimitInPeriod(String elementId, String limitKind) {
		LimitValue lv = null;
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = zjxt_ConnectionPool.Instance().getConnection();
			stat = conn.createStatement();
			String sql = "select t1.* from tblelementlimit t1, tblcommonlistcode t2 where t1.limitkind=t2.id and " +
					" t2.TITLECODE='"+limitKind+"' and " +
					"t1.elementid="+elementId+" and curtime() between t1.PERIODBEGIN and t1.PERIODEND";
//			System.out.println(sql);
			rs = stat.executeQuery(sql);
			if(rs.next()) {
				lv = new LimitValue();
				lv.LimitName = rs.getString("LIMITNAME");
				lv.upup = rs.getFloat("HH_LIMIT");
				lv.lolo = rs.getFloat("LL_LIMIT");
				lv.up = rs.getFloat("H_LIMIT");
				lv.lo = rs.getFloat("L_LIMIT");
			} else {
				throw new Exception(zjxt_build.GetById(elementId).getName()+"没有获取到当前时间"+limitKind+"限值!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			zjxt_msg.showwarn(zjxt_build.GetById(elementId).getName()+"获取时段"+limitKind+"限值异常!");
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(stat != null) {
					stat.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return lv;
	}
	
	public static LimitValue GetLimitByNameNoTime(String LimitName) {
		try {
			LimitValue lv = null;
			Connection conn = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = conn.createStatement();
			// String sql
			// ="SELECT * FROM tblelementlimit WHERE limitname='"+LimitName+"'";

			String ts = zjxt_msg.GetTime();
			String sql;
			String LimitTypeString;
			if (zjxt_ConnectionPool.dbProvider == 1) {
				sql = "select * from tblvaluelimit where LimitName='"
						+ LimitName + "'";
				LimitTypeString = "LIMITTYPE";
			} else {
				sql = "select * from tblelementlimit where LimitName='"
						+ LimitName + "'";
				LimitTypeString = "LIMITKIND";
			}
			ResultSet rSet = stat.executeQuery(sql);
			if (rSet.next()) {
				lv = new LimitValue();
				lv.LimitName = rSet.getString("LIMITNAME");
				lv.LimitType = rSet.getString(LimitTypeString);
				lv.IsTemp = false;
				lv.IsValid = true;
				lv.IsCommon = true;
				float lo = rSet.getFloat("L_LIMIT");
				float lolo = rSet.getFloat("LL_LIMIT");
				float up = rSet.getFloat("H_LIMIT");
				float upup = rSet.getFloat("HH_LIMIT");
				lv.lolo = lolo;
				lv.lo = lo;
				lv.up = up;
				lv.upup = upup;
			}
			conn.close();
			return lv;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			zjxt_msg.showwarn("Limit.GetLimitByNameNoTime->", e);
			return null;
		}

	}

	public static LimitValue GetLimitByName(String LimitName) {

		try {
			LimitValue lv = null;
			Connection conn = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = conn.createStatement();
			// String sql
			// ="SELECT * FROM tblelementlimit WHERE limitname='"+LimitName+"'";

			String ts = zjxt_msg.GetTime(); // 按时段找通用值
			String sql = null;
			String LimitTypeString;
			int dbProvider = zjxt_ConnectionPool.dbProvider; // 1 oracle; 2
																// mysql;
			if (dbProvider == 1) {
				sql = "select t.* from tblvaluelimit t " + "where LimitName='"
						+ LimitName + "' and '" + ts
						+ "' > to_char(periodBegin,'hh24:mi:ss') and '" + ts
						+ "'< to_char(periodEnd,'hh24:mi:ss')"
						+ "order by t.istemp*-1,t.periodend-t.periodbegin";
				LimitTypeString = "LIMITTYPE";
			} else {
				sql = "select t.* from tblelementlimit t "
						+ "where LimitName='" + LimitName
						+ "' and now() between t.PERIODBEGIN and t.PERIODEND "
						+ "order by t.istemp*-1,t.periodend-t.periodbegin";
				LimitTypeString = "LIMITKIND";
				// now() between t.PERIODBEGIN and t.PERIODEND
			}
			ResultSet rSet = stat.executeQuery(sql);

			if (rSet.next()) {
				lv = new LimitValue();
				lv.LimitName = rSet.getString("LIMITNAME");
				lv.LimitType = rSet.getString(LimitTypeString);
				lv.IsTemp = false;
				lv.IsValid = true;
				lv.IsCommon = true;
				float lo = rSet.getFloat("L_LIMIT");
				float lolo = rSet.getFloat("LL_LIMIT");
				float up = rSet.getFloat("H_LIMIT");
				float upup = rSet.getFloat("HH_LIMIT");
				lv.lolo = lolo;
				lv.lo = lo;
				lv.up = up;
				lv.upup = upup;

			}
			conn.close();
			return lv;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			zjxt_msg.showwarn("Limit.Refresh->", e);
			return null;
		}

	}

	public static class LimitValue {
		public boolean IsTemp = false; // 是否临时限值
		public boolean IsValid = false; // 是否有效
		public boolean IsCommon = true; // 是否通用限值
		public String LimitType = "";
		public String LimitName = "";
		public float up = Float.NaN; // 浮点数上限
		public float upup = Float.NaN; // 上上限
		public float lo = Float.NaN; // 下限
		public float lolo = Float.NaN; // 下下限
		public int int_up = -9999; // 整数上限
		public int int_upup = -9999; // 上上限
		public int int_lo = -9999; // 下限
		public int int_lolo = -9999; // 下下限
		public float gap_lo = Float.NaN; // 电压偏差上限
		public float gap_up = Float.NaN; // 电压偏差下限
		public float cos_up = Float.NaN;
		public float cos_lo = Float.NaN;
		public float cos_upup = Float.NaN;
		public float cos_lolo = Float.NaN;
		public int act = -1; // 动作次数上限
		public int tap_up = -1; // 档位上限
		public int tap_lo = -1; // 档位下限
	}
}

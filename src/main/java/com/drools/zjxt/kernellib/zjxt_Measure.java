package com.drools.zjxt.kernellib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.drools.zjxt.kernellib.zjxt_CimBuild.zCapacitor;
import com.softcore.cim.common.CommonListCode;
import com.softcore.cim.entity.PowerSystemResource;

//import SOFTCORE.scmdbc;

//import javax.swing.LookAndFeel;

//import org.drools.lang.DRLExpressions.instanceof_key_return;

import zjxt.zjxt_msg;

public class zjxt_Measure {

	/** 死数据判断时间(单位:分钟) */
	private static final int DEAD_DATE_MINUTE = 10;

	// private Connection conn = null;
	// private Statement stat = null;
	private static zjxt_Measure instance = null;
	private static Lock lock = new ReentrantLock();

	public static zjxt_Measure Instance() {
		lock.lock();
		if (instance == null)
			instance = new zjxt_Measure();
		lock.unlock();
		return instance;
	}

	private zjxt_Measure() {

	}

	// public boolean IsFileDb = false;
	// private scmdbc rtdb = new scmdbc();
	// public String rtdb_ip = "127.0.0.1";
	// // public String rtdb_ip = "192.168.2.229";
	// public int rtdb_port = 31833;

	// public Connection getConnection() throws Exception {
	// if (ConnectRtdb())
	// return conn;
	// else {
	// return null;
	// }
	// }

	// public boolean ConnectRtdb() throws Exception {
	// if (IsFileDb) {
	// if (rtdb.ConnectFDB("sa", "sa", rtdb_ip, rtdb_port)) {
	// conn = rtdb.connection;
	// stat = rtdb.statement;
	// return true;
	// }
	// return false;
	// } else {
	// if (rtdb.Connect("sa", "sa", rtdb_ip, rtdb_port)) {
	// conn = rtdb.connection;
	// stat = rtdb.statement;
	// return true;
	// }
	// return false;
	// }
	// }

	private List<zjxt_yc> yclist = new ArrayList<zjxt_yc>();
	private Map<String, zjxt_yc> ycMap = new HashMap<String, zjxt_yc>(2408);
	private List<zjxt_yk> yklist = new ArrayList<zjxt_yk>();
	private List<zjxt_yx> yxlist = new ArrayList<zjxt_yx>();
	private Map<String, zjxt_yx> yxMap = new HashMap<String, zjxt_yx>(2408);
	private List<zjxt_yt> ytlist = new ArrayList<zjxt_yt>();

	public void Clear() {
		yclist.clear();
		yklist.clear();
		yxlist.clear();
		ytlist.clear();
	}

	public zjxt_yx Addyx(String idString, int ca, int czh, int yxh, String name)
			throws Exception {
		// for (Iterator<zjxt_yx> iterator = yxlist.iterator();
		// iterator.hasNext();) {
		// zjxt_yx ayx = (zjxt_yx) iterator.next();
		// if (ayx.idString.equals(idString)) {
		// throw new Exception("遥信名已存在:" + idString);
		// } else if (ayx.ca == ca && ayx.yxh == yxh && ayx.czh == czh) {
		// if (yxh == 0 || ca == 0 || czh == 0)
		// zjxt_msg.show("遥信表当前记录在controlarea,czh,yxh字段中可能为null。");
		// throw new Exception("遥信号(YXH)已存在:" + yxh + " 所属遥信id:"
		// + idString + " 雷同遥信id:" + ayx.idString);
		// }
		// }
		zjxt_yx yx = new zjxt_yx();
		yx.idString = idString;
		yx.ca = ca;
		yx.czh = czh;
		yx.yxh = yxh;
		yx.Name = name;
		// yxlist.add(yx);
		yxMap.put(yx.idString, yx);
		return yx;
	}

	public zjxt_yc Addyc(String idString, int ca, int czh, int ych, float offset,
			float ratio, String name, int valueStyle) throws Exception {
		// for (Iterator<zjxt_yc> iterator = yclist.iterator();
		// iterator.hasNext();) {
		// zjxt_yc ayc = (zjxt_yc) iterator.next();
		// if (ayc.idString.equals(idString)) {
		// throw new Exception("遥测名已存在:" + idString);
		// } else if (ayc.ca == ca && ayc.ych == ych && ayc.czh == czh) {
		// if (ych == 0 || ca == 0 || czh == 0)
		// zjxt_msg.show("遥测表当前记录在controlarea,czh,ych字段中可能为null。");
		// throw new Exception("遥测号(YCH)已存在:" + ych + " 所属遥测id:"
		// + idString + " 雷同遥测id:" + ayc.idString);
		// }
		// }
		zjxt_yc yc = new zjxt_yc();
		yc.idString = idString;
		yc.ca = ca;
		yc.czh = czh;
		yc.ych = ych;
		yc.Offset = offset;
		yc.Ratio = ratio;
		yc.Name = name;
		yc.valueStyle = valueStyle;
		// yclist.add(yc);
		ycMap.put(yc.idString, yc);
		return yc;
	}

	public zjxt_yk Addyk(String idString, String elementId, int ca, int czh_up,
			int czh_down, int dh_up, int dh_down, int fixv_up, int fixv_down,
			String ykname, String ykkind) throws Exception {
		for (Iterator<zjxt_yk> iterator = yklist.iterator(); iterator.hasNext();) {
			zjxt_yk ayt = (zjxt_yk) iterator.next();
			if (ayt.id.equals(idString)) {
				throw new Exception("遥控id已存在:" + idString);
			}
		}
		zjxt_yk yk = new zjxt_yk();
		yk.Name = ykname;
		yk.yk_Kind = ykkind;
		yk.elementId = elementId;
		yk.id = idString;
		yk.ca = ca;
		yk.czh_upper = czh_up;
		yk.dh_upper = dh_up;
		yk.czh_down = czh_down;
		yk.dh_down = dh_down;
		yk.FixValue_down = fixv_down;
		yk.FixValue_upper = fixv_up;
		yklist.add(yk);
		return yk;
	}

	public zjxt_yt Addyt(String idString, String elementId, String ytkind,
			int ca, int czh, int dh, float multiplevalue) throws Exception {
		for (Iterator<zjxt_yt> iterator = ytlist.iterator(); iterator.hasNext();) {
			zjxt_yt ayt = (zjxt_yt) iterator.next();
			if (ayt.id.equals(idString)) {
				throw new Exception("遥调id已存在:" + idString);
			}
		}
		zjxt_yt yt = new zjxt_yt();
		yt.elementId = elementId;
		yt.id = idString;
		yt.ca = ca;
		yt.dh = dh;
		yt.czh = czh;
		yt.ytKind = ytkind;
		yt.multiplevalue = multiplevalue;
		ytlist.add(yt);
		return yt;
	}

	public zjxt_yt GetYt(String elementid, String ytkind) {
		for (Iterator<zjxt_yt> iterator = ytlist.iterator(); iterator.hasNext();) {
			zjxt_yt ayt = (zjxt_yt) iterator.next();
			if (ayt.elementId.equals(elementid) && ayt.ytKind.equals(ytkind)) {
				return ayt;
			}
		}
		return null;
	}

	public zjxt_yk GetYk(String elementid) {
		for (Iterator<zjxt_yk> iterator = yklist.iterator(); iterator.hasNext();) {
			zjxt_yk ayk = (zjxt_yk) iterator.next();
			if (ayk.elementId.equals(elementid)) {
				return ayk;
			}
		}
		return null;
	}

	public zjxt_yk GetYkByName(String elementid, String ykname) {
		for (Iterator<zjxt_yk> iterator = yklist.iterator(); iterator.hasNext();) {
			zjxt_yk ayk = (zjxt_yk) iterator.next();
			if (ayk.elementId.equals(elementid) && ayk.Name.equals(ykname)) {
				return ayk;
			}
		}
		return null;
	}

	public zjxt_yk GetYkByKind(String elementid, String ykKind) {
		for (Iterator<zjxt_yk> iterator = yklist.iterator(); iterator.hasNext();) {
			zjxt_yk ayk = (zjxt_yk) iterator.next();
			if (ayk.elementId.equals(elementid) && ayk.yk_Kind.equals(ykKind)) {
				return ayk;
			}
		}
		return null;
	}

	public zjxt_yc GetYc(String ycid) {
		// for (Iterator<zjxt_yc> iterator = yclist.iterator();
		// iterator.hasNext();) {
		// zjxt_yc obj = (zjxt_yc) iterator.next();
		// if (obj.idString.equals(ycid)) {
		// return obj;
		// }
		// }
		// return null;
		return ycMap.get(ycid);
	}

	public zjxt_yx GetYx(String yxid) {
		// for (Iterator<zjxt_yx> iterator = yxlist.iterator();
		// iterator.hasNext();) {
		// zjxt_yx obj = (zjxt_yx) iterator.next();
		// if (obj.idString.equals(yxid)) {
		// return obj;
		// }
		// }
		// return null;
		return yxMap.get(yxid);
	}

	public boolean SetYcValue(String idString, float ycValue) {
		// for (Iterator<zjxt_yc> iterator = yclist.iterator();
		// iterator.hasNext();) {
		// zjxt_yc ayc = (zjxt_yc) iterator.next();
		// if (ayc.idString.equals(idString)) {
		// ayc.OldValue = ayc.Value;
		// ayc.Value = ycValue * ayc.Ratio + ayc.Offset;
		// return true;
		// }
		// }
		zjxt_yc zjxt_yc = ycMap.get(idString);
		if (zjxt_yc != null) {
			zjxt_yc.OldValue = zjxt_yc.Value;
			zjxt_yc.Value = ycValue * zjxt_yc.Ratio + zjxt_yc.Offset;
			if (zjxt_yc.valueStyle == CommonListCode.ACTIVE_POWER_CODE
					|| zjxt_yc.valueStyle == CommonListCode.REACTIVE_POWER_CODE
					|| zjxt_yc.valueStyle == CommonListCode.VOLTAGE_CODE
					|| zjxt_yc.valueStyle == CommonListCode.ELECTRIC_CURRENT_CODE) { // 有功、无功、电压、电流需要死数据判断
				if (zjxt_kernel.jkParam.isJudgeDeadData) // 是否进行死数据判断，正式环境需要为true
				{
					if (zjxt_yc.OldValue == zjxt_yc.Value) {
						zjxt_yc.noResreshSecond += 19;
						if (zjxt_yc.noResreshSecond >= (DEAD_DATE_MINUTE * 60)) { // 遥测10分钟未刷新，即为死数据
							zjxt_yc.NoRefresh = true;
						}
					} else {
						zjxt_yc.NoRefresh = false;
						zjxt_yc.noResreshSecond = 0;
					}
				}

			}
			return true;
		}

		return false;
	}

	public boolean SetYcValue(int ca, int czh, int ych, float ycValue,
			Date rtime) {
		for (Iterator<zjxt_yc> iterator = yclist.iterator(); iterator.hasNext();) {
			zjxt_yc ayc = (zjxt_yc) iterator.next();
			if (ayc.ca == ca && ayc.czh == czh && ayc.ych == ych) {
				ayc.OldValue = ayc.Value;
				ayc.Value = ycValue * ayc.Ratio + ayc.Offset;
				ayc.RefreshTime = rtime;
				if (ayc.valueStyle == CommonListCode.ACTIVE_POWER_CODE
						|| ayc.valueStyle == CommonListCode.REACTIVE_POWER_CODE
						|| ayc.valueStyle == CommonListCode.VOLTAGE_CODE
						|| ayc.valueStyle == CommonListCode.ELECTRIC_CURRENT_CODE) { // 有功、无功、电压、电流需要死数据判断
					if (ayc.OldValue == ayc.Value) {
						ayc.noResreshSecond += 5;
						if (ayc.noResreshSecond >= (DEAD_DATE_MINUTE * 60)) { // 遥测10分钟未刷新，即为死数据
							ayc.NoRefresh = true;
						}
					} else {
						ayc.NoRefresh = false;
						ayc.noResreshSecond = 0;
					}
				}
			}
			return true;
		}
		return false;
	}

	public float GetYcValue(String idString) throws Exception {
		// for (Iterator<zjxt_yc> iterator = yclist.iterator();
		// iterator.hasNext();) {
		// zjxt_yc ayc = (zjxt_yc) iterator.next();
		// if (ayc.idString.equals(idString)) {
		// return ayc.Value;
		// }
		// }
		// for (int i = 0; i < yclist.size(); i++) {
		// zjxt_yc obj = (zjxt_yc) yclist.get(i);
		// if (obj.idString.equals(idString)) {
		// return obj.Value;
		// }
		// }
		if (ycMap.containsKey(idString)) {
			return ycMap.get(idString).Value;
		} else {
			throw new Exception("GetYcValue idString");
		}

	}

	public float GetYcValue(int ca, int czh, int ych) throws Exception {
		// for (Iterator<zjxt_yc> iterator = yclist.iterator();
		// iterator.hasNext();) {
		// zjxt_yc ayc = (zjxt_yc) iterator.next();
		// if (ayc.ca == ca && ayc.czh == czh && ayc.ych == ych) {
		// return ayc.Value;
		// }
		// }
		// for (int i = 0; i < yclist.size(); i++) {
		// zjxt_yc ayc = (zjxt_yc) yclist.get(i);
		// if (ayc.ca == ca && ayc.czh == czh && ayc.ych == ych) {
		// return ayc.Value;
		// }
		// }
		throw new Exception("GetYcValue ca czh ych");
	}

	public boolean isDeadData(int ca, int czh, int ych) throws Exception {
		for (int i = 0; i < yclist.size(); i++) {
			zjxt_yc ayc = (zjxt_yc) yclist.get(i);
			if (ayc.ca == ca && ayc.czh == czh && ayc.ych == ych) {
				return ayc.NoRefresh;
			}
		}
		throw new Exception("GetYcValue ca czh ych");
	}

	public boolean isDeadData(String id) throws Exception {
		zjxt_yc zjxt_yc = ycMap.get(id);
		if (zjxt_yc != null) {
			return zjxt_yc.NoRefresh;
		}
		throw new Exception("GetYcValue id");
	}

	public Date GetYcTime(int ca, int czh, int ych) throws Exception {
		for (int i = 0; i < yclist.size(); i++) {
			zjxt_yc ayc = (zjxt_yc) yclist.get(i);
			if (ayc.ca == ca && ayc.czh == czh && ayc.ych == ych) {
				return ayc.RefreshTime;
			}
		}
		throw new Exception("GetYcValue ca czh ych");
	}

	public float GetYcByName(String name) {
		for (int i = 0; i < yclist.size(); i++) {
			zjxt_yc obj = (zjxt_yc) yclist.get(i);
			if (obj.Name.equals(name)) {
				return obj.Value;
			}
		}
		zjxt_msg.showwarn("GetYcByName->" + "YC名称不存在:" + name);
		return -1;
	}

	public boolean SetYxValue(String id, int value) {
		zjxt_yx zjxt_yx = yxMap.get(id);
		if (zjxt_yx != null) {
			zjxt_yx.OldValue = zjxt_yx.Value;
			zjxt_yx.Value = value;
			return true;
		}
		return false;
	}

	public boolean SetYxValue(int ca, int czh, int yxh, int yxValue, Date rtime) {
		for (Iterator<zjxt_yx> iterator = yxlist.iterator(); iterator.hasNext();) {
			zjxt_yx obj = (zjxt_yx) iterator.next();
			if (obj.ca == ca && obj.czh == czh && obj.yxh == yxh) {
				obj.OldValue = obj.Value;
				obj.Value = yxValue;
				obj.RefreshTime = rtime;
				return true;
			}
		}
		return false;
	}

	public int GetYxValue(int ca, int czh, int yxh) {
		// for (Iterator<zjxt_yx> iterator = yxlist.iterator();
		// iterator.hasNext();) {
		// zjxt_yx obj = (zjxt_yx) iterator.next();
		// if (obj.ca == ca && obj.czh == czh && obj.yxh == yxh) {
		// return obj.Value;
		// }
		// }
		for (int i = 0; i < yxlist.size(); i++) {
			zjxt_yx obj = (zjxt_yx) yxlist.get(i);
			if (obj.ca == ca && obj.czh == czh && obj.yxh == yxh) {
				return obj.Value;
			}
		}
		return -1;
	}

	// public int GetYxValue(String idString) {
	// // for (Iterator<zjxt_yx> iterator = yxlist.iterator();
	// // iterator.hasNext();) {
	// // zjxt_yx obj = (zjxt_yx) iterator.next();
	// // if (obj.idString.equals(idString)) {
	// // return obj.Value;
	// // }
	// // }
	// for (int i = 0; i < yxlist.size(); i++) {
	// zjxt_yx obj = (zjxt_yx) yxlist.get(i);
	// if (obj.idString.equals(idString)) {
	// return obj.Value;
	// }
	// }
	// zjxt_msg.showwarn("GetYxValue->" + "YXID不存在:" + idString);
	// return -1;
	// }

	public int GetYxValue(String idString) {
		zjxt_yx zjxt_yx = yxMap.get(idString);
		if (zjxt_yx != null) {
			return zjxt_yx.Value;
		}
		zjxt_msg.showwarn("GetYxValue->" + "YXID不存在:" + idString);
		return -1;
	}

	public int GetYxByName(String name) {
		for (int i = 0; i < yxlist.size(); i++) {
			zjxt_yx obj = (zjxt_yx) yxlist.get(i);
			if (obj.Name.equals(name)) {
				return obj.Value;
			}
		}
		zjxt_msg.showwarn("GetYxByName->" + "YX名称不存在:" + name);
		return -1;
	}

	// public static boolean YCYXDB = false;

	public void Refresh_ycyx() throws SQLException, Exception {
		try {
			Connection connection = null;
			Statement statement = null;
			String yxsql = "";
			String ycsql = "";
			// if (YCYXDB) {
			zjxt_msg.show("Measure refresh from db...");
			connection = zjxt_ConnectionPool.Instance().getConnection();
			statement = connection.createStatement();
			ycsql = "select t1.CONTROLAREA, t1.czh,t1.ych,t1.ycvalue,t1.refreshtime,t1.REPLACED,t1.REPLACEDVALUE,multiplevalue,id from tblycvalue t1 where t1.czh is not null and t1.czh <> ''";
			yxsql = "select t1.CONTROLAREA, t1.czh,t1.yxh,t1.yxvalue,t1.refreshtime,t1.REPLACED,t1.REPLACEDVALUE,id from tblyxvalue t1";
			// }
			// zjxt_msg.show("Measure refresh from memory db...");
			// else {
			// if (conn == null || conn.isClosed() || stat.isClosed())
			// ConnectRtdb();
			// connection = conn;
			// statement = stat;
			// ycsql = "select t1.CONTROLAREA,
			// t1.czh,t1.ych,t1.ycvalue,t1.refreshtime,id from tblycvalue t1";
			// // ycsql = "SELECT t1.CONTROLAREA,
			// t1.czh,t1.ych,t1.ycvalue,t1.refreshtime,t1.id,CASE WHEN
			// t2.titlecode IS NULL THEN '' ELSE t2.titlecode END valuestyle
			// FROM tblycvalue t1 LEFT JOIN tblcommonlistcode t2 ON
			// t1.valuestyle=t2.id";
			// yxsql = "select t1.CONTROLAREA,
			// t1.czh,t1.yxh,t1.yxvalue,t1.refreshtime,id from tblyxvalue t1";
			// }

			ResultSet rSet = statement.executeQuery(ycsql);
			while (rSet.next()) {
				float m = rSet.getFloat(8); // 倍率
				int id = rSet.getInt(9); // id
				float f = rSet.getFloat(4) * m;
				float f3 = (float) (Math.round(f * 1000)) / 1000; // 保留小数点后3位精度。

				// if (YCYXDB) { // 取人工置数
				int substituted = rSet.getInt(6);

				if (!rSet.wasNull())
					if (substituted == 1) {
						f3 = (float) (Math.round(rSet.getFloat(7) * 1000)) / 1000;
						zjxt_msg.showwarn("人工置数 ycid:" + rSet.getString(9)
								+ " value=" + f3);
					}
				// }

				// SetYcValue(rSet.getInt(1), rSet.getInt(2), rSet.getInt(3),
				// f3,
				// rSet.getTimestamp(5));
				SetYcValue(id + "", f3);
			}
			rSet = statement.executeQuery(yxsql);
			while (rSet.next()) {
				String id = rSet.getString(8);
				int vi = rSet.getInt(4);
				// if (YCYXDB) { // 取人工置数
				int substituted = rSet.getInt(6);
				if (!rSet.wasNull())
					if (substituted == 1) {
						vi = rSet.getInt(7);
						zjxt_msg.showwarn("人工置数 yxid:" + rSet.getString(8)
								+ " value=" + vi);
					}
				// }
				// SetYxValue(rSet.getInt(1), rSet.getInt(2), rSet.getInt(3),
				// vi,
				// rSet.getTimestamp(5));
				SetYxValue(id + "", vi);
			}
			connection.close();
			if (zjxt_kernel.jkParam.isJudgeDeadData) {
				updateDeadData();
			}
		} catch (Exception e) {
			throw new Exception("Refresh_ycyx()->"+ e.toString());
		}

	}

	private void updateDeadData() throws Exception {
		try {
			// if (conn == null || conn.isClosed() || stat.isClosed())
			// ConnectRtdb();
			Connection conn = zjxt_ConnectionPool.Instance().getConnection();
			conn.setAutoCommit(false); // 手动提交
			// PrepareStatement statement = conn.createStatement();
			String updateYcSql = "update tblycvalue set notfresh=? where id=?";
			PreparedStatement prep = conn.prepareStatement(updateYcSql);
			// for(int i=0; i<ycMap.size(); i++) {
			// zjxt_yc ycObj = yclist.get(i);
			// prep.setBoolean(1, ycObj.NoRefresh);
			// prep.setInt(2, ycObj.ca);
			// prep.setInt(3, ycObj.czh);
			// prep.setInt(4, ycObj.ych);
			// prep.addBatch();
			// if(i % 500 == 0) { //每更新500条执行一次
			// prep.executeBatch();
			// prep.clearBatch();
			// } else if(i==yclist.size()) {
			// prep.executeBatch();
			// prep.clearBatch();
			// }
			// }
			int i = 0;
			for (Entry<String, zjxt_yc> entry : ycMap.entrySet()) {
				zjxt_yc ycObj = entry.getValue();
				prep.setBoolean(1, ycObj.NoRefresh);
				prep.setString(2, ycObj.idString);
				prep.addBatch();
				if (i % 500 == 0) { // 每更新500条执行一次
					prep.executeBatch();
					prep.clearBatch();
				} else if (i == ycMap.size()) {
					prep.executeBatch();
					prep.clearBatch();
				}
				i++;
			}
			conn.commit();
			prep.close();
			conn.close();
		} catch (Exception e) {
			throw new Exception("更新遥测死数据标记异常:" + e.toString());
		}

	}

	public void Init_ycyx() throws Exception {
		try {
			// if (conn == null || conn.isClosed() || stat.isClosed())
			// ConnectRtdb();
			Connection conn = zjxt_ConnectionPool.Instance().getConnection();
			Statement statement = conn.createStatement();
			ResultSet rSet = statement
					.executeQuery("select t1.CONTROLAREA, t1.czh,t1.ych,t1.id, t1.name,t1.valuestyle,t1.channel from tblycvalue t1 where t1.czh IS NOT NULL AND t1.czh <> ''");
			while (rSet.next()) {

				zjxt_yc yc = Addyc(rSet.getString(4), rSet.getInt(1), rSet.getInt(2),
						rSet.getInt(3), 0f, 1f, rSet.getString(5),
						rSet.getInt(6));
				yc.channel = rSet.getInt(7);
			}
			rSet = statement
					.executeQuery("select t1.CONTROLAREA, t1.czh,t1.yxh,t1.id,t1.name,t1.channel from tblyxvalue t1");
			while (rSet.next()) {
				zjxt_yx yx = Addyx(rSet.getString(4), rSet.getInt(1), rSet.getInt(2),
						rSet.getInt(3), rSet.getString(5));
				yx.channel = rSet.getInt(6);
			}
			conn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception("Init_ycyx->", e);
		}
	}

	public void Init_yk() throws Exception {
		try {
			Connection conn = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = conn.createStatement();
			String ykname;
			if (zjxt_ConnectionPool.dbProvider == 1) { // is oracle
				ykname = "CMDELEMENTNAME";
			} else {
				ykname = "NAME";
			}
			String sql = "SELECT * FROM TBLYKPARAM";
			ResultSet rSet = stat.executeQuery(sql);
			while (rSet.next()) {
				String eid = rSet.getString("CMDELEMENTID");
				String ykkind = "";
				if (rSet.getString("YKKIND") != null) {
					ykkind = rSet.getString("YKKIND");
				}
				zjxt_yk yk = Addyk(rSet.getString("ID"), eid, rSet.getInt("CONTROLAREA"),
						rSet.getInt("UPPER_CZH"), rSet.getInt("LOWER_CZH"),
						rSet.getInt("UPPER_YKYTH"), rSet.getInt("LOWER_YKYTH"),
						rSet.getInt("UPPER_YKYTVALUE"),
						rSet.getInt("LOWER_YKYTVALUE"), rSet.getString(ykname),
						ykkind);
				yk.channel = rSet.getInt("CHANNEL");

			}
			conn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception("Init_yk->", e);
		}

	}

	public void Init_yt() throws Exception {
		try {
			Connection conn = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = conn.createStatement();
			String sql = "SELECT * FROM TBLYTPARAM";
			ResultSet rSet = stat.executeQuery(sql);
			String bugstr;
			String ytkind;
			// boolean IsSmartPrj = JkParam.Instance().IsSmartPrj;
			while (rSet.next()) {
				ytkind = rSet.getString("YTKIND");
				if (ytkind == null)
					ytkind = "undefined";
				zjxt_yt yt = Addyt(rSet.getString("ID"),
						rSet.getString("CMDELEMENTID"), ytkind.toLowerCase(),
						rSet.getInt("CONTROLAREA"), rSet.getInt("CZH"),
						rSet.getInt("YTH"), rSet.getFloat("MULTIPLEVALUE"));
				yt.channel = rSet.getInt("CHANNEL");
				PowerSystemResource resource = zjxt_CimBuild
						.GetById(yt.elementId);
				if (resource == null) {
					bugstr = "Init_yt->无该遥调设备ID:" + yt.elementId + " 对应遥调ID:"
							+ yt.id;
					zjxt_msg.showwarn(bugstr);
					continue;
				}
				
				if (resource.getClass() == zCapacitor.class) {
					zCapacitor compensator = (zCapacitor) resource;
					compensator.IsYT = true;
				}

			}
			conn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception("Init_yt->", e);
		}

	}

	public void ExecuteSql(String sql) throws Exception {
		try {
			Connection conn = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = conn.createStatement();
			stat.execute(sql);
			conn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception("ExecuteSql->", e);
		}
	}

	public static class zjxt_yc {
		public int ych;
		public int czh;
		public int ca;
		public int channel;
		public float Value;
		public float OldValue;
		public float Offset;
		public float Ratio;
		public String idString;
		public String Name;
		public boolean NoRefresh = false;
		public int noResreshSecond = 0; // 量测未刷新秒数
		public int valueStyle; // 量测类型
		public Date RefreshTime;
	}

	public static class zjxt_yx {
		public int yxh;
		public int czh;
		public int ca;
		public int channel;
		public int Value;
		public int OldValue;
		public String idString;
		public String Name;
		public boolean NoRefresh = false;
		public int noResreshSecond = 0; // 量测未刷新秒数
		public Date RefreshTime;
	}

	public static class zjxt_yk {
		public int dh_upper;
		public int dh_down;
		public int czh_upper;
		public int czh_down;
		public int ca;
		public int channel;
		public int FixValue_upper;
		public int FixValue_down;
		public String id;
		public String Name;
		public String yk_Kind;
		public String elementId;
	}

	public static class zjxt_yt {
		public int dh;
		public int czh;
		public int ca;
		public int channel;
		public String ytKind;
		public String id;
		public String Name;
		public String elementId;
		public float multiplevalue; // 遥调倍率
	}

}

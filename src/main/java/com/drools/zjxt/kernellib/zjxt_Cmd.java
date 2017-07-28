package com.drools.zjxt.kernellib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.softcore.cim.entity.Equipment;
import com.softcore.cim.entity.PowerSystemResource;

import zjxt.zjxt_State;
import zjxt.zjxt_msg;

//import com.softcore.cim.entity.block.switchs.Breaker;

public class zjxt_Cmd {
	
	private static final Logger logger = LoggerFactory.getLogger(zjxt_Cmd.class);
	
	public static LinkedList<CmdObj> cmdlist = new LinkedList<zjxt_Cmd.CmdObj>();

	public static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static int DelExpireCmdObj()throws Exception {
		java.util.Date date = getDbTime();//new java.util.Date();
		int ct = 0;
		for (int i = cmdlist.size() - 1; i >= 0; i--) {
			long between = (date.getTime() - cmdlist.get(i).DateTime.getTime()) / 1000;
			if (between >= 300) {
				cmdlist.remove(i);
				ct++;
			}
		}
		return ct;
	}

	public static void DelCmdObj(String CmdId) {
		int index = -1;
		for (int i = 0; i < cmdlist.size(); i++) {
			if (cmdlist.get(i).CmdId.equals(CmdId)) {
				index = i;
				break;
			}
		}
		if (index != -1)
			cmdlist.remove(index);
	}

	public static CmdObj GetCmdObj(String CmdId) {
		for (int i = 0; i < cmdlist.size(); i++) {
			CmdObj obj = cmdlist.get(i);
			if (obj.CmdId.equals(CmdId)) {
				return obj;
			}
		}
		return null;
	}

	public static void AddCmdObj(String CmdId, String ElementId, String Desc, java.util.Date date, String actionType) {
		CmdObj obj = new CmdObj();
		obj.CmdId = CmdId;
		obj.ElementId = ElementId;
		obj.Desc = Desc;
		obj.DealTag = 0;
		obj.DateTime = date;
		obj.actionType = actionType;
		cmdlist.add(obj);
	}

	public static long NewId(String TableName) throws Exception {
		long id;
		Connection conn = zjxt_ConnectionPool.Instance().getConnection();
		Statement sm = conn.createStatement();
		ResultSet rSet = sm
				.executeQuery("select max(CAST(id as SIGNED)) mid FROM " + TableName);
		if (rSet.next()) {
			id = rSet.getLong(1) + 10;
			if (rSet.wasNull())
				id = 1;
		} else {
			id = 1;
		}
		conn.close();
		return id;
	}

	public static int NewSchemeId() {
		return 1;
	}

	public static void UpdateRunTime(String id, int field, int value) { // field=1
																		// 运行时间;
																		// field=0
																		// 停运时间;
																		// value
																		// :
																		// 时间值（分钟）;
		try {
			// 成功失败次数累计
			Connection conn = zjxt_ConnectionPool.Instance().getConnection();
			Statement state = conn.createStatement();
			String sql = "";
			if (field == 1) {
				sql = "update tblelementruntime t set t.runtime = "
						+ "case when t.runtime is null then 1 else t.runtime+"
						+ value + " end where t.elementid=" + id;
			} else {
				sql = "update tblelementruntime t set t.stoptime = "
						+ "case when t.runtime is null then 1 else t.stoptime+"
						+ value + " end where t.elementid=" + id;
			}

			state.execute(sql);
			conn.close();
		} catch (Exception e) {
			// TODO: handle exception
			zjxt_msg.showwarn("UpdateRunTime->", e);
		}
	}

	public static void AddActNum(String id, int direct, int operator,
			boolean IsSuccess) { // direct=0 down; =1 upper; operator=1:self; =0
									// other;
		try {
			// 成功失败次数累计
			Connection conn = zjxt_ConnectionPool.Instance().getConnection();
			Statement state = conn.createStatement();
			String sql = "select t.* from tblelementaction t where t.elementid="
					+ id;
			ResultSet rs = state.executeQuery(sql);
			if (rs.next()) {
				int UPPERONCOUNTSELF = rs.getInt("UPPERONCOUNTSELF");
				int DOWNOFFCOUNTSELF = rs.getInt("DOWNOFFCOUNTSELF");
				// int UPPERONCOUNTOTHER = rs.getInt("UPPERONCOUNTOTHER");
				// int DOWNOFFCOUNTOTHER = rs.getInt("DOWNOFFCOUNTOTHER");
				int SUCCESSCOUNT = rs.getInt("SUCCESSCOUNT");
				int FAILURECOUNT = rs.getInt("FAILURECOUNT");
				int YTCOUNTSELF = rs.getInt("YTCOUNTSELF"); //遥调自身动作次数

				String tmp = "";

				if (IsSuccess) {
					SUCCESSCOUNT++;
					tmp = "t1.SUCCESSCOUNT=" + SUCCESSCOUNT;
				} else {
					FAILURECOUNT++;
					tmp = "t1.FAILURECOUNT=" + FAILURECOUNT;
				}

				if (operator == 1) {
					if (direct == 1) { //遥控升档/合闸
						UPPERONCOUNTSELF++;
						tmp += ",t1.UPPERONCOUNTSELF=" + UPPERONCOUNTSELF;
					} else if(direct == 0) { //遥控降档/分闸
						DOWNOFFCOUNTSELF++;
						tmp += ",t1.DOWNOFFCOUNTSELF=" + DOWNOFFCOUNTSELF;
					} else if(direct == 3) { //遥调
						YTCOUNTSELF++;
						tmp += ",t1.YTCOUNTSELF=" + YTCOUNTSELF;
					}
				}
				sql = "update tblelementaction t1 set " + tmp
						+ " where t1.elementid=" + id;
			}
			state.execute(sql);
			conn.close();
		} catch (Exception e) {
			// TODO: handle exception
			zjxt_msg.showwarn("AddActNum->", e);
		}
	}

	public static int AddActCount(String id, int direct) { // direct=0 down; =1
															// upper;
															// operator=1:self;
															// =0 other;
		try {
			// 单纯次数累计
			Connection conn = zjxt_ConnectionPool.Instance().getConnection();
			Statement state = conn.createStatement();
			String sql = "select t.* from tblelementaction t where t.elementid="
					+ id;
			ResultSet rs = state.executeQuery(sql);
			int re = 0;
			if (rs.next()) {
				int UPPERONCOUNTSELF = rs.getInt("UPPERONCOUNTSELF");
				int DOWNOFFCOUNTSELF = rs.getInt("DOWNOFFCOUNTSELF");
				String tmp = "";
				if (direct == 1) {
					UPPERONCOUNTSELF++;
					re = UPPERONCOUNTSELF;
					tmp = "t1.upperoncountself=" + UPPERONCOUNTSELF
							+ ",t1.UPPERONCOUNTTOTAL=" + UPPERONCOUNTSELF;
				} else {
					DOWNOFFCOUNTSELF++;
					re = DOWNOFFCOUNTSELF;
					tmp = "t1.DOWNOFFCOUNTSELF=" + DOWNOFFCOUNTSELF
							+ ",t1.DOWNOFFCOUNTTOTAL=" + DOWNOFFCOUNTSELF;
				}

				sql = "update tblelementaction t1 set " + tmp
						+ " where t1.elementid=" + id;
			}
			state.execute(sql);
			conn.close();
			return re;
		} catch (Exception e) {
			// TODO: handle exception
			zjxt_msg.showwarn("AddActNum->", e);
			return -1;
		}
	}

	
	
	public static void CmdTraceDeal() {
		try {
			List failCmdList = new ArrayList();
			List successCmdList = new ArrayList();
			Connection conn = zjxt_ConnectionPool.Instance().getConnection();
			Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			//Statement state = conn.createStatement();

			int cmdct = cmdlist.size();
			zjxt_msg.showwarn("正在跟踪的命令数量:" + cmdct);

			zjxt_msg.showwarn("检查命令情况...");
			//String d = df.format(getDbTime());
			String sql;
			sql = "delete from TBLCOMMAND  where time_to_sec(timediff(now(),cmddatetime)) >= 300 or dealtag=7";// 300s后命令过期处理
//			sql = "delete from TBLCOMMAND t where DateDiff(ss,t.cmddatetime,'"
//					+ d + "')>=300 or dealtag=7"; 
			state.execute(sql);
			int c = state.getUpdateCount();
			zjxt_msg.showwarn("删除库中超过5分钟的命令记录数量:" + c);
			c = DelExpireCmdObj();
			//zjxt_msg.showwarn("删除内存中超过5分钟命令对象数量:" + c);

			sql = "select * from TBLCOMMAND t";
			ResultSet rs = state.executeQuery(sql);
			rs.last();
			int size = rs.getRow();
			zjxt_msg.show("库中命令数量:" + size);
			if(size <= 0) {
//				zjxt_CimBuild.canControl = true;
				for(int i=0; i<zjxt_CimBuild.cbList.size(); i++) {
					if(zjxt_CimBuild.cbList.get(i) instanceof zjxt_CimBuild.zFeederLine) {
						((zjxt_CimBuild.zFeederLine) zjxt_CimBuild.cbList.get(i)).setCanControl(true);
//						zjxt_CimBuild.cbList.get(i).line.setCanControl(true);
					}
				}
			}
			rs.beforeFirst();
			int index = 0;
			while(rs.next()) {
				index++;
				String cmdId = rs.getString("ID");
				String cmdElementId = rs.getString("CMDELEMENTID");
				int dealTag = rs.getInt("DEALTAG");
//				java.util.Date cmdDateTime = new java.util.Date(rs.getTimestamp("CMDDATETIME").getTime());
//				java.util.Date dealDateTime = null;
//				if(rs.getTimestamp("DEALDATETIME") != null) {
//					dealDateTime = new java.util.Date(rs.getTimestamp("DEALDATETIME").getTime());
//				}
				for(int i=0; i<cmdlist.size(); i++) {
					CmdObj cmdObj = cmdlist.get(i);
					if(cmdObj.CmdId.equals(cmdId) && cmdObj.DealTag != dealTag) {
						cmdObj.DealTag = dealTag;
						Equipment equip = (Equipment) zjxt_CimBuild.GetById(cmdElementId);
						switch(dealTag) {
							case CommandDealTagCode.HasRead:
//								equip.prop.SetAlarm("接口已读!");
								equip.prop.setResult("接口已读!");
								break;
							case CommandDealTagCode.Success:
//								equip.prop.SetAlarm("发令成功!");
								equip.prop.setResult("发令成功!");
								break;	
							case CommandDealTagCode.Failure:
//								equip.prop.SetAlarm("返校失败!");
								equip.prop.setResult("返校失败!");
//								zjxt_CimBuild.canControl = true;
								equip.line.setCanControl(true);
								zjxt_State.SetFailureLock(cmdElementId);
								zjxt_State.SetActNumLock(cmdElementId);
								failCmdList.add(cmdElementId);
								break;
							case CommandDealTagCode.Execute:
								zjxt_msg.show(equip.getName() + "命令执行成功!");
								successCmdList.add(cmdElementId);
//								equip.prop.SetAlarm("执行成功!");
								equip.prop.setResult("执行成功!");
								zjxt_State.SetSuccessLock(cmdElementId);
								zjxt_State.SetActNumLock(cmdElementId);
//								zjxt_CimBuild.canControl = true;
								equip.line.setCanControl(true);
								equip.prop.AddSelfActNum(true, cmdObj.actionType);
								break;
							case CommandDealTagCode.Refused:
								zjxt_msg.show(equip.getName() + "命令执行失败!");
								failCmdList.add(cmdElementId);
//								equip.prop.SetAlarm("执行失败!");
								equip.prop.setResult("执行失败!");
								zjxt_State.SetFailureLock(cmdElementId);
								zjxt_State.SetActNumLock(cmdElementId);
//								zjxt_CimBuild.canControl = true;
								equip.line.setCanControl(true);
								equip.prop.AddSelfActNum(false, cmdObj.actionType);
								break;
						}
					}
				}
			}
			
			for(int i=0; i<failCmdList.size(); i++) {
				state.execute("delete from tblcommand where cmdelementid="+failCmdList.get(i));
			}
			for(int i=0; i<successCmdList.size(); i++) {
				state.execute("delete from tblcommand where cmdelementid="+successCmdList.get(i));
			}
			failCmdList.clear();
			successCmdList.clear();
			failCmdList = null;
			successCmdList = null;
			rs.close();
			conn.close();

		} catch (Exception e) {
			zjxt_msg.showwarn("CmdTraceDeal->", e);
		}
	}

	/**
	 * 
	 * @Title: updateCmd
	 * @Description: 根据遥测遥信结果判断是否执行成功,并更新命令表标记
	 * @author: lixu
	 */
	public static void updateCmd(String elementId, int dealTag) {
		Connection conn = null;
		Statement state = null;
		try {
			conn = zjxt_ConnectionPool.Instance().getConnection();
			state = conn.createStatement();
			String sql = "update tblcommand set dealtag="+dealTag+" where cmdelementid='"+elementId+"'";
			int size = state.executeUpdate(sql);
			if(size>0) {
				zjxt_msg.show("更新"+zjxt_CimBuild.getEquipmentById(elementId).getName()+"命令标记dealtag=" + dealTag);
			} else {
				zjxt_msg.showwarn(zjxt_CimBuild.getEquipmentById(elementId).getName() + "命令不存在!");
			}
			state.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
	public static void ShowCmdResultInfo(String CmdId, int DealTag) {
		try {
			CmdObj obj = GetCmdObj(CmdId);
			if (obj == null)
				return;
			if (DealTag <= 1)
				return;
			String tagstr = "";
			switch (DealTag) {
			case 2:
				tagstr = " 接口发令成功";
				break;
			case 3:
				tagstr = " 接口发令失败";
				break;
			case 4:
				tagstr = " 超时取消";
				break;
			case 5:
				tagstr = " 对端反馈成功";
				break;
			case 6:
				tagstr = " 对端反馈失败";
				break;
			case 7:
				tagstr = " 结束命令";
				break;

			}
			String info = obj.Desc + tagstr;
			String sound = info;
			zjxt_msg.showwarn(info);
			if (DealTag != 2)
				cmdlist.remove(obj);
		} catch (Exception e) {
			zjxt_msg.showwarn("ShowCmdResultInfo->", e);
		}

	}

	public static long SendAdvice(String elementid, String Kind, String Action,
			String CmdContent) throws Exception {
		return SendAdviceSound(elementid, Kind, Action, CmdContent, "");
	}

	public static long SendAdviceSound(String elementid, String Kind,
			String Action, String CmdContent, String Sound) throws Exception {

		PowerSystemResource psr = zjxt_CimBuild.GetById(elementid);
		long id = NewId("tblCommandRecord");
		Connection conn = zjxt_ConnectionPool.Instance().getConnection();
		PreparedStatement ps = null;
		String sql = "INSERT INTO TBLCOMMANDRECORD (schemeid,cmddatetime,cmdelementid,cmdelementstyle,cmdelementname,cmdcontent,cmdkind,cmdaction,soundlist,sendtag,showtag)"
				+ "VALUES(1, now(), ?, 0, ?, ?, ?, ?, ?,0,0)";
		ps = conn.prepareStatement(sql);
//		ps.setLong(1, id);
//		java.util.Date date = new java.util.Date();
//		ps.setTimestamp(1, new Timestamp(date.getTime()));
		ps.setString(1, elementid);
		String ename = psr.getName();
		ps.setString(2, ename);
		ps.setString(3, CmdContent);
		ps.setString(4, Kind);
		ps.setString(5, Action);
		ps.setString(6, Sound);
		ps.execute();
		conn.close();
		if (Kind.equals(zjxt_msg.JianYi))
			zjxt_State.SetAdviceLock(elementid);
		else if (Kind.equals(zjxt_msg.KongZhi))
			zjxt_State.SetPrepareLock(elementid);
		zjxt_msg.show("*事项:" + CmdContent);
		return id;

	}

	/**
	 * 更新命令执行结果
	 * @param cmdRecordId
	 * @param result
	 */
	public static void updateCmdResult(int cmdRecordId, String result) throws Exception {
		Connection conn = zjxt_ConnectionPool.Instance().getConnection();
		PreparedStatement ps = null;
		String sql = "update tblcommandrecord set cmdresult=? where id=?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, result);
		ps.setInt(2, cmdRecordId);
		ps.execute();
		conn.close();
	}
	
	public static long SendCmdResult(String elementid,
			String result, String Sound) throws Exception {

		PowerSystemResource psr = zjxt_CimBuild.GetById(elementid);
		long id = NewId("tblCommandRecord");
		Connection conn = zjxt_ConnectionPool.Instance().getConnection();
		PreparedStatement ps = null;
		String sql = "INSERT INTO TBLCOMMANDRECORD (schemeid,cmddatetime,cmdelementid,cmdelementstyle,cmdelementname,cmdkind,cmdresult, soundlist,sendtag,showtag)"
				+ "VALUES(1, now(), ?, 0, ?, ?, ?, ?, 0, 0)";
		ps = conn.prepareStatement(sql);
//		ps.setLong(1, id);
		//java.util.Date date = new java.util.Date();
		//ps.setTimestamp(1, new Timestamp(date.getTime()));
		ps.setString(1, elementid);
		String ename = psr.getName();
		ps.setString(2, ename);
		ps.setString(3, zjxt_msg.TIP);
		ps.setString(4, result);
		ps.setString(5, Sound);
		ps.execute();
		conn.close();
		return id;
	}
	
	public static java.util.Date getDbTime() throws Exception{
		Connection conn = zjxt_ConnectionPool.Instance().getConnection();
		Statement st = conn.createStatement();
		String sql = "select now();";	
		ResultSet rs = st.executeQuery(sql);
		rs.first();
		java.sql.Timestamp d = rs.getTimestamp(1);
		java.util.Date ud = new Date(d.getTime());
		st.close();
		conn.close();
		return ud;
	}
	
	public static void SendYkCmd(String elementid, int channel, int czh, int dh,
			int ykValue, long adviceId) throws Exception {
		
		if (!zjxt_State.CanControl(elementid))
			return;
		PowerSystemResource psr = zjxt_CimBuild.GetById(elementid);
		Connection conn = zjxt_ConnectionPool.Instance().getConnection();
		PreparedStatement ps = null;
		String sql = "INSERT INTO TBLCOMMAND (id,controlarea,schemeid,schemeindex,cmddatetime,czh,ykyth,ykytvalue,ykyttype,dealtag,cmdelementid,channel)"
				+ "VALUES(?, ?, '1', '1', now(), ?, ?, ?, 0, 0,?,?)";
		ps = conn.prepareStatement(sql);
		java.util.Date currDate = getDbTime();//new java.util.Date();
		ps.setLong(1, adviceId);
		//String ctime = df.format(currDate);
		ps.setInt(2, channel);
		//ps.setString(3, ctime);
		ps.setInt(3, czh);
		ps.setInt(4, dh);
		ps.setFloat(5, ykValue);
		ps.setString(6, elementid);
		ps.setInt(7, channel);
		ps.execute();
		conn.close();
		AddCmdObj("" + adviceId, elementid, psr.getName() + zjxt_msg.YaoKong
				+ "命令ID:" + adviceId, currDate, zjxt_msg.YaoKong);
		zjxt_msg.show("*遥控命令发送成功");
//		zjxt_CimBuild.canControl = false;
		psr.line.setCanControl(false);
	}

	public static void SetControl() {

	}

	public static void SendYtCmd(String elementid, int channel, int czh, int dh,
			float ytValue, long adviceId) throws Exception {
		if (!zjxt_State.CanControl(elementid))
			return;
		PowerSystemResource psr = zjxt_CimBuild.GetById(elementid);
		Connection conn = zjxt_ConnectionPool.Instance().getConnection();
		PreparedStatement ps = null;
		String sql = "INSERT INTO TBLCOMMAND (id,controlarea,schemeid,schemeindex,cmddatetime,czh,ykyth,ytvalue,ykyttype,dealtag,cmdelementid,channel)"
				+ "VALUES(?, ?, '1', '1', now(), ?, ?, ?, 1, 0,?,?)";
		ps = conn.prepareStatement(sql);
		java.util.Date currDate = getDbTime();//new java.util.Date();
		ps.setLong(1, adviceId);
		//String ctime = df.format(currDate);
		ps.setInt(2, channel);
		//ps.setString(3, ctime);
		ps.setInt(3, czh);
		ps.setInt(4, dh);
		ps.setFloat(5, ytValue);
		ps.setString(6, elementid);
		ps.setInt(7,channel);
		ps.execute();
		conn.close();
		AddCmdObj("" + adviceId, elementid, psr.getName() + zjxt_msg.YaoTiao
				+ "命令ID:" + adviceId, currDate, zjxt_msg.YaoTiao);
		zjxt_msg.show("*遥调命令发送成功");
//		zjxt_CimBuild.canControl = false;  //命令执行成功或失败之前所有设备都不可控
		psr.line.setCanControl(false);
		// ps.close();
	}

//	public static void SendLTCmd(String name, float lowLimit, float upLimit)
//			throws Exception {
//		Connection conn = zjxt_ConnectionPool.Instance().getConnection();
//		String sql = "delete from tblltxx";
//		Statement st = conn.createStatement();
//		st.execute(sql);
//		st.close();
//		java.util.Date dd = new java.util.Date();
//		String stime = df.format(dd);
//		long tt = dd.getTime() + 15 * 60 * 1000; // +15分钟
//		dd.setTime(tt);
//		String etime = df.format(dd);
//		sql = "insert into tblltxx(substationname,l_limit,h_limit,starttime,endtime)"
//				+ " values('"
//				+ name
//				+ "',"
//				+ lowLimit
//				+ ","
//				+ upLimit
//				+ ",to_date('"
//				+ stime
//				+ "','yyyy-mm-dd hh24:mi:ss'),to_date('"
//				+ etime + "','yyyy-mm-dd hh24:mi:ss'))";
//		st = conn.createStatement();
//		st.execute(sql);
//		conn.close();
//
//		zjxt_msg.show("*联调命令发送成功");
//		// ps.close();
//	}

	public static class CmdObj {
		public String CmdId;
		public String ElementId;
		public String Desc;
		public java.util.Date DateTime;
		public java.util.Date dealDateTime;
		public String actionType;
		public int DealTag;
		public boolean visited = false;
	}

	public static class CommandDealTagCode {
		/** 人工确认 */
		public static final int Confirm = -1;

		/** 初始状态 */
		public static final int NotDeal = 0;

		/** 接口已读 */
		public static final int HasRead = 1;

		/** 发令成功 */
		public static final int Success = 2;

		/** 发令失败 返校失败 */
		public static final int Failure = 3;

		/** 超时取消 */
		public static final int OutTime = 4;

		/** 执行成功 */
		public static final int Execute = 5;

		/** 执行失败 */
		public static final int Refused = 6;

		/** 清除命令 */
		public static final int ClearUp = 7;
	}

}

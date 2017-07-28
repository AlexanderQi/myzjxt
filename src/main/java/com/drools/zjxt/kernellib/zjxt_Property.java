package com.drools.zjxt.kernellib;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.helpers.MessageFormatter;

import com.drools.zjxt.kernellib.zjxt_CimBuild.zCapacitor;
import com.drools.zjxt.kernellib.zjxt_Measure;
import com.drools.zjxt.kernellib.zjxt_Measure.zjxt_yk;
import com.drools.zjxt.kernellib.zjxt_Measure.zjxt_yt;
import com.drools.zjxt.kernellib.zjxt_Measure.zjxt_yx;
import com.softcore.cim.entity.Equipment;
import com.softcore.cim.entity.PowerSystemResource;
//import zjxt.zjxt_CimBuild.zBreaker;
//import zjxt.zjxt_CimBuild.zCompensator;

import zjxt.zjxt_ProtectionTable;
import zjxt.zjxt_State;
import zjxt.zjxt_msg;

public class zjxt_Property {
	// public String elementId;
	private zjxt_Measure measure;
	private List<PropertyItem> pList = new ArrayList<zjxt_Property.PropertyItem>();
	public PowerSystemResource Owner;

	private float TargetValue, OldValue;

	// TODO 一次发送多个命令
	private List<CommandOldTargetVal> cmdValue = new ArrayList<CommandOldTargetVal>(4);
	private Date TargetSetTime = null;
	private int dp = -1; // dbProvider code: 1 oracle; 2 mysql;

	/** 上一次告警信息 */
	private List<AlarmInfo> lastAlarmInfo = new ArrayList<AlarmInfo>();

	/** 上一次告警时间 */
	// private long lastAlarmMillis;
	public String pinfo = "prop info";

	public zjxt_Property() throws Exception {
		iniDb();
	}

	private void iniDb() {
		try {
			dp = zjxt_ConnectionPool.dbProvider;
		} catch (Exception e) {
			zjxt_msg.showwarn("zjxt_Property->iniDb()->", e);
		}
	}

	public long SecondFromTime(Date t) {
		long s = 0;
		if (t == null)
			return -1;
		Date cur = new Date();
		s = (cur.getTime() - t.getTime()) / 1000;
		return s;
	}

	public PowerSystemResource GetLeastActEquip(String schemeId) {
		try {
			PowerSystemResource psr = null;
			Connection dbConnection = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = dbConnection.createStatement();
			String sql = null;
			if (dp == 1) {
				sql = "select t.actionoutstate,t.elementid from tblelementstate t where t.elementid in(select c.id from tblfeedcapacitor c where c.schemeid='"
						+ schemeId + "') order by to_number(t.actionoutstate)";
			} else if (dp == 2) {
				sql = "select t.actionoutstate,t.elementid from tblelementstate t where t.elementid in(select c.id from tblfeedcapacitor c where c.schemeid='"
						+ schemeId + "') order by (t.actionoutstate+0)";
			}
			ResultSet rSet = stat.executeQuery(sql);
			if (rSet.next()) {
				String eid = rSet.getString("elementid");
				psr = zjxt_CimBuild.GetById(eid);
			}
			stat.close();
			dbConnection.close();
			return psr;
		} catch (Exception e) {
			zjxt_msg.showwarn("GetLeastActEquip->", e);
			return null;
		}
	}

	public boolean IsLeastActEquip(String schemeId) {
		boolean isleast = false;
		try {
			Connection dbConnection = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = dbConnection.createStatement();
			String sql = null;
			if (dp == 1) {
				sql = "select t.actionoutstate,t.elementid from tblelementstate t where t.elementid in(select c.id from tblfeedcapacitor c where c.schemeid='"
						+ schemeId + "') order by to_number(t.actionoutstate)";
			} else if (dp == 2) {
				sql = "select t.actionoutstate,t.elementid from tblelementstate t where t.elementid in(select c.id from tblfeedcapacitor c where c.schemeid='"
						+ schemeId + "') order by (t.actionoutstate+0)";
			}
			ResultSet rSet = stat.executeQuery(sql);
			String eid = "";
			String myid = Owner.getMrID();
			int myact = -1;
			int miact = -1;
			if (rSet.next()) {
				eid = rSet.getString("elementid");
				miact = rSet.getInt("actionoutstate");
			}
			if (myid.equals(eid)) {
				isleast = true;
			} else {
				myact = zjxt_State.GetActCount(myid);
				if (miact == myact)
					isleast = true;
			}
			stat.close();
			dbConnection.close();
			return isleast;
		} catch (Exception e) {
			zjxt_msg.showwarn("IsLeastActEquip->", e);
			return isleast;
		}
	}

	public void SetSchemeLock(String SchemeId) {
		String d = zjxt_Cmd.df.format(new Date());
		String sql = null;

		if (dp == 1) {
			sql = "update tblelementstate t set t.belockstate='" + zjxt_State.Ls_Kongzhi + "',t.lockstarttime="
					+ "to_date('" + d + "','yyyy-mm-dd hh24:mi:ss')"
					+ "where t.elementid in(select c.id from tblfeedcapacitor c where c.schemeid=" + SchemeId
					+ ") and (t.belockstate is null or t.belockstate <> '动作次数闭锁')";
		} else {
			sql = "update tblelementstate t set t.belockstate='" + zjxt_State.Ls_Kongzhi + "',t.lockstarttime=now() "
					+ "where t.elementid in(select c.id from tblfeedcapacitor c where c.schemeid=" + SchemeId
					+ ") and (t.belockstate is null or t.belockstate <> '动作次数闭锁')";
		}
		try {
			Connection conn = zjxt_ConnectionPool.Instance().getConnection();
			Statement state = conn.createStatement();
			state.execute(sql);
			int c = state.getUpdateCount();
			state.close();
			conn.close();
			zjxt_msg.showwarn("# 联动闭锁 Schemeid:" + SchemeId + " 数量:" + c);
			zjxt_State.Refresh();
		} catch (Exception e) {
			// TODO: handle exception
			zjxt_msg.showwarn("SetSchemeLock->", e);
		}
	}

	public PowerSystemResource GetOtherSchemeEquip(String schemeId) {
		PowerSystemResource psr = null;
		try {
			Connection dbConnection = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = dbConnection.createStatement();
			String selfid = Owner.getMrID();
			String sql = "select c.id,c.schemeid from tblfeedcapacitor c where c.schemeid=" + schemeId + " and c.id <> "
					+ selfid;
			ResultSet rSet = stat.executeQuery(sql);
			String eid;
			if (rSet.next()) {
				eid = rSet.getString("id");
				psr = zjxt_CimBuild.GetById(eid);
			}
			stat.close();
			dbConnection.close();
			return psr;
		} catch (Exception e) {
			zjxt_msg.showwarn("GetOtherSchemeEquip->", e);
			return psr;
		}
	}

	public int runtimetag = 12; // 运行时间统计标记 12*5=60s

	// // field=1 运行时间; field=0 停运时间; value : 时间值（分钟）
	public void UpdateRunTime(int field, int value) throws Exception {
		String id = Owner.getMrID();
		zjxt_Cmd.UpdateRunTime(id, field, value);
	}

	public void SetTarget(float oValue, float tValue) {
		TargetSetTime = new Date();
		TargetValue = tValue;
		OldValue = oValue;
	}

	public void CancelTarget() {
		TargetSetTime = null;
		TargetValue = Float.NaN;
	}

	// return -1:无效检查；0:失败；1：成功；2：等待
	public int CheckTarget(int second, float curValue) { // second
															// 目标值设定的时间与当前时间最大的间隔秒数.
		if (TargetSetTime == null)
			return -1;
		Date curDate = new Date();
		// int tmp = zjxt_State.GetPLockTimeLimit(Owner.getMrID());
		// if (second >= tmp) {
		// second = tmp - 5;
		// }
		long between = (curDate.getTime() - TargetSetTime.getTime()) / 1000;
		if (between >= second) { // 超时处理
			TargetSetTime = null;
			return 0;
		} else {

			if (Math.abs(TargetValue - curValue) < 0.01) {
				// if(Math.abs(Owner.oldU - Owner.U)>0.1) {
				TargetSetTime = null;
				return 1; // 成功
				// } else {
				// return 2;
				// }
			} else {
				return 2; // 未达到，需要等待。
			}
		}
	}

	public zjxt_Property(PowerSystemResource Owner, zjxt_Measure measure) {
		this.Owner = Owner;
		this.measure = measure;
		iniDb();
	}

	public void Add(PropertyItem item) {
		item.elementId = Owner.getMrID();
		pList.add(item);
	}

	public PropertyItem Add(String id, String name, int ca, int czh, int dh) {
		PropertyItem item = new PropertyItem(id, name, ca, czh, dh);
		item.elementId = Owner.getMrID();
		pList.add(item);
		zjxt_msg.show(
				"Add Measure " + Owner.getName() + Owner.getMrID() + " " + name + " " + ca + " " + czh + " " + dh);
		return item;
	}

	public boolean isDeadData(String name) throws Exception {
		PropertyItem item = getItem(name);
		if (item != null) {
			return measure.isDeadData(item.id);
		}
		throw new Exception(Owner.getName() + "找不到" + name + "属性");
	}

	public float getyc(String Name) throws Exception {
		PropertyItem item = getItem(Name);
		if (item != null) {
			// Owner.hasDeadData = Owner.hasDeadData ||
			// measure.isDeadData(item.ca, item.czh, item.dh);
			return measure.GetYcValue(item.id);
			// return measure.GetYcValue(item.ca, item.czh, item.dh);
		}
		throw new Exception(Owner.getName() + "getYc->" + Name + " 未找到.");
	}

	public Date getyctime(String Name) throws Exception {
		PropertyItem item = getItem(Name);
		if (item != null) {
			return measure.GetYcTime(item.ca, item.czh, item.dh);
		}
		throw new Exception("getyctime ->" + Name + " 未找到.");
	}

	public boolean setyc(String Name, float value) throws Exception {
		PropertyItem item = getItem(Name);
		if (item != null) {
			return measure.SetYcValue(item.ca, item.czh, item.dh, value, new Date());
		}
		return false;
	}

	public int getyx(String Name) {
		PropertyItem item = getItem(Name);
		if (item != null) {
			return measure.GetYxValue(item.ca, item.czh, item.dh);
		}
		return -1;
	}

	public float getYC(String name) {
		return measure.GetYcByName(name);
	}

	public int getYX(String name) {
		return measure.GetYxByName(name);
	}

	public float getycById(String ycid) {
		try {
			return measure.GetYc(ycid).Value;

		} catch (Exception e) {
			zjxt_msg.showwarn("getycById(" + ycid + ")	" + e.toString());
			return Float.NaN;
		}
	}

	public int getyxById(String yxid) {
		try {
			return measure.GetYx(yxid).Value;
		} catch (Exception e) {
			zjxt_msg.showwarn("getyxById(" + yxid + ")	" + e.toString());
			return -1;
		}
	}

	public boolean setyk(int ykValue, String Kind, String Action, String CmdContent, String Sound) throws Exception {
		String elementId = Owner.getMrID();	
		PowerSystemResource eq = Owner;	
		if (Owner instanceof zCapacitor) { // 电容器特殊处理
			zCapacitor cap = (zCapacitor) Owner;
			if (cap.IsItem) {
				elementId = cap.MyGroup.Id;
				eq = zjxt_CimBuild.GetById(elementId);
				if (eq == null) {
					throw new Exception("prop->setyk->电容子组为null");
				}
			}
		}
		Kind = eq.controlState;
		if (Kind.equals(zjxt_msg.KongZhi)) {
			Sound = "动作;" + Sound;
		} else {
			Sound = "建议;" + Sound;
		}
		long aid = 0;
		aid = zjxt_Cmd.SendAdviceSound(elementId, Kind, Action, CmdContent, Sound);


		// Kind ：控制或建议
		if (Kind.equals(zjxt_msg.KongZhi)) { // Kind ：控制或建议

			zjxt_yk yk = measure.GetYk(elementId);
			if (yk == null) {
				zjxt_msg.showwarn(Owner.getName() + "-被控设备id:" + elementId + " 未设置遥控参数.");
				return false;
			}
			if (Action.equals("投入") || Action.equals("升档")) // 投，切，升，降
				zjxt_Cmd.SendYkCmd(elementId, yk.channel, yk.czh_upper, yk.dh_upper, ykValue, aid);
			else {
				zjxt_Cmd.SendYkCmd(elementId, yk.channel, yk.czh_down, yk.dh_down, ykValue, aid);
			}
		}
		return true;
	}

	public boolean setykByName(int ykValue, String ykname, String Kind, String Action, String CmdContent, String Sound)
			throws Exception {
		String elementId = Owner.getMrID();	
		PowerSystemResource eq = Owner;	
		if (Owner instanceof zCapacitor) { // 电容器特殊处理
			zCapacitor cap = (zCapacitor) Owner;
			if (cap.IsItem) {  //特殊处理原因：电容子组没有控制状态，只有电容器才有。
				elementId = cap.MyGroup.Id;
				eq = zjxt_CimBuild.GetById(elementId);
				if (eq == null) {
					throw new Exception("prop->setyk->电容子组为null");
				}
			}
		}
		Kind = eq.controlState;
		if (Kind.equals(zjxt_msg.KongZhi)) {
			Sound = "动作;" + Sound;
		} else {
			Sound = "建议;" + Sound;
		}
		long aid = 0;
		aid = zjxt_Cmd.SendAdviceSound(elementId, Kind, Action, CmdContent, Sound);

		// Kind ：控制或建议
		if (Kind.equals(zjxt_msg.KongZhi)) { // Kind ：控制或建议
			zjxt_yk yk = measure.GetYkByName(eq.getMrID(), ykname);
			if (yk == null) {
				zjxt_msg.showwarn("【{}】{}未设置.", eq.getName(), ykname);
				return false;
			}
			if (Action.equals("投入") || Action.equals("升档")) // 投，切，升，降
				zjxt_Cmd.SendYkCmd(elementId, yk.channel, yk.czh_upper, yk.dh_upper, yk.FixValue_upper, aid);
			else {
				zjxt_Cmd.SendYkCmd(elementId, yk.channel, yk.czh_down, yk.dh_down, yk.FixValue_down, aid);
			}
		}
		return true;
	}

	// public boolean setykByName(String ykname, String Kind, String Action,
	// String CmdContent, String Sound) throws Exception {
	// String elementId = Owner.getMrID();
	// long aid = zjxt_Cmd.SendAdviceSound(elementId, Kind, Action, CmdContent,
	// Sound);
	// // Kind ：控制或建议
	// if (Kind.equals(zjxt_msg.KongZhi)) { // Kind ：控制或建议
	//
	// zjxt_yk yk = measure.GetYkByName(elementId, ykname);
	// if (yk == null) {
	// zjxt_msg.showwarn(ykname + " 未设置遥控参数.");
	// return false;
	// }
	// if (Action.equals("投入") || Action.equals("升档")) // 投，切，升，降
	// zjxt_Cmd.SendYkCmd(elementId, yk.channel, yk.czh_upper, yk.dh_upper,
	// yk.FixValue_upper, aid);
	// else {
	// zjxt_Cmd.SendYkCmd(elementId, yk.channel, yk.czh_down, yk.dh_down,
	// yk.FixValue_down, aid);
	// }
	// }
	// return true;
	// }

	public String GetSpecYXID(String TableName, String FieldName, String EquipmentId) {
		try {
			// to do in will
			return null;
		} catch (Exception e) {
			zjxt_msg.showwarn("GetSpecYXID->", e);
			return null;
		}
	}

	public boolean setyxyk(int ykValue, String yxid, String Kind, String Action, String CmdContent, String Sound)
			throws Exception {
		String elementId = Owner.getMrID();
		long aid = zjxt_Cmd.SendAdviceSound(elementId, Kind, Action, CmdContent, Sound);
		// Kind ：控制或建议
		if (Kind.equals(zjxt_msg.KongZhi)) { // Kind ：控制或建议
			zjxt_yx yx = measure.GetYx(yxid);
			if (yx == null) {
				zjxt_msg.showwarn(CmdContent + "因 yxid:" + yxid + " 不存在,无法执行.");
				return false;
			}
			zjxt_Cmd.SendYkCmd(elementId, yx.channel, yx.czh, yx.yxh, ykValue, aid);
		}
		return true;
	}

	public boolean setyt(String ytKind, float ytValue, String Kind, String Action, String CmdContent, String Sound)
			throws Exception {
		String elementId = Owner.getMrID();
		zjxt_yt yt = measure.GetYt(elementId, ytKind);

		// return measure.SetYtValue(item.ca, item.czh, item.dh, ytValue);
		long aid = zjxt_Cmd.SendAdviceSound(elementId, Kind, Action, CmdContent, Sound); // +"
																							// "+Action+":
																							// "+ytValue
		if (yt == null) {
			zjxt_msg.showwarn(Owner.getName() + " id=" + elementId + " 没有遥调号,无法操作!");
			return false;
		}
		if (Kind.equals(zjxt_msg.KongZhi)) {// Kind ：控制
			zjxt_Cmd.SendYtCmd(elementId, yt.channel, yt.czh, yt.dh, ytValue * yt.multiplevalue, aid);
		}
		return true;
	}

	/**
	 * 设置命令执行结果
	 * 
	 * @param result
	 * @return
	 */
	public void setResult(String result) throws Exception {
		zjxt_Cmd.SendCmdResult(Owner.getMrID(), result, result);
	}

	public boolean SetAlarm(String info, Object... params) throws Exception {
		// info = MessageFormatter.arrayFormat(info, params).getMessage();
		info = zjxt_msg.format(info, params);
		long currentTimeMillis = System.currentTimeMillis();
		for (AlarmInfo alarmInfo : lastAlarmInfo) {
			if (alarmInfo.info.equals(info)) {
				if ((currentTimeMillis - alarmInfo.alarmDate) / 1000 >= (5 * 60)) {
					alarmInfo.alarmDate = currentTimeMillis;
					String elementId = Owner.getMrID();
					zjxt_Cmd.SendAdviceSound(elementId, zjxt_msg.GaoJing, "提示", info, info);
				}
				return true;
			}
		}
		for (AlarmInfo alarmInfo : lastAlarmInfo) {
			if ((currentTimeMillis - alarmInfo.alarmDate) / 1000 >= (5 * 60)) {
				lastAlarmInfo.remove(alarmInfo);
			}
		}
		AlarmInfo alarmInfo = new AlarmInfo();
		alarmInfo.info = info;
		alarmInfo.alarmDate = currentTimeMillis;
		lastAlarmInfo.add(alarmInfo);
		String elementId = Owner.getMrID();
		zjxt_Cmd.SendAdviceSound(elementId, zjxt_msg.GaoJing, "提示", info, info);
		return true;
	}

	public boolean SetAdvice(String info) throws Exception {
		String elementId = Owner.getMrID();
		// return measure.SetYtValue(item.ca, item.czh, item.dh, ytValue);
		zjxt_Cmd.SendAdvice(elementId, zjxt_msg.JianYi, "提示", info);
		return true;
	}

	public boolean SetAdviceSound(String info, String sound) throws Exception {
		String elementId = Owner.getMrID();
		// return measure.SetYtValue(item.ca, item.czh, item.dh, ytValue);
		zjxt_Cmd.SendAdviceSound(elementId, zjxt_msg.JianYi, "提示", info, sound);
		return true;
	}

	private PropertyItem getItem(String Name) {
		for (Iterator<PropertyItem> iterator = pList.iterator(); iterator.hasNext();) {
			PropertyItem obj = (PropertyItem) iterator.next();
			if (obj.Name.equals(Name)) {
				return obj;
			}
		}
		return null;
	}

	public String LockedInfo = "";

	public boolean IsLocked() {
		String elementId = Owner.getMrID();
		try {
			LockedInfo = zjxt_State.GetLockState(elementId);
			if (LockedInfo == null || LockedInfo.equals(""))
				return false;
			// else
			// zjxt_msg.showwarn(Owner.getName() + " id:" + elementId
			// + " 闭锁信息->" + LockedInfo);
		} catch (Exception e) {
			zjxt_msg.showwarn(e.toString());
		}
		return true;
	}

	public String GetLockInfo() {
		return LockedInfo;
	}

	public void SetException(String eStr) throws Exception {
		String elementId = Owner.getMrID();
		zjxt_State.SetExceptionLock(elementId, eStr);
		// zjxt_msg.showwarn(Owner.getName() + eStr);
	}

	public String ProtectInfo = "";

	public void protectAlarm(String info, String type) throws Exception {
		zjxt_Cmd.SendAdvice(Owner.getMrID(), zjxt_ProtectionTable.PROTECT_INFO, type, info);
	}

	public boolean IsProtected() {
		String elementId = Owner.getMrID();
		try {
			ProtectInfo = zjxt_ProtectionTable.GetProtection(elementId);
			if (ProtectInfo.equals(""))
				return false;
			// else
			// zjxt_msg.showwarn(Owner.getName() + " id:" + elementId
			// + " 保护信息->" + LockedInfo);
		} catch (Exception e) {
			zjxt_msg.showwarn(e.toString());
		}
		return true;
	}

	public String GetProtectInfo() {
		return ProtectInfo;
	}

	public String CanControl() {
		String elementId = Owner.getMrID();
		try {
			// String name = Owner.getName() + " 可以";
			String str = zjxt_State.GetControlState(elementId);

			if (str.equals(zjxt_State.cs_bucanyu)) {
				return str;
			} else if (str.equals(zjxt_State.cs_JanYi)) {
				// zjxt_msg.showwarn(name + str);
				return str;
			} else if (str.equals(zjxt_State.cs_Kongzhi)) {
				// zjxt_msg.showwarn(name + str);
				return str;
			}

		} catch (Exception e) {
			zjxt_msg.showwarn("zjxt_Property->CanControl()->", e);
		}
		return zjxt_State.cs_bucanyu;
	}

	public void AddSelfActNum(boolean IsSuccess, String actionType) throws Exception {

		if (actionType.equals(zjxt_msg.YaoKong)) {
			int direct = TargetValue > OldValue ? 1 : 0; // 目标值大于原始值为升档
			zjxt_Cmd.AddActNum(Owner.getMrID(), direct, 1, IsSuccess);
		}
		if (actionType.equals(zjxt_msg.YaoTiao)) {
			zjxt_Cmd.AddActNum(Owner.getMrID(), 3, 1, IsSuccess);
		}
		// int direct = TargetValue > OldValue ? 1 : 0; // 目标值大于原始值为升档
		// zjxt_Cmd.AddActNum(Owner.getMrID(), direct, 1, IsSuccess);
	}

	public void AddSelfActCount(int direct) throws Exception {

		zjxt_Cmd.AddActCount(Owner.getMrID(), direct);
	}

	public void AddOtherActNum() throws Exception {
		int direct = TargetValue > OldValue ? 1 : 0;
		zjxt_Cmd.AddActNum(Owner.getMrID(), direct, 0, false);
		zjxt_msg.showwarn(Owner.getName() + " 其他动作次数+1");
	}

	public void SendLTCmd(String name, float lowLimit, float upLimit) throws Exception {
		// zjxt_Cmd.SendLTCmd(name, lowLimit, upLimit);
		zjxt_msg.showwarn("此版本不支持发送联调命令");
	}

	public static class PropertyItem {
		protected String id;
		protected String Name;
		protected int ca, czh, dh;
		protected String elementId;

		public PropertyItem(String id, String Name, int ca, int czh, int dh) {
			this.id = id;
			this.ca = ca;
			this.Name = Name;
			this.czh = czh;
			this.dh = dh;
		}
	}

	class AlarmInfo {
		String info; // 告警信息
		long alarmDate; // 告警时间毫秒数
	}

	/**
	 * 
	 * @ClassName: CommandOldTargetVal
	 * @Description: 命令对应的旧值，及目标值
	 * @author: lixu
	 * @Company: 南京软核
	 * @Date: 2017年1月3日 下午4:10:31
	 *
	 */
	class CommandOldTargetVal {
		float oldValue; // 旧值
		float targetValue; // 目标值
	}

}

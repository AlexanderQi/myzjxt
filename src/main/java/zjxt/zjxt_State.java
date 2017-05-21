package zjxt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import com.drools.zjxt.kernellib.Limit;
import com.drools.zjxt.kernellib.Limit.LimitValue;
import com.drools.zjxt.kernellib.zjxt_CimBuild;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zBreaker;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zCapacitor;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zDisconnector;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zFeederLine;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zSVG;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zSubControlArea;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zSubStation;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zTSF;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zTransformerFormer;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zVoltageRegulator;
import com.drools.zjxt.kernellib.zjxt_Cmd;
import com.drools.zjxt.kernellib.zjxt_ConnectionPool;
import com.drools.zjxt.kernellib.zjxt_kernel;
import com.softcore.cim.entity.PowerSystemResource;
import com.softcore.cim.entity.container.FeederLine;
import com.softcore.cim.entity.container.SubControlArea;
import com.softcore.cim.entity.container.SubStation;

import zjxt2_app.zApf;
import zjxt2_app.zTpunbalance;
import zjxt2_app.zjxt_build;

public class zjxt_State {
	private static Map<String, zState> StateTable = new HashMap<String, zjxt_State.zState>();

	public static void Clear() {
		StateTable.clear();
	}

	public static final String Ls_Kongzhi = "控制闭锁";
	public static final String ls_ChengGong = "成功闭锁";
	public static final String ls_ShiBai = "失败闭锁";
	public static final String ls_HuaDang = "滑档闭锁";
	public static final String ls_JanYi = "建议闭锁";
	
	public static final String ls_BaoHu = "保护闭锁";

	public static final String ls_DongZuo = "动作次数闭锁";
	public static final String ls_YiChang = "异常闭锁";
	public static final String ls_LXShiBai = "连续失败闭锁";
	
	
	public static final String cs_Kongzhi = "控制";
	public static final String cs_JanYi = "建议";
	public static final String cs_canyu = "参与计算";
	public static final String cs_bucanyu = "不参与计算";
	
	public static void AutoUnLock() throws Exception {
		String idString = "";
		zState val = null;
		try {
			zjxt_msg.show("AutoUnLock checking...");
			Iterator<Entry<String, zState>> iter = StateTable.entrySet()
					.iterator();
			Date curDate = new Date();		
			while (iter.hasNext()) {
				Entry<String, zState> entry = iter.next();
				// String elementId = entry.getKey();
				val = entry.getValue();	
				idString = val.ELEMENTID;
				if (val.LockStartTime != null && (!val.BelockState.equals(""))) {
					long between = (curDate.getTime() - val.LockStartTime.getTime()) / 1000;
					
					PowerSystemResource pr = zjxt_CimBuild.GetById(val.ELEMENTID);
					idString += pr.getName();
					zjxt_msg.showwarn("自动解锁检查  设备:"+pr.getName()+" id:"+val.ELEMENTID+",闭锁状态:"+val.BelockState
							+",闭锁开始时间："+zjxt_kernel.XtDateFormat.format(val.LockStartTime) + " 已过去"+between+ "s");

					if (val.BelockState.equals(ls_JanYi)) {  //建议闭锁解除
						if (between >= val.AdviceLockSec){
							val.BelockState = "";
							val.LockStartTime = null;
							SaveOne(val.ELEMENTID);
							zjxt_msg.showwarn("建议信息自动解锁时间设定："+val.AdviceLockSec +"s 到时已解锁");
						}
					} else if (val.BelockState.equals(Ls_Kongzhi)) { //控制（预置）闭锁解除
						if (between >= val.PreparLockSec){
							val.BelockState = "";
							val.LockStartTime = null;
							SaveOne(val.ELEMENTID);
							zjxt_msg.showwarn("控制命令自动解锁时间设定："+val.PreparLockSec +"s 到时已解锁");
						}
					} else if(val.BelockState.equals(ls_ChengGong)) { //成功闭锁自动解锁
						if (between >= val.SuccessLockSec){
							val.BelockState = "";
							if(!val.ActionOutState.equals(ls_DongZuo)) {
								val.LockStartTime = null;
							}
							SaveOne(val.ELEMENTID);
							zjxt_msg.showwarn("成功闭锁自动解锁时间设定："+val.PreparLockSec +"s 到时已解锁");
						}
					} else if(val.BelockState.equals(ls_ShiBai)) { //失败闭锁自动解锁
						if (between >= val.FailureLockSec){
							val.BelockState = "";
							if(!val.ActionOutState.equals(ls_DongZuo)) {
								val.LockStartTime = null;
							}
							SaveOne(val.ELEMENTID);
							zjxt_msg.showwarn("失败闭锁自动解锁时间设定："+val.PreparLockSec +"s 到时已解锁");
						}
					} else if(val.BelockState.equals(ls_LXShiBai)) { //连续失败闭锁自动解锁
						if(between >= val.RepeatedFailureLockSec) {
							val.BelockState = "";
							if(!val.ActionOutState.equals(ls_DongZuo)) {
								val.LockStartTime = null;
							}
							val.RepeatedFailureCount = 0;
							SaveOne(val.ELEMENTID);
							zjxt_msg.showwarn("连续失败闭锁自动解锁时间设定："+val.RepeatedFailureLockSec +"s 到时已解锁");
						}
					} 
				}
				if (val.ActionOutState.equals(ls_DongZuo)) {  //动作次数闭锁解除
					LimitValue lv = Limit.GetActNumLimitEx(val.ELEMENTID);  //2015-1-13 获取该设备或通用动作次数限值
					int ActNum = 0;
					if(lv == null){
							ActNum = Limit.ActNum;  //如果没有设定限值，用系统默认值。
						}else {
							ActNum = (int)lv.up;
						}
					if(val.LockStartTime != null) {
						long lockZero = val.LockStartTime.getTime()/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset(); //闭锁时间当天零点
						long curZero = curDate.getTime()/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset(); //当前时间当天零点
						if ((curZero-lockZero)/1000/60/60/24>=1 ||
								getActionCount(val.ELEMENTID) < ActNum) {
							val.ActDatetime = curDate;
							val.LockStartTime = null;
							val.ActNumber = 0;
							val.ActionOutState = "";
							SaveOne(val.ELEMENTID);
							zjxt_msg.showwarn("{}动作次数闭锁已解锁", zjxt_CimBuild.getEquipmentById(val.ELEMENTID).getName());
						}
					} else { //闭锁时间为空
						Calendar cal = Calendar.getInstance();
						if((cal.get(Calendar.HOUR_OF_DAY)==0 &&
								cal.get(Calendar.MINUTE)<=5) ||
								getActionCount(val.ELEMENTID) < ActNum) {
							val.ActDatetime = curDate;
							val.LockStartTime = null;
							val.ActNumber = 0;
							val.ActionOutState = "";
							SaveOne(val.ELEMENTID);
							zjxt_msg.showwarn("{}动作次数闭锁已解锁", zjxt_CimBuild.getEquipmentById(val.ELEMENTID).getName());
						}
					}
					
				}

			}
		} catch (Exception e) {
			throw new Exception("AutoUnLock->" + e.toString()+" id:"+idString + ",闭锁状态:"+val.ActionOutState);
		}
	}
	
	

	public static void SetAdviceLock(String elementId) throws Exception {
		zState val = GetStateItem(elementId);
		val.BelockState = ls_JanYi;
		val.LockStartTime = new Date();
		SaveOne(elementId);
	}

	public static void SetSuccessLock(String elementId) throws Exception {
		zState val = GetStateItem(elementId);
		val.BelockState = ls_ChengGong;
		val.LockStartTime = new Date();
		SaveOne(elementId);
	}

	public static synchronized void SetPrepareLock(String elementId) throws Exception {
		zState val = GetStateItem(elementId);
		val.BelockState = Ls_Kongzhi;
		val.LockStartTime = new Date();		
		val.ActNumber++;	
//		int ActNum = -1;
//		LimitValue lv = Limit.GetActNumLimitEx(elementId);  //2015-1-13 获取该设备或通用动作次数限值
//		if(lv == null){
//			ActNum = Limit.ActNum;  //如果没有设定限值，用系统默认值。
//		}else {
//			ActNum = (int)lv.up;
//		}
//		
//		int actCount = getActionCount(elementId);
//		
//		//val.ActionOutState = Integer.toString(val.ActNumber);  //2015-1-13 动作状态保存当前设备动作次数
//		if (actCount >= ActNum) {
//			val.ActDatetime = val.LockStartTime;
////			val.BelockState = ls_DongZuo;
//			val.ActionOutState = ls_DongZuo;
//			zjxt_msg.showwarn("设备id:"+elementId+" 动作次数达到上限:"+ActNum +"次,当天动作次数用尽!");
//		}	
		SaveOne(elementId);
	}

	public static void SetFailureLock(String elementId) throws Exception {
		zState val = GetStateItem(elementId);
		val.BelockState = ls_ShiBai;
		val.RepeatedFailureCount++;
		if(val.RepeatedFailureCount>=val.MaxRepeatedFailureCount) val.BelockState = ls_LXShiBai;
		val.LockStartTime = new Date();
		SaveOne(elementId);
	}

	public static void SetActNumLock(String elementId) throws Exception {
		
		//////////////////////////////////////////////////////////////////
		//
		//该函数功能由 预置闭锁功能 兼任
		//////////////////////////////////////////////////////////////////
		zState val = GetStateItem(elementId);
		val.ActNumber++;	
		int ActNum = -1;
		LimitValue lv = Limit.GetActNumLimitEx(elementId);  //2015-1-13 获取该设备或通用动作次数限值
		if(lv == null){
			ActNum = Limit.ActNum;  //如果没有设定限值，用系统默认值。
		}else {
			ActNum = (int)lv.up;
		}
		
		int actCount = getActionCount(elementId);
//		val.ActionOutState = Integer.toString(val.ActNumber);  //2015-1-13 动作状态保存当前设备动作次数
		if (actCount >= ActNum) {
			val.ActDatetime = new Date();
			val.ActionOutState = ls_DongZuo;
			val.LockStartTime = new Date();	
		}	
		SaveOne(elementId);

	}

	/**
	 * 从elementaction表中获取设备动作总次数
	 * @param elementId
	 * @return
	 */
	public static int getActionCount(String elementId) {
		Connection conn = null;
		Statement state = null;
		ResultSet rs = null;
		int count = 0;
		try {
			conn = zjxt_ConnectionPool.Instance().getConnection();
			state = conn.createStatement();
			String sql = "select FAILURECOUNT+SUCCESSCOUNT as count from tblelementaction where elementid="+elementId;
			rs = state.executeQuery(sql);
			if(rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			zjxt_msg.showwarn(zjxt_build.GetById(elementId).getName()+"获取设备动作次数异常!");
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(state != null) {
					state.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return count;
	}
	
	public static void SetExceptionLock(String elementId,String ExceptionStr) throws Exception {
		zState val = GetStateItem(elementId);
		if(val.ExceptionState != ExceptionStr){
			val.ExceptionState = ExceptionStr;
			SaveOne(elementId);
		}
	}

	
	public static void SetAutoProtectLock(String elementId,String pString) throws Exception {
		zState val = GetStateItem(elementId);
		if(val.AutoUnlockProtectionState != pString){
			val.AutoUnlockProtectionState = pString;
			SaveOne(elementId);
		}
	}
	
	public static void SetProtectLock(String elementId,String pString) throws Exception {
		zState val = GetStateItem(elementId);
		if(val.HandUnlockProtectionState != pString){
			val.HandUnlockProtectionState = pString;
			SaveOne(elementId);
		}
	}
	
	
	
	public static void UnLockExceptionLock(String elementId) throws Exception {
		zState val = GetStateItem(elementId);
		if(val.ExceptionState != null && val.ExceptionState.equals(ls_YiChang)){
			zjxt_msg.showwarn(elementId+" 解除异常闭锁");
			val.ExceptionState = "";
			//val.LockStartTime = new Date();
			SaveOne(elementId);
		}
	}
	
	public static void RefreshSystemSign(Statement st)throws Exception{
		try {
			if(!IsMysql) return; //oracle 库没有此表
			String sql = "update tblprogrammgr t set t.sign_time=now() where t.name='zjxt2.0'";
			st.execute(sql);
		} catch (Exception e) {
			throw new Exception("zjxt_State->RefreshSystemSign->" + e.toString());
		}
		
	}
	
	public static void Refresh()throws Exception{
		String idString = "";
		try {
			zjxt_msg.show("Elementstate refresh...");
			Connection dbConnection = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = dbConnection.createStatement();
			ResultSet rSet = stat.executeQuery("select * from tblElementstate");
			while (rSet.next()) {
				String eid = rSet.getString("ELEMENTID");
				idString = eid;
				zState zs = StateTable.get(eid);
				if (zs == null)
					continue;
				zs.BelockState = rSet.getString("BelockState");
				if(zs.BelockState == null) zs.BelockState = "";
				zs.ControlState = rSet.getString("ControlState");
				if(zs.ControlState == null) zs.ControlState = zjxt_msg.JianYi;
				zs.ExceptionState = rSet.getString("ExceptionState");
				if(zs.BelockState == null) zs.BelockState = "";
				
				zs.HandUnlockProtectionState = rSet.getString("HandUnlockProtectionState");
				if(zs.HandUnlockProtectionState == null) zs.HandUnlockProtectionState=""; 
				zs.AutoUnlockProtectionState = rSet.getString("AutoUnlockProtectionState");
				if(zs.AutoUnlockProtectionState == null) zs.AutoUnlockProtectionState="";
				String dateString = rSet.getString("LockStartTime");
				if (dateString != null && !dateString.equals(""))
					zs.LockStartTime = zjxt_Cmd.df.parse(dateString);
				
				zs.MaxRepeatedFailureCount = rSet.getInt("MaxRepeatedFailureCount");
				zs.PreparLockSec = rSet.getInt("PreparLockSec");
//				zs.RepeatedFailureCount = rSet.getInt("RepeatedFailureCount");
				zs.RepeatedFailureLockSec = rSet
						.getInt("RepeatedFailureLockSec");
//				zs.SignTagState = rSet.getString("SignTagState");
//				zs.SlipTapLockSec = rSet.getInt("SlipTapLockSec");
				zs.SuccessLockSec = rSet.getInt("SuccessLockSec");
				zs.AdviceLockSec = rSet.getInt("AdviceLockSec");
				zs.FailureLockSec = rSet.getInt("FailureLockSec");
				
//				zs.ActionOutState = rSet.getString("ACTIONOUTSTATE"); //动作次数记录
//				if(zs.ActionOutState == null || zs.ActionOutState.equals("")){
//					zs.ActionOutState = "";
//					zs.ActNumber = 0;
//				}else
//					zs.ActNumber = Integer.parseInt(zs.ActionOutState);
			}
			RefreshSystemSign(stat);
			dbConnection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception("zjxt_Statetable-Refresh->" + e.toString()+" id="+idString);
		}
	}

	private static int MinLockSecond = 5; //最小闭锁时间
	public static boolean IsMysql = true;
	public static void LoadFromDB() throws Exception {
		try {
			Connection dbConnection = zjxt_ConnectionPool.Instance().getConnection();
			IsMysql = (zjxt_ConnectionPool.dbProvider == 2);
			Statement stat = dbConnection.createStatement();
			ResultSet rSet = stat.executeQuery("select t1.*, (t2.FAILURECOUNT+t2.SUCCESSCOUNT) ActNumber from tblElementstate t1 left join tblelementaction t2 on t1.ELEMENTID=t2.ELEMENTID");
			while (rSet.next()) {
				String eid = rSet.getString("ELEMENTID");
				zState zs = StateTable.get(eid);
				if (zs == null)
					continue;
				zs.ControlState = rSet.getString("ControlState");
				if(zs.ControlState == null) zs.ControlState = zjxt_msg.JianYi;
				zs.BelockState = rSet.getString("BelockState");
				if(zs.BelockState == null) zs.BelockState = "";
				zs.ExceptionState = rSet.getString("ExceptionState");
				if(zs.ExceptionState == null) zs.ExceptionState = "";
				zs.FailureLockSec = rSet.getInt("FailureLockSec");
				if(zs.FailureLockSec < MinLockSecond)zs.FailureLockSec = MinLockSecond;
				zs.HandUnlockProtectionState = rSet
						.getString("HandUnlockProtectionState");
				if(zs.HandUnlockProtectionState == null) zs.HandUnlockProtectionState = "";
				zs.AutoUnlockProtectionState = rSet.getString("AutoUnlockProtectionState");
				if(zs.AutoUnlockProtectionState == null) zs.AutoUnlockProtectionState = ""; 
				String dateString = rSet.getString("LockStartTime");
				if (dateString != null && !dateString.equals(""))
					zs.LockStartTime = zjxt_Cmd.df.parse(dateString);
				
				zs.MaxRepeatedFailureCount = rSet
						.getInt("MaxRepeatedFailureCount");
				zs.AdviceLockSec = rSet.getInt("ADVICELOCKSEC");
				zs.PreparLockSec = rSet.getInt("PreparLockSec");
				if(zs.PreparLockSec < MinLockSecond)zs.PreparLockSec = MinLockSecond;
//				zs.RepeatedFailureCount = rSet.getInt("RepeatedFailureCount");
				zs.RepeatedFailureCount = 0; //初始化为0
				zs.RepeatedFailureLockSec = rSet
						.getInt("RepeatedFailureLockSec");
				zs.SignTagState = rSet.getString("SignTagState");
				
				zs.SlipTapLockSec = rSet.getInt("SlipTapLockSec");
				zs.SuccessLockSec = rSet.getInt("SuccessLockSec");
				
				zs.ActionOutState = rSet.getString("ACTIONOUTSTATE"); //动作次数状态
				if(zs.ActionOutState ==null) zs.ActionOutState = ""; 
//				if(zs.ActionOutState == null || zs.ActionOutState.equals("")){
////					zs.ActionOutState = "0";
//					zs.ActionOutState = "";
//					zs.ActNumber = 0;
//				}else
//					zs.ActNumber = Integer.parseInt(zs.ActionOutState);
				zs.ActNumber = rSet.getInt("ActNumber");
				if(zs.FailureLockSec < MinLockSecond)zs.FailureLockSec = MinLockSecond;
				if(zs.PreparLockSec < MinLockSecond)zs.PreparLockSec = MinLockSecond;
				if(zs.SuccessLockSec < MinLockSecond)zs.SuccessLockSec = MinLockSecond;
				if(zs.AdviceLockSec < MinLockSecond)zs.AdviceLockSec = MinLockSecond;
			}
			dbConnection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception("LoadFromDB->" + e.toString());
		}
		
	}

	private static final String update_sql = "UPDATE TBLELEMENTSTATE SET controlstate=?,signtagstate=?,handunlockprotectionstate=?,autounlockprotectionstate=?,exceptionstate=?,actionoutstate=?,belockstate=?,lockstarttime=?, RepeatedFailureCount=? WHERE elementid=?";

	// 1 2 3 4 5 6 7 8 9
	public static void SaveToDB() throws Exception {
		Connection dbConnection = zjxt_ConnectionPool.Instance()
				.getConnection();
		PreparedStatement ps = null;
		ps = dbConnection.prepareStatement(update_sql);
		Iterator<Entry<String, zState>> iter = StateTable.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, zState> entry = iter.next();
			// String elementId = entry.getKey();
			zState val = entry.getValue();
			_PrepareStat(ps, val);
			ps.execute();
		}
		dbConnection.close();
		//ps.close();
	}

	public static void SaveOne(String elementId) throws Exception {
		try {
			Connection dbConnection = zjxt_ConnectionPool.Instance()
					.getConnection();
			PreparedStatement ps = null;
			ps = dbConnection.prepareStatement(update_sql);
			zState val = GetStateItem(elementId);
			_PrepareStat(ps, val);
			ps.execute();
			dbConnection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			zjxt_kernel.mlog.warn("SaveOne->"+e.toString());
		}
		//ps.close();
	}

	private static void _PrepareStat(PreparedStatement ps, zState val)
			throws Exception {
		ps.setString(1, val.ControlState==null?"":val.ControlState);
		ps.setString(2, val.SignTagState==null?"":val.SignTagState);
		ps.setString(3, val.HandUnlockProtectionState==null?"":val.HandUnlockProtectionState);
		ps.setString(4, val.AutoUnlockProtectionState==null?"":val.AutoUnlockProtectionState);
		ps.setString(5, val.ExceptionState==null?"":val.ExceptionState);
		ps.setString(6, val.ActionOutState==null?"":val.ActionOutState);
		ps.setString(7, val.BelockState==null?"":val.BelockState);
		//ps.setString(8, zjxt_Cmd.df.format(val.LockStartTime));
		if(val.LockStartTime == null)
			ps.setTimestamp(8, null);
		else
			ps.setTimestamp(8, new Timestamp(val.LockStartTime.getTime()));
		ps.setInt(9, val.RepeatedFailureCount);
		ps.setString(10, val.ELEMENTID);
		
	}

	public static boolean ContainsId(String idString) {
		return StateTable.containsKey(idString);
	}

	public static zState Add(String elementId) throws Exception {
		if (!StateTable.containsKey(elementId)) {
			zState state = new zState();
			state.ELEMENTID = elementId;
			StateTable.put(state.ELEMENTID, state);
			return state;
		} else {
			throw new Exception("zjxt_StateTable->Add 该id已使用:"+elementId);
		}
	}
	
	public static boolean CanControl(String elementId) throws Exception{
		//boolean b = false;
		String cs = GetControlState(elementId);
		if(cs.equals(cs_Kongzhi))
			return true;
		else {
			zjxt_msg.showwarn("*设备:"+elementId + "不能控制，原因:"+cs);
			return false;
		}
	}
	
	public static String GetControlState(String elementId)throws Exception{
		try {
			String cString = "non";
			PowerSystemResource resource = zjxt_CimBuild.GetById(elementId);
//			int act = zjxt_State.GetActCount(elementId);
//			if(act > Limit.ActNum)
//			{
//				return "当天动作次数用尽 ("+act+")";
//			}
			
			
			
			if(resource.getClass() == zSubControlArea.class){
				return GetOneCtrlState(elementId);
			}
			else if(resource.getClass() == zSubStation.class){
				return GetStationCtrl(resource);
			}
			else if(resource.getClass() == zFeederLine.class){
				return GetFeedLine(resource);
			}
			else {
				if(resource.getClass() == zCapacitor.class){
					zCapacitor compensator = (zCapacitor)resource;
					cString = GetOneCtrlState(compensator.getMrID());
					FeederLine feederLine = compensator.getFeederLine();
					String feederLineCS = getFeederLineCS(feederLine);
					if(feederLineCS.equals(cs_Kongzhi)) {
						return cString;
					} else if(feederLineCS.equals(cs_JanYi)) {
						if(cString.equals(cs_bucanyu)) {
							return cs_bucanyu;
						} else {
							return cs_JanYi;
						}
					} else {
						return cs_bucanyu;
					}
				}
				else if(resource.getClass() == zBreaker.class){
					zBreaker breaker = (zBreaker)resource;
					cString = GetOneCtrlState(elementId);
					FeederLine feederLine = breaker.getFeederLine();
					String feederLineCS = getFeederLineCS(feederLine);
					if(feederLineCS.equals(cs_Kongzhi)) {
						return cString;
					} else if(feederLineCS.equals(cs_JanYi)) {
						if(cString.equals(cs_bucanyu)) {
							return cs_bucanyu;
						} else {
							return cs_JanYi;
						}
					} else {
						return cs_bucanyu;
					}
				}
				else if(resource.getClass() == zDisconnector.class){
					zDisconnector disconnector = (zDisconnector)resource;
					cString = GetOneCtrlState(elementId);
					FeederLine feederLine = disconnector.getFeederLine();
					String feederLineCS = getFeederLineCS(feederLine);
					if(feederLineCS.equals(cs_Kongzhi)) {
						return cString;
					} else if(feederLineCS.equals(cs_JanYi)) {
						if(cString.equals(cs_bucanyu)) {
							return cs_bucanyu;
						} else {
							return cs_JanYi;
						}
					} else {
						return cs_bucanyu;
					}
				}
				else if(resource.getClass() == zSVG.class){
					zSVG obj = (zSVG)resource;
					cString = GetOneCtrlState(elementId);
					FeederLine feederLine = obj.getFeederLine();
					String feederLineCS = getFeederLineCS(feederLine);
					if(feederLineCS.equals(cs_Kongzhi)) {
						return cString;
					} else if(feederLineCS.equals(cs_JanYi)) {
						if(cString.equals(cs_bucanyu)) {
							return cs_bucanyu;
						} else {
							return cs_JanYi;
						}
					} else {
						return cs_bucanyu;
					}
				}
				else if(resource.getClass() == zApf.class){
					zApf obj = (zApf)resource;
					cString = GetOneCtrlState(elementId);
					FeederLine feederLine = obj.getFeederLine();
					String feederLineCS = getFeederLineCS(feederLine);
					if(feederLineCS.equals(cs_Kongzhi)) {
						return cString;
					} else if(feederLineCS.equals(cs_JanYi)) {
						if(cString.equals(cs_bucanyu)) {
							return cs_bucanyu;
						} else {
							return cs_JanYi;
						}
					} else {
						return cs_bucanyu;
					}
				}
				else if(resource.getClass() == zTpunbalance.class){
					zTpunbalance obj = (zTpunbalance)resource;
					cString = GetOneCtrlState(elementId);
					FeederLine feederLine = obj.getFeederLine();
					String feederLineCS = getFeederLineCS(feederLine);
					if(feederLineCS.equals(cs_Kongzhi)) {
						return cString;
					} else if(feederLineCS.equals(cs_JanYi)) {
						if(cString.equals(cs_bucanyu)) {
							return cs_bucanyu;
						} else {
							return cs_JanYi;
						}
					} else {
						return cs_bucanyu;
					}
				}
				else if(resource.getClass() == zTSF.class){
					zTSF obj = (zTSF)resource;
					cString = GetOneCtrlState(elementId);
					FeederLine feederLine = obj.getFeederLine();
					String feederLineCS = getFeederLineCS(feederLine);
					if(feederLineCS.equals(cs_Kongzhi)) {
						return cString;
					} else if(feederLineCS.equals(cs_JanYi)) {
						if(cString.equals(cs_bucanyu)) {
							return cs_bucanyu;
						} else {
							return cs_JanYi;
						}
					} else {
						return cs_bucanyu;
					}
				}
				else if(resource.getClass() == zVoltageRegulator.class){
					zVoltageRegulator obj = (zVoltageRegulator)resource;
					cString = GetOneCtrlState(elementId);
					FeederLine feederLine = obj.getFeederLine();
					String feederLineCS = getFeederLineCS(feederLine);
					if(feederLineCS.equals(cs_Kongzhi)) {
						return cString;
					} else if(feederLineCS.equals(cs_JanYi)) {
						if(cString.equals(cs_bucanyu)) {
							return cs_bucanyu;
						} else {
							return cs_JanYi;
						}
					} else {
						return cs_bucanyu;
					}
				}
				else if(resource.getClass() == zTransformerFormer.class){
					zTransformerFormer obj = (zTransformerFormer)resource;
					cString = GetOneCtrlState(elementId);
					FeederLine feederLine = obj.getFeederLine();
					String feederLineCS = getFeederLineCS(feederLine);
					if(feederLineCS.equals(cs_Kongzhi)) {
						return cString;
					} else if(feederLineCS.equals(cs_JanYi)) {
						if(cString.equals(cs_bucanyu)) {
							return cs_bucanyu;
						} else {
							return cs_JanYi;
						}
					} else {
						return cs_bucanyu;
					}
				}
			}
			return cString;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			String es = "GetControlState->"+e.toString()+" id:"+elementId;
			zjxt_msg.showwarn(es);
			return es;
		}
		
	}
	
	private static String GetStationCtrl(PowerSystemResource resource)throws Exception{
		zSubStation station = (zSubStation)resource;
		String cs = GetOneCtrlState(station.getMrID());
		if(cs == cs_Kongzhi){
			SubControlArea ca = station.getSubStationArea();
			cs = GetOneCtrlState(ca.getMrID());
		}
		return cs;
	}
	
	private static String GetFeedLine(PowerSystemResource resource)throws Exception{
		zFeederLine feederLine = (zFeederLine)resource;
		String fs = GetOneCtrlState(feederLine.getMrID());
		if(fs == cs_Kongzhi){
			SubStation station = feederLine.getSubStation();
			return GetStationCtrl(station);
		}
		return fs;
		
	}
	
	
	private static String GetOneCtrlState(String elementId)throws Exception{
		zState item = StateTable.get(elementId);
		if (item == null)
			throw new Exception("GetOneCtrlState->获取设备状态失败，设备ID="+elementId);
		if(item.ControlState.equals(""))
			throw new Exception("GetOneCtrlState->设备状态为空，设备ID="+elementId);
		if(item.ControlState == null)
			item.ControlState = "";
		return item.ControlState;

	}

	public static String GetLockState(String elementId) throws Exception {
		zState item = StateTable.get(elementId);
		if (item == null)
			throw new Exception("GetLockState->获取设备状态失败，设备ID="+elementId);
		String LockReason = "";
		
		PowerSystemResource psr = zjxt_CimBuild.GetById(elementId); //对于电容器组，要额外考虑组和电容单元的状态。
		if(psr.getClass() == zCapacitor.class){
			zCapacitor comp = (zCapacitor)psr;
			if(comp.IsItem){       //是电容器子组
				zCapacitor group = comp.MyGroup; //取组设备
				LockReason = GetLockState(group.getMrID());  //取电容组状态
			}
		}
//		if (item.HandUnlockProtectionState != null
//				&& !item.HandUnlockProtectionState.equals("")) {
//			LockReason = " 保护(需人工解锁): "+item.HandUnlockProtectionState;
//		}
//		if (item.AutoUnlockProtectionState != null
//				&& item.AutoUnlockProtectionState != "") {
//			LockReason += " 保护(自动解锁): " + item.HandUnlockProtectionState;
//		}
		if (item.ExceptionState != null && !item.ExceptionState.equals("")) {
			LockReason += " 异常状态: " + item.ExceptionState;
		}
		if (item.ActionOutState != null && !item.ActionOutState.equals("")) {
			LockReason += " 动作次数: " + item.ActionOutState;
		}
		if (item.BelockState != null && !item.BelockState.equals("")) {
			LockReason += " 闭锁状态: " + item.BelockState;
		}
		return LockReason;
	}
	
	public static int GetPLockTimeLimit(String elementId){ 
		//取预置（控制）闭锁时间限值 返回预置闭锁秒数
		try{
			zState state = GetStateItem(elementId);
		if (state == null)
			return 0;
			return state.PreparLockSec;
		}catch(Exception e){
			zjxt_msg.showwarn("GetPLockTimeLimit() "+e.toString());
			return 0;
		}
		
	}

	public static zState GetStateItem(String elementId) throws Exception {
/*
//		PowerSystemResource resource = zjxt_CimBuild.GetById(elementId);  //如果是电容器单元则取电容器组的状态。
//		if(resource.getClass() == zCompensator.class){
//			zCompensator compensator = (zCompensator)resource;
//			if(!compensator.IsGroup)
//				elementId = compensator.MyGroup.getMrID();
//		}
*/
		
		if (StateTable.containsKey(elementId))
			return StateTable.get(elementId);
		else {
			//return null;
			throw new Exception("zjxt_StateTable->GetStateItem->错误设备id:"+elementId + " 不在设备表,但出现在状态表或保护表中。");
		}
	}
	
	public static int GetActCount(String elementid)throws Exception{
		zState state = GetStateItem(elementid);
		return state.ActNumber;		
	}

	public static void LoadProtections() {
		Iterator<Entry<String, zState>> iter = StateTable.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, zState> entry = iter.next();
			String elementId = entry.getKey();
			zState val = entry.getValue();
			val.AutoUnlockProtectionState = zjxt_ProtectionTable
					.GetAutoUnlockProtection(elementId);
			val.HandUnlockProtectionState = zjxt_ProtectionTable
					.GetProtection(elementId);
			if (!val.HandUnlockProtectionState.equals("")) { // 需要手动解锁，就闭锁该设备，等待人工解锁。
				val.BelockState = "保护闭锁:" + val.HandUnlockProtectionState; // 只写，不清空。保证只有人工清空（解锁）
			}

		}
	}

	public static class zState {
		public String ELEMENTID; // 元件编号
		public int ELEMENTSTYLE; // 元件类型：设备类型代码
		public String ControlState = ""; // 控制状态：控制状态代码
		public String SignTagState = ""; // 挂牌状态
		public String HandUnlockProtectionState = ""; // 保护状态（手动解锁）
		public String AutoUnlockProtectionState = ""; // 保护状态（自动解锁）
		public String ExceptionState = ""; // 异常状态
		public String ActionOutState = ""; // 动作次数状态
		public String BelockState = ""; // 闭锁状态：闭锁状态代码
		public Date LockStartTime; // 闭锁开始时间
		public int AdviceLockSec = 30; // 建议闭锁时间
		public int PreparLockSec = 15; // 预置闭锁时间
		public int SuccessLockSec = 15; // 成功闭锁时间
		public int FailureLockSec = 30; // 失败闭锁时间
		public int SlipTapLockSec = 3600; // 滑档闭锁时间
		public int RepeatedFailureLockSec = 3600; // 连续失败闭锁时间
		public int RepeatedFailureCount = 0; // 连续失败次数
		public int MaxRepeatedFailureCount = 3; // 最大连续失败次数

		public int ActNumber = 0; // 当天动作次数
		//public int MaxActDaily = 40;
		public Date ActDatetime;

	}
	
	/**
	 * 获取馈线及以上的控制状态
	 * @param feederLine
	 * @return
	 */
	public static String getFeederLineCS(FeederLine feederLine) throws Exception {
		String feederLineCS = GetOneCtrlState(feederLine.getMrID());
		SubStation subStation = feederLine.getSubStation();
//		String subStationCS = GetOneCtrlState(subStation.getMrID());
		SubControlArea subStationArea = subStation.getSubStationArea();
		String subStationAreaCS = GetOneCtrlState(subStationArea.getMrID());
		if(subStationAreaCS.equals(cs_Kongzhi)) {
			if(feederLineCS.equals(cs_Kongzhi)) {
				return cs_Kongzhi;
			} else if(feederLineCS.equals(cs_bucanyu)) {
				return cs_bucanyu;
			} else if(feederLineCS.equals(cs_JanYi)) {
				return cs_JanYi;
			}
		} else if(subStationAreaCS.equals(cs_bucanyu)) {
			return cs_bucanyu;
		} else if(subStationAreaCS.equals(cs_JanYi)) {
			return cs_JanYi;//return cs_bucanyu; debug 2017-4-4
		}
		return cs_bucanyu;
	}

}

package zjxt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.drools.zjxt.kernellib.zjxt_CimBuild;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zCapacitor;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zSVG;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zTransformerFormer;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zVoltageRegulator;
import com.drools.zjxt.kernellib.zjxt_ConnectionPool;
import com.drools.zjxt.kernellib.zjxt_Measure;
import com.softcore.cim.entity.Equipment;
import com.softcore.cim.entity.PowerSystemResource;

import zjxt2_app.zApf;
import zjxt2_app.zTpunbalance;
import zjxt2_app.zjxt_build;



public class zjxt_ProtectionTable {
	
	public static final String PROTECT_INFO = "保护信息";
	
	public static final String ACTION = "动作";
	
	public static final String REVERT = "复归";
	
	public static final String AUTO_UNLOCK = "自动解锁保护";
	
	public static final String HAND_UNLOCK = "手动解锁保护";
	
	private static List<zProtection> pList = new ArrayList<zProtection>();
	
//	private static Map<String, Boolean> lockMap = new HashMap<String, Boolean>();
	
	private static Map<String, List<zProtection>> locks = new HashMap<String, List<zProtection>>();
	
	public static void Clear() {
		pList.clear();
	}

	
	public static zProtection Add(zProtection item)throws Exception{
		pList.add(item);
		return item;
	}
	
	public static void LoadFromDb() throws Exception{
		try {
			Connection dbConnection = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = dbConnection.createStatement();
			ResultSet rSet = stat.executeQuery("select * from tblprotection");
			pList.clear();
			while (rSet.next()) {
				zProtection pt = new zProtection();
				pt.ObjectElementID = rSet.getString("ObjectElementID");
				pt.ID = rSet.getString("id");
				pt.AutoUnlock = rSet.getInt("AutoUnlock");
				pt.AlarmEnable = rSet.getInt("AlarmEnable");
				pt.IsProteced = false;
				pt.NormalValue = rSet.getInt("NormalValue");
				pt.Value = -1;
				pt.YXID = rSet.getString("YXID");
				pt.Name = rSet.getString("Name");
				if(locks.get(pt.ObjectElementID) != null) {
					locks.get(pt.ObjectElementID).add(pt);
				} else {
					List<zProtection> list = new ArrayList<zProtection>();
					list.add(pt);
					locks.put(pt.ObjectElementID, list);
				}
				for(PowerSystemResource power: zjxt_CimBuild.cbList) {
					if(power instanceof zApf) {
						zApf apf = (zApf) power;
						if(apf.getMrID().equals(pt.ObjectElementID)) {
							apf.protectList.add(pt);
						}
					}
					if(power instanceof zCapacitor) {
						zCapacitor com = (zCapacitor) power;
						if(com.getMrID().equals(pt.ObjectElementID)) {
							com.protectList.add(pt);
						}
					}
					if(power instanceof zSVG) {
						zSVG svg = (zSVG) power;
						if(svg.getMrID().equals(pt.ObjectElementID)) {
							svg.protectList.add(pt);
						}
					}
					if(power instanceof zTpunbalance) {
						zTpunbalance tpunbalance = (zTpunbalance) power;
						if(tpunbalance.getMrID().equals(pt.ObjectElementID)) {
							tpunbalance.protectList.add(pt);
						}
					}
					if(power instanceof zTransformerFormer) {
						zTransformerFormer trans = (zTransformerFormer) power;
						if(trans.getMrID().equals(pt.ObjectElementID)) {
							trans.protectList.add(pt);
						}
					}
					if(power instanceof zVoltageRegulator) {
						zVoltageRegulator volReg = (zVoltageRegulator) power;
						if(volReg.getMrID().equals(pt.ObjectElementID)) {
							volReg.protectList.add(pt);
						}
					}
				}
				pList.add(pt);	
				zjxt_msg.show("保护ID:"+ pt.ID + " 保护名称:"+pt.Name + " 设备ID:"+pt.ObjectElementID + " 是否自解锁:"+pt.AutoUnlock+" 保护正常值:"+pt.NormalValue);
			}
			dbConnection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("zjxt_ProtectionTable->LoadFromDB->", e);
		}
	}
	
	
	//private static int AlarmInterval = 60; //告警周期,同一个告警60个刷新周期内不重复告警
	public static void Refresh() throws Exception{
		zjxt_msg.show("Protection refresh...");
		zjxt_Measure measure =zjxt_Measure.Instance();  //直接从量测类中获取YX数据
		for (Iterator<zProtection> iterator = pList.iterator(); iterator.hasNext();) {
			zProtection item = (zProtection) iterator.next();
			item.Value = measure.GetYxValue(item.YXID);
			boolean tmp = item.Value != item.NormalValue;
			if(tmp) {
				if(!item.IsProteced) {
					Equipment equipment = zjxt_build.getEquipmentById(item.ObjectElementID);
					equipment.prop.protectAlarm(item.Name + "(" +(item.AutoUnlock==1? zjxt_ProtectionTable.AUTO_UNLOCK : zjxt_ProtectionTable.HAND_UNLOCK) +")",
							zjxt_ProtectionTable.ACTION);
				}
			}
			if(!tmp) {
				if(item.IsProteced) {
					Equipment equipment = zjxt_build.getEquipmentById(item.ObjectElementID);
					equipment.prop.protectAlarm(item.Name + "("+(item.AutoUnlock==1? zjxt_ProtectionTable.AUTO_UNLOCK : zjxt_ProtectionTable.HAND_UNLOCK)+")", 
								zjxt_ProtectionTable.REVERT);
				}
			}
			item.IsProteced = tmp;
			
//			if(item.IsProteced){
//				if(lockMap.get(item.ObjectElementID) == null) {
//					lockMap.put(item.ObjectElementID, true);
//				} else {
//					lockMap.put(item.ObjectElementID, true || lockMap.get(item.ObjectElementID));
//				}
//				
//////				zjxt_State.SetExceptionLock(item.ObjectElementID,item.Name);
////				if(item.AutoUnlock == 0){  //非自动解锁
//////					zjxt_State.SetProtectLock(item.ObjectElementID, item.Name);
////					zjxt_State.SetProtectLock(item.ObjectElementID, zjxt_State.ls_BaoHu);
////				}else if(item.AutoUnlock == 1){
//////					zjxt_State.SetAutoProtectLock(item.ObjectElementID, item.Name);
////					zjxt_State.SetAutoProtectLock(item.ObjectElementID, zjxt_State.ls_BaoHu);
////				}
//			}else{
//				if(item.AutoUnlock == 1) {
//					if(lockMap.get(item.ObjectElementID) == null) {
//						lockMap.put(item.ObjectElementID, false);
//					} else {
//						lockMap.put(item.ObjectElementID, false || lockMap.get(item.ObjectElementID));
//					}
//				} else if(item.AutoUnlock == 0) {
//					if(lockMap.get(item.ObjectElementID) == null) {
//						lockMap.put(item.ObjectElementID, true);
//					} else {
//						lockMap.put(item.ObjectElementID, true || lockMap.get(item.ObjectElementID));
//					}
//					
//				}
////				if(item.AutoUnlock == 1){
////					zjxt_State.SetAutoProtectLock(item.ObjectElementID,""); //设置为空字符串即可
////				}
//				
//			}
//		}
//		for(Entry<String, Boolean> entry: lockMap.entrySet()) {
//			if(entry.getValue()) {
//				zjxt_State.SetAutoProtectLock(entry.getKey(), zjxt_State.ls_BaoHu);
//				zjxt_State.GetStateItem(entry.getKey()).AutoUnlockProtectionState = zjxt_State.ls_BaoHu;
//			} else {
//				zjxt_State.SetAutoProtectLock(entry.getKey(), "");
//				zjxt_State.GetStateItem(entry.getKey()).AutoUnlockProtectionState = "";
//			}
		}
			
		dealProtectInfo();
	}
	
	/**
	 * 
	 * @Title: dealProtectInfo
	 * @Description: 处理保护信息
	 * @author: lixu
	 */
	public static void dealProtectInfo() throws Exception {
		for(Entry<String, List<zProtection>> entry : locks.entrySet()) {
			String elementId = entry.getKey();
			List<zProtection> protectList = entry.getValue();
			boolean isLock = false; //是否闭锁
			boolean isHandUnlock = false; //是否有手动保护
			for(zProtection protect: protectList) {
				if(protect.IsProteced) {
					isLock = isLock || true;
					isHandUnlock = isHandUnlock || (protect.AutoUnlock == 0);
				}
			}
			if(isLock) {
				if(isHandUnlock) {
					zjxt_State.SetProtectLock(elementId, zjxt_State.ls_BaoHu);
					zjxt_State.SetAutoProtectLock(entry.getKey(), "");
					zjxt_State.GetStateItem(entry.getKey()).HandUnlockProtectionState = zjxt_State.ls_BaoHu;
					zjxt_State.GetStateItem(entry.getKey()).AutoUnlockProtectionState = "";
				} else {
					zjxt_State.SetAutoProtectLock(entry.getKey(), zjxt_State.ls_BaoHu);
					zjxt_State.SetProtectLock(elementId, "");
					zjxt_State.GetStateItem(entry.getKey()).AutoUnlockProtectionState = zjxt_State.ls_BaoHu;
					zjxt_State.GetStateItem(entry.getKey()).HandUnlockProtectionState = "";
				}
			} else {
				if(zjxt_State.GetStateItem(entry.getKey()).AutoUnlockProtectionState.equals(zjxt_State.ls_BaoHu)) {
					zjxt_State.SetAutoProtectLock(entry.getKey(), "");
					zjxt_State.GetStateItem(entry.getKey()).AutoUnlockProtectionState = "";
				}
			}
		}
	}
	
	public static String GetAutoUnlockProtection(String elementId){
		StringBuilder sBuilder = new StringBuilder();
		for (Iterator<zProtection> iterator = pList.iterator(); iterator.hasNext();) {
			zProtection item = (zProtection) iterator.next();
			if(item.ObjectElementID.equals(elementId)){
				if(item.IsProteced && (item.AutoUnlock==1)){
					sBuilder.append(item.Name).append(';');
				}
			}
		}
		return sBuilder.toString();
	}
	
	public static String GetProtection(String elementId){
		StringBuilder sBuilder = new StringBuilder();
		for (Iterator<zProtection> iterator = pList.iterator(); iterator.hasNext();) {
			zProtection item = (zProtection) iterator.next();
			if(item.ObjectElementID.equals(elementId)){
//				if(item.IsProteced && (item.AutoUnlock!=1)){
				if(item.IsProteced){
					sBuilder.append(item.Name).append(';');
				}
			}
		}
		return sBuilder.toString();
	}
	
	
	
	
	
	public static class zProtection {
		   /** 编号
		    * 
		    * @pdOid 63814be4-b75e-4785-86ec-c97f6f2bacda */
		   public String ID;
		   /** 名称
		    * 
		    * @pdOid 6e75abed-5117-4015-9135-efc427f3a538 */
		   public String Name;
		   /** 目标元件编号
		    * 
		    * @pdOid cc4ab9eb-53c1-4d1d-a96a-321b0c787814 */
		   public String ObjectElementID;
		   /** 保护遥信编号
		    * 
		    * @pdOid 5861c54e-043e-47c0-b0b9-5ecf6eee5f4f */
		   public String YXID;
		   /** 保护正常值
		    * 
		    * @pdOid 54be8514-79c3-43ef-814f-bc75a5201368 */
		   public int NormalValue;
		   /** 保护撤销时是否自动解锁
		    * 
		    * @pdOid 7f1705ba-d993-4079-820c-86afb110be26 */
		   public int AutoUnlock;
		   /** 保护动作时是否触发警报
		    * 
		    * @pdOid 43fd341f-520b-4d0d-b844-809147ea4316 */
		   public int AlarmEnable;
		   
		   public int Value;
		   
		   public boolean IsProteced = false;
		   
		   
		   //private int AlarmNum = 0; //告警时间计数;
		}

}

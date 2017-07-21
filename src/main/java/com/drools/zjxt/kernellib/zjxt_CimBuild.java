package com.drools.zjxt.kernellib;

//import java.security.acl.Owner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import zjxt.zjxt_ProtectionTable;
import zjxt.zjxt_State;
import zjxt.zjxt_msg;
import zjxt.zjxt_topo.ControlParam;
import zjxt.zjxt_topo.zNode;
import zjxt2_app.zApf;
import zjxt2_app.zTpunbalance;

import com.drools.zjxt.kernellib.zjxt_Measure.zjxt_yt;
import com.nikhaldimann.inieditor.IniEditor;
import com.softcore.cim.common.CommonListCode;
import com.softcore.cim.entity.Equipment;
import com.softcore.cim.entity.PowerSystemResource;
import com.softcore.cim.entity.block.Conductor;
import com.softcore.cim.entity.block.LineGroup;
import com.softcore.cim.entity.block.connector.BusbarSection;
import com.softcore.cim.entity.block.line.ACLineSegment;
import com.softcore.cim.entity.block.regulating.Capacitor;
import com.softcore.cim.entity.block.regulating.SVG;
import com.softcore.cim.entity.block.regulating.TSF;
import com.softcore.cim.entity.block.regulating.VoltageRegulator;
import com.softcore.cim.entity.block.switchs.Breaker;
import com.softcore.cim.entity.block.switchs.Disconnector;
import com.softcore.cim.entity.container.FeederLine;
import com.softcore.cim.entity.container.SubControlArea;
import com.softcore.cim.entity.container.SubStation;
import com.softcore.cim.entity.container.VoltageLevel;
import com.softcore.cim.entity.transformer.TransformerFormer;


/*
 * 电器设备类声明与构建模块。
 */
public class zjxt_CimBuild {
	
	private static final Logger logger = LoggerFactory.getLogger(zjxt_CimBuild.class);
	public static zjxt_Measure Measure = zjxt_Measure.Instance();
	public static List<PowerSystemResource> cbList = new ArrayList<PowerSystemResource>();
	public static List<List<PowerSystemResource>> list = new ArrayList<List<PowerSystemResource>>();
//	public static Map<String, Float> XMap = new HashMap<String, Float>(); //读取设备至线路首端的电抗配置文件
//	public static boolean canControl = true;
	public static PowerSystemResource GetByName(String name) {
		for (Iterator<PowerSystemResource> iterator = cbList.iterator(); iterator
				.hasNext();) {
			PowerSystemResource obj = (PowerSystemResource) iterator.next();
			if (obj.getName().equals(name)) {
				return obj;
			}
		}
		return null;
	}

	public static PowerSystemResource GetById(String Id) {
		for (Iterator<PowerSystemResource> iterator = cbList.iterator(); iterator
				.hasNext();) {
			PowerSystemResource obj = (PowerSystemResource) iterator.next();
			if (obj.getMrID().equals(Id)) {
				return obj;
			} 		
		}
		return null;
	}

	// 根据配变ID取得关联的电容对象.
	public static zCapacitor GetBytransformId(String id) {
		for (Iterator<PowerSystemResource> iterator = cbList.iterator(); iterator
				.hasNext();) {
			PowerSystemResource obj = (PowerSystemResource) iterator.next();
			if (obj.getClass() != zCapacitor.class)
				continue;
			zCapacitor comp = (zCapacitor) obj;
			if (comp.COMPENSATEPOINTID.equals(id)) {
				return comp;
			}
		}
		return null;
	}

	public static void StatisticRunTime() {
		try {
			zjxt_msg.showwarn("StatisticRunTime...");
			// for (Iterator<PowerSystemResource> iterator = cbList.iterator();
			// iterator
			// .hasNext();) {
			// PowerSystemResource obj = (PowerSystemResource) iterator.next();
			// if (obj.getClass() == zCompensator.class) {
			// zCompensator comp = (zCompensator) obj;
			// String id = comp.getMrID();
			// String state = zjxt_State.GetControlState(id);
			//
			// Connection conn = zjxt_ConnectionPool.Instance()
			// .getConnection();
			// Statement stat = conn.createStatement();
			// String sql = "SELECT * FROM TBLYTPARAM";
			// // ResultSet rSet = stat.executeQuery(sql);
			//
			// conn.close();
			// }
			// }
		} catch (Exception e) {
			zjxt_msg.showwarn("StatisticRunTime->" + e.toString());
		}
	}

	public static zPowerEquipment newpPowerEquipment(String id) {
		zPowerEquipment psr = new zPowerEquipment(id);
		psr.setMrID(id);
		psr.property = new zjxt_Property(psr, zjxt_CimBuild.Measure);
		cbList.add(psr);
		return psr;
	}

	public static zSubControlArea newSubControlArea() {
		zSubControlArea sArea = new zSubControlArea();
		sArea.property = new zjxt_Property(sArea, zjxt_CimBuild.Measure);
		cbList.add(sArea);
		return sArea;
	}

	public static zSubStation newSubStation(zSubControlArea Owner) {
		zSubStation station = new zSubStation();
		station.property = new zjxt_Property(station, zjxt_CimBuild.Measure);
		if (Owner != null)
			Owner.subStations.add(station);
		station.setSubControlArea(Owner);
		cbList.add(station);
		return station;
	}

	public static zBusbarSection newBusbarSection(zFeederLine Owner) {
		zBusbarSection busbarSection = new zBusbarSection();
		busbarSection.property = new zjxt_Property(busbarSection,
				zjxt_CimBuild.Measure);
		if (Owner != null) {
//			Owner.equipments.add(busbarSection);
			busbarSection.setSubStation(Owner.getSubStation());
		}
		cbList.add(busbarSection);
		return busbarSection;
	}

	public static zLineGroup newLineGroup(PowerSystemResource Owner) {
		zLineGroup lineGroup = new zLineGroup();
		if (Owner != null)
			if (Owner.getClass() == zFeederLine.class) {
				zFeederLine fo = (zFeederLine) Owner;
				fo.LineGroups.add(lineGroup);
				lineGroup.setFeederLine(fo);
			} else if (Owner.getClass() == zSubControlArea.class) {
				zSubControlArea so = (zSubControlArea) Owner;
				so.LineGroups.add(lineGroup);

			}

		return lineGroup;
	}

	public static zFeederLine newFeederLine(zSubStation Owner) {
		zFeederLine feederLine = new zFeederLine();
		feederLine.property = new zjxt_Property(feederLine,
				zjxt_CimBuild.Measure);
		feederLine.setSubStation(Owner);
		cbList.add(feederLine);
		return feederLine;
	}

	// 配变
	public static zTransformerFormer newTransformerFormer(zFeederLine Owner) {
		zTransformerFormer tFormer = new zTransformerFormer();
		tFormer.property = new zjxt_Property(tFormer, zjxt_CimBuild.Measure);
		if (Owner != null) {
			Owner.AddTf(tFormer);
			Owner.equipments.add(tFormer);
		}
		tFormer.setFeederLine(Owner);
		tFormer.line = Owner;
		cbList.add(tFormer);
		return tFormer;
	}

	// 调压器
	public static zVoltageRegulator newVoltageRegulator(zFeederLine Owner) {
		zVoltageRegulator vRegulator = new zVoltageRegulator();
		vRegulator.property = new zjxt_Property(vRegulator,
				zjxt_CimBuild.Measure);
		if (Owner != null)
			Owner.equipments.add(vRegulator);
		vRegulator.setFeederLine(Owner);
		vRegulator.line = Owner;
		cbList.add(vRegulator);
		return vRegulator;
	}

	// 电能质量检测仪
	public static zPowerQualityIns newPowerQualityIns(zFeederLine Owner) {
		zPowerQualityIns obj = new zPowerQualityIns();
		obj.property = new zjxt_Property(obj, zjxt_CimBuild.Measure);
		if (Owner != null)
			Owner.equipments.add(obj);
		obj.typeid = 210;
		obj.setFeederLine(Owner);
		cbList.add(obj);
		return obj;
	}

	public static zBreaker newBreaker(zFeederLine Owner) {
		zBreaker breaker = new zBreaker();
		breaker.property = new zjxt_Property(breaker, zjxt_CimBuild.Measure);
		if (Owner != null)
			Owner.equipments.add(breaker);
		breaker.setFeederLine(Owner);
		cbList.add(breaker);
		return breaker;
	}

	public static zACLineSegment newAcLineSegment(zLineGroup Owner) {
		zACLineSegment acLineSegment = new zACLineSegment();
		if (Owner != null)
			Owner.conductores.add(acLineSegment);
		acLineSegment.setLineGroup(Owner);
		cbList.add(acLineSegment);
		return acLineSegment;
	}

	public static zSVG newSvg(zFeederLine Owner) {
		zSVG svg = new zSVG();
		svg.property = new zjxt_Property(svg, zjxt_CimBuild.Measure);
		if (Owner != null)
			Owner.equipments.add(svg);
		svg.setFeederLine(Owner);
		svg.line = Owner;
		svg.typeid = 220;
		cbList.add(svg);
		return svg;
	}

	public static zTSF newTsf(zFeederLine Owner) {
		zTSF tsf = new zTSF();
		tsf.property = new zjxt_Property(tsf, zjxt_CimBuild.Measure);
		if (Owner != null)
			Owner.equipments.add(tsf);
		tsf.setFeederLine(Owner);
		tsf.line = Owner;
		tsf.typeid = 230;
		cbList.add(tsf);
		return tsf;
	}

	// 电容
	public static zCapacitor newCapacitor(zFeederLine Owner, boolean isItem) {
		zCapacitor capa = new zCapacitor();
		capa.property = new zjxt_Property(capa,
				zjxt_CimBuild.Measure);
		if (!isItem) {
			Owner.AddCp(capa);  //增加到馈线电容器列表
			Owner.equipments.add(capa); //增加到馈线设备列表
		}
		capa.setFeederLine(Owner);
		capa.line = Owner;
		if(!isItem)
			cbList.add(capa);
		return capa;
	}

	// 虚拟通用电力设备
	public static class zPowerEquipment extends PowerSystemResource {
		public zjxt_Property property;
		public int EquipmentType = -1;
		private FeederLine feederLine;

		public zPowerEquipment(String id) {
			super();
			this.setMrID(id);
		}

		public FeederLine getFeederLine() {
			return feederLine;
		}

		public void setFeederLine(FeederLine feederLine) {
			this.feederLine = feederLine;
		}
	}

	// 控制区
	public static class zSubControlArea extends SubControlArea {
		public zjxt_Property property;

		public zSubControlArea() {
			super();
			subStations = new ArrayList<SubStation>();
			LineGroups = new ArrayList<LineGroup>();
		}

	}

	// 子站，变电站
	public static class zSubStation extends SubStation {
		public zjxt_Property property;

		public zSubStation() {
			super();
			equipments = new ArrayList<Equipment>();
			busbarSections = new ArrayList<BusbarSection>();
		}
	}

	// 母线
	public static class zBusbarSection extends BusbarSection {
		public zjxt_Property property;
		public String uabycid;

		public zBusbarSection() {
			super();
			// feederLines = new ArrayList<FeederLine>();
			// equipments = new ArrayList<Equipment>();
		}

	}

	// 馈线
	public static class zFeederLine extends FeederLine {
		public zjxt_Property property;
		public List<PowerSystemResource> equipments;
		public List<zTransformerFormer> tfList;
		public List<zCapacitor> cpList;
		public int tag = 180;
		public String busid = "";

		public zFeederLine() {
			super();
			// LineGroups = new ArrayList<LineGroup>();
			tfList = new ArrayList<zTransformerFormer>();
			cpList = new ArrayList<zjxt_CimBuild.zCapacitor>();
			equipments = new ArrayList<PowerSystemResource>();
		}

		public zBusbarSection getStartBus() {
			zBusbarSection bus = (zBusbarSection) GetById(busid);
			return bus;
		}

		public void AddTf(zTransformerFormer psr) {
			tfList.add(psr);
		}

		public void AddCp(zCapacitor psr) {
			cpList.add(psr);
		}

		public PowerSystemResource GetByEquipName(String name) {
			PowerSystemResource result = null;
			for (int i = 0; i < equipments.size(); i++) {
				PowerSystemResource psr = equipments.get(i);
				if (psr.getName().equals(name)) {
					result = psr;
					break;
				}
			}
			return result;
		}

		public PowerSystemResource GetEquipById(String name) {
			PowerSystemResource result = null;
			for (int i = 0; i < equipments.size(); i++) {
				PowerSystemResource psr = equipments.get(i);
				if (psr.getMrID().equals(name)) {
					result = psr;
					break;
				}
			}
			return result;
		}

		public PowerSystemResource GetEquipByIndex(int i) {
			return equipments.get(i);
		}

		public int GetEquipCount() {
			return equipments.size();
		}

		public zTransformerFormer GetTfByIndex(int index) {
			return tfList.get(index);
		}

		public int GetTfCount() {
			return tfList.size();
		}

		public int GetCpCount() {
			return cpList.size();
		}

		public zCapacitor GetCpByIndex(int index) {
			return cpList.get(index);
		}

		public zTransformerFormer GetTfByName(String name) {
			zTransformerFormer result = null;
			for (int i = 0; i < tfList.size(); i++) {
				zTransformerFormer psr = tfList.get(i);
				if (psr.getName().equals(name)) {
					result = psr;
					break;
				}
			}
			return result;
		}

		public zTransformerFormer GetTfById(String name) {
			zTransformerFormer result = null;
			for (int i = 0; i < tfList.size(); i++) {
				zTransformerFormer psr = tfList.get(i);
				if (psr.getMrID().equals(name)) {
					result = psr;
					break;
				}
			}
			return result;
		}

		public zCapacitor GetCpByName(String name) {
			zCapacitor result = null;
			for (int i = 0; i < cpList.size(); i++) {
				zCapacitor psr = cpList.get(i);
				if (psr.getName().equals(name)) {
					result = psr;
					break;
				}
			}
			return result;
		}

		public zCapacitor GetCpById(String name) {
			zCapacitor result = null;
			for (int i = 0; i < cpList.size(); i++) {
				zCapacitor psr = cpList.get(i);
				if (psr.getMrID().equals(name)) {
					result = psr;
					break;
				}
			}
			return result;
		}
	}

	// 电能质量检测仪
	public static class zPowerQualityIns extends PowerSystemResource {
		public zjxt_Property property;
		private FeederLine feederLine;

		public zPowerQualityIns() {
			super();
		}

		public FeederLine getFeederLine() {
			return feederLine;
		}

		public void setFeederLine(FeederLine feederLine) {
			this.feederLine = feederLine;
		}
	}

	// 线路集
	public static class zLineGroup extends LineGroup {
		public zjxt_Property property;
		private FeederLine feederLine;

		public zLineGroup() {
			super();
			conductores = new ArrayList<Conductor>();
		}

		public FeederLine getFeederLine() {
			return feederLine;
		}

		public void setFeederLine(FeederLine feederLine) {
			this.feederLine = feederLine;

		}
	}

	// 开关
	public static class zBreaker extends Breaker {
		public zjxt_Property property;
		private FeederLine feederLine;
		public String YXID;

		public zBreaker() {
			super();
		}

		public FeederLine getFeederLine() {
			return feederLine;
		}

		public void setFeederLine(FeederLine feederLine) {
			if (feederLine == null)
				return;
			this.feederLine = feederLine;
			setSubStation(feederLine.getSubStation());
		}
	}

	// 刀闸
	public static class zDisconnector extends Disconnector {
		public zjxt_Property property;
		private FeederLine feederLine;

		public zDisconnector() {
			super();
		}

		public FeederLine getFeederLine() {
			return feederLine;
		}

		public void setFeederLine(FeederLine feederLine) {
			if (feederLine == null)
				return;
			this.feederLine = feederLine;
			setSubStation(feederLine.getSubStation());
		}
	}

	// 交流线路
	public static class zACLineSegment extends ACLineSegment {

		public zACLineSegment() {
			super();
		}
	}

	// 变压器
	public static class zTransformerFormer extends TransformerFormer {
		public zjxt_Property property;
		public List<zjxt_ProtectionTable.zProtection> protectList = new ArrayList<zjxt_ProtectionTable.zProtection>();
		private FeederLine feederLine;
		public String VLID;

		public zTransformerFormer() {
			super();
		}

		public FeederLine getFeederLine() {
			return feederLine;
		}

		public void setFeederLine(FeederLine feederLine) {
			if (feederLine == null)
				return;
			this.feederLine = feederLine;
			setSubStation(feederLine.getSubStation());
		}

		public int CurTap = -1;
		
		public int getTap() {
			try {
				float tap = property.getyc("tap");
				CurTap = (int) tap;
				return CurTap;
			} catch (Exception e) {
				zjxt_msg.showdebug("zTransformerFormer->getTap() "
						+ e.toString());
				return -1;
			}
		}

		public float getUa() throws Exception {
			try {
				return property.getyc("ua");
			} catch (Exception e) {
				zjxt_msg.showwarn(e.toString() + " " + getName());
				return -1;
			}
		}

		public float getIa() throws Exception {
			return property.getyc("ia");
		}

		public float getP() throws Exception {
			return property.getyc("p");
		}

		public float getQ() throws Exception {
			return property.getyc("q");
		}

		public boolean Upshift(String RuleName, int CurTap, int TargetTap,
				float TargetVol) {
			try {
				String eid = getMrID();
				String ename = getName();
				if (!property.IsLocked()) {
					String DownReason = ename + "电压越下限(" + Limit.lower_v10000
							+ ") 当前档位:" + CurTap + " 升档后档位:" + TargetTap
							+ " 升档后电压预计为:" + TargetVol;
					long aid = zjxt_Cmd.SendAdvice(eid, "遥调", "变压器升档",
							DownReason);
					if (zjxt_State.CanControl(eid)) { // 如果允许实际控制 则下发控制命令
						zjxt_yt yt = zjxt_Measure.Instance().GetYt(eid, "升档");
						zjxt_Cmd.SendYtCmd(eid, yt.ca, yt.czh, yt.dh,
								TargetTap, aid);

					} else {
						zjxt_msg.showwarn(getName() + "没有设置遥调参数.");
						return false;
					}
				}

			} catch (Exception e) {
				zjxt_msg.showdebug("zTransformerFormer->Upshift()"
						+ e.toString());
				return false;
			}
			return true;
		}

		public boolean Downshift(String RuleName, int CurTap, int TargetTap,
				float TargetVol) {
			try {
				String eid = getMrID();
				String ename = getName();
				if (!property.IsLocked()) {
					String DownReason = ename + "电压越上限(" + Limit.upper_v10000
							+ ") 当前档位:" + CurTap + " 降档后档位:" + TargetTap
							+ " 降档后电压预计为:" + TargetVol;
					long aid = zjxt_Cmd.SendAdvice(eid, "遥调", "变压器降档",
							DownReason);
					if (zjxt_State.CanControl(eid)) { // 如果允许实际控制 则下发控制命令
						zjxt_yt yt = zjxt_Measure.Instance().GetYt(eid, "降档");
						zjxt_Cmd.SendYtCmd(eid, yt.ca, yt.czh, yt.dh,
								TargetTap, aid);

					} else {
						zjxt_msg.showwarn(getName() + "没有设置遥调参数.");
						return false;
					}
				}

			} catch (Exception e) {
				zjxt_msg.showdebug("zTransformerFormer->Upshift()"
						+ e.toString());
				return false;
			}
			return true;
		}

		public boolean IsNoBlance() throws Exception { // 三相不平衡判断
			float umax = getMaxVol();
			float umin = getMinVol();
			if (umax - umin > Limit.blacne_v10000) {
				zjxt_State.SetExceptionLock(getMrID(), "三相电压不平衡");
				zjxt_msg.showwarn(MaxVolPh + "相电压:" + umax + "," + MinVolPh
						+ "相电压:" + umin + ",判断三相电压不平衡或数据异常，将闭锁设备 " + getName());
				zjxt_msg.showlock(getMrID());
				return true;
			}
			return false;
		}

		public String MaxVolPh = "";

		public float getMaxVol() throws Exception {
			try {
				float a = property.getyc("ua");
				float b = property.getyc("ub");
				float c = property.getyc("uc");
				float v = -1;
				if (a > b) {
					v = a;
					MaxVolPh = "A";
				} else {
					v = b;
					MaxVolPh = "B";
				}
				if (c > v) {
					v = c;
					MaxVolPh = "C";
				}

				return v;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new Exception("getMaxVol->" + getName() + " id:"
						+ getMrID() , e);
			}
		}

		public String MinVolPh = "";

		public float getMinVol() throws Exception {
			try {
				float a = property.getyc("ua");
				float b = property.getyc("ub");
				float c = property.getyc("uc");
				float v = -1;
				if (a < b) {
					v = a;
					MinVolPh = "A";
				} else {
					v = b;
					MinVolPh = "B";
				}
				if (c < v) {
					v = c;
					MinVolPh = "C";
				}
				return v;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new Exception("getMinVol->" + getName() + " id:"
						+ getMrID() + " ", e);
			}
		}

		//
		public float getCos() throws Exception {
			try {
				float p = property.getyc("p");
				float q = property.getyc("q");
				double v = p / Math.sqrt(p * p + q * q);
				return (float) v;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new Exception("getCos->", e);
			}
		}
	}

	// 调压器
	public static class zVoltageRegulator extends VoltageRegulator {
		public zjxt_Property property;
		public List<zjxt_ProtectionTable.zProtection> protectList = new ArrayList<zjxt_ProtectionTable.zProtection>();
		private FeederLine feederLine;
		public String VLID;

		public zVoltageRegulator() {
			super();
		}

		public FeederLine getFeederLine() {
			return feederLine;
		}

		public void setFeederLine(FeederLine feederLine) {
			if (feederLine == null)
				return;
			this.feederLine = feederLine;
			setSubStation(feederLine.getSubStation());
		}

		public boolean Upshift(String RuleName, int CurTap, int TargetTap,
				float TargetVol) {
			try {
				String eid = getMrID();
				String ename = getName();
				if (!property.IsLocked()) {
					String DownReason = ename + "电压偏差越下限,当前档位:" + CurTap
							+ " 升档后档位:" + TargetTap + " 升档后电压预计为:" + TargetVol;
					long aid = zjxt_Cmd.SendAdvice(eid, "遥调", "调压器升档",
							DownReason);
					if (zjxt_State.CanControl(eid)) { // 如果允许实际控制 则下发控制命令
						zjxt_yt yt = zjxt_Measure.Instance().GetYt(eid, "升档");
						zjxt_Cmd.SendYtCmd(eid, yt.ca, yt.czh, yt.dh,
								TargetTap, aid);

					} else {
						zjxt_msg.showwarn(getName() + "没有设置遥调参数.");
						return false;
					}
				}

			} catch (Exception e) {
				zjxt_msg.showdebug("zVoltageRegulator->Upshift()"
						+ e.toString());
				return false;
			}
			return true;
		}

		public boolean Downshift(String RuleName, int CurTap, int TargetTap,
				float TargetVol) {
			try {
				String eid = getMrID();
				String ename = getName();
				if (!property.IsLocked()) {
					String DownReason = ename + "电压偏差越上限,当前档位:" + CurTap
							+ " 降档后档位:" + TargetTap + " 降档后电压预计为:" + TargetVol;
					long aid = zjxt_Cmd.SendAdvice(eid, "遥调", "调压器降档",
							DownReason);
					if (zjxt_State.CanControl(eid)) { // 如果允许实际控制 则下发控制命令
						zjxt_yt yt = zjxt_Measure.Instance().GetYt(eid, "降档");
						zjxt_Cmd.SendYtCmd(eid, yt.ca, yt.czh, yt.dh,
								TargetTap, aid);

					} else {
						zjxt_msg.showwarn(getName() + "没有设置遥调参数.");
						return false;
					}
				}

			} catch (Exception e) {
				zjxt_msg.showdebug("zVoltageRegulator->Upshift()"
						+ e.toString());
				return false;
			}
			return true;
		}

		public boolean IsNoBlance() throws Exception { // 三相不平衡判断
			float umax = getMaxVol();
			float umin = getMinVol();
			if (umax - umin > Limit.blacne_v10000) {
				zjxt_State.SetExceptionLock(getMrID(), "三相电压不平衡");
				zjxt_msg.showwarn(MaxVolPh + "相电压:" + umax + "," + MinVolPh
						+ "相电压:" + umin + ",判断三相电压不平衡或数据异常，将闭锁设备 " + getName());
				zjxt_msg.showlock(getMrID());
				return true;
			}
			return false;
		}

		public int CurTap = -1;

		public int getTap() {
			try {
				float tap = property.getyc("tap");
				CurTap = (int) tap;
				return CurTap;
			} catch (Exception e) {
				zjxt_msg.showdebug("zVoltageRegulator->getTap() "
						, e);
				return -1;
			}
		}

		public float getUa() throws Exception {
			return property.getyc("ua");
		}

		public float getIa() throws Exception {
			return property.getyc("ia");
		}

		public float getP() throws Exception {
			return property.getyc("p");
		}

		public float getQ() throws Exception {
			return property.getyc("q");
		}

		public String MaxVolPh = "";

		public float getMaxVol() throws Exception {
			try {
				float a = property.getyc("ua");
				float b = property.getyc("ub");
				float c = property.getyc("uc");
				float v = -1;
				if (a > b) {
					v = a;
					MaxVolPh = "A";
				} else {
					v = b;
					MaxVolPh = "B";
				}
				if (c > v) {
					v = c;
					MaxVolPh = "C";
				}

				return v;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new Exception("zVoltageRegulator->getMaxVol->"
						+ getName() + " id:" + getMrID() + " ", e);
			}
		}

		public String MinVolPh = "";

		public float getMinVol() throws Exception {
			try {
				float a = property.getyc("ua");
				float b = property.getyc("ub");
				float c = property.getyc("uc");
				float v = -1;
				if (a < b) {
					v = a;
					MinVolPh = "A";
				} else {
					v = b;
					MinVolPh = "B";
				}
				if (c < v) {
					v = c;
					MinVolPh = "C";
				}
				return v;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new Exception("zVoltageRegulator->getMinVol->"
						+ getName() + " id:" + getMrID() + " ", e);
			}
		}

		public float getCos() throws Exception {
			try {
				float p = property.getyc("p");
				float q = property.getyc("q");
				double v = p / Math.sqrt(p * p + q * q);
				return (float) v;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new Exception("zVoltageRegulator->getCos->"
						, e);
			}
		}

	}

	// 静态无功发生器
	public static class zSVG extends SVG {
		public zjxt_Property property;
		public List<zjxt_ProtectionTable.zProtection> protectList = new ArrayList<zjxt_ProtectionTable.zProtection>();
		private FeederLine feederLine;
		public String VLID;

		// public String OPERATIONMODEYXID; // 运行模式 0： 功率因数模式， 1：恒电压模式
		// public String RUNNINGSTATEYXID; // 运行状态 投入/退出
		// public String CONTROLMODEYXID; // 控制模式 远方/就地
		// public String FAULTSTATEYXID; // 故障状态
		// public String BREAKERONYXID; // 柜内开关状态 判断设备是否可运行
		// public String COMBINEDMODEYXID; // 联合运行模式；0为独立，1为联合。
		// public String MASTERMODEYXID; // 主从模式：0为从机，1为主机。

		// public String UAYCID; // Ua电压遥测ID

		public zSVG() {
			super();
		}

		public FeederLine getFeederLine() {
			return feederLine;
		}

		public void setFeederLine(FeederLine feederLine) {
			if (feederLine == null)
				return;
			this.feederLine = feederLine;
			setSubStation(feederLine.getSubStation());
		}
		//
		// public float[] LoadValues = new float[288];
		// private Date lastForecastTime = null;

		// public int GetLoadForecast() { // return -1 error; 0 无预测; 1 负荷爬升 ; 2
		// // 负荷下降; 3 负荷稳定
		// try {
		// Date cur = new Date();
		// if (lastForecastTime != null) {
		// long between = (cur.getTime() - lastForecastTime.getTime()) / 1000;
		// if (between <= 3600)
		// return -1; // 每小时仅预测一次。
		// }
		// lastForecastTime = cur;
		// Connection connection = zjxt_ConnectionPool.Instance()
		// .getConnection();
		// Statement statement = connection.createStatement();
		// String sql =
		// "select t.loadforecastlist from tblloadforecast t where t.targetelementid='"
		// + getMrID() + "'";
		// ResultSet rSet = statement.executeQuery(sql);
		// String str = "";
		// while (rSet.next()) {
		// str = rSet.getString(1);
		// }
		// connection.close();
		// if (str.equals("")) {
		// return 0;
		// }
		// String[] sa = str.split(";");
		// String[] sa2;
		// for (int i = 0; i < sa.length; i++) {
		// sa2 = sa[i].split("=");
		// LoadValues[i] = Float.parseFloat(sa2[1]);
		// }
		//
		// int dm = (new Date()).getMinutes(); // 当前时间折算分钟数
		// dm /= 5;
		// float p60 = 0; // 60分钟历史平均负荷
		// for (int i = dm; i <= dm + 12; i++)
		// // 每5分钟一个点，i代表5分钟间隔
		// p60 += LoadValues[i];
		// p60 /= 12;
		//
		// float f = p60 * 0.15f;
		// if (Math.abs(p60 - LoadValues[dm]) < f
		// && Math.abs(p60 - LoadValues[dm + 12]) < f)
		// return 3;
		// else {
		// if (LoadValues[dm] < p60 && LoadValues[dm + 12] > p60)
		// return 1;
		// if (LoadValues[dm] > p60 && LoadValues[dm + 12] < p60)
		// return 2;
		// }
		// } catch (Exception e) {
		// zjxt_msg.showwarn("zSVG->GetLoadForecast()->" + e.toString());
		// return -1;
		// }
		// return -1;
		// }
	}

	// 无源滤波器
	public static class zTSF extends TSF {
		// public ArrayList<String> CapaIdList = new ArrayList<String>();
		public String item1, item2, item3, item4;
		public zjxt_Property property;
		public List<zjxt_ProtectionTable.zProtection> protectList = new ArrayList<zjxt_ProtectionTable.zProtection>();
		private FeederLine feederLine;
		public String h3ycid, h5ycid, h7ycid, h9ycid, uaid;
		public String tqModeYcid; // 投切模式

		public FeederLine getFeederLine() {
			return feederLine;
		}

		public void setFeederLine(FeederLine feederLine) {
			if (feederLine == null)
				return;
			this.feederLine = feederLine;
			setSubStation(feederLine.getSubStation());
		}
	}

	// 补偿电容
	public static class zCapacitor extends Capacitor {
		public zjxt_Property property;
		public List<zjxt_ProtectionTable.zProtection> protectList = new ArrayList<zjxt_ProtectionTable.zProtection>();
		private FeederLine feederLine;
		public String SCHEMEID; // 方案ID 某些项目(祥云)用来区别虚拟电容。
		public String COMPENSATEPOINTID; // 补偿点设备ID，通常为关联配变ID
		public String SWITCHYXID; // 电容开关遥信ID
		public boolean IsYT = false; // 是否是遥调型的电容设备。
		public boolean HasItems = false; // 是否有电容器子组，如果是则具体控制在其UnitList中的具体电容器上。
		public boolean IsItem = false;  //是否是电容子组。
		//public String GroupId = "-1"; // 电容器组id,如果为-1表示是当前电容是电容器组，否则值为电容器单元的上级电容组的id
		public List<zCapacitor> ItemList;
		public zCapacitor MyGroup = null;
		
		public String itemType = ""; // 补偿方式：共补,A,B,C
		
		public float RATEDCAPACITY; // 容量
		public String VLID = ""; // 电压等级ID
		// private float weighting = 0.0f; // 计算用权重
		public float calc_vol = 0f; // 预算后电压
		
		
		
		
		
		public zCapacitor() {
			super();
			ItemList = new ArrayList<zjxt_CimBuild.zCapacitor>();
		}

		public void addItem(zCapacitor item) {
			ItemList.add(item);
			item.HasItems = false;
			item.MyGroup = this;
		}
		
		public zCapacitor getItem(String ItemId){
			for(zCapacitor c : ItemList){
				if(c.Id.equals(ItemId)){
					return c;
				}
			}
			return null;
		}
		
		/**
		 * switchStat 开关状态
		 * @return 返回一个可以开关状态等于switchStat值的电容子组
		 */
		public zCapacitor getActItem(int switchStat) {
			if (IsItem)
				return null;
		    	for(int i=0;i<ItemList.size();i++){
		    		zCapacitor cap = ItemList.get(i); 			
		    		if(cap.property.getyxById(cap.SWITCHYXID) == switchStat){
		    			return cap;
		    		}
		    	}
		    	return null;
		}

//		public int GetSwitch() {
//			if (SWITCHID == null) {
//				zjxt_msg.showwarn(getName() + " id:" + getMrID()
//						+ " SWITCHID未填写.");
//				return -1;
//			}
//			zjxt_CimBuild.zBreaker breaker = (zjxt_CimBuild.zBreaker) zjxt_CimBuild
//					.GetById(SWITCHID);
//			if (breaker == null) {
//				zjxt_msg.showwarn(getName() + " 电容关联开关为空. 开关id:" + SWITCHID);
//				return -1;
//			}
//			return Measure.GetYxValue(breaker.YXID);
//		}

		public FeederLine getFeederLine() {
			return feederLine;
		}

		public void setFeederLine(FeederLine feederLine) {
			if (feederLine == null)
				return;
			this.feederLine = feederLine;
			setSubStation(feederLine.getSubStation());
		}
		
	}
	
	public static void checkDeadData() {
		try {
			for(PowerSystemResource p :cbList) {
				if(p instanceof zVoltageRegulator) {
					p.hasDeadData = p.prop.isDeadData("OUTPUTUABYCID") || 
							p.prop.isDeadData("LINEIYCID");
					
				} else if(p instanceof zCapacitor) {
					p.hasDeadData = p.prop.isDeadData("UYCID") ||
							p.prop.isDeadData("IYCID") ||
							p.prop.isDeadData("QCYCID") ||
							p.prop.isDeadData("QYCID");
				} else if(p instanceof zSVG) {
					p.hasDeadData = p.prop.isDeadData("UYCID") ||
							p.prop.isDeadData("PYCID") ||
							p.prop.isDeadData("IYCID") ||
							p.prop.isDeadData("QYCID");
				} else if(p instanceof zTransformerFormer) {
					p.hasDeadData = p.prop.isDeadData("UYCID") ||
							p.prop.isDeadData("IYCID") ||
							p.prop.isDeadData("PYCID") ||
							p.prop.isDeadData("IYCID") ||
							p.prop.isDeadData("QYCID");
				} else if(p instanceof zApf) {
					p.hasDeadData = p.prop.isDeadData("UYCID") ||
							p.prop.isDeadData("IYCID") ||
							p.prop.isDeadData("QYCID") ||
							p.prop.isDeadData("PYCID") ||
							p.prop.isDeadData("QCYCID");
				} else if(p instanceof zTpunbalance) {
					p.hasDeadData = p.prop.isDeadData("UYCID") ||
							p.prop.isDeadData("IYCID") ||
							p.prop.isDeadData("QYCID") ||
							p.prop.isDeadData("PYCID") ||
							p.prop.isDeadData("QCYCID");
				}
				
				if(p.hasDeadData)
				{
					p.prop.SetAlarm("检测到"+p.getName()+"存在十分钟未刷新的量测!");
				}
			}
		} catch(Exception e) {
			zjxt_msg.showwarn(e.getMessage());
		}
	}
	
	public static void calcUabc(zNode n){
		n.Uavg = (n.UA+n.UB+n.UC)/3;
		if(n.UA>=n.UB && n.UA>=n.UC){
			n.Umax = n.UA;
			n.UmaxName = "A";
		}
		else if(n.UB>=n.UA && n.UB>=n.UC){
			n.Umax = n.UB;
			n.UmaxName = "B";
		}
		else {
			n.Umax = n.UC;
			n.UmaxName = "C";
		}
		
		if(n.UA<=n.UB && n.UA<=n.UC){
			n.Umin = n.UA;
			n.UminName = "A";
		}
		else if(n.UB<=n.UA && n.UB <= n.UC){
			n.Umin = n.UB;
			n.UminName = "B";
		}
		else {
			n.Umin = n.UC;
			n.UminName = "C";
		}
	}
	
	public static void refreshNodeMesure() {
		try {
			for(PowerSystemResource p :cbList) {
				if(p instanceof zVoltageRegulator) {
					p.controlState = p.prop.CanControl();
					//p.PF = p.prop.getyc("PFYCID");
					p.U = p.prop.getyc("OUTPUTUABYCID");
					p.I = p.prop.getyc("LINEIYCID");
					p.currentStep = (int)p.prop.getyc("TAPCHANGERYCID");
					p.loadFactor = Math.sqrt(3) * p.U * p.I / p.capacity / 1000;
//					if(XMap.get(p.getMrID()) != null && XMap.get(p.getMrID())!=0.0f) {
//						if(XMap.get(p.getMrID())!=0.0f) {
//							p.X  = XMap.get(p.getMrID());
//						}
//					}
//					p.stepvoltageincrement = ((zVoltageRegulator)p).stepvoltageincrement;
					p.kind = CommonListCode.VOLTAGE_KIND;
					
				} else if(p instanceof zCapacitor) {
					if(((zCapacitor) p).vlid==10000) {
						p.X = 5;
					}
					p.controlState = p.prop.CanControl();
					p.U = p.prop.getyc("UYCID");
					p.I = p.prop.getyc("IYCID");
					p.Qc = p.prop.getyc("QCYCID");
					p.PF = p.prop.getyc("PFYCID");
					p.Q = p.prop.getyc("QYCID");
					p.workMode = p.prop.getyc("WORKMODEYCID");
					p.TargetPF = p.prop.getyc("TARGETPFYCID");
					if(p.PF >= 1)
					{
						if(((zCapacitor) p).vlid==10000) {
							p.P = (float) (Math.sqrt(3)*p.U*p.I*p.PF) / 1000;
						} else {
							p.P = 3 * p.U * p.I * p.PF / 1000;
						}
					} else {
						p.P = (float) (p.Q / Math.tan(Math.acos(p.PF)));
					}
					
//					p.capacity = (Double) p.get("RATEDCAPACITY");
//					if(XMap.get(p.getMrID()) != null) {
//						if(XMap.get(p.getMrID())!=0.0f) {
//							p.X  = XMap.get(p.getMrID());
//						}
//					}
					if(p.capacity-p.Qc == 0) {
						p.Qm = 0;
					} else if(p.Qc == 0) {
						p.Qm = p.capacity/2;
					} else {
						p.Qm = p.capacity/2;
					}
//					List<zCompensator> unitList = ((zCompensator)p).UnitList;
//					for(int i=0; i<unitList.size(); i++) {
//						if(unitList.get(i).property.getyxById(unitList.get(i).SWITCHID) == 0) {
//							p.Qm = p.Qm + (Double)unitList.get(i).get("RATEDCAPACITY");
//						}
//						unitList.get(i).prop = unitList.get(i).property; 
//					}
					p.kind = CommonListCode.REACTIVE_POWER_KIND;
				} else if(p instanceof zSVG) {
					p.controlState = p.prop.CanControl();
					p.U = ((zSVG)p).property.getyc("UYCID");
					p.P = p.prop.getyc("PYCID");
					p.I = p.prop.getyc("IYCID");
					p.Q = p.prop.getyc("QYCID");
					p.PF = p.prop.getyc("PFYCID");
					p.TargetPF = p.prop.getyc("TARGETPFYCID");
					p.Qc = p.prop.getyc("QCYCID");
					p.Qm = (Double)p.get("MAXRATEDCAPACITY")-p.Qc;
					p.minratedCapacity = (double) p.get("MINRATEDCAPACITY");
//					if(XMap.get(p.getMrID()) != null) {
//						if(XMap.get(p.getMrID())!=0.0f) {
//							p.X  = XMap.get(p.getMrID());
//						}
//					}
					p.kind = CommonListCode.REACTIVE_POWER_KIND;
				} else if(p instanceof zTransformerFormer) {
					p.controlState = p.prop.CanControl();
					p.U = p.prop.getyc("UYCID");
					p.UA = p.prop.getyc("UAYCID");
					p.UB = p.prop.getyc("UBYCID");
					p.UC = p.prop.getyc("UCYCID");
					calcUabc(p);
					
					p.Q = p.prop.getyc("QYCID");
					p.loadFactor = Math.sqrt(Math.pow(p.P, 2)+Math.pow(p.Q, 2)) /p.capacity;
					p.currentStep = (int)p.prop.getyc("TAPCHANGERYCID");
//					p.stepvoltageincrement = ((zTransformerFormer)p).stepvoltageincrement;
//					if(XMap.get(p.getMrID()) != null) {
//						if(XMap.get(p.getMrID())!=0.0f) {
//							p.X  = XMap.get(p.getMrID());
//						}
//					}
					p.kind = CommonListCode.VOLTAGE_KIND;
				} else if(p instanceof zApf) {
					p.controlState = p.prop.CanControl();
					p.U = p.prop.getyc("UYCID");
					p.I = p.prop.getyc("IYCID");
					p.Q = p.prop.getyc("QYCID");
					p.PF = p.prop.getyc("PFYCID");
					p.thdu = p.prop.getyc("THDUYCID");
					p.thdi = p.prop.getyc("THDIYCID");
					p.TargetPF = p.prop.getyc("TARGETPFYCID");
					p.workMode = p.prop.getyc("WMYCID");
//					double ratedvoltage = (Double) p.get("RATEDVOLTAGE");
					double basicVoltage = p.voltage.getBasicVoltage();
					double ratedcompensationi =  (Double) p.get("RATEDCOMPENSATIONI");
					p.P = p.prop.getyc("PYCID");
					p.Qc = p.prop.getyc("QCYCID");
					p.Qm = (3*basicVoltage*ratedcompensationi/1000)-p.Qc;
//					if(XMap.get(p.getMrID()) != null) {
//						if(XMap.get(p.getMrID())!=0.0f) {
//							p.X  = XMap.get(p.getMrID());
//						}
//					}
					p.minratedCapacity = (3*basicVoltage*ratedcompensationi/1000)*(-1);
					p.kind = CommonListCode.REACTIVE_POWER_KIND;
				} else if(p instanceof zTpunbalance) {
					p.controlState = p.prop.CanControl();
					p.U = p.prop.getyc("UYCID");
					p.I = p.prop.getyc("IYCID");
					p.Q = p.prop.getyc("QYCID");
					p.P = p.prop.getyc("PYCID");
					p.PF = p.prop.getyc("PFYCID");
					p.Qc = p.prop.getyc("QCYCID");
					p.sxbphd = p.prop.getyc("SXBPHDYCID");
					p.TargetPF = p.prop.getyc("TARGETPFYCID");
					p.workMode = p.prop.getyc("WMYCID");
					p.minratedCapacity = (double) p.get("MINCOMPENSATION");
					p.Qm = (float)((Double) p.get("MAXCOMPENSATION")-p.Qc);
//					if(XMap.get(p.getMrID()) != null) {
//						if(XMap.get(p.getMrID())!=0.0f) {
//							p.X  = XMap.get(p.getMrID());
//						}
//					}
					p.kind = CommonListCode.REACTIVE_POWER_KIND;
				}
			}
		} catch(Exception e) {
			zjxt_msg.showwarn("refreshNodeMesure()->", e);
		}
		
	}
	
	public static Equipment getEquipmentById(String id) {
		for(PowerSystemResource p : cbList) {
			if(p.getMrID().equals(id)) {
				return (Equipment) p;
			}
		}
		return null;
	}
	
	/**
	 * 读取动态电抗值存入XMap
	 */
//	public static void loadX() {
//		clearX();
//		IniEditor editor = new IniEditor();
//		File file = new File(System.getProperty("user.dir")+File.separator+"equipmentX.ini");
//		try {
//			editor.load(file);
//			List<String> optionNames = editor.optionNames("equipment-X");
//			for(int i=0; i<optionNames.size(); i++) {
//				XMap.put(optionNames.get(i), Float.parseFloat(editor.get("equipment-X", optionNames.get(i))));
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * 修改电抗值
	 * @param key
	 * @param value
	 */
	public static void writeX(String key, Float value) {
		IniEditor editor = new IniEditor();
		File file = new File(System.getProperty("user.dir")+"\\equipmentX.ini");
		try {
			editor.load(file);
			editor.set("equipment-X", key, value.toString());
			editor.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除无效的键值对
	 */
	public static void clearX() {
		IniEditor editor = new IniEditor();
		File file = new File(System.getProperty("user.dir")+"\\equipmentX.ini");
		try {
			editor.load(file);
			List<String> optionNames = editor.optionNames("equipment-X");
			for(int i=0; i<optionNames.size(); i++) {
				for(int j=0; j<cbList.size(); j++) {
					if(optionNames.get(i).equals(cbList.get(j).getMrID())) {
						continue;
					}
					editor.remove("equipment-X", optionNames.get(i));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//检查遥控遥调执行结果
	public static void checkYKYTResult() throws Exception {
		zjxt_msg.show("检查调控反馈信息...");
		for(Object p: cbList) {
			if(p instanceof zFeederLine){
				zFeederLine f = (zFeederLine)p;			
				zjxt_msg.show("【{}】控制状态：{}", f.getName(), f.property.CanControl()); 
			}
			if(p instanceof Equipment) {
				if(p instanceof zBusbarSection) {
					continue;
				}

				Equipment e = (Equipment)p;
				String name = e.getName();
				String cs = null;
				if(e.prop == null)
				{
					zjxt_msg.showwarn("【{}】属性为空！", name);
					continue;
				}
				else
				{
					cs = e.prop.CanControl();
					zjxt_msg.show("【{}】控制状态：{}", name, cs);
				}
				
				
				if(p instanceof zCapacitor) {
					zCapacitor cap = (zCapacitor)p;
					float pf = cap.TargetPF;
	    			//int sonoff = cap.property.getyxById(cap.SWITCHID);
	    			//zjxt_msg.show(cap.getName() + ":" + sonoff);
					//int ch = cap.property.CheckTarget(30,sonoff);  //设定反馈检查间隔，当30秒后，检查这个电容开关开合状态是否发生变化。
					
					if(cs.equals(zjxt_State.cs_Kongzhi)) { //当且仅当设备状态为“控制”，才有反馈。 
						int ch = cap.property.CheckTarget(200,pf);
						zjxt_msg.show(name + " 获取目标执行反馈:"+ch);
						if(ch == 1) {
							zjxt_msg.show(name+" 上次命令执行成功");
//							caculateX((Equipment)p);
							zjxt_Cmd.updateCmd(((Equipment) p).getMrID(), 5);
//							cap.property.AddSelfActNum(true);
							
						} else if(ch == 0) {
							zjxt_msg.show(name+" 上次命令执行失败.");
							zjxt_Cmd.updateCmd(((Equipment) p).getMrID(), 6);
//							cap.property.AddSelfActNum(false);
						} else if(ch == 2) {
							zjxt_msg.show("等待上次命令执行结果.");
						}
					}
	    		}
	    		if(p instanceof zVoltageRegulator) {
	    			int tap = ((zVoltageRegulator)p).currentStep;
					
					if(cs.equals(zjxt_State.cs_Kongzhi)){
						int ch = ((zVoltageRegulator)p).property.CheckTarget(200,tap);
						zjxt_msg.show(name + " 获取目标执行反馈:"+ch);
						if(ch == 1){
							zjxt_msg.show(name+" 命令执行成功");
							zjxt_Cmd.updateCmd(((Equipment) p).getMrID(), 5);
//							((zVoltageRegulator)p).property.AddSelfActNum(true);
						}else if(ch == 0){
							zjxt_msg.show(name+" 命令执行失败.");
							zjxt_Cmd.updateCmd(((Equipment) p).getMrID(), 6);
//							((zVoltageRegulator)p).property.AddSelfActNum(false);
						}else if(ch == 2){
							zjxt_msg.show(name+" 等待命令执行结果.");
						}
					}
	    		}
	    		if(p instanceof zSVG) {
	    			float pf = ((zSVG)p).TargetPF;
					
					if(cs.equals(zjxt_State.cs_Kongzhi)){
						int ch = ((zSVG)p).property.CheckTarget(200,pf);
						zjxt_msg.show(name + " 获取目标执行反馈:"+ch);
						if(ch == 1){
							zjxt_msg.show(name+" 命令执行成功");
//							caculateX((Equipment)p);
							zjxt_Cmd.updateCmd(((Equipment) p).getMrID(), 5);
//							((zSVG)p).property.AddSelfActNum(true);
						}else if(ch == 0){
							zjxt_msg.show(name+" 命令执行失败.");
							zjxt_Cmd.updateCmd(((Equipment) p).getMrID(), 6);
//							((zSVG)p).property.AddSelfActNum(false);
						}else if(ch == 2){
							zjxt_msg.show(name+" 等待命令执行结果.");
						}
					}
	    		}
	    		if(p instanceof zTransformerFormer) {
	    			int tap = ((zTransformerFormer)p).currentStep;
					
					if(cs.equals(zjxt_State.cs_Kongzhi)){
						int ch = ((zTransformerFormer)p).property.CheckTarget(200,tap);
						zjxt_msg.show(name + " 获取目标执行反馈:"+ch);
						if(ch == 1){
							zjxt_msg.show(name+" 命令执行成功");
//							caculateX((Equipment)p);
							zjxt_Cmd.updateCmd(((Equipment) p).getMrID(), 5);
//							((zTransformerFormer)p).property.AddSelfActNum(true);
						}else if(ch == 0){
							zjxt_msg.show(name+" 命令执行失败.");
							zjxt_Cmd.updateCmd(((Equipment) p).getMrID(), 6);
//							((zTransformerFormer)p).property.AddSelfActNum(false);
						}else if(ch == 2) {
							zjxt_msg.show(name+" 等待命令执行结果.");
						}
					}
	    		}
	    		if(p instanceof zApf) {
	    			float targetPf = ((zApf)p).TargetPF;
					
					if(cs.equals(zjxt_State.cs_Kongzhi)){
						int ch = ((zApf)p).property.CheckTarget(200,targetPf);
						zjxt_msg.show(name + " 获取目标执行反馈:"+ch);
						if(ch == 1) {
							zjxt_msg.show(name+" 命令执行成功");
//							caculateX((Equipment)p);
							zjxt_Cmd.updateCmd(((Equipment) p).getMrID(), 5);
//							((zApf)p).property.AddSelfActNum(true);
						}else if(ch == 0){
							zjxt_msg.show(name+" 命令执行失败.");
							zjxt_Cmd.updateCmd(((Equipment) p).getMrID(), 6);
//							((zApf)p).property.AddSelfActNum(false);
						}else if(ch == 2){
							zjxt_msg.show(name+" 等待命令执行结果.");
						}
					}
	    		}
	    		if(p instanceof zTpunbalance) {
	    			float pf = ((zTpunbalance)p).TargetPF;
					
					if(cs.equals(zjxt_State.cs_Kongzhi)){
						int ch = ((zTpunbalance)p).property.CheckTarget(200,pf);
						zjxt_msg.show(name + " 获取目标执行反馈:"+ch);
						if(ch == 1){
							zjxt_msg.show(name+" 命令执行成功");
//							caculateX((Equipment)p);
							zjxt_Cmd.updateCmd(((Equipment) p).getMrID(), 5);
//							((zTpunbalance)p).property.AddSelfActNum(true);
						}else if(ch == 0){
							zjxt_msg.show(name+" 命令执行失败.");
							zjxt_Cmd.updateCmd(((Equipment) p).getMrID(), 6);
//							((zTpunbalance)p).property.AddSelfActNum(false);
						}else if(ch == 2){
							zjxt_msg.show(name+" 等待命令执行结果.");
						}
					}
	    		}
	    	}
		}
	}
	
	//计算电抗值
	public static void caculateX(Equipment e) {
		if(e instanceof zCapacitor) {
			e.X = (float)(Math.abs(e.U-e.oldU)*10.5/(((zCapacitor) e).capacity/2));
		} else if(e instanceof zVoltageRegulator) {
			e.X = (float)(Math.abs(e.U-e.oldU)*10.5/(e.P*(Math.tan(Math.acos(e.oldPF))-Math.tan(Math.acos(e.PF)))));
		} else if(e instanceof zTransformerFormer) {
			e.X = (float)(Math.abs(e.U-e.oldU)/220*10000*10.5/(e.P*(Math.tan(Math.acos(e.oldPF))-Math.tan(Math.acos(e.PF)))));
		} else {
			e.X = (float)(Math.abs(e.U-e.oldU)*Math.sqrt(3)/e.vlid*10000*10.5/(e.P*(Math.tan(Math.acos(e.oldPF))-Math.tan(Math.acos(e.PF)))));
		}
		
		Math.round(e.X);
		zjxt_CimBuild.writeX(e.getMrID(), e.X); //更新电抗值
		zjxt_msg.show(e.getName() + "oldapf:" + e.oldPF);
		zjxt_msg.show(e.getName() + "oldU:" + e.oldU);
		zjxt_msg.show(e.getName() + "电抗：" + e.X);
	}
	
	/**
	 * 
	 * @Title: filterMeasure
	 * @Description: 量测过滤
	 * @author: lixu
	 * @param e
	 * @return boolean
	 * 	返回电压是否量测错误
	 */
	public static boolean filterMeasure(Equipment e) {
		VoltageLevel voltage = e.voltage;
		String Name = e.getName();
//		boolean isVolError = false;
		try {
			if(e instanceof zCapacitor ||
		    		e instanceof zVoltageRegulator) {
				if("0.38kv".equals(voltage.getName())) { //低压 线电压转相电压
					if(e.U*Math.sqrt(3)>voltage.getHighVoltageLimit() ||
				    		e.U*Math.sqrt(3)<voltage.getLowVoltageLimit()) {
				    		e.prop.SetAlarm("【{}】当前电压:{}V,判断为量测异常,请人工检查!", Name, e.U);
				    		e.prop.SetException("电压量测异常");
				    		e.isMeasureError = true;
				    		e.isVolError = true;
				    		return true;
				    	} else {
				    		e.isMeasureError = false;
				    		e.isVolError = false;
				    	}
				} else {
					if(e.U>voltage.getHighVoltageLimit() ||
				    		e.U<voltage.getLowVoltageLimit()) {
				    		e.prop.SetAlarm("【{}】当前电压:{}V,判断为量测异常,请人工检查!", Name, e.U);
				    		e.prop.SetException("电压量测异常");
				    		e.isMeasureError = true;
				    		e.isVolError = true;
				    		return true;
				    	} else {
				    		e.isMeasureError = false;
				    		e.isVolError = false;
				    	}
				}
	    		
//	    		if(e.I <= 0) {
//	    			e.prop.SetAlarm("{}当前电流：{}A,判断为量测异常,请人工检查!", e.getName(), e.I);
//	    			e.isMeasureError = true;
//	    			return e.isVolError;
//	    		}
	    		if(e instanceof zCapacitor) { //针对电容器判断无功、功率因数量测的正确性
	    			double tmp = 0.0;
	    			if(e.vlid==10000) {
	    				if(e.PF == 1 && Math.abs(e.Q)<=0.5)
	    				{
	    					e.isMeasureError = false;
	    					return false;
	    				}
	    				tmp = Math.abs(e.Q/(Math.sqrt(3)*e.U*e.I*Math.sqrt(1-Math.pow(e.PF, 2))) * 1000);
	    			} else {
	    				if(e.PF == 1 && Math.abs(e.Q)<=0.5)
	    				{
	    					e.isMeasureError = false;
	    					return false;
	    				}
	    				tmp = Math.abs(e.Q/(3*e.U*e.I*Math.sqrt(1-Math.pow(e.PF, 2))) * 1000);
	    			}
	    			zjxt_msg.show("【{}】验证无功、功率因数量测的正确性值(0.8~1.2范围内为正常)：{}", Name,String.format("%.2f", tmp));
	    			if(0.8>=tmp || tmp>=1.2) {
	    				zjxt_msg.showwarn("【{}】当前无功:{}kVar,功率因数:{},与当前电压{}V、电流{}A不对应!", Name, e.Q, e.PF, e.U, e.I);
	    				e.isMeasureError = true;
	    				return e.isVolError;
	    			}
	    		}
	    	} else if(e instanceof zTransformerFormer) {
	    		if(e.U>=280 ||
		    		e.U<=150) {
		    		e.prop.SetAlarm("【{}】当前电压:{}V,判断为量测异常,请人工检查!",Name, e.U);
		    		e.prop.SetException("电压量测异常");
		    		e.isMeasureError = true;
		    		e.isVolError = true;
		    		return true;
		    	}
//	    		if(e.I <= 0) {
//	    			e.prop.SetAlarm("{}当前电流：{}A,判断为量测异常，请人工检查!", e.getName(), e.I);
//	    			e.isMeasureError = true;
//	    			return e.isVolError;
//	    		}
	    	} else {
	    		if(e.U*Math.sqrt(3)>voltage.getHighVoltageLimit() ||
	    			e.U*Math.sqrt(3)<voltage.getLowVoltageLimit()) {  
	    			e.prop.SetAlarm("【{}】当前电压:{}V,判断为量测异常,请人工检查!", Name, e.U);
	    			e.prop.SetException("电压量测异常");
	    			e.isMeasureError = true;
		    		return e.isVolError;
	    		}
	    		double tmp = Math.abs(e.Q/(3*e.U*e.I/1000)/Math.sqrt(1-Math.pow(e.PF, 2)));
	    		zjxt_msg.show("【{}】验证无功、功率因数量测的正确性值(0.8~1.2范围内为正常)：{}", Name, String.format("%.2f", tmp));
	    		if(tmp<=0.8 || tmp>=1.2) {
	    			zjxt_msg.showwarn("【{}】当前无功:{}kVar,功率因数:{}V,与当前电压{}V、电；流{}A不对应！", Name, e.Q, e.PF, e.U, e.I);
	    			e.isMeasureError = true;
	    			return e.isVolError;
	    		}
	    	}
			//System.out.println("TEST");
			e.isMeasureError = false;
			return e.isVolError;
		} catch(Exception ex) {
			logger.warn(ex.getMessage());
			e.isMeasureError = true;
			return e.isVolError;
		}
	}
}

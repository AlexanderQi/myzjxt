package com.softcore.cim.entity.container;

//import java.util.LinkedList;
import java.util.List;

import com.softcore.cim.entity.BaseVoltage;
import com.softcore.cim.entity.Equipment;
import com.softcore.cim.entity.block.connector.BusbarSection;

/**
 * 变电站类
 *
 */
public class SubStation extends EquipmentContainer {
	
	/**
	 * 类型
	 */
	private String type;
	
	/**
	 * 总有功	站-总有功；厂-发电有功。
	 */
	private float p;
	
	/**
	 * 总无功,站-总无功；厂-发电无功。
	 */
	private float q;
	
	/**
	 * 厂站经度
	 * 厂站经度坐标(x)
	 */
	private float x;
	
	/**
	 * 厂站纬度，纬度坐标y
	 */
	private float y;
	
	/**
	 *  电流量测标识	i_flag,是否有电流量测。0-没有电流量测；1-有电流量测。
	 */
	private boolean i_flag;
	
	/**
	 * 地刀量测标识	mGdis_flag	地刀是否有量测。0-没有电流量测；1-有电流量测。
	 */
	private boolean mgdis_flag;
	
	/**
	 * 机组变压器量测标识	机组变压器是否有量测。0-没有电流量测；1-有电流量测。
	 */
	private boolean munXf_flag;
	
	/**
	 * 最大电基准电压
	 */
	private BaseVoltage maxBaseVoltage;
	
	
	/**
	 * 所包含的母线
	 */
	public List<BusbarSection> busbarSections;
	
	/**
	 * 该变电站 所包含的所有主治设备
	 */
	public List<Equipment> equipments;
	
	/**
	 * 厂站所属子控制区
	 */
	private SubControlArea subControlArea;

	
	
	/*****************************  getter  **************************************/
	/*****************************  setter  **************************************/
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public float getP() {
		return p;
	}

	public void setP(float p) {
		this.p = p;
	}

	public float getQ() {
		return q;
	}

	public void setQ(float q) {
		this.q = q;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public boolean isI_flag() {
		return i_flag;
	}

	public void setI_flag(boolean i_flag) {
		this.i_flag = i_flag;
	}

	public boolean isMgdis_flag() {
		return mgdis_flag;
	}

	public void setMgdis_flag(boolean mgdis_flag) {
		this.mgdis_flag = mgdis_flag;
	}

	public boolean isMunXf_flag() {
		return munXf_flag;
	}

	public void setMunXf_flag(boolean munXf_flag) {
		this.munXf_flag = munXf_flag;
	}

	public BaseVoltage getMaxBaseVoltage() {
		return maxBaseVoltage;
	}

	public void setMaxBaseVoltage(BaseVoltage maxBaseVoltage) {
		this.maxBaseVoltage = maxBaseVoltage;
	}



	public SubControlArea getSubStationArea() {
		return subControlArea;
	}

	public void setSubControlArea(SubControlArea subStationArea) {
		this.subControlArea = subStationArea;
//		if(subControlArea.subStations == null){
//			subControlArea.subStations = new LinkedList<SubStation>();
//		}
//		subControlArea.subStations.add(this);
	}

}

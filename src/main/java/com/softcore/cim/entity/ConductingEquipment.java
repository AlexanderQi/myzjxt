package com.softcore.cim.entity;

import java.util.List;

import com.softcore.cim.entity.container.VoltageLevel;

/**
 * 导电装置类（抽象类）
 *该类继承了Equipment类，意为可导电设备。也表示一种规范
 *此类的派生类，可通过继承的terminal属性，获得与自己相连的其他导电设备。
 *
 */
public abstract class ConductingEquipment extends Equipment {
	/**
	 * 基准电压
	 */
	private BaseVoltage baseVoltage;
	
	/**
	 * 所属电压等级
	 */
	private VoltageLevel voltageLevel;
	
	/**
	 * 指明该导电装置所包含的终端点
	 */
	public List<Terminal> terminals;

	
	
	
	public BaseVoltage getBaseVoltage() {
		return baseVoltage;
	}

	public void setBaseVoltage(BaseVoltage baseVoltage) {
		this.baseVoltage = baseVoltage;
	}

	public VoltageLevel getVoltageLevel() {
		return voltageLevel;
	}

	public void setVoltageLevel(VoltageLevel voltageLevel) {
		this.voltageLevel = voltageLevel;
	}


	
	
}

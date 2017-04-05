package com.softcore.cim.entity.container;

import java.util.List;

import com.softcore.cim.entity.BaseVoltage;
import com.softcore.cim.entity.PowerSystemResource;

/**
 * 等级电压
 * 实现了EquipmentContainer接口。说明该类为装置容器类
 *
 */
public class VoltageLevel extends PowerSystemResource {
	/**
	 * 电压上限 没有null
	 */
	private float highVoltageLimit;
	
	/**
	 * 电压下限  没有null
	 */
	private float lowVoltageLimit;
	
	private float basicVoltage;
	
	private float nominalVoltage;
	
	private float avgratedVoltage;
	
	/**
	 * 基准电压
	 */
	private BaseVoltage baseVoltage;
	
	/**
	 * 所属变电站
	 */
	private SubStation memberOfSubstation;
	
	/**
	 * 包含的间隔集合
	 */
	public List<Bay> bays;

	
	/*****************************  getter  **************************************/
	/*****************************  setter  **************************************/
	
	public float getBasicVoltage() {
		return basicVoltage;
	}

	public void setBasicVoltage(float basicVoltage) {
		this.basicVoltage = basicVoltage;
	}

	public float getNominalVoltage() {
		return nominalVoltage;
	}

	public void setNominalVoltage(float nominalVoltage) {
		this.nominalVoltage = nominalVoltage;
	}

	public float getAvgratedVoltage() {
		return avgratedVoltage;
	}

	public void setAvgratedVoltage(float avgratedVoltage) {
		this.avgratedVoltage = avgratedVoltage;
	}
	
	public float getHighVoltageLimit() {
		return highVoltageLimit;
	}

	public void setHighVoltageLimit(float highVoltageLimit) {
		this.highVoltageLimit = highVoltageLimit;
	}

	public float getLowVoltageLimit() {
		return lowVoltageLimit;
	}

	public void setLowVoltageLimit(float lowVoltageLimit) {
		this.lowVoltageLimit = lowVoltageLimit;
	}

	public BaseVoltage getBaseVoltage() {
		return baseVoltage;
	}

	public void setBaseVoltage(BaseVoltage baseVoltage) {
		this.baseVoltage = baseVoltage;
	}

	public SubStation getMemberOfSubstation() {
		return memberOfSubstation;
	}

	public void setMemberOfSubstation(SubStation memberOfSubstation) {
		this.memberOfSubstation = memberOfSubstation;
	}

	public List<Bay> getBays() {
		return bays;
	}

	public void setBays(List<Bay> bays) {
		this.bays = bays;
	}
	
	
	
}

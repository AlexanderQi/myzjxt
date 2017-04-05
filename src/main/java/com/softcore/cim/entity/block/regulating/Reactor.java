package com.softcore.cim.entity.block.regulating;


import com.softcore.cim.entity.block.RegulatingCondEq;

/**
 * 电抗器
 * 一组串联或并联的可切换的电抗器组。
 * 电抗器组的串联或并联用法是由它在网络中的接线关系决定的。
 * @author hupin
 *
 */
public class Reactor extends RegulatingCondEq {
	/**
	 * 投运容量
	 */
	private int capacity;
	/**
	 * 投切电压差
	 */
	private int voltageChange;
	/**
	 * 变电所内投入顺序
	 */
	private int upperOrder;
	/**
	 * 变电所内切除顺序
	 */
	private int lowerOrder;
	/**
	 * 额定电压
	 */
	private int ratedVoltage;
	/**
	 * 单组容量
	 */
	private int unitCapacity;
	/**
	 * 单组电压改变量
	 */
	private int unitVoltageChange;
	/**
	 * 总配组数
	 */
	private int totalUnit;
	/**
	 * 投运组数
	 */
	private int operatedUnit;
	
	
	
	
	/**-----------------------getter-----------------------**/
	/**-----------------------setter-----------------------**/
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public int getVoltageChange() {
		return voltageChange;
	}
	public void setVoltageChange(int voltageChange) {
		this.voltageChange = voltageChange;
	}
	public int getUpperOrder() {
		return upperOrder;
	}
	public void setUpperOrder(int upperOrder) {
		this.upperOrder = upperOrder;
	}
	public int getLowerOrder() {
		return lowerOrder;
	}
	public void setLowerOrder(int lowerOrder) {
		this.lowerOrder = lowerOrder;
	}
	public int getRatedVoltage() {
		return ratedVoltage;
	}
	public void setRatedVoltage(int ratedVoltage) {
		this.ratedVoltage = ratedVoltage;
	}
	public int getUnitCapacity() {
		return unitCapacity;
	}
	public void setUnitCapacity(int unitCapacity) {
		this.unitCapacity = unitCapacity;
	}
	public int getUnitVoltageChange() {
		return unitVoltageChange;
	}
	public void setUnitVoltageChange(int unitVoltageChange) {
		this.unitVoltageChange = unitVoltageChange;
	}
	public int getTotalUnit() {
		return totalUnit;
	}
	public void setTotalUnit(int totalUnit) {
		this.totalUnit = totalUnit;
	}
	public int getOperatedUnit() {
		return operatedUnit;
	}
	public void setOperatedUnit(int operatedUnit) {
		this.operatedUnit = operatedUnit;
	}
	
}

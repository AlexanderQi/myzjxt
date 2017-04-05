package com.softcore.cim.entity.block;

import java.util.Date;

import com.softcore.cim.entity.ConductingEquipment;

/**
 * 开关类(抽象类)
 *
 */
public abstract class Switch extends ConductingEquipment {
	/**
	 * 预期寿命
	 */
	private int expectLifetime;
	/**
	 * 开始使用时间
	 */
	private Date startUsingTime;
	
	/**
	 * 开合状态
	 */
	private boolean openStatus;
	
	private int yxh;
	
	private int czh;
	
	private int controlArea;

	
	
	
	
	
	public int getExpectLifetime() {
		return expectLifetime;
	}

	public void setExpectLifetime(int expectLifetime) {
		this.expectLifetime = expectLifetime;
	}

	public Date getStartUsingTime() {
		return startUsingTime;
	}

	public void setStartUsingTime(Date startUsingTime) {
		this.startUsingTime = startUsingTime;
	}

	public boolean isOpenStatus() {
		return openStatus;
	}

	public void setOpenStatus(boolean openStatus) {
		this.openStatus = openStatus;
	}

	public int getYxh() {
		return yxh;
	}

	public void setYxh(int yxh) {
		this.yxh = yxh;
	}

	public int getCzh() {
		return czh;
	}

	public void setCzh(int czh) {
		this.czh = czh;
	}

	public int getControlArea() {
		return controlArea;
	}

	public void setControlArea(int controlArea) {
		this.controlArea = controlArea;
	}
	
	
	
	
	
}

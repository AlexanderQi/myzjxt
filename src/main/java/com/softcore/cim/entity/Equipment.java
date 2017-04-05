package com.softcore.cim.entity;

import java.util.Date;

import zjxt.zjxt_topo.zNode;
//import java.util.LinkedList;
//import java.util.List;

import com.drools.zjxt.kernellib.zjxt_Property;
import com.softcore.cim.entity.container.SubStation;

/**
 * 物理设备类(抽象类)，供继承用。
 *该类继承了PowerSystemResource(电力系统资源)类。
 *继承该抽象类的都是物理装置。
 *
 */
public abstract class Equipment extends PowerSystemResource {
	/**
	 * 装置路径
	 */
	private String pathName;
	
	/**
	 * 装置类型
	 */
	private String type;
	
	/**
	 * 装置所属厂站
	 */
	private SubStation subStation;

	/**
	 * 预期寿命
	 */
	private int expectLifetime;
	
	/**
	 * 使用时间
	 */
	private Date startUsingTime;
	
	public zNode node;
	
	/***----------------------getter--------------------**/
	/***----------------------setter--------------------**/
	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public SubStation getSubStation() {
		return subStation;
	}

	public void setSubStation(SubStation subStation) {
		this.subStation = subStation;
	}

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
	
	
	
}

package com.softcore.cim.entity.block;

import java.util.List;

import com.softcore.cim.entity.PowerSystemResource;
import com.softcore.cim.entity.Terminal;
import com.softcore.cim.entity.container.SubStation;
import com.softcore.cim.entity.container.VoltageLevel;

/**
 * 线组类 抽象类,负责管理线段。和标明位置信息，电压等级信息 。
 * version 1.2
 * 		变动： 1.改动LineGroup继承关系，改为继承PowerSystemResource类
 * 			  2.添加 conductores 属性。
 */
public abstract class LineGroup extends PowerSystemResource {
	
	/**
	 * 开始厂站
	 */
	private SubStation startSt;
	/**
	 * 结束厂站
	 */
	private SubStation endSt;
	
	/**
	 * 线路终端号 i端
	 */
	private Terminal i_terminal;
	
	/**
	 * 线路终端号 j端
	 */
	private Terminal j_terminal;

	/**
	 * 电压等级
	 */
	private VoltageLevel voltageLevel;
	
	/**
	 * 该线组管理的线段
	 */
	public List<Conductor> conductores;
	
	
	
	/***----------------------getter--------------------**/
	/***----------------------setter--------------------**/
	public SubStation getStartSt() {
		return startSt;
	}

	public void setStartSt(SubStation startSt) {
		this.startSt = startSt;
	}

	public SubStation getEndSt() {
		return endSt;
	}

	public void setEndSt(SubStation endSt) {
		this.endSt = endSt;
	}

	public Terminal getI_terminal() {
		return i_terminal;
	}

	public void setI_terminal(Terminal i_terminal) {
		this.i_terminal = i_terminal;
	}

	public Terminal getJ_terminal() {
		return j_terminal;
	}

	public void setJ_terminal(Terminal j_terminal) {
		this.j_terminal = j_terminal;
	}

	public VoltageLevel getVoltageLevel() {
		return voltageLevel;
	}

	public void setVoltageLevel(VoltageLevel voltageLevel) {
		this.voltageLevel = voltageLevel;
	}

	public List<Conductor> getConductores() {
		return conductores;
	}

	public void setConductores(List<Conductor> conductores) {
		this.conductores = conductores;
	}
	
	
	
	
}

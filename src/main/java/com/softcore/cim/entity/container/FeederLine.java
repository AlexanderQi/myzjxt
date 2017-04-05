package com.softcore.cim.entity.container;

import java.util.List;

import com.softcore.cim.entity.block.LineGroup;
import com.softcore.cim.entity.block.connector.BusbarSection;


/**
 * 馈线，是一种管理单元
 * 不作为物理单元，作为逻辑概念
 * version 1.2 
 * 		变动: 1.改动了busbarSectione的属性的类型。之前为List<BusbarSectione>
 * 			  2.删除了 预期寿命expectLifetime、开始使用时间startUsingTime属性。
 *            3.删除了isTransfer是否为旁路属性，改添加到BusbarSection中。
 *            4.添加了voltageLevel属性。
 *
 */
public class FeederLine extends EquipmentContainer {
	/**
	 * 序号
	 */
	private int ordinal;
	
	/**
	 * 所属变电站
	 */
	private SubStation subStation;
	
	/**
	 * 所包含的 环网柜
	 */
	public List<RingMainUnit> ringMainUnites;
	
	/**
	 * 所包含的线路
	 */
	public List<LineGroup> LineGroups;
	
	
	/**
	 * 所属的母线段
	 */
	private BusbarSection busbarSectione;

	/**
	 * 所属电压等级
	 */
	private VoltageLevel voltageLevel;
	
	private boolean canControl = true;
	
	/*****************************  getter  **************************************/
	/*****************************  setter  **************************************/
	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public SubStation getSubStation() {
		return subStation;
	}

	public void setSubStation(SubStation subStation) {
		this.subStation = subStation;
	}

	public List<RingMainUnit> getRingMainUnites() {
		return ringMainUnites;
	}

	public void setRingMainUnites(List<RingMainUnit> ringMainUnites) {
		this.ringMainUnites = ringMainUnites;
	}

	public BusbarSection getBusbarSectione() {
		return busbarSectione;
	}

	public void setBusbarSectione(BusbarSection busbarSectione) {
		this.busbarSectione = busbarSectione;
	}

	public VoltageLevel getVoltageLevel() {
		return voltageLevel;
	}

	public void setVoltageLevel(VoltageLevel voltageLevel) {
		this.voltageLevel = voltageLevel;
	}

	public boolean isCanControl() {
		return canControl;
	}

	public void setCanControl(boolean canControl) {
		this.canControl = canControl;
	}
	
}

package com.softcore.cim.entity.container;

import java.util.List;

import com.softcore.cim.entity.block.LineGroup;


/**
 * 控制区域
 *
 */
public class SubControlArea extends EquipmentContainer {
	/**
	 * 总有功	
	 *区域总加：总出力，总负荷，火电出力，水电出力、核电出力、风电出力
	 */
	private float p;
	/**
	 * 总无功
	 *区域总加：总出力，总负荷，火电出力，水电出力、核电出力、风电出力
	 */
	private float q;
	
	/**
	 * 所属父区域
	 */
	private SubControlArea parent;
	
	/**
	 * 包含的产站
	 */
	public List<SubStation> subStations;
	
	/**
	 * 所包含的线路
	 */
	public List<LineGroup> LineGroups;

	
	
	/*****************************  getter  **************************************/
	/*****************************  setter  **************************************/
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

	public SubControlArea getParent() {
		return parent;
	}

	public void setParent(SubControlArea parent) {
		this.parent = parent;
	}

	public List<SubStation> getSubStations() {
		return subStations;
	}

	public void setSubStations(List<SubStation> subStations) {
		this.subStations = subStations;
	}
}

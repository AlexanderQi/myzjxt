package com.softcore.cim.entity.block.connector;

import java.util.List;

import com.softcore.cim.entity.Equipment;
import com.softcore.cim.entity.block.Connector;
import com.softcore.cim.entity.container.FeederLine;


/**
 * 母线段
 */
public class BusbarSection extends Connector {
	/**
	 * 位置信息	
	 * 母线类型：上(1)/下(2)/旁(3)，指220kV双母带旁母的接线图，如果不是该类型的接线图，则为NULL。
	 * 自动生成图形需要确定的位置。
	 */
	private String location;
	
	/**
	 * 电压测量
	 */
	private float v;
	
	/**
	 * 相角测量
	 */
	private float a;
	
	/**
	 * 所分支的馈线集合
	 */
	public List<FeederLine> feederLines;
	public List<Equipment> equipments;
	private boolean isTransfer;

	
	/**-----------------------getter-----------------------**/
	/**-----------------------setter-----------------------**/
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public float getV() {
		return v;
	}

	public void setV(float v) {
		this.v = v;
	}

	public float getA() {
		return a;
	}

	public void setA(float a) {
		this.a = a;
	}


	public boolean isTransfer() {
		return isTransfer;
	}

	public void setTransfer(boolean isTransfer) {
		this.isTransfer = isTransfer;
	}
	
	
	
	
	
}

package com.softcore.cim.entity.block.line;

import com.softcore.cim.entity.block.Conductor;


/**
 * 直流线段
 *
 */
public class DCLineSegment extends Conductor {
	/**
	 * 电压量测
	 */
	private float i_V;
	
	/**
	 * 电流量测
	 */
	private float i_I;
	
	private float j_V;
	private float j_I;
	
	
	/**-----------------------getter-----------------------**/
	/**-----------------------setter-----------------------**/
	public float getI_V() {
		return i_V;
	}
	public void setI_V(float i_V) {
		this.i_V = i_V;
	}
	public float getI_I() {
		return i_I;
	}
	public void setI_I(float i_I) {
		this.i_I = i_I;
	}
	public float getJ_V() {
		return j_V;
	}
	public void setJ_V(float j_V) {
		this.j_V = j_V;
	}
	public float getJ_I() {
		return j_I;
	}
	public void setJ_I(float j_I) {
		this.j_I = j_I;
	}
	
	
}

package com.softcore.cim.entity.block.line;

import com.softcore.cim.entity.block.Conductor;


/**
 * 交流线路
 * 
 * version 1.2
 * 		变动：删除了一些属性。那些删除的属性可通过继承获得
 */
public class ACLineSegment extends Conductor {
	/**
	 * 正常功率限值（热稳）
	 */
	private float ratedMW;
	
	/**
	 * 允许载流量	即正常电流限值
	 */
	private float ratedCurrent;
	
	/**
	 * 无功 量测 i
	 */
	private float i_q;
	
	/**
	 * 无功量测 j
	 */
	private float j_q;

	
	/**-----------------------getter-----------------------**/
	/**-----------------------setter-----------------------**/
	public float getRatedMW() {
		return ratedMW;
	}

	public void setRatedMW(float ratedMW) {
		this.ratedMW = ratedMW;
	}

	public float getRatedCurrent() {
		return ratedCurrent;
	}

	public void setRatedCurrent(float ratedCurrent) {
		this.ratedCurrent = ratedCurrent;
	}


	public float getI_q() {
		return i_q;
	}

	public void setI_q(float i_q) {
		this.i_q = i_q;
	}

	public float getJ_q() {
		return j_q;
	}

	public void setJ_q(float j_q) {
		this.j_q = j_q;
	}
	
	
}

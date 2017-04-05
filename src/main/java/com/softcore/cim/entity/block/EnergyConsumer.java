package com.softcore.cim.entity.block;

import com.softcore.cim.entity.ConductingEquipment;

/**
 * 耗能用户 (能量消费者),最终耗能电器
 *
 */
public class EnergyConsumer extends ConductingEquipment {
	/**
	 * 有功量测
	 */
	private float p;
	/**
	 * 无功量测
	 */
	private float q;
	
	
	/**-----------------------getter-----------------------**/
	/**-----------------------setter-----------------------**/
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
}

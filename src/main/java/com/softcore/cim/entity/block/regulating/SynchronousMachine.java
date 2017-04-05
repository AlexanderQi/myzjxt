package com.softcore.cim.entity.block.regulating;

import com.softcore.cim.entity.block.RegulatingCondEq;

/**
 * 同步发电机
 *
 */
public class SynchronousMachine extends RegulatingCondEq {
	/**
	 * 额定功率
	 */
	private float ratedMW;
	/**
	 * 最大电压限值
	 */
	private float maxU;
	/**
	 * 最小电压限值
	 */
	private float minU;
	
	/**
	 *  最大无功限值
	 */
	private float maxQ;
	/**
	 *  最小无功限值
	 */
	private float minQ;
	/**
	 *  最大有功限值
	 */
	private float maxP;
	/**
	 *  最小有功限值
	 */
	private float minP;
	/**
	 * 正序电阻
	 */
	private float r;
	/**
	 * 正序电抗
	 */
	private float x;
	/**
	 * 零序电阻
	 */
	private float r0;
	/**
	 * 零序电抗
	 */
	private float x0;
	/**
	 * 厂用电率
	 */
	private float AuxRatio;
	
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
	public float getRatedMW() {
		return ratedMW;
	}
	public void setRatedMW(float ratedMW) {
		this.ratedMW = ratedMW;
	}
	public float getMaxU() {
		return maxU;
	}
	public void setMaxU(float maxU) {
		this.maxU = maxU;
	}
	public float getMinU() {
		return minU;
	}
	public void setMinU(float minU) {
		this.minU = minU;
	}
	public float getMaxQ() {
		return maxQ;
	}
	public void setMaxQ(float maxQ) {
		this.maxQ = maxQ;
	}
	public float getMinQ() {
		return minQ;
	}
	public void setMinQ(float minQ) {
		this.minQ = minQ;
	}
	public float getMaxP() {
		return maxP;
	}
	public void setMaxP(float maxP) {
		this.maxP = maxP;
	}
	public float getMinP() {
		return minP;
	}
	public void setMinP(float minP) {
		this.minP = minP;
	}
	public float getR() {
		return r;
	}
	public void setR(float r) {
		this.r = r;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getR0() {
		return r0;
	}
	public void setR0(float r0) {
		this.r0 = r0;
	}
	public float getX0() {
		return x0;
	}
	public void setX0(float x0) {
		this.x0 = x0;
	}
	public float getAuxRatio() {
		return AuxRatio;
	}
	public void setAuxRatio(float auxRatio) {
		AuxRatio = auxRatio;
	}
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

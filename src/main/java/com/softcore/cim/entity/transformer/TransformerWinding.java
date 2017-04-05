package com.softcore.cim.entity.transformer;

import com.softcore.cim.entity.ConductingEquipment;

/**
 * 变压器绕组类
 *
 */
public class TransformerWinding extends ConductingEquipment {
	/**
	 * 所属变压器 n..1
	 */
	private TransformerFormer powerTransform;
	
	/**
	 * 所对应的切换装置  1..1
	 */
	private TapChanger tapChanger;
	
	/**
	 * 物理连接点
	 */
	private String i_node;
	
	/**
	 * 额定功率
	 */
	private float ratedMVA;
	
	/**
	 * 短路损耗
	 */
	private float loadLoss;
	
	/**
	 * 短路电压百分比
	 */
	private float leakageImpedence;
	
	/**
	 * 电阻
	 */
	private float x;
	
	/**
	 * 电抗
	 */
	private float r;
	
	/**
	 * 零序电阻
	 */
	private float x0;
	/**
	 * 零序电抗
	 */
	private float r0;
	
	/**
	 * 有功测量
	 */
	private float p;
	/**
	 * 无功量测
	 */
	private float q;
	/**
	 * 档位量测
	 */
	private float d;
	
	
	
	
	/*****************************  getter  **************************************/
	/*****************************  setter  **************************************/
	public TransformerFormer getPowerTransform() {
		return powerTransform;
	}
	public void setPowerTransform(TransformerFormer powerTransform) {
		this.powerTransform = powerTransform;
	}
	public TapChanger getTapChanger() {
		return tapChanger;
	}
	public void setTapChanger(TapChanger tapChanger) {
		this.tapChanger = tapChanger;
	}
	public String getI_node() {
		return i_node;
	}
	public void setI_node(String i_node) {
		this.i_node = i_node;
	}
	public float getRatedMVA() {
		return ratedMVA;
	}
	public void setRatedMVA(float ratedMVA) {
		this.ratedMVA = ratedMVA;
	}
	public float getLoadLoss() {
		return loadLoss;
	}
	public void setLoadLoss(float loadLoss) {
		this.loadLoss = loadLoss;
	}
	public float getLeakageImpedence() {
		return leakageImpedence;
	}
	public void setLeakageImpedence(float leakageImpedence) {
		this.leakageImpedence = leakageImpedence;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getR() {
		return r;
	}
	public void setR(float r) {
		this.r = r;
	}
	public float getX0() {
		return x0;
	}
	public void setX0(float x0) {
		this.x0 = x0;
	}
	public float getR0() {
		return r0;
	}
	public void setR0(float r0) {
		this.r0 = r0;
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
	public float getD() {
		return d;
	}
	public void setD(float d) {
		this.d = d;
	}
	
	
	
}

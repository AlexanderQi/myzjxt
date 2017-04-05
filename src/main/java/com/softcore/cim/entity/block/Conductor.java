package com.softcore.cim.entity.block;

import com.softcore.cim.entity.ConductingEquipment;

/**
 * 导体类(抽象类)。
 * 具备动作作用，但本身不具备物理属性。
 * 供导线继承（直流线路，交流线路
 *
 * version 1.2
 * 		变动：1.属性大部分变动。
 * 			  2.关联LineGroup属性
 *
 */
public class Conductor extends ConductingEquipment {
	/**
	 * 正序电阻
	 */
	private float r;
	/**
	 * 零序电阻
	 */
	private float r0;
	/**
	 * 正序电抗
	 */
	private float x;
	/**
	 * 零序电抗
	 */
	private float x0;
	/**
	 * 正序电导
	 */
	private float g;
	/**
	 * 零序电导
	 */
	private float g0;
	/**
	 * 正序电纳
	 */
	private float b;
	/**
	 * 	零序电纳
	 */
	private float b0;
	
	/**
	 * 有功量测 i
	 */
	private float i_P;
	
	/**
	 * 有功量测 j
	 */
	private float j_P;
	/**
	 * 线段长度
	 */
	private double length;
	/**
	 * 所属线组
	 */
	private LineGroup lineGroup;
	
	
	
	
	/***----------------------getter--------------------**/
	/***----------------------setter--------------------**/
	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}

	public float getI_P() {
		return i_P;
	}

	public void setI_P(float i_P) {
		this.i_P = i_P;
	}

	public float getJ_P() {
		return j_P;
	}

	public void setJ_P(float j_P) {
		this.j_P = j_P;
	}

	public float getR0() {
		return r0;
	}

	public void setR0(float r0) {
		this.r0 = r0;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getX0() {
		return x0;
	}

	public void setX0(float x0) {
		this.x0 = x0;
	}

	public float getG() {
		return g;
	}

	public void setG(float g) {
		this.g = g;
	}

	public float getG0() {
		return g0;
	}

	public void setG0(float g0) {
		this.g0 = g0;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	public float getB0() {
		return b0;
	}

	public void setB0(float b0) {
		this.b0 = b0;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public LineGroup getLineGroup() {
		return lineGroup;
	}

	public void setLineGroup(LineGroup lineGroup) {
		this.lineGroup = lineGroup;
	}
	
	
}

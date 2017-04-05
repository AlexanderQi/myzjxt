package com.softcore.cim.entity.transformer;

import com.softcore.cim.entity.Equipment;

/**
 * 改变变压器绕组分接头位置的装置。
 * 属性typeName（类型名）表示切换器的类型，指定为“Fixed（固定）”或“LTC”。
 * TapChanger从属于某个TransformerWinding
 */
public class TapChanger extends Equipment {
	/**
	 * 额定档位,中点分接头位置
	 */
	private int neutralStep;
	/**
	 * 中点电压 ,分接头在中点位置时对应的电压
	 */
	private float neutralKV;
	/**
	 * 最大档位
	 */
	private int highStep;
	/**
	 * 最小档位a
	 */
	private int lowStep;
	/**
	 * 步长,每个档位引起的电压百分数变化量
	 */
	private float stepVolIncre;
	/**
	 * 档位量测
	 */
	private float d;
	
	/**
	 * 所对应的变压器绕组
	 */
	private TransformerWinding transformerWinding;

	
	
	
	
	/*****************************  getter  **************************************/
	/*****************************  setter  **************************************/
	public int getNeutralStep() {
		return neutralStep;
	}

	public void setNeutralStep(int neutralStep) {
		this.neutralStep = neutralStep;
	}

	public float getNeutralKV() {
		return neutralKV;
	}

	public void setNeutralKV(float neutralKV) {
		this.neutralKV = neutralKV;
	}

	public int getHighStep() {
		return highStep;
	}

	public void setHighStep(int highStep) {
		this.highStep = highStep;
	}

	public int getLowStep() {
		return lowStep;
	}

	public void setLowStep(int lowStep) {
		this.lowStep = lowStep;
	}

	public float getStepVolIncre() {
		return stepVolIncre;
	}

	public void setStepVolIncre(float stepVolIncre) {
		this.stepVolIncre = stepVolIncre;
	}

	public float getD() {
		return d;
	}

	public void setD(float d) {
		this.d = d;
	}

	public TransformerWinding getTransformerWinding() {
		return transformerWinding;
	}

	public void setTransformerWinding(TransformerWinding transformerWinding) {
		this.transformerWinding = transformerWinding;
	}
	
	
	
}

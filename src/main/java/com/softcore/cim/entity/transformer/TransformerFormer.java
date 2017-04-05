package com.softcore.cim.entity.transformer;

import java.util.List;

import com.softcore.cim.entity.Equipment;

/**
 * 变压器类
 *
 */
public class TransformerFormer extends Equipment {
	/**
	 * 空载损耗
	 */
	private float noLoadLoss;
	
	/**
	 * 空载电流百分比
	 */
	private float excitingCurrent;
	
	/**
	 * 所包含的变压器绕组
	 */
	private List<TransformerWinding> transformerWindingses;

	
	
	/*****************************  getter  **************************************/
	/*****************************  setter  **************************************/
	public float getNoLoadLoss() {
		return noLoadLoss;
	}

	public void setNoLoadLoss(float noLoadLoss) {
		this.noLoadLoss = noLoadLoss;
	}

	public float getExcitingCurrent() {
		return excitingCurrent;
	}

	public void setExcitingCurrent(float excitingCurrent) {
		this.excitingCurrent = excitingCurrent;
	}

	public List<TransformerWinding> getTransformerWindingses() {
		return transformerWindingses;
	}

	public void setTransformerWindingses(
			List<TransformerWinding> transformerWindingses) {
		this.transformerWindingses = transformerWindingses;
	}
	
}

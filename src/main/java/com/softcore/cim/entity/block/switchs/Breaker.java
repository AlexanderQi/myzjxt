package com.softcore.cim.entity.block.switchs;

import com.softcore.cim.entity.block.Switch;

/**
 * 断路器
 *
 */
public class Breaker extends Switch {
	
	/**
	 * 代码：开关默认状态代码,用于无法获得开关状态时的粗判
	 */
	private int normalState;
	
	/**
	 * 断路器型号
	 */
	private int breakTypeID;
	
	
	
	/**-----------------------getter-----------------------**/
	/**-----------------------setter-----------------------**/
	public int getNormalState() {
		return normalState;
	}

	public void setNormalState(int normalState) {
		this.normalState = normalState;
	}

	public int getBreakTypeID() {
		return breakTypeID;
	}

	public void setBreakTypeID(int breakTypeID) {
		this.breakTypeID = breakTypeID;
	}
	
	
}	

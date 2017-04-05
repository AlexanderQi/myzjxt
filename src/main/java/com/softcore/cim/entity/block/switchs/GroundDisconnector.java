package com.softcore.cim.entity.block.switchs;

import com.softcore.cim.entity.block.Switch;

/**
 * 接地刀闸类 (GroundDisconnector)
 * 一套手动或电动驱动的机械切换设备，用来使电路或设备与地隔离。
 * 简称地刀
 *
 */
public class GroundDisconnector extends Switch {
	/**
	 * 常关还是常开
	 */
	private boolean normalOpen;
	/**
	 * 运行还是停运 true表示运行  false表示停运
	 */
	private boolean devState;
	
	
	
	public boolean isNormalOpen() {
		return normalOpen;
	}
	public void setNormalOpen(boolean normalOpen) {
		this.normalOpen = normalOpen;
	}
	public boolean isDevState() {
		return devState;
	}
	public void setDevState(boolean devState) {
		this.devState = devState;
	}
	
	
}	

package com.softcore.cim.entity.block.switchs;

import com.softcore.cim.entity.Terminal;
import com.softcore.cim.entity.block.Switch;

/**
 * 刀闸类 (Disconnector)
 * 一种手动或电动的机械切换装置，用于改变电路接线或从电源隔离某个电路或设备。
 * 当断开或闭合电路时要求它只断开或闭合可忽略的电流。
 *
 */
public class Disconnector extends Switch {
	private Terminal i_terminal;
	private Terminal j_terminal;
	public Terminal getI_terminal() {
		return i_terminal;
	}
	public void setI_terminal(Terminal i_terminal) {
		this.i_terminal = i_terminal;
	}
	public Terminal getJ_terminal() {
		return j_terminal;
	}
	public void setJ_terminal(Terminal j_terminal) {
		this.j_terminal = j_terminal;
	}
	
	
}

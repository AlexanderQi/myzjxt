package zjxt;

import org.slf4j.helpers.MessageFormatter;

/**
 * 
 * @author lixu
 * 命令信息组合
 */
public class zjxt_CommandContent {

	/** 动作策略*/
	private String action;
	
	/** 原因描述*/
	private String reason;
	
	/** 预期*/
	private String expect;

	/** 控制状态*/
	private String controlState;
	
	public zjxt_CommandContent(String controlState) {
		this.controlState = controlState;
	}
	
	public String getAction() {
		return action;
	}

	@SuppressWarnings("restriction")
	public void setAction(String action, Object... params) {
		String info = MessageFormatter.arrayFormat(action, params).getMessage();
		this.action = controlState + info + ";";
	}

	public String getReason() {
		return reason;
	}

	@SuppressWarnings("restriction")
	public void setReason(String reason, Object... params) {
		String info = MessageFormatter.arrayFormat(reason, params).getMessage();
		this.reason = "原因描述:" + info + ";";
	}

	public String getExpect() {
		return expect;
	}

	@SuppressWarnings("restriction")
	public void setExpect(String expect, Object... params) {
		String info = MessageFormatter.arrayFormat(expect, params).getMessage();
		this.expect = "预期效果:" + info + ";";
	}
	
	@Override
	public String toString() {
		return action + reason + expect; 
	}
}

package com.softcore.cim.common;

/**
 * 
 * @ClassName: CommonListCode
 * @Description: 类型代码
 * @author: lixu
 * @Company: 南京软核
 * @Date: 2017年1月18日 下午5:29:57
 *
 */
public final class CommonListCode {

	/***********************设备类型代码 start************************/
	/** 主馈线*/
	public static int FEEDER_CODE = 37;
	
	/** 配网电容器*/
	public static int CAPACITOR_CODE = 29;
	
	/** 线路接点|T接点*/
	public static int T_NODE_CODE = 22;
	
	/** 端点*/
	public static int TERMINAL_CODE = 23;
	
	/** 连接节点*/
	public static int NODE_CODE = 24;
	
	/** 配网线路*/
	public static int LINE_CODE = 25;
	
	/** 配网线段*/
	public static int LINE_SEGMENT_CODE = 26;
	
	/** 配变*/
	public static int TRANS_CODE = 28;
	
	/** 线路调压器*/
	public static int VOLTAGEREGULATOR_CODE = 30;
	
	/** APF*/
	public static int APF_CODE = 31;
	
	/** TSF*/
	public static int TSF_CODE = 32;
	
	/** SVG*/
	public static int SVG_CODE = 33;
	
	/***********************设备类型代码 end************************/
	
	/***********************量测类型代码 start************************/
	/** 电压遥测类型代码*/
	public static int VOLTAGE_CODE;
	
	/** 电流遥测类型代码*/
	public static int ELECTRIC_CURRENT_CODE;
	
	/** 有功功率遥测类型代码*/
	public static int ACTIVE_POWER_CODE;
	
	/** 无功功率遥测类型代码*/
	public static int REACTIVE_POWER_CODE;
	
	/** 分接头档位遥测类型代码*/
	public static int TAP_POSITION_CODE;
	
	/** 功率因数遥测类型代码*/
	public static int POWER_FACTOR_CODE;
	
	/** 开关位置遥信类型代码*/
	public static int SWITCH_CODE;
	
	/** 保护信号遥信类型代码*/
	public static int PROTECTION_SIGNAL_CODE;
	
	/** 通道信号遥信类型代码*/
	public static int CHANNEL_SIGNAL_CODE;
	
	/***********************量测类型代码 end************************/

	public static final String VOLTAGE_KIND = "电压类设备";
	
	public static final String REACTIVE_POWER_KIND = "无功类设备";
}

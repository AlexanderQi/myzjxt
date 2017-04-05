package com.softcore.cim.entity;

import java.util.HashMap;
import java.util.Map;

import zjxt.zjxt_topo.zNode;

//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;

/**
 *电力系统资源类PowerSystemResource，抽象类
 *为所有元器件的和所有电力资源的基类
 *
 */
public class PowerSystemResource extends zNode {
	/**
	 * 唯一标识号
	 */
	public String graphid = "-1";
	public int typeid = -1;
	public int Tag = -1;
	public String groupid = "0";
	public String parentid = "-1";
	public int getTypeid() {
		return typeid;
	}

	public void setTypeid(int typeid) {
		this.typeid = typeid;
	}

	private String mrID;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 别名
	 */
	//private String aliasName;
	
	/**
	 * 描述
	 */
	//private String Description;
	
	
	/**
	 * 类型代码
	 */
	private String ElementStyle;	
	
	private Map<String, Object>Parametes = new HashMap<String,Object>();
	
	/**-----------------------getter-----------------------**/
	/**-----------------------setter-----------------------**/
	public Object get(String FieldName){
		Object object = null;	
		object = Parametes.get(FieldName);
		return object;
	}
	
	public void set(String FieldName,Object value){
		Parametes.put(FieldName, value);
	}
	
	public void add(String FieldName,Object value){
		if(Parametes.containsKey(FieldName))
			return;
		Parametes.put(FieldName, value);
	}
	
	public String getMrID() {
		return mrID;
	}

	public void setMrID(String mrID) {
		this.mrID = mrID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

//	public String getAliasName() {
//		return aliasName;
//	}
//
//	public void setAliasName(String aliasName) {
//		this.aliasName = aliasName;
//	}
//
//	public String getDescription() {
//		return Description;
//	}
//
//	public void setDescription(String description) {
//		Description = description;
//	}

	public String getElementStyle() {
		return ElementStyle;
	}

	public void setElementStyle(String elementStyle) {
		ElementStyle = elementStyle;
	}
	
	
	
}

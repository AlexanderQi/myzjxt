package com.softcore.cim.entity.container;

import com.softcore.cim.entity.PowerSystemResource;

/**
 * 装置容器(抽象类)。
 *表示一种规范，继承了PowerSystemResource类。
 *实现该抽象类的类，意为装置容器类。
 *
 */
public abstract class EquipmentContainer extends PowerSystemResource {
	/**
	 * 所属路径
	 */
	private String pathName;

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}
	
	
}

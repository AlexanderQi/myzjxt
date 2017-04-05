package zjxt2_app;

import java.util.ArrayList;
import java.util.List;

import com.drools.zjxt.kernellib.zjxt_Property;
import com.softcore.cim.entity.Equipment;
import com.softcore.cim.entity.container.FeederLine;

import zjxt.zjxt_ProtectionTable;

/**
 * 三相不平衡装置
 * @author Administrator
 *
 */
public class zTpunbalance extends Equipment {

	public zjxt_Property property;
	public List<zjxt_ProtectionTable.zProtection> protectList = new ArrayList<zjxt_ProtectionTable.zProtection>();
	private FeederLine feederLine;
	public String SCHEMEID;
	public String id;
	public String SWITCHID;
	public boolean IsYT = true;
	public boolean IsGroup = true;
	public String GroupId = "-1";
	public List<zTpunbalance> UnitList;
	public zTpunbalance MyGroup = null;
	    
	public String itemType = "";
	public float VOLTAGECHANGE;
	public float RATEDCOMPENSATIONI;
	public int MAXFILTER;
	public String VLID = "";
	public Double MINUNBALANCEI;
	public Double MAXUNBALANCEI;
	public Double MINCOMPENSATION;
	public Double MAXCOMPENSATION;
	
	public zTpunbalance() {
		this.UnitList = new ArrayList();
	}
	
	public void AddUnit(zTpunbalance unit) {
		this.UnitList.add(unit);
		unit.IsGroup = false;
		unit.MyGroup = this;
	}
	
	public FeederLine getFeederLine()
	{
		return this.feederLine;
	}
	   
	public void setFeederLine(FeederLine feederLine) {
		if (feederLine == null)
		return;
		this.feederLine = feederLine;
		setSubStation(feederLine.getSubStation());
	}
	
}

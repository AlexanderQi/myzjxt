package zjxt2_app;

import java.util.List;

import com.drools.zjxt.kernellib.zjxt_CimBuild;
import com.drools.zjxt.kernellib.zjxt_Property;
import com.softcore.cim.entity.PowerSystemResource;

public class zjxt_build extends zjxt_CimBuild {

	public static zApf newApf(zFeederLine Owner) {
		zApf apf = new zApf();
		apf.property = new zjxt_Property(apf, Measure);
		if (Owner != null)
			Owner.equipments.add(apf);
		apf.setFeederLine(Owner);
		apf.line = Owner;
		apf.typeid = 220;
		cbList.add(apf);
		return apf;
	}
	
	public static zTpunbalance newTpunbalance(zFeederLine Owner) {
		zTpunbalance balance = new zTpunbalance();
		balance.property = new zjxt_Property(balance, Measure);
		if (Owner != null)
			Owner.equipments.add(balance);
		balance.setFeederLine(Owner);
		balance.line = Owner;
		balance.typeid = 220;
		cbList.add(balance);
		return balance;
	}
	
	public class FeederLine extends zFeederLine {
		public int getItemCount() {
			return equipments.size();
		}
		
		public List<PowerSystemResource> getEquipmentList() {
			return equipments;
		}
	}
}

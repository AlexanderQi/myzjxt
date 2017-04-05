package zjxt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class zjxt_StateGroup {
	public StateItem Owner = null;
	private Map<String, StateItem> sMap;
	
	public zjxt_StateGroup(){
		sMap = new HashMap<String, StateItem>();
	}
	
	public StateItem GetState(String StateName) {
		return sMap.get(StateName);		
	}
	
	public void Clear(){
		sMap.clear();
	}
	
	public StateItem AddStateItem(String StateName,String StateValue)throws Exception{
		if(sMap.containsKey(StateName))
		{
			throw new Exception("AddStateItem->状态同名");
		}			
		StateItem stateItem = new StateItem();
		stateItem.Owner = this;
		stateItem.StateName = StateName;
		stateItem.Value = StateValue;
		stateItem.OccurrenceTime = new Date();
		sMap.put(stateItem.StateName, stateItem);
		return stateItem;		
	}
	
	
	public void SetState(StateItem si)throws Exception{
		si.Owner = this;
		sMap.put(si.StateName, si);
	}
	
	public void SetStateValue(String StateName,String Value) throws Exception{
		StateItem sItem = sMap.get(StateName);
		if(sItem == null){
			throw new Exception("SetStateValue->无此状态:"+StateName);
		}
		sItem.Value = Value;
	}
	
	public String GetStateValue(String StateName) throws Exception{
		StateItem sItem = sMap.get(StateName);
		if(sItem == null){
			throw new Exception("GetStateValue->无此状态:"+StateName);
		}
		return sItem.Value;
	}
	
	
	
	public static class StateItem{
		public zjxt_StateGroup Owner;
		public String StateName;
		public String Value;
		public Date OccurrenceTime;
		public zjxt_StateGroup subStateGroup;
		
		public void CreateSubStateGroup(){
			subStateGroup = new zjxt_StateGroup();
			subStateGroup.Owner = this;			
		}
		
	}
	

}

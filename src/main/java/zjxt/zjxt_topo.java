package zjxt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.drools.zjxt.kernellib.Limit.LimitValue;
import com.drools.zjxt.kernellib.zjxt_CimBuild;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zFeederLine;
import com.drools.zjxt.kernellib.zjxt_ConnectionPool;
import com.drools.zjxt.kernellib.zjxt_Property;
import com.softcore.cim.common.CommonListCode;
import com.softcore.cim.entity.PowerSystemResource;
import com.softcore.cim.entity.container.VoltageLevel;

public class zjxt_topo {
	public List<zNode> TopoGraph = new ArrayList<zjxt_topo.zNode>();
	public Map<String, zNode> zNodeList = new HashMap<String, zNode>(256);
	public List<zNode> Result = new ArrayList<zjxt_topo.zNode>();
	private zNode StartNode = null;
	private zNode FinishNode = null;
	public int FilterTag = -1;
	
	public zjxt_topo(){
		
	}
	
	public void IniTopo(){
		zNode tmp = null;
		Result.clear();
		StartNode = null;
		FinishNode = null;
		int ct = TopoGraph.size();
		for(int i=0;i<ct;i++){
			tmp = TopoGraph.get(i);
			tmp.Visited = false;
			tmp.Enabled = true;
		}
	}
	
	public zNode getNode(String idString){
//		zNode re = null;
//		zNode tmp = null;
//		int ct = TopoGraph.size();
//		for(int i=0;i<ct;i++){
//			tmp = TopoGraph.get(i);
//			if(tmp.Id.equals(idString)){
//				re = tmp;
//			}
//		}
//		return re;
		zNode node = zNodeList.get(idString);
		return node;
	}
	
	public zNode getNodeByName(String name){
//		zNode re = null;
//		zNode tmp = null;
//		int ct = TopoGraph.size();
//		for(int i=0;i<ct;i++){
//			tmp = TopoGraph.get(i);
//			if(tmp.Name.equals(name)){
//				re = tmp;
//			}
//		}
//		return re;
		zNode node = null;
		for(Entry<String, zNode> entry : zNodeList.entrySet()) {
			node = entry.getValue();
			if(node != null && node.Name.equals(name)) {
				return node;
			}
		}
		return null;
	}
	
	public void IniLinks() throws Exception{
		zNode tmp = null;
		for(Entry<String, zNode> entry : zNodeList.entrySet()) {
			tmp = entry.getValue();
			if(tmp.LinkInfo == null ||
					"".equals(tmp.LinkInfo.trim())) {
				zjxt_msg.showwarn("初始化拓扑节点:{}的LinkInfo为空!", tmp.Id);
				continue;
			}
			String[] info = tmp.LinkInfo.split(";");
			for(int j=0;j<info.length;j++){
//				zNode tmp2 = getNodeByName(info[j]);
				zNode tmp2 = getNode(info[j]);
				if(tmp2 == null) throw new Exception("IniLinks()->拓扑信息异常:找不到ID为" + info[j] + "的节点!");
				tmp.Links.add(tmp2);
			}
		}
//		for(int i=0;i<ct;i++){
//			tmp = TopoGraph.get(i);
//			if(tmp.LinkInfo == null ||
//					"".equals(tmp.LinkInfo.trim())) {
//				zjxt_msg.showwarn("初始化拓扑节点:{}的LinkInfo为空!", tmp.Id);
//				continue;
//			}
//			String[] info = tmp.LinkInfo.split(";");
//			for(int j=0;j<info.length;j++){
////				zNode tmp2 = getNodeByName(info[j]);
//				zNode tmp2 = getNode(info[j]);
//				if(tmp2 == null) throw new Exception("IniLinks()->拓扑信息异常:"+info[j]);
//				tmp.Links.add(tmp2);
//			}
//		}
	}
	
	public void replaceTnode() {
		Iterator<Entry<String, zNode>> iterator = zNodeList.entrySet().iterator();
		while(iterator.hasNext()) {
			zNode node = iterator.next().getValue();
			replaceTnode(node);
		}
		
	}
	
	public void replaceTnode(zNode node) {
		int size = node.Links.size();
		List<zNode> tempLinks = new ArrayList<zNode>(node.Links);
		for(int i=0; i<size; i++) {
			zNode linkNode = tempLinks.get(i);
			if(linkNode.Style == CommonListCode.T_NODE_CODE) { //如果是T节点，则替换为T节点连接的其他节点
				zNode tNode = zNodeList.get(linkNode.Id);
				node.Links.remove(linkNode);
				for(zNode tNodeLink : tNode.Links) {
					if(!tNodeLink.Id.equals(node.Id)) {
						node.Links.add(tNodeLink);
						replaceTnode(tNodeLink);
					}
				}
			}
		}
		tempLinks = null;
	}
	
	public void LoadFromDb(){
		try {
			Connection dbConnection = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = dbConnection.createStatement();
			zjxt_msg.show("载入静态拓扑信息...");
			String sql = "select * from tblgraphtopoterminal";
			ResultSet rSet = stat.executeQuery(sql);
			while(rSet.next()){
				zNode equipNode = new zNode();
				equipNode.Id = rSet.getString("ID");
				equipNode.Name = rSet.getString("NAME");
				equipNode.Style = rSet.getInt("TAG");	
				equipNode.sId = rSet.getString("STID");
				equipNode.LinkInfo = rSet.getString("LINKS");
//				equipNode.parentId = rSet.getString("PARENTID");
//				TopoGraph.add(equipNode);
				zNodeList.put(equipNode.Id, equipNode);
			}
			dbConnection.close();		
			IniLinks();
			replaceTnode();
			List<zNode> headNodes = getHeadNode();
			for(zNode node : headNodes) {
				initLevelRelation(node);
			}
			for(Entry<String, zNode> entry : zNodeList.entrySet()) {
				zjxt_msg.show("id="+entry.getKey() + ", parent="+entry.getValue().parentId);
			}
			for(Entry<String, zNode> entry : zNodeList.entrySet()) {
				String info = "";
				for(zNode node: entry.getValue().Links) {
					info += node.Id + ";";
				}
				zjxt_msg.show("id="+entry.getKey() + ", links="+info);
			}
			
		} catch (Exception e) {
			zjxt_msg.showwarn("zjxt_topo->LoadFromDb()->", e);
		}
		
	}
	
	/**
	 * 
	 * @Title: getBeginNode
	 * @Description: 获取首端节点
	 * 有可能多条馈线在一张拓扑图，所以会存在多个馈线首端节点
	 * @author: lixu
	 * @return
	 */
	public List<zNode> getHeadNode() {
		List<zNode> headNodes = new ArrayList<zNode>(8);
		for(Entry<String, zNode> entry : zNodeList.entrySet()) {
			zNode node = entry.getValue();
			if(node.Style == CommonListCode.FEEDER_CODE) { //主馈线
				node.parentId = "-1"; //根节点
				headNodes.add(node);
			}
		}
		return headNodes;
	}
	
	/**
	 * 
	 * @Title: initLevelRelation
	 * @Description: 初始化设备上下级关系
	 * @author: lixu
	 */
	public void initLevelRelation(zNode node) {
		if(!node.Visited) {
			node.Visited = true;
			List<zNode> links = node.Links;
			boolean hasParallelEquipment = false; //连接点是否有并联电容器，若有优先处理电容器
			for(zNode no: links) {
				if(no.Style == CommonListCode.CAPACITOR_CODE && !no.Visited) {
					hasParallelEquipment = true;
					break;
				}
			}
			for(zNode no : links) {
				if(!no.Visited) {
					if(hasParallelEquipment) {
						if(no.Style==CommonListCode.CAPACITOR_CODE) {
							if(node.Style == CommonListCode.CAPACITOR_CODE ||
									node.Style == CommonListCode.TRANS_CODE ||
									node.Style == CommonListCode.VOLTAGEREGULATOR_CODE) {
								no.parentId = node.Id;
							} else {
								no.parentId = node.parentId;
							}
							initLevelRelation(no);
						}
					} else {
						if(node.Style == CommonListCode.CAPACITOR_CODE ||
								node.Style == CommonListCode.TRANS_CODE ||
								node.Style == CommonListCode.VOLTAGEREGULATOR_CODE) {
							no.parentId = node.Id;
						} else {
							no.parentId = node.parentId;
						}
						initLevelRelation(no);
					}
					
				}
			}
		}
		
	}
	
	public boolean CheckConnectivity(String BeginNodeId,String EndNodeId){
		StartNode = getNode(BeginNodeId);
		FinishNode = getNode(EndNodeId);
		if(StartNode == null || FinishNode == null)
			return false;
		
		IniTopo();
		Search_dfs(StartNode);
		zNode tmp;
		int ct = Result.size();
		for(int i=0;i<ct;i++){
			tmp = Result.get(i);
			if(tmp == FinishNode){
				return true;
			}
		}
		return false;
	}
	
	
	
	public String GetResultInfo(){
		StringBuilder sBuilder = new StringBuilder();
		zNode tmp;
		int ct = Result.size();
		for(int i=0;i<ct;i++){
			tmp = Result.get(i);
			sBuilder.append("(Name:").append(tmp.Name).append(" Id:").append(tmp.Id).append(" Style:")
			.append(tmp.Style).append(')');
		}
		return sBuilder.toString();
	}
	
	private void Search_dfs(zNode CurNode){
		if(CurNode == null) return;
		if(!CurNode.Enabled) return;
		if(CurNode.Visited)return;
		CurNode.Visited = true;
		Result.add(CurNode);
		int ct = CurNode.Links.size();
		zNode tmp;
		for(int i=0;i<ct;i++){
			tmp = CurNode.Links.get(i);
			Search_dfs(tmp);
		}
	}
	
	public static class Node {
		
	}
	
	public static class zNode{
		public int Style = 0;
		public int Tag = 0;
		public boolean Visited = false;
		public boolean Enabled = true;
		public String Name = "";
		public String Id = "";
		public String parentId = "";
		public String sId = "";
		public String LinkInfo = "";
		public List<zNode> Links = new ArrayList<zjxt_topo.zNode>();
		public zTerminal t1,t2,t3;
		public zjxt_Property prop;
		
		public zFeederLine line;
		public float U;
		public float oldU;
		public int vlid;
		public float I;
		public float P = 0;
		public float oldP;
		public float Q = 0;
		public float PF = 0;
		public float oldPF;
		public float TargetPF = 0; //目标功率因数
		public float workMode = 0; //工作模式
		public float R = 0;
		public float X = 60; //到首端的线路电抗
		public float thdu; //总电压谐波含有率
		public float thdi; //总电流谐波含有率
		public float sxbphd; //三相不平衡度
		public double Qc; //无功出力
		public double Qm; //无功裕度
		public double Qq; //上限最高可投入无功
		public double capacity = 0; //额定容量
		public double minratedCapacity = 0; //最小补偿容量
		public int highStep = -1; //最高档位
		
		public int lowStep = -1; //最低档位
		
		public int currentStep = -1; //当前档位
		
		public double stepvoltageincrement = 0; //调节一档的电压变化(百分比)
		
		public double volOptimizeLo = 0; //电压优化下限
		
		public double volOptimizeUp = 0; //电压优化上限
		
		public double loadFactor = 0; //负荷负载率
		
		public String kind; //设备类型（无功类KIND_Q、电压类KIND_V）
		
		public boolean isHarmonicsProblem = false; //谐波超标
		
		public VoltageLevel voltage; //电压等级
		
		public LimitValue limit;
		
		public boolean isMeasureError; //是否量测错误
		
		public boolean isVolError = false; //电压量测错误
		
		public boolean hasDeadData = false; //是否含有死数据
		
		public String controlState; //控制状态
		
		public ControlParam controlParam = new ControlParam(); //参数值
		
		//查询当前节点的父节点
		public zNode getParentNode() {
//			zNode equipNode = new zNode();
			String parentId = this.parentId;
			zNode node = zjxt_Initialize.topo.zNodeList.get(parentId);
			return node;
		}
		//查询所有终端节点
		public static List<zNode> getTiminalNodes(String feederId) {
//			List<zNode> nodes = new ArrayList<zNode>();
			List<zNode> terminals = new ArrayList<zNode>();
			PowerSystemResource feeder = zjxt_CimBuild.GetById(feederId);
			if(!(feeder instanceof zFeederLine)) { //如果根据feederId查出来的设备不是馈线类型
				zjxt_msg.showwarn("[查询终端节点]根据馈线id{}查询的设备不是馈线类型，请检查!", feederId);
				return terminals;
			}
			zFeederLine feederLine = (zFeederLine) feeder;
			List<PowerSystemResource> equipments = feederLine.equipments;
			for(PowerSystemResource power : equipments) {
				boolean isTerminal = true;
				for(PowerSystemResource power2 : equipments) {
					if(power2 == power) continue;
					if(power.getMrID().equals(power2.parentId)) {
						isTerminal = false;
						break;
					}
				}
				if(isTerminal) {
					terminals.add((zNode)power);
				}
				
			}
			return terminals;
		}
		
		public List<zNode> getChildzNodes() {
			List<zNode> nodes = new ArrayList<zNode>();
			for(Entry<String, zNode> entry :zjxt_Initialize.topo.zNodeList.entrySet()) {
				zNode value = entry.getValue();
				if(Id.equals(value.parentId) && value.kind!=null) {
					nodes.add(value);
				}
			}
			return nodes;
		}
	}
	
	/**
	 * 
	 * @ClassName: ControlParam
	 * @Description: 设备对应的参数值
	 * @author: lixu
	 * @Company: 南京软核
	 * @Date: 2016年9月21日 下午6:47:55
	 *
	 */
	public static class ControlParam {
		
		/** 合闸状态值*/
		public int putOnyxval; 
		
		/** 切除状态值*/
		public int putOffyxval;
		
		/** 远方状态值*/
		public int distantyxval;
		
		/** 就地状态值*/
		public int localyxval;
		
		/** 合闸遥控值*/
		public int putonykval;
		
		/** 切除遥控值*/
		public int putoffykval;
		
		/** 远方遥调值*/
		public int distantykval;
		
		/** 就地遥调值*/
		public int localykval;
		
		/** 电压控制模式遥测值*/
		public int volCtrlModycval;
		
		/** 功率因数控制模式遥测值*/
		public int cosCtrlModycval;
		
		/** 电压控制模式遥调值*/
		public int volCtrlModytval;
		
		/** 功率因数控制模式遥调值*/
		public int cosCtrlModytval;
		
		/** 无功功率工作模式遥测值*/
		public int qctrlmodycval;
		
		/** 无功功率工作模式遥调值*/
		public int qctrlmodytval;
	}
	
	public static class zTerminal{
		public String Name = "";
		public String Id = "";
		public int Style = 0;
		public int Tag = 0;
	}

}

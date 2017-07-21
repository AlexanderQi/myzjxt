package zjxt;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.drools.zjxt.kernellib.JkParam;
import com.drools.zjxt.kernellib.zjxt_CimBuild;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zBreaker;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zCapacitor;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zFeederLine;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zPowerQualityIns;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zSVG;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zSubControlArea;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zSubStation;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zTSF;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zTransformerFormer;
import com.drools.zjxt.kernellib.zjxt_CimBuild.zVoltageRegulator;
import com.drools.zjxt.kernellib.zjxt_ConnectionPool;
import com.drools.zjxt.kernellib.zjxt_Measure;
import com.drools.zjxt.kernellib.zjxt_Measure.zjxt_yc;
import com.drools.zjxt.kernellib.zjxt_Measure.zjxt_yx;
import com.drools.zjxt.kernellib.zjxt_Property;
import com.drools.zjxt.kernellib.zjxt_kernel;
import com.softcore.cim.common.CommonListCode;
import com.softcore.cim.entity.PowerSystemResource;
import com.softcore.cim.entity.container.VoltageLevel;

import zjxt.zjxt_topo.zNode;
import zjxt2_app.zApf;
import zjxt2_app.zTpunbalance;
import zjxt2_app.zjxt_build;
public class zjxt_Initialize {
	public static boolean InitError = false;
	public static List<zNode> nodes = new ArrayList<zNode>();
	public static Map<String, List> nodesMap = new HashMap<String, List>();
	public static Map<String, VoltageLevel> voltageMap = new HashMap<String, VoltageLevel>();
	public static zjxt_topo topo = new zjxt_topo();
	public static Map<String, zSubStation> stationList = new HashMap<String, zSubStation>(8);

	public static boolean Init() throws Exception{
		try {			
			
			Init_VoltageLevel();
			Init_CommonListCode();
			Init_PowerNet();
			Init_topo();
			
			if(InitError ){
				return false;
			}
			Init_State();
			Init_Measure();			
			Init_Protect();
			
			Init_BasicProperty();
			if(JkParam.Instance().IsSmartPrj) {
				zjxt_msg.showwarn("配网AVC1.0 不支持SVG,APF等设备.");
				//Init_SmartEquipmentProperty();
			}else {
				Init_TransformerProperty();//配网变压器
				Init_TyqProperty();	//配网调压器
				Init_CapaProperty(); //配网电容器
				Init_ComControlParam();  //配网电容器控制参数

				//Init_TfProperty();	
				//Ini_CompPropertyEx(); //10kV电容量测
				//Init_TransformerProperty(); //配变的量测
				//Init_ApfProperty();
				//Init_balanceProperty();
				//Init_SvgProperty();
			}
			//zjxt_Measure.Instance().YCYXDB = true;    //遥测遥信数据来自数据库，否则来自内存库
			return true;
		} catch (Exception e) {
			zjxt_msg.showwarn("Init()->"+e.getMessage());
			return false;
		}
		
	}
	
	public final static boolean isNumeric(String s) {  
        if (s != null && !"".equals(s.trim()))  
            return s.matches("^[0-9]*$");  
        else  
            return false;  
    } 
	
	/**
	 * 
	 * @Title: Init_CommonListCode
	 * @Description: 初始化类型代码
	 * @author: lixu
	 */
	public static void Init_CommonListCode()
	{
		Connection dbConnection;
		try {
			dbConnection = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = dbConnection.createStatement();
			String sql = "select * from tblcommonlistcode";
			ResultSet rSet = stat.executeQuery(sql);
			while(rSet.next()) {
				String title = rSet.getString("TITLECODE");
				String valueCode = rSet.getString("VALUECODE");
				if(isNumeric(valueCode))
				{
					if("有功功率".equals(title))
					{
						CommonListCode.ACTIVE_POWER_CODE = Integer.parseInt(valueCode);
					}
					else if("无功功率".equals(title))
					{
						CommonListCode.REACTIVE_POWER_CODE = Integer.parseInt(valueCode);
					}
					else if("功率因数".equals(title))
					{
						CommonListCode.POWER_FACTOR_CODE = Integer.parseInt(valueCode);
					}
					else if("电压".equals(title))
					{
						CommonListCode.VOLTAGE_CODE = Integer.parseInt(valueCode);
					}
					else if("电流".equals(title))
					{
						CommonListCode.ELECTRIC_CURRENT_CODE = Integer.parseInt(valueCode);
					}
					else if("分接头档位".equals(title))
					{
						CommonListCode.TAP_POSITION_CODE = Integer.parseInt(valueCode);
					}
					else if("开关位置".equals(title))
					{
						CommonListCode.SWITCH_CODE = Integer.parseInt(valueCode);
					}
					else if("保护信号".equals(title))
					{
						CommonListCode.PROTECTION_SIGNAL_CODE = Integer.parseInt(valueCode);
					}
					else if("通道信号".equals(title))
					{
						CommonListCode.CHANNEL_SIGNAL_CODE = Integer.parseInt(valueCode);
					}
					else if("线路接点|T接点".equals(title))
					{
						CommonListCode.T_NODE_CODE = Integer.parseInt(valueCode);
					}
					else if("连接节点".equals(title))
					{
						CommonListCode.NODE_CODE = Integer.parseInt(valueCode);
					}
					else if("端点".equals(title))
					{
						CommonListCode.TERMINAL_CODE = Integer.parseInt(valueCode);
					}
					else if("配网线路".equals(title))
					{
						CommonListCode.LINE_CODE = Integer.parseInt(valueCode);
					}
					else if("配网线路线段".equals(title))
					{
						CommonListCode.LINE_SEGMENT_CODE = Integer.parseInt(valueCode);
					}
					else if("配变|公用变、专用变、综合变、农用变".equals(title))
					{
						CommonListCode.TRANS_CODE = Integer.parseInt(valueCode);
					}
					else if("配网无功补偿设备|配变电容器、线路电容器".equals(title))
					{
						CommonListCode.CAPACITOR_CODE = Integer.parseInt(valueCode);
					}
					else if("配网无功补偿设备|配变电容器、线路电容器".equals(title))
					{
						CommonListCode.CAPACITOR_CODE = Integer.parseInt(valueCode);
					}
					else if("配网线路调压器".equals(title))
					{
						CommonListCode.VOLTAGEREGULATOR_CODE = Integer.parseInt(valueCode);
					}
					else if("配网SVG".equals(title))
					{
						CommonListCode.SVG_CODE = Integer.parseInt(valueCode);
					}
					else if("配网电源点|馈线主母".equals(title))
					{
						CommonListCode.FEEDER_CODE = Integer.parseInt(valueCode);
					}
					else if("配网有源滤波器|APF".equals(title))
					{
						CommonListCode.APF_CODE = Integer.parseInt(valueCode);
					}
					else if("配网无源滤波器|TSF".equals(title))
					{
						CommonListCode.TSF_CODE = Integer.parseInt(valueCode);
					}
				}
				
			}
			dbConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 加载电压等级表
	 */
	public static void Init_VoltageLevel() {
		Connection dbConnection;
		try {
			dbConnection = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = dbConnection.createStatement();
			String sql = "select * from tblvoltagelevel";
			ResultSet rSet = stat.executeQuery(sql);
			while(rSet.next()) {
				VoltageLevel voltageLevel = new VoltageLevel();
				voltageLevel.setMrID(rSet.getString("ID"));
				voltageLevel.setHighVoltageLimit((float)rSet.getDouble("UPPERBOUND"));
				voltageLevel.setLowVoltageLimit((float)rSet.getDouble("LOWERBOUND"));
				voltageLevel.setName(rSet.getString("NAME"));
				voltageLevel.setBasicVoltage(rSet.getFloat("BASICVOLTAGE"));
				voltageLevel.setNominalVoltage(rSet.getFloat("NOMINALVOLTAGE"));
				voltageLevel.setAvgratedVoltage(rSet.getFloat("AVGRATEDVOLTAGE"));
				voltageMap.put(voltageLevel.getMrID(), voltageLevel);
			}
			dbConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 初始化拓扑
	 * @throws Exception
	 */
	public static void Init_topo() throws Exception {
		zjxt_msg.show("初始化拓扑...");
		if(topo.LoadFromDb())
			topo.IniTopo();
		else
			InitError = true;
			
	}
	
	public static void Init_SmartPrj()throws Exception{
		try {
			Connection dbConnection = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = dbConnection.createStatement();
			zjxt_msg.show("智能配网项目初始化...");
			zjxt_msg.show("初始化TSF设备...");
			String sql = "select t.*,m.excisionmodeykid,m.capacitancecontrol1_16ykid,m.capacitancecontrol17_21ykid from tblfeedtsf t inner join tblfeedtsfmeasure m on m.id=t.id";
			ResultSet rSet = stat.executeQuery(sql);
			String tsfid;
			String gid;
			String name;
			zTSF tsf;
			
			while(rSet.next()){
				tsfid = rSet.getString("ID");
				gid = rSet.getString("GRAPHID");
				name = rSet.getString("NAME");
				tsf = null;
				PowerSystemResource psr = zjxt_CimBuild.GetById(tsfid);
				if(psr == null){
					zFeederLine feederLine = (zFeederLine)zjxt_CimBuild.GetById(gid);
					if(feederLine == null)
					{
						zjxt_msg.showwarn(rSet.getString("NAME")+" 定义馈线错误. 馈线id:"+gid);
						dbConnection.close();
						InitError = true;
						break;
					}else{
						tsf = zjxt_CimBuild.newTsf(feederLine);
						tsf.setMrID(tsfid);
						tsf.setName(name);
						Mapping(rSet, tsf);
					
						tsf.item1 = rSet.getString("capacitancecontrol1_16ykid");
						tsf.item2 = rSet.getString("capacitancecontrol17_21ykid");
						tsf.tqModeYcid = rSet.getString("EXCISIONMODEYKID");  //投切模式：模糊：1； 线性：0
											
					}
				}else {
					zjxt_msg.showwarn(" TSF定义id重复. 名称:"+name+" 重复id:"+tsfid + " 已存在设备: "+psr.getName());
					dbConnection.close();
					InitError = true;
					break;
				}
			}
					
			rSet.close();
			String svgid;
			zSVG svg;
			zjxt_msg.show("初始化SVG设备...");
			sql = "select t.*,t2.NOMINALVOLTAGE from tblfeedsvg t left join tblvoltagelevel t2 on t.voltagelevelid=t2.id";
			rSet = stat.executeQuery(sql);
			while(rSet.next()){
				svgid = rSet.getString("ID");
				if(!topo.zNodeList.containsKey(svgid)) continue;
//				gid = rSet.getString("GRAPHID");
				gid = rSet.getString("feedid");
				name = rSet.getString("NAME");
				svg = null;
				PowerSystemResource psr = zjxt_CimBuild.GetById(svgid);
				if(psr == null){
					zFeederLine feederLine = (zFeederLine)zjxt_CimBuild.GetById(gid);
					if(feederLine == null)
					{
						zjxt_msg.showwarn(rSet.getString("NAME")+" 定义馈线错误. 馈线id:"+gid);
						InitError = true;
						break;
					}else{
						svg = zjxt_CimBuild.newSvg(feederLine);
						svg.setMrID(svgid);
						svg.setName(name);
						svg.line = feederLine;
						svg.VLID = rSet.getString("VOLTAGELEVELID");
						svg.vlid = rSet.getInt("NOMINALVOLTAGE");
//						if("1".equals(svg.VLID)) {
//							svg.vlid = 220;
//						} else if("2".equals(svg.VLID)) {
//							svg.vlid = 10000;
//						} else if("3".equals(svg.VLID)) {
//							svg.vlid = 380;
//						}
						svg.voltage = voltageMap.get(svg.VLID);
						Mapping(rSet, svg);
					}
					
				}else {
					zjxt_msg.showwarn(" SVG定义id重复. 名称:"+name+" 重复id:"+svgid + " 已存在设备: "+psr.getName());
					dbConnection.close();
					InitError = true;
					break;
				}
			}
			
			rSet.close();
			
			
			String apfId;
			zApf apf;
			zjxt_msg.show("初始化APF设备...");
			sql = "select t.*,t2.NOMINALVOLTAGE from tblfeedapf t left join tblvoltagelevel t2 on t.voltagelevelid=t2.id";
			rSet = stat.executeQuery(sql);
			while(rSet.next()){
				apfId = rSet.getString("ID");
				if(!topo.zNodeList.containsKey(apfId)) continue;
//				gid = rSet.getString("GRAPHID");
				gid = rSet.getString("feedid");
				name = rSet.getString("NAME");
				apf = null;
				PowerSystemResource psr = zjxt_CimBuild.GetById(apfId);
				if(psr == null){
					zFeederLine feederLine = (zFeederLine)zjxt_CimBuild.GetById(gid);
					if(feederLine == null)
					{
						zjxt_msg.showwarn(rSet.getString("NAME")+" 定义馈线错误. 馈线id:"+gid);
						InitError = true;
						break;
					}else{
						apf = zjxt_build.newApf(feederLine);
						apf.setMrID(apfId);
						apf.setName(name);
						apf.line = feederLine;
						apf.VLID = rSet.getString("VOLTAGELEVELID");
						apf.vlid = rSet.getInt("NOMINALVOLTAGE");
//						if("1".equals(apf.VLID)) {
//							apf.vlid = 220;
//						} else if("2".equals(apf.VLID)) {
//							apf.vlid = 10000;
//						} else if("3".equals(apf.VLID)) {
//							apf.vlid = 380;
//						}
						apf.voltage = voltageMap.get(apf.VLID);
						Mapping(rSet, apf);
					}
					
				}else {
					zjxt_msg.showwarn(" APF定义id重复. 名称:"+name+" 重复id:"+apfId + " 已存在设备: "+psr.getName());
					dbConnection.close();
					InitError = true;
					break;
				}
			}
			rSet.close();
			
			String bTpunbalanceId;
			zTpunbalance bTpunbalance;
			zjxt_msg.show("初始化三相不平衡设备...");
			sql = "select t.*,t2.NOMINALVOLTAGE from tblfeedtpunbalance t left join tblvoltagelevel t2 on t.voltagelevelid=t2.id";
			rSet = stat.executeQuery(sql);
			while(rSet.next()){
				bTpunbalanceId = rSet.getString("ID");
				if(!topo.zNodeList.containsKey(bTpunbalanceId)) continue;
//				gid = rSet.getString("GRAPHID");
				gid = rSet.getString("feedid");
				name = rSet.getString("NAME");
				bTpunbalance = null;
				PowerSystemResource psr = zjxt_CimBuild.GetById(bTpunbalanceId);
				if(psr == null){
					zFeederLine feederLine = (zFeederLine)zjxt_CimBuild.GetById(gid);
					if(feederLine == null)
					{
						zjxt_msg.showwarn(rSet.getString("NAME")+" 定义馈线错误. 馈线id:"+gid);
						InitError = true;
						break;
					}else{
						bTpunbalance = zjxt_build.newTpunbalance(feederLine);
						bTpunbalance.setMrID(bTpunbalanceId);
						bTpunbalance.setName(name);
						bTpunbalance.line = feederLine;
						bTpunbalance.VLID = rSet.getString("VOLTAGELEVELID");
						bTpunbalance.vlid = rSet.getInt("NOMINALVOLTAGE");
//						if("1".equals(bTpunbalance.VLID)) {
//							bTpunbalance.vlid = 220;
//						} else if("2".equals(bTpunbalance.VLID)) {
//							bTpunbalance.vlid = 10000;
//						} else if("3".equals(bTpunbalance.VLID)) {
//							bTpunbalance.vlid = 380;
//						}
						bTpunbalance.voltage = voltageMap.get(bTpunbalance.VLID);
						Mapping(rSet, bTpunbalance);
					}
					
				}else {
					zjxt_msg.showwarn(" 三相不平衡定义id重复. 名称:"+name+" 重复id:"+bTpunbalanceId + " 已存在设备: "+psr.getName());
					dbConnection.close();
					InitError = true;
					break;
				}
			}
			rSet.close();
			
			String pqid;
			zPowerQualityIns pqi;
			zjxt_msg.show("初始化电能质量检测设备...");
			sql = "select t.* from tblfeedpowerquality t";
			rSet = stat.executeQuery(sql);
			while(rSet.next()){
				pqid = rSet.getString("ID");
				gid = rSet.getString("GRAPHID");
				name = rSet.getString("NAME");
				pqi = null;
				PowerSystemResource psr = zjxt_CimBuild.GetById(pqid);
				if(psr == null){
					zFeederLine feederLine = (zFeederLine)zjxt_CimBuild.GetById(gid);
					if(feederLine == null)
					{
						zjxt_msg.showwarn(rSet.getString("NAME")+" 定义馈线错误. 馈线id:"+gid);
						InitError = true;
						break;
					}else{
						pqi = zjxt_CimBuild.newPowerQualityIns(feederLine);
						pqi.setMrID(pqid);
						pqi.setName(name);
						Mapping(rSet, pqi);
					}
					
				}else {
					zjxt_msg.showwarn(" 电能质量检测仪定义id重复. 名称:"+name+" 重复id:"+pqid + " 已存在设备: "+psr.getName());
					dbConnection.close();
					InitError = true;
					break;
				}
			}
			rSet.close();			
			dbConnection.close();
			InitError = false;
			
		} catch (Exception e) {
			// TODO: handle exception
			InitError = true;
			zjxt_msg.showwarn("Init_SmartPrj()->", e);
		}
	}
	
	public static void Init_SmartEquipmentProperty()throws Exception{
		String eid = "";
		String name = "";
		int ct = 0;
		try {
			zjxt_msg.show("初始化TSF量测...");
			Connection connection = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = connection.createStatement();
			String sql = "select t.name,t.graphid,m.* from tblfeedtsf t left join tblfeedtsfmeasure m on t.id=m.id";
					
			ResultSet rSet = stat.executeQuery(sql);
			while(rSet.next()){
			    eid = rSet.getString("ID");
				zTSF tsf = (zTSF)zjxt_CimBuild.GetById(eid);
				Mapping(rSet, tsf.property);
				
			}		
			rSet.close();
			
			zjxt_msg.show("初始化SVG量测...");
			sql = "select t.name,t.graphid,m.* from tblfeedsvg t left join tblfeedsvgmeasure m on t.id=m.id";
			rSet = stat.executeQuery(sql);
			while(rSet.next()){
			    eid = rSet.getString("ID");
				zSVG svg = (zSVG)zjxt_CimBuild.GetById(eid);
				Mapping(rSet, svg.property);
				
			}
			rSet.close();
			
			
			zjxt_msg.show("初始化APF量测...");
			sql = "select t.name,t.graphid,m.* from tblfeedapf t left join tblfeedapfmeasure m on t.id=m.id";
			rSet = stat.executeQuery(sql);
			while(rSet.next()){
			    eid = rSet.getString("ID");
				zApf apf = (zApf)zjxt_CimBuild.GetById(eid);
				Mapping(rSet, apf.property);
				
			}
			rSet.close();
			
			zjxt_msg.show("初始化电能质量检测仪量测...");
			sql = "select * from tblfeedpowerqualitymeasure";
			rSet = stat.executeQuery(sql);
			while(rSet.next()){
			    eid = rSet.getString("ID");
				zPowerQualityIns obj = (zPowerQualityIns)zjxt_CimBuild.GetById(eid);
				Mapping(rSet, obj.property);
				
			}
			rSet.close();

			connection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			zjxt_msg.showwarn("Init_TsfSvgProperty()->"+e.toString()+" id:"+eid);
		}
	}
	
	public static void Init_PowerNet()throws Exception{
		try {
			Connection dbConnection = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = dbConnection.createStatement();
			zjxt_msg.show("DBinfo: "+ zjxt_ConnectionPool.Instance().getDbInfo());
			zjxt_msg.show("初始化控制区...");
			String sql = "SELECT id,name FROM TBLSUBCONTROLAREA";
			ResultSet rSet = stat.executeQuery(sql);
			zSubControlArea cArea = zjxt_CimBuild.newSubControlArea();
			//zSubStation feedStation = null;
			if(rSet.next()) {
				cArea.setName(rSet.getString(2));	
				cArea.setMrID(rSet.getString(1));
			}
			
			
			//普通厂站初始化
			zjxt_msg.show("初始化厂站...");
			sql = "SELECT id,name FROM TBLSUBSTATION";
			rSet = stat.executeQuery(sql);
			
			while(rSet.next()){
				zSubStation station = zjxt_CimBuild.newSubStation(cArea);
				station.setName(rSet.getString(2));
				station.setMrID(rSet.getString(1));
				stationList.put(station.getMrID(), station);
			}
			
			
			
			zjxt_msg.show("初始化馈线...");
//			sql = "select * from tblfeedgraph ";
			sql = "select * from tblfeeder";
			rSet = stat.executeQuery(sql);
			while(rSet.next()){
				String stationId = rSet.getString("substationid");
				String id = rSet.getString("id");
				//if(!topo.zNodeList.containsKey(id)) continue;
				zFeederLine fLine = zjxt_CimBuild.newFeederLine(stationList.get(stationId));  
				
				fLine.setName(rSet.getString("NAME"));
				fLine.setMrID(id);
				fLine.setSubStation(stationList.get(stationId));
				fLine.busid = "-1";
				fLine.vlid = 10000;
				Mapping(rSet, fLine);
			}
			
			if(JkParam.Instance().IsSmartPrj){
				//智能配网项目
				//Init_SmartPrj();
				zjxt_msg.showwarn("配网AVC1.0 不支持SVG,APF等设备.");
				InitError = false;
			}else{
				//普通配网项目
				zjxt_msg.show("普通配网项目初始化...");
//				zjxt_msg.show("初始化配网母线...");
//				sql = "select t.*,m.UABYCID from tblfeedbus t inner join tblfeedbusmeasure m on t.id=m.id";
//		
//				rSet = stat.executeQuery(sql);
//				while(rSet.next()){
//					String gid = rSet.getString("GRAPHID");
//					zFeederLine feederLine = (zFeederLine)zjxt_CimBuild.GetById(gid);
//					if(feederLine == null)
//					{
//						zjxt_msg.showwarn(rSet.getString("NAME")+" 定义馈线错误. 馈线id:"+gid);
//						InitError = true;
//						break;
//					}
//					zBusbarSection bus = zjxt_CimBuild.newBusbarSection(feederLine);
//					bus.setName(rSet.getString("NAME"));
//					bus.setMrID(rSet.getString("id"));
//					bus.uabycid = rSet.getString("UABYCID");
//					bus.graphid = gid;
//					Mapping(rSet, bus);
//				}

				zjxt_msg.show("初始化补偿电容...");
				sql = "SELECT *,t2.NOMINALVOLTAGE FROM TBLFEEDCAPACITOR t1 left join tblvoltagelevel t2 on t1.VOLTAGELEVELID=t2.id";
				rSet = stat.executeQuery(sql);
				while(rSet.next()){
					String fid = rSet.getString("FEEDID");
					String name = rSet.getString("NAME");
					String id = rSet.getString("id");
					//if(!topo.zNodeList.containsKey(id)) continue;
					zFeederLine fLine = (zFeederLine)zjxt_CimBuild.GetById(fid);
					if(fLine == null)
					{
						zjxt_msg.showwarn("电容【{}】引用了不存在的上级馈线id {}",name,fid);
						InitError = true;
						return;
					}
					zCapacitor obj = zjxt_CimBuild.newCapacitor(fLine, false);
					obj.line = fLine;
					obj.setName(rSet.getString("name"));
					obj.setMrID(rSet.getString("id"));
					obj.COMPENSATEPOINTID = rSet.getString("COMPENSATEPOINTID");	
					obj.VLID = rSet.getString("VOLTAGELEVELID");
					obj.vlid = rSet.getInt("NOMINALVOLTAGE");
					obj.voltage = voltageMap.get(obj.VLID);
					obj.HasItems = rSet.getBoolean("HASITEMS");//是否包含子组，一般高压电容分为两组子电容，需要分别投切。
					obj.IsItem = false;
					obj.capacity = rSet.getDouble("RATEDCAPACITY");
					obj.volChange = rSet.getFloat("VOLTAGECHANGE");//投切电压变化值
					
					Mapping(rSet, obj);
				}
				zjxt_msg.show("初始化补偿电容子组...");
				sql = "select fi.*,fm.SWITCHYXID from tblfeedcapacitoritem fi left join tblfeedcapacitoritemmeasure fm on fm.ID=fi.ID order by fi.feedcapacitorid ";
				rSet = stat.executeQuery(sql);
				while(rSet.next()){
					String fcid = rSet.getString("feedcapacitorid");    
					String iid = rSet.getString("ID");
					String name = rSet.getString("NAME");
					zCapacitor cg = (zCapacitor)zjxt_CimBuild.GetById(fcid);  //取得电容器组
					if(cg == null)
					{
						zjxt_msg.showwarn("电容子组【{}】引用了不存在的上级电容器组Id {}",name,fcid);
						InitError = true;
						return;
					}
					zFeederLine feederLine = (zFeederLine)cg.getFeederLine();
					
					zCapacitor obj = zjxt_CimBuild.newCapacitor(feederLine, true);
					obj.line = feederLine;
					obj.graphid = cg.graphid;
					obj.setName(name);
					obj.setMrID(iid);
					//obj.AarrayType = rSet.getString("ArrayType");  //电容器组号
					//obj.itemType = rSet.getString("COMPENSATIONMODE");      //电容器所补相位
					obj.Id = iid;
					obj.HasItems = false;
					obj.IsItem = true;
					obj.volChange = rSet.getFloat("VOLTAGECHANGE");  //电压改变量
					obj.RATEDCAPACITY = rSet.getFloat("RATEDCAPACITY");  //容量
					obj.SWITCHYXID = rSet.getString("SWITCHYXID");                //控制开关遥信ID。
					obj.COMPENSATEPOINTID = cg.COMPENSATEPOINTID;
					obj.capacity = rSet.getDouble("RATEDCAPACITY");
					obj.MyGroup = cg;
					
					obj.VLID = cg.VLID;
					obj.vlid = cg.vlid;
					Mapping(rSet, obj);
					cg.addItem(obj);			
				}
				
				zjxt_msg.show("初始化配变...");
				sql = "SELECT t2.*,t4.HWRC, t3.LOWSTEP,t3.HIGHSTEP,t3.STEPVOLTAGEINCREMENT,t5.NOMINALVOLTAGE FROM "
						+ "TBLFEEDTRANS t2 "
						+ "left join tbltapchangertype t3 on t2.TAPCHANGERID=t3.id "
						+ "left join tbltransformertype t4 on t2.TRANSFORMERTYPEID=t4.id "
						+ "left join tblvoltagelevel t5 on t2.voltagelevelid=t5.id "
						+ "where t2.ISONLOADTAPCHANGER=1"; //有载调压
				rSet = stat.executeQuery(sql);
				while(rSet.next()){
					String name = rSet.getString("NAME");    
					String id = rSet.getString("ID");
					//if(!topo.zNodeList.containsKey(id)) continue;
					String fid = rSet.getString("FEEDID");          
					zFeederLine fLine = (zFeederLine)zjxt_CimBuild.GetById(fid);
					if(fLine == null)
					{
						zjxt_msg.showwarn("【{}】引用了不存在的上级馈线id {}",name,fid);
						InitError = true;
						return;
					}
					zTransformerFormer obj = zjxt_CimBuild.newTransformerFormer(fLine);
					obj.setName(rSet.getString("NAME"));
					obj.setMrID(rSet.getString("id"));
					obj.capacity = rSet.getDouble("HWRC");
					obj.lowStep = rSet.getInt("LOWSTEP");
					obj.highStep = rSet.getInt("HIGHSTEP");
					obj.VLID = rSet.getString("VOLTAGELEVELID");  //电压ID
					obj.vlid = rSet.getInt("NOMINALVOLTAGE");	//电压值
					//obj.stepvoltageincrement = rSet.getDouble("STEPVOLTAGEINCREMENT");
					int u2 = 400; //默认有载配变额定输出电压为400V;
					double k = rSet.getDouble("STEPVOLTAGEINCREMENT"); //调压变比.
					obj.stepvoltageincrement = k;//((1 - 1/(1+k)) + (1/(1-k) - 1))*u2*0.5;  //计算调压电压变化值
					obj.line = fLine;
					
//					if("1".equals(obj.VLID)) {
//						obj.vlid = 220;
//					} else if("2".equals(obj.VLID)) {
//						obj.vlid = 10000;
//					} else if("3".equals(obj.VLID)) {
//						obj.vlid = 220;
//					}
					obj.voltage = voltageMap.get(obj.VLID);
					obj.graphid = fid;
					Mapping(rSet, obj);
				}
				
				zjxt_msg.show("初始化调压器...");
				sql = "SELECT t2.*,t3.LOWSTEP,t3.HIGHSTEP,t3.STEPVOLTAGEINCREMENT,t4.NOMINALVOLTAGE FROM "
						+ "tblfeedvoltageregulator t2 "
						+ "left join tbltapchangertype t3 on t3.id=t2.TAPCHANGERID "
						+ "left join tblvoltagelevel t4 on t2.voltagelevelid=t4.id";
				rSet = stat.executeQuery(sql);
				while(rSet.next()){
					String id = rSet.getString("id");
					//if(!topo.zNodeList.containsKey(id)) continue;
					String fid = rSet.getString("feedid");  
					String name = rSet.getString("NAME");
					zFeederLine fLine = (zFeederLine)zjxt_CimBuild.GetById(fid);
					if(fLine == null)
					{
						zjxt_msg.showwarn("【{}】引用了不存在的上级馈线id {}",name,fid);
						InitError = true;
						return;
					}
					zVoltageRegulator obj = zjxt_CimBuild.newVoltageRegulator(fLine);
					obj.lowStep = rSet.getInt("LOWSTEP");
					obj.highStep = rSet.getInt("HIGHSTEP");
					//obj.stepvoltageincrement = rSet.getDouble("STEPVOLTAGEINCREMENT");
					obj.capacity = rSet.getDouble("RATEDCAPACITY");
					obj.setName(name);
					obj.setMrID(id);
					obj.line = fLine;
					obj.VLID = rSet.getString("VOLTAGELEVELID");
					obj.vlid = rSet.getInt("NOMINALVOLTAGE");
					int u2 = 10000; //默认有载调压变额定输出电压为10000V;
					double k = rSet.getDouble("STEPVOLTAGEINCREMENT"); //调压变比.
					obj.stepvoltageincrement = k;//((1 - 1/(1+k)) + (1/(1-k) - 1))*u2*0.5;  //计算调压电压变化值
					obj.voltage = voltageMap.get(obj.VLID);
					//obj.graphid = fid;
					Mapping(rSet, obj);
				}
				
				zjxt_msg.show("初始化开关...");
				sql = "select t1.*,t.switchyxid from tblfeedswitch t1 left join tblfeedswitchmeasure t on t.id=t1.id";
				rSet = stat.executeQuery(sql);
				while(rSet.next()){
					String fid = rSet.getString("feedid");      
					String name = rSet.getString("NAME");
					zFeederLine fLine = (zFeederLine)zjxt_CimBuild.GetById(fid);
					if(fLine == null)
					{
						zjxt_msg.showwarn("【{}】引用了不存在的上级馈线id {}",name,fid);
						InitError = true;
						return;
					}
					zBreaker obj = zjxt_CimBuild.newBreaker(fLine);
					obj.setName(name);
					obj.setMrID(rSet.getString("id"));
					obj.line = fLine;
					obj.setElementStyle("1");
					obj.YXID = rSet.getString("switchyxid");
					Mapping(rSet, obj);
				}
			}
			
			//Init_SmartPrj();
//			Init_SmartEquipmentProperty();			
			dbConnection.close();
			//print cblist for debug
			for(PowerSystemResource psr : zjxt_CimBuild.cbList){
				System.out.println(psr.getName()+'\t'+psr.getMrID());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			InitError = true;
			zjxt_msg.showwarn("Init_PowerNet()->", e);
		}		
	}	
		
	public static void Init_State() throws Exception {
		zjxt_msg.show("初始化状态...");
		for (Iterator<PowerSystemResource> iterator = zjxt_CimBuild.cbList
				.iterator(); iterator.hasNext();) {
			PowerSystemResource obj = (PowerSystemResource) iterator.next();
			zjxt_msg.show(obj.getName()+" id:"+obj.getMrID());
			zjxt_State.Add(obj.getMrID());
		}
		zjxt_State.LoadFromDB();

	}
	
	public static void Init_Measure(){
		zjxt_msg.show("开始初始化总量测,必须确保遥测遥信表controlarea,czh,ych,yxh字段不能为null。");
		try {
			
			zjxt_CimBuild.Measure.Init_ycyx();
			zjxt_CimBuild.Measure.Init_yk();
			zjxt_CimBuild.Measure.Init_yt();
			
		} catch (Exception e) {
			InitError = true;
			zjxt_msg.showwarn("Init_Measure()->", e);
		}
	}
	
	public static void Init_Protect(){
		zjxt_msg.show("开始初始化保护信息...");
		try {
			
			zjxt_ProtectionTable.LoadFromDb();
			
		} catch (Exception e) {
			InitError = true;
			zjxt_msg.showwarn("Init_Protect()->", e);
		}
	}
	
	
	private static boolean addProperty(zjxt_Property property,String MeasureId,String ProName,boolean IsYcType){
		if(MeasureId == null){
			zjxt_msg.showwarn("addProperty->MeasureId=null, 量测名:"+ProName);
			return false;
		}
		Object obj = null;
		if(IsYcType){
			zjxt_yc yc = zjxt_CimBuild.Measure.GetYc(MeasureId);
			if(yc == null){
				String Name = property.Owner.getName();
				String msg = zjxt_msg.showwarn("[{}]该YCID[{}]不存在, 量测名:{}",Name,MeasureId,ProName);
				System.err.println(msg);
				return false;
			}
			obj = property.Add(MeasureId, ProName, yc.ca, yc.czh, yc.ych);
		}else{
			zjxt_yx yx = zjxt_CimBuild.Measure.GetYx(MeasureId);
			if(yx == null){
				String Name = property.Owner.getName();
				String msg = zjxt_msg.showwarn("[{}]该YXID[{}]不存在, 量测名:{}",Name,MeasureId,ProName);
				System.err.println(msg);
				return false;
			}
			obj = property.Add(MeasureId, ProName, yx.ca, yx.czh, yx.yxh);	
		}
		if(obj != null)
			return true;
		else
			return false;
	}
	
//	private static void addproperty_tf(zTransformerFormer tf,String ycid, String proName){
//		if(ycid == null){
//			zjxt_msg.show("addproperty_tyq->YCID=null,设备名:"+tf.getName()+" YC类型:"+proName);
//			return;
//		}
//		zjxt_yc yc = zjxt_CimBuild.Measure.GetYc(ycid);
//		if(yc == null){
//			zjxt_msg.show("addproperty_comp->该YCID不存在于遥测表:"+ycid+" 设备名:"+tf.getName()+" YC类型:"+proName);
//			return;
//		}
//		tf.property.Add(ycid, proName, yc.ca, yc.czh, yc.ych);
//	}
//	
//	private static void addproperty_apf(zApf apf,String ycid, String proName){
//		if(ycid == null){
//			zjxt_msg.show("addproperty_apf->YCID=null,设备名:"+apf.getName()+" YC类型:"+proName);
//			return;
//		}
//		zjxt_yc yc = zjxt_CimBuild.Measure.GetYc(ycid);
//		if(yc == null){
//			zjxt_msg.show("addproperty_apf->该YCID不存在于遥测表:"+ycid+" 设备名:"+apf.getName()+" YC类型:"+proName);
//			return;
//		}
//		apf.property.Add(ycid, proName, yc.ca, yc.czh, yc.ych);
//	}
//	
//	private static void addproperty_yx_apf(zApf apf,String yxid, String proName){
//		if(yxid == null){
//			zjxt_msg.show("addproperty_yx_apf->YXID=null,设备名:"+apf.getName()+" YX类型:"+proName);
//			return;
//		}
//		zjxt_yx yx = zjxt_CimBuild.Measure.GetYx(yxid);
//		if(yx == null){
//			zjxt_msg.show("addproperty_yx_apf->该YXID不存在于遥测表:"+yxid+" 设备名:"+apf.getName()+" YX类型:"+proName);
//			return;
//		}
//		apf.property.Add(yxid, proName, yx.ca, yx.czh, yx.yxh);
//	}
//	
//	private static void addproperty_balance(zTpunbalance balance,String ycid, String proName){
//		if(ycid == null){
//			zjxt_msg.show("addproperty_balance->YCID=null,设备名:"+balance.getName()+" YC类型:"+proName);
//			return;
//		}
//		zjxt_yc yc = zjxt_CimBuild.Measure.GetYc(ycid);
//		if(yc == null){
//			zjxt_msg.show("addproperty_balance->该YCID不存在于遥测表:"+ycid+" 设备名:"+balance.getName()+" YC类型:"+proName);
//			return;
//		}
//		balance.property.Add(ycid, proName, yc.ca, yc.czh, yc.ych);
//	}
//	
//	private static void addproperty_yx_balance(zTpunbalance balance,String yxid, String proName){
//		if(yxid == null){
//			zjxt_msg.show("addproperty_yx_balance->YXID=null,设备名:"+balance.getName()+" YX类型:"+proName);
//			return;
//		}
//		zjxt_yx yx = zjxt_CimBuild.Measure.GetYx(yxid);
//		if(yx == null){
//			zjxt_msg.show("addproperty_yx_balance->该YXID不存在于遥测表:"+yxid+" 设备名:"+balance.getName()+" YX类型:"+proName);
//			return;
//		}
//		balance.property.Add(yxid, proName, yx.ca, yx.czh, yx.yxh);
//	}
//	
//	
//	
	private static void addproperty_tyq(zVoltageRegulator tyq,String ycid, String proName){
		if(ycid == null){
			zjxt_msg.show("addproperty_tyq->YCID=null,设备名:"+tyq.getName()+" YC类型:"+proName);
			return;
		}
		zjxt_yc yc = zjxt_CimBuild.Measure.GetYc(ycid);
		if(yc == null){
			zjxt_msg.show("addproperty_comp->该YCID不存在于遥测表:"+ycid+" 设备名:"+tyq.getName()+" YC类型:"+proName);
			return;
		}
		tyq.property.Add(ycid, proName, yc.ca, yc.czh, yc.ych);
	}
	
	private static void addproperty_yx_tyq(zVoltageRegulator tyq,String yxid, String proName){
		if(yxid == null){
			zjxt_msg.show("addproperty_tyq->YXID=null,设备名:"+tyq.getName()+" YX名:"+proName);
			return;
		}
		zjxt_yx yx = zjxt_CimBuild.Measure.GetYx(yxid);
		if(yx == null){
			zjxt_msg.show("addproperty_comp->该YXID不存在于遥测表:"+yxid+" 设备名:"+tyq.getName()+" YX名:"+proName);
			return;
		}
		tyq.property.Add(yxid, proName, yx.ca, yx.czh, yx.yxh);
	}
//	
//	private static void addproperty_comp(zCompensator comp,String ycid, String proName){
//		if(ycid == null){
//			zjxt_msg.show("addproperty_comp->YCID=null,设备名:"+comp.getName()+" YC类型:"+proName);
//			return;
//		}
//		zjxt_yc yc = zjxt_CimBuild.Measure.GetYc(ycid);
//		if(yc == null){
//			zjxt_msg.show("addproperty_comp->该YCID不存在于遥测表:"+ycid+" 设备名:"+comp.getName()+" YC类型:"+proName);
//			return;
//		}
//		comp.property.Add(ycid, proName, yc.ca, yc.czh, yc.ych);
//	}
//	
//	private static void addproperty_tsf(zTSF tsf,String ycid, String proName){
//		if(ycid == null){
//			zjxt_msg.show("addproperty_tsf->YCID=null,设备名:"+tsf.getName()+" Measure名:"+proName);
//			return;
//		}
//		zjxt_yc yc = zjxt_CimBuild.Measure.GetYc(ycid);
//		if(yc == null){
//			zjxt_msg.show("addproperty_tsf->该YCID不存在于遥测表:"+ycid+" 设备名:"+tsf.getName()+" Measure名:"+proName);
//			return;
//		}
//		tsf.property.Add(ycid, proName, yc.ca, yc.czh, yc.ych);
//	}
//	
//	private static void addproperty_yx_tsf(zTSF tsf,String yxid, String proName){
//		if(yxid == null){
//			zjxt_msg.show("addproperty_yx_tsf->YXID=null,设备名:"+tsf.getName()+" Measure名:"+proName);
//			return;
//		}
//		zjxt_yx yx = zjxt_CimBuild.Measure.GetYx(yxid);
//		if(yx == null){
//			zjxt_msg.show("addproperty_yx_tsf->该YXID不存在于遥信表:"+yxid+" 设备名:"+tsf.getName()+" Measure名:"+proName);
//			return;
//		}
//		tsf.property.Add(yxid, proName, yx.ca, yx.czh, yx.yxh);
//	}
//	
//	private static void addproperty_svg(zSVG svg,String ycid, String proName){
//		if(ycid == null){
//			zjxt_msg.show("addproperty_svg->YCID=null,设备名:"+svg.getName()+" Measure名:"+proName);
//			return;
//		}
//		zjxt_yc yc = zjxt_CimBuild.Measure.GetYc(ycid);
//		if(yc == null){
//			zjxt_msg.show("addproperty_svg->该YCID不存在于遥测表:"+ycid+" 设备名:"+svg.getName()+" Measure名:"+proName);
//			return;
//		}
//		svg.property.Add(ycid, proName, yc.ca, yc.czh, yc.ych);
//	}
//	
//	private static void addproperty_yx_svg(zSVG svg,String yxid, String proName){
//		if(yxid == null){
//			zjxt_msg.show("addproperty_yx_svg->YXID=null,设备名:"+svg.getName()+" Measure名:"+proName);
//			return;
//		}
//		zjxt_yx yx = zjxt_CimBuild.Measure.GetYx(yxid);
//		if(yx == null){
//			zjxt_msg.show("addproperty_yx_svg->该YXID不存在于遥信表:"+yxid+" 设备名:"+svg.getName()+" Measure名:"+proName);
//			return;
//		}
//		svg.property.Add(yxid, proName, yx.ca, yx.czh, yx.yxh);
//	}
	
	public static void Init_BasicProperty()throws Exception{
		String eid = "";
		try {
			zjxt_msg.show("初始化设备基础属性...");
			Connection connection = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = connection.createStatement();
			String sql = "select t.id,t.parentid,t.groupid from tblelement t";
			ResultSet rSet = stat.executeQuery(sql);
			while(rSet.next()){
			    eid = rSet.getString("ID");
				PowerSystemResource psr = zjxt_CimBuild.GetById(eid);
				
				if(psr == null){
					continue;
				}
				psr.groupid = rSet.getString("groupid");
				psr.parentid = rSet.getString("parentid");
				
				//Mapping(rSet, psr);
			}						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			zjxt_msg.showwarn("Init_BasicProperty->"+e.toString()+" id:"+eid);
		}
	}
	
	public static void Init_CapaProperty()throws Exception{
		try {
			zjxt_msg.show("初始化配电电容器量测...");
			Connection connection = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = connection.createStatement();
			String sql = "select t.*,c.name,c.schemeid from tblfeedcapacitormeasure t inner join tblfeedcapacitor c on t.id=c.id";
			ResultSet rSet = stat.executeQuery(sql);
			while(rSet.next()){
				String eid = rSet.getString("ID");
				zCapacitor comp = (zCapacitor)zjxt_CimBuild.GetById(eid);  
				if(comp == null){
					zjxt_msg.showwarn("Init_CompProperty->电容量测初始化失败! ID:"+eid);
					continue;
				}
				comp.prop = comp.property;	
				comp.SWITCHYXID = rSet.getString("SWITCHYXID");
				//comp.SCHEMEID =  rSet.getString("SCHEMEID");
				comp.parentId = topo.zNodeList.get(eid).parentId;
				comp.Id = eid;
				boolean b = Mapping(rSet, comp.property);
				if(!b){
					InitError = true;
					return;
				}
				//comp.U = comp.property.getyc("UYCID");
				//comp.Q = comp.property.getyc("QYCID");
			}	
			
			zjxt_msg.show("初始化配电电容器子组量测...");
			sql = "SELECT t.*,c.FEEDCAPACITORID FROM tblfeedcapacitoritemmeasure t INNER JOIN tblfeedcapacitoritem c ON t.id=c.id";
			rSet = stat.executeQuery(sql);
			while(rSet.next()) {
				String eid = rSet.getString("ID");
				String parentId = rSet.getString("FEEDCAPACITORID");
				zCapacitor comp = (zCapacitor)zjxt_CimBuild.GetById(parentId);  
				comp = comp.getItem(eid); //电容子组对象只能从电容器对象中获得。
				if(comp == null) {
					zjxt_msg.showwarn("Init_CompProperty->电容子组量测初始化失败! 找不到电容子组ID:"+eid);
					continue;
				}
				
				Mapping(rSet, comp.property);
				comp.prop = comp.property;
//				comp.SCHEMEID =  rSet.getString("SCHEMEID");
				for(zCapacitor c:comp.ItemList) {
					if(c.Id.equals(eid)) {
						comp.SWITCHYXID = rSet.getString("SWITCHYXID");
					}
				}
			}	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			zjxt_msg.showwarn("Init_CompItemProperty->", e);
		}
	}
	public static void Init_TfProperty()throws Exception{
		String eid = "";
		//String name = "";
		
		try {
			zjxt_msg.show("初始化变压器量测...");
			Connection connection = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = connection.createStatement();
			String sql = "select t.* from tblfeedtransmeasure t inner join tblfeedtrans f on t.id=f.id";
					//"select * from tblfeedtransmeasure";
			ResultSet rSet = stat.executeQuery(sql);
			String ycid;
			while(rSet.next()){
			    eid = rSet.getString("ID");
				zTransformerFormer tf = (zTransformerFormer)zjxt_CimBuild.GetById(eid);
				zjxt_Property p = tf.property;
				
				//tf.U = tf.property.getyc("UYCID");			
				ycid = rSet.getString("UYCID");  //当前电压量测
				addProperty(p, ycid, "U", true);
				
				ycid = rSet.getString("PYCID");
				addProperty(p, ycid, "P", true);
				
				ycid = rSet.getString("QYCID");
				addProperty(p, ycid, "Q", true);

				ycid = rSet.getString("IYCID");
				addProperty(p, ycid, "I", true);

				ycid = rSet.getString("TAPCHANGERYCID");  //当前档位
				addProperty(p, ycid, "TAP", true);

				ycid = rSet.getString("ACTIONCOUNTINDAYYCID"); //日动作次数
				addProperty(p, ycid, "D_ACT", true);
				
				ycid = rSet.getString("ACTIONCOUNTTOTALYCID"); //总动作次数
				addProperty(p, ycid, "T_ACT", true);
				tf.prop = tf.property;
			}						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			zjxt_msg.showwarn("Init_Property->"+e.toString()+" id:"+eid);
		}
	}
	public static void Init_TyqProperty()throws Exception{
		try {
			zjxt_msg.show("初始化调压器量测...");
			Connection connection = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = connection.createStatement();
			String sql = "SELECT t2.* FROM tblfeedvoltageregulatormeasure t2";
			ResultSet rSet = stat.executeQuery(sql);
			while(rSet.next()){
				String eid = rSet.getString("ID");
				zVoltageRegulator tyq = (zVoltageRegulator)zjxt_CimBuild.GetById(eid);
				if(tyq == null) continue;
				boolean b = Mapping(rSet, tyq.property);
				if(!b){
					InitError = true;
					return;
				}
				tyq.prop = tyq.property;			
				tyq.parentId = topo.zNodeList.get(eid).parentId;
				tyq.Id = eid;
//				for(int i=0; i<nodes.size(); i++) {
//					if(nodes.get(i).Id.equals(eid)) {
//						nodes.get(i).prop = tyq.property;
//						tyq.node = nodes.get(i);
//						tyq.Id = nodes.get(i).Id;
//						tyq.parentid = nodes.get(i).parentId;
//					}
//				}
				
				
			}						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			zjxt_msg.showwarn("Init_TyqProperty->", e);
		}
	}
	
//	public static void Init_ApfProperty()throws Exception {
//		try {
//			zjxt_msg.show("初始化APF量测...");
//			Connection connection = zjxt_ConnectionPool.Instance().getConnection();
//			Statement stat = connection.createStatement();
//			String sql = "SELECT * FROM tblfeedapfmeasure";
//			ResultSet rSet = stat.executeQuery(sql);
//			while(rSet.next()){
//				String eid = rSet.getString("ID");
//				zApf apf = (zApf)zjxt_CimBuild.GetById(eid);
//				if(apf == null) continue;
//				apf.parentId = topo.zNodeList.get(apf.getMrID()).parentId;
//				apf.Id = apf.getMrID();
////				for(int i=0; i<nodes.size(); i++) {
////					if(nodes.get(i).Id.equals(eid)) {
////						nodes.get(i).prop = apf.property;
////						apf.node = nodes.get(i);
////						apf.Id = nodes.get(i).Id;
////						apf.parentid = nodes.get(i).parentId;
////						
////					}
////				}
//				Mapping(rSet, apf.property);
//				apf.prop = apf.property;
//				String ycid = rSet.getString("UYCID");
//				addproperty_apf(apf, ycid, "UYCID");
//				apf.U = apf.property.getyc("UYCID");
//				apf.P = apf.property.getyc("PYCID");
//				apf.Q = apf.property.getyc("QYCID");
//				ycid = rSet.getString("IYCID");
//				addproperty_apf(apf, ycid, "IYCID");
//				
//				ycid = rSet.getString("PYCID");
//				addproperty_apf(apf, ycid, "PYCID");
//				
//				ycid = rSet.getString("QYCID");
//				addproperty_apf(apf, ycid, "QYCID");
//				
//				ycid = rSet.getString("PFYCID");
//				addproperty_apf(apf, ycid, "PFYCID");
//				
//				ycid = rSet.getString("THDUYCID");
//				addproperty_apf(apf, ycid, "THDUYCID");
//				
//				ycid = rSet.getString("THDIYCID");
//				addproperty_apf(apf, ycid, "THDIYCID");
//				
//				ycid = rSet.getString("QCYCID");
//				addproperty_apf(apf, ycid, "QCYCID");
//				
//				ycid = rSet.getString("CONSERVATIONYCID");
//				addproperty_apf(apf, ycid, "CONSERVATIONYCID");
//				
//				String yxid = rSet.getString("GUARDSIGNALYXID");
//				
//				
////				for(int i=0; i<nodes.size(); i++) {
////					if(nodes.get(i).Id.equals(eid)) {
////						nodes.get(i).prop = apf.property;
////						apf.node = nodes.get(i);
////					}
////				}
//				
//				addproperty_yx_apf(apf, yxid, "GUARDSIGNALYXID");
//				
//			}						
//		} catch (Exception e) {
//			zjxt_msg.showwarn("Init_Property->", e);
//		}
//	}
	
//	public static void Init_balanceProperty()throws Exception {
//		try {
//			zjxt_msg.show("初始化三相不平衡量测...");
//			Connection connection = zjxt_ConnectionPool.Instance().getConnection();
//			Statement stat = connection.createStatement();
//			String sql = "SELECT * FROM tblfeedtpunbalancemeasure";
//			ResultSet rSet = stat.executeQuery(sql);
//			while(rSet.next()){
//				String eid = rSet.getString("ID");
//				zTpunbalance balance = (zTpunbalance)zjxt_CimBuild.GetById(eid);
//				if(balance == null) continue;
//				balance.parentId = topo.zNodeList.get(balance.getMrID()).parentId;
//				balance.Id = balance.getMrID();
////				for(int i=0; i<nodes.size(); i++) {
////					if(nodes.get(i).Id.equals(eid)) {
////						nodes.get(i).prop = balance.property;
////						balance.node = nodes.get(i);
////						balance.Id = nodes.get(i).Id;
////						balance.parentid = nodes.get(i).parentId;
////						
////					}
////				}
//				
//				Mapping(rSet, balance.property);
//				balance.prop = balance.property;
//				String ycid = rSet.getString("UYCID");
//				addproperty_balance(balance, ycid, "UYCID");
//				balance.U = balance.property.getyc("UYCID");
//				balance.P = balance.property.getyc("PYCID");
//				balance.Q = balance.property.getyc("QYCID");
//				
//				
//				ycid = rSet.getString("IYCID");
//				addproperty_balance(balance, ycid, "IYCID");
//				
//				ycid = rSet.getString("PYCID");
//				addproperty_balance(balance, ycid, "PYCID");
//				
//				ycid = rSet.getString("QYCID");
//				addproperty_balance(balance, ycid, "QYCID");
//				
//				ycid = rSet.getString("PFYCID");
//				addproperty_balance(balance, ycid, "PFYCID");
//				
//				ycid = rSet.getString("SXBPHDYCID");
//				addproperty_balance(balance, ycid, "SXBPHDYCID");
//				
//				ycid = rSet.getString("CONSERVATIONYCID");
//				addproperty_balance(balance, ycid, "CONSERVATIONYCID");
//				
//				String yxid = rSet.getString("GUARDSIGNALYXID");
//				
//				
////				for(int i=0; i<nodes.size(); i++) {
////					if(nodes.get(i).Id.equals(eid)) {
////						nodes.get(i).prop = balance.property;
////						balance.node = nodes.get(i);
////					}
////				}
//				addproperty_yx_balance(balance, yxid, "GUARDSIGNALYXID");
//			}						
//		} catch (Exception e) {
//			zjxt_msg.showwarn("Init_Property->", e);
//		}
//	}
	
//	public static void Init_SvgProperty()throws Exception {
//		try {
//			zjxt_msg.show("初始化SVG量测...");
//			Connection connection = zjxt_ConnectionPool.Instance().getConnection();
//			Statement stat = connection.createStatement();
//			String sql = "SELECT * FROM tblfeedsvgmeasure";
//			ResultSet rSet = stat.executeQuery(sql);
//			while(rSet.next()){
//				String eid = rSet.getString("ID");
//				zSVG svg = (zSVG)zjxt_CimBuild.GetById(eid);
//				if(svg == null) continue;
//				svg.parentId = topo.zNodeList.get(svg.getMrID()).parentId;
//				svg.Id = svg.getMrID();
////				for(int i=0; i<nodes.size(); i++) {
////					if(nodes.get(i).Id.equals(eid)) {
////						nodes.get(i).prop = svg.property;
////						svg.node = nodes.get(i);
////						svg.Id = nodes.get(i).Id;
////						svg.parentid = nodes.get(i).parentId;
////					}
////				}
//				
//				Mapping(rSet, svg.property);
//				svg.prop = svg.property;
//				String ycid = rSet.getString("UYCID");
//				addproperty_svg(svg, ycid, "UYCID");
//				svg.U = svg.property.getyc("UYCID");
//				svg.P = svg.property.getyc("PYCID");
//				svg.Q = svg.property.getyc("QYCID");
//				
//				ycid = rSet.getString("IYCID");
//				addproperty_svg(svg, ycid, "IYCID");
//				
//				ycid = rSet.getString("PYCID");
//				addproperty_svg(svg, ycid, "PYCID");
//				
//				ycid = rSet.getString("QYCID");
//				addproperty_svg(svg, ycid, "QYCID");
//				
//				ycid = rSet.getString("PFYCID");
//				addproperty_svg(svg, ycid, "PFYCID");
//				
//				ycid = rSet.getString("QCYCID");
//				addproperty_svg(svg, ycid, "QCYCID");
//				
//				ycid = rSet.getString("CONSERVATIONYCID");
//				addproperty_svg(svg, ycid, "CONSERVATIONYCID");
//				
//				String yxid = rSet.getString("GUARDSIGNALYXID");
//				
//				
////				for(int i=0; i<nodes.size(); i++) {
////					if(nodes.get(i).Id.equals(eid)) {
////						nodes.get(i).prop = svg.property;
////						svg.node = nodes.get(i);
////					}
////				}
//				addproperty_svg(svg, yxid, "GUARDSIGNALYXID");
//				
//			}			
//			connection.close();
//		} catch (Exception e) {
//			zjxt_msg.showwarn("Init_Property->", e);
//		}
//	}
	
	//Mapping函数：数据库实体映射到drools规则文件，使规则文件可以访问实体表的各字段。
	
	public static void Mapping(ResultSet resultSet,PowerSystemResource psr){
		try {
			ResultSetMetaData resultMeta = resultSet.getMetaData();
			
			for(int i=1;i<=resultMeta.getColumnCount();i++){
				Object obj = resultSet.getObject(i);
				if(obj == null)
					continue;
				String key = resultMeta.getColumnName(i).toUpperCase();
				psr.add(key, obj);
			}

		} catch (Exception e) {
			// TODO: handle exception
			zjxt_msg.showwarn("Mapping->参数", e);
		}
	}
	
	public static boolean Mapping(ResultSet resultSet,zjxt_Property property){
		try {
	
			String key = "";
			String value = "";
			ResultSetMetaData resultMeta = resultSet.getMetaData();
			
			for(int i=1;i<=resultMeta.getColumnCount();i++){
				boolean isyc = true;
				boolean isMeasure = false;
				value = resultSet.getString(i);
				if(value == null || value.equals(""))
					continue;
				key = resultMeta.getColumnName(i).toUpperCase();
				if(key.indexOf("YXID")>0 || key.indexOf("YKID")>0){
					isyc = false;
					isMeasure = true;
				}else if(key.indexOf("YCID")>0 || key.indexOf("YTID")>0){
					isMeasure = true;
				}
				if(isMeasure){
					boolean b = addProperty(property, value, key, isyc);	
					if(!b)
						return false;
				}
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			zjxt_msg.showwarn("Mapping->量测", e);
			return false;
		}
	}
	
	public static void Init_ComControlParam() throws Exception {
		Connection connection = null;
		Statement stat = null;
		ResultSet rSet = null;
		try {
			zjxt_msg.show("初始化配电电容器参数值...");
			connection = zjxt_ConnectionPool.Instance().getConnection();
			stat = connection.createStatement();
			String sql = "select t.* from tblfeedcapacitorcontrol t inner join tblfeedcapacitor c on t.id=c.id";
			rSet = stat.executeQuery(sql);
			while(rSet.next()){
				String eid = rSet.getString("ID");
				zCapacitor comp = (zCapacitor)zjxt_CimBuild.GetById(eid);  
				if(comp == null){
					zjxt_msg.showwarn("Init_CompProperty->电容参数初始化失败! ID:"+eid);
					continue;
				}
				comp.controlParam.cosCtrlModycval = rSet.getInt("cosCtrlModycval");
				comp.controlParam.cosCtrlModytval = rSet.getInt("cosCtrlModytval");
				comp.controlParam.distantykval = rSet.getInt("distantykval");
				comp.controlParam.distantyxval = rSet.getInt("distantyxval");
				comp.controlParam.localykval = rSet.getInt("localykval");
				comp.controlParam.localyxval = rSet.getInt("localyxval");
				comp.controlParam.putoffykval = rSet.getInt("putoffykval");
				comp.controlParam.putOffyxval = rSet.getInt("putOffyxval");
				comp.controlParam.putonykval = rSet.getInt("putonykval");
				comp.controlParam.putOnyxval = rSet.getInt("putOnyxval");
				comp.controlParam.volCtrlModycval = rSet.getInt("volCtrlModycval");
				comp.controlParam.volCtrlModytval = rSet.getInt("volCtrlModytval");
				comp.controlParam.qctrlmodycval = rSet.getInt("qctrlmodycval");
				comp.controlParam.qctrlmodytval = rSet.getInt("qctrlmodytval");
			}
		} catch(Exception e) {
			zjxt_msg.showwarn("Init_CompParam->", e);
		} finally {
			if(rSet!=null) {
				rSet.close();
			}
			if(stat!=null) {
				stat.close();
			}
			if(connection!=null) {
				connection.close();
			}
		}
	}
	
	


		public static void Init_TransformerProperty()throws Exception{
		try {
			zjxt_msg.show("初始化配变量测...");
			Connection connection = zjxt_ConnectionPool.Instance().getConnection();
			Statement stat = connection.createStatement();
			String sql = "SELECT T3.* FROM TBLFEEDTRANS T2 "
					+ "INNER JOIN TBLFEEDTRANSMEASURE T3 ON T2.ID=T3.ID "
					+ "where t2.ISONLOADTAPCHANGER =1";  //where t2.ISOLTC =1; “ISOLTC 是否有载调压变”字段解释存疑 debug 2017-4-4
			ResultSet rSet = stat.executeQuery(sql);
			while(rSet.next()){
				String eid = rSet.getString("ID");
				zTransformerFormer tf = (zTransformerFormer)zjxt_CimBuild.GetById(eid);  
				if(tf == null){
					zjxt_msg.show("Init_TransformerProperty->配变量测初始化失败! ID:"+eid);
					continue;
				}
				tf.parentId = topo.zNodeList.get(tf.getMrID()).parentId;
				tf.Id = tf.getMrID();
//				for(int i=0; i<nodes.size(); i++) {
//					if(nodes.get(i).Id.equals(eid)) {
//						nodes.get(i).prop = tf.property;
//						tf.node = nodes.get(i);
//						tf.Id = nodes.get(i).Id;
//						tf.parentid = nodes.get(i).parentId;
//					}
//				}
				boolean b = Mapping(rSet, tf.property);
				if(!b){
					InitError = true;
					return;
				}
				tf.U = tf.property.getyc("UYCID");
				tf.UA = tf.property.getyc("UAYCID");
				tf.UB = tf.property.getyc("UBYCID");
				tf.UC = tf.property.getyc("UCYCID");
				
				//tf.P = tf.property.getyc("PYCID");
				tf.Q = tf.property.getyc("QYCID");
				tf.prop = tf.property;
			}						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			zjxt_msg.showwarn("Init_TransformerProperty->", e);
		}
	}
	
	//@SuppressWarnings("restriction")
	private static void extracted(Exception e) {
		zjxt_kernel.mlog.warn(e.toString());
	}

}

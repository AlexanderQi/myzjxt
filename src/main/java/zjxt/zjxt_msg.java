package zjxt;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.drools.zjxt.kernellib.Limit;
import com.drools.zjxt.kernellib.zjxt_CimBuild;
import com.drools.zjxt.kernellib.zjxt_ConnectionPool;
import com.drools.zjxt.kernellib.zjxt_kernel;

//import org.slf4j.impl.StaticLoggerBinder;

//import org.mvel2.sh.command.basic.ShowVars;


public class zjxt_msg {
	public static final String DY = "电压";
	public static final String CurrVol = "测点 电压:";
	public static final String DY_YSX = "电压越上限,";
	public static final String DY_YXX = "电压越下限,";
	public static final String DY_HG = "电压合格,";
	
	public static final String DY_YJ = " 动作后电压预计:";
	
	public static final String DY_A = "A相电压:";
	public static final String DY_B = "B相电压:";
	public static final String DY_C = "C相电压:";
	
	public static final String GongBu = "共补";
	
	public static final String WuSheBei = " 暂无设备可动作.";
	public static final String BuPingHeng = " 三相不平衡";
	public static final String Wu = " 暂无调节方案.";
	public static final String GL_YSX = "测点功率因数越上限,";
	public static final String GL_YXX = "测点功率因数越下限,";
	public static final String GL_HG = "功率因数合格 ";
	
	public static final String TYCL = "调压处理";
	public static final String YSJG = "预算结果:";
	public static final String Cos = "功率因数:";
	public static final String Cos_ = "功率因数";
	public static final String cos_ = "cos";
	public static final String vol_ = "vol";
	public static final String SX = "设定上限:";
	public static final String XX = "设定下限:";
	public static final String YSX = "越上限.";
	public static final String YXX = "越下限.";
	public static final String HG = "合格.";
	
	public static final String DongZuo = "动作";
	public static final String BiSuo = "闭锁";
	public static final String BaoHu = "保护";
	public static final String YiChang = "异常";
	public static final String CiShu = "次数";
	
	public static final String JianYi = "建议";
	public static final String KongZhi = "控制";
	public static final String GaoJing = "告警";
	public static final String YaoTiao = "遥调";
	public static final String YaoKong = "遥控";
	
	public static final String TIP = "提示";
	public static final String MuBiao = "目标值";
	public static final String MuBiaoSheDin = "根据负荷预测设定目标值";
	public static final String TouDianRong = "投入电容 ";
	public static final String QieDianRong = "切除电容 ";
	public static final String BSQJ = "闭锁期间，原因:";
	
	public static final String touTSF = "谐波电流越限，投入TSF";
	public static final String qieTSF = "电压越上限，切除TSF";
	
	
	public static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat df_time = new SimpleDateFormat(
			"HH:mm:ss");
//	public static SimpleDateFormat df_sss = new SimpleDateFormat(
//			"HH:mm:ss.SSS");
	
	public static int State1 = -1;
	public static int State2 = -1;
	public static int State3 = -1;
	
	
	public static String GetDateTime(){
		Date date = new Date();
		return df.format(date);
	}
	
	public static String GetTime(){
		Date date = new Date();
		return df_time.format(date);
	}
	
	public static String format(String str, String... args) {  
        for (int i = 0; i < args.length; i++) {  
            str = str.replaceFirst("\\{\\}", args[i]);  
        }  
        return str;  
    }  
  
    public static String format(String str, Object... args) {  
        for (int i = 0; i < args.length; i++) {  
            str = str.replaceFirst("\\{\\}", String.valueOf(args[i]));  
        }  
        return str;  
    } 
    
    public static synchronized void RemoveMsg(){
    	try {
    		Connection conn = zjxt_ConnectionPool.Instance().getConnection();
			Statement ps = conn.createStatement();
			String sql = "delete from tblmsglist where time_to_sec(timediff(current_timestamp,MSGTIME)) > 3600";
			ps.execute(sql);
			conn.close();
			
			sendMsg(5002,"zjxt RemoveMsg");
		} catch (SQLException e) {
			zjxt_kernel.mlog.error(e.toString());
		} catch (Exception e) {
			//e.printStackTrace();
			zjxt_kernel.mlog.error(e.toString());
		}
    }
	
	public static synchronized void sendMsg(int MsgTag,String msg, Object... args){
		String message = format(msg, args);
		Connection conn = null;
		try {
		    conn = zjxt_ConnectionPool.Instance().getConnection();
			PreparedStatement ps = null;
			 String sql = "INSERT INTO TBLMSGLIST (id,sender,receiver,msg,msgtag)"
					+ "VALUES(0, 'zjxt', 'anyone', ?, ?)";
			ps = conn.prepareStatement(sql);
			//java.util.Date date = new java.util.Date();
			//ps.setTimestamp(1, new Timestamp(date.getTime()));
			ps.setString(1, message);
			ps.setInt(2, MsgTag);
			ps.execute();
			conn.close();
		} catch (SQLException e) {
			zjxt_kernel.mlog.error(e.toString());
		} catch (Exception e) {
			//e.printStackTrace();
			zjxt_kernel.mlog.error(e.toString());
		}

	
	}
	
	public static synchronized void show(String msg, Object... args){
		sendMsg(0,msg,args);
		zjxt_kernel.mlog.info(msg, args);
	}
	
	public static synchronized void showdebug(String msg, Object... args){
		
		zjxt_kernel.mlog.debug(msg, args);
	}
	
	public static synchronized void showwarn(String msg, Object... args){
		sendMsg(5001,msg,args);
		zjxt_kernel.mlog.warn(msg, args);
	}
	
	public static String getLimitStr(String msg,float v,float c){
		String str = ".";
		if(v<Limit.upper_v220 && v>Limit.lower_v220)
			str += DY_HG;
		else if(v<Limit.lower_v220){
			str += DY_YXX;
		}
		else if(v>Limit.upper_v220){
			str += DY_YSX;
		}		
		
		if(c<Limit.lower_cos)
		{
			str += Cos_+YXX;
		}else {
			str += Cos_+HG;
			
		}
		return msg+zjxt_msg.CurrVol+v +"  "+ zjxt_msg.Cos+c+str;
	}
	
	public static void showlimit(String msg,float v,float c){
		String str = getLimitStr(msg,v, c);
		zjxt_msg.show(str);
	}
	
	public static void showlimit_(String msg,float v,float c){
		String str = ".";
		if(v<Limit.upper_v220 && v>Limit.lower_v220)
			str += DY_HG;
		else if(v<Limit.lower_v220){
			str += DY_YXX;
		}
		else if(v>Limit.upper_v220){
			str += DY_YSX;
		}		
		
		if(c<Limit.lower_cos)
		{
			str += Cos_+YXX;
		}else {
			str += Cos_+HG;
		}
		zjxt_msg.show(msg+zjxt_msg.CurrVol+v +"  "+ zjxt_msg.Cos+c+str);
	}
	
	public static void showlock(String elementid)throws Exception{
		try {
			String name = zjxt_CimBuild.GetById(elementid).getName();
			String msg = zjxt_State.GetLockState(elementid);
			show("*"+name+BSQJ+msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//zjxt_main.mlog.warn("showlock->"+e.toString());
			throw new Exception();
		}
	}
	

}

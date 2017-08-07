package com.drools.zjxt.kernellib;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;
//import org.apache.log4j.spi.LoggerFactory;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
//import org.drools.StatelessSession;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
//import org.drools.command.CommandFactory;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;

import com.drools.zjxt.kernellib.zjxt_CimBuild.zBusbarSection;
import com.softcore.cim.entity.Equipment;
import com.softcore.cim.entity.PowerSystemResource;

import zjxt.zjxt_Initialize;
import zjxt.zjxt_ProtectionTable;
import zjxt.zjxt_State;
import zjxt.zjxt_msg;

/**
 * This is a sample class to launch a rule.
 */
public class zjxt_kernel {
	
	@SuppressWarnings("restriction")
	public final static Logger mlog = org.slf4j.LoggerFactory.getLogger("zjxt");
	private static boolean LicensePast = true;
	public static String ZJXT_DRL = "rules/test.drl";
	public static JkParam jkParam = null;

	private static String log4j_ini = null;
	public static final void IniLog4j(String ini){
		if(ini == null || ini.equals(""))
			log4j_ini = "zjxt2-log4j.ini";
		else
			log4j_ini = ini;
		PropertyConfigurator.configure(log4j_ini);
	}
	
	public static final void launch() {
		try {
//			KieServices ks = KieServices.Factory.get();
//		    KieContainer kContainer = ks.getKieClasspathContainer();
//		    KieSession kSession = kContainer.newKieSession("ksession-rules");
//		    StatelessKieSession skSession = kContainer.newStatelessKieSession("ksession-rules");
			if(log4j_ini == null)
				IniLog4j(null);
			PropertyConfigurator.configure("zjxt2-log4j.ini");
			jkParam = JkParam.Instance();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			ShowDivideLine();
			mlog.warn(zjxt_msg.GetDateTime()
					+ " 系统开始运行. Ver: ZJXT2.1.201708051722"); //Ver: ZJXT2.0.201603231045 2016-3-23 10:45 2.0第一次定版本， 1.0大概是2014年的事情吧
			mlog.info(zjxt_ConnectionPool.Instance().getDbInfo());
			//			if (!jkParam.CheckLicense()) {
//				mlog.warn("已启动但证书错误，请确认程序已被授权.(数小时后停止运行)\r\n授权方法，将程序目录下license.txt发给程序提供方.\r\n");
//				LicensePast = false;
//			}

			
			KnowledgeBase kbase = readKnowledgeBase();
			StatefulKnowledgeSession ksession = kbase
					.newStatefulKnowledgeSession();
			KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory
					.newFileLogger(ksession, "zjxt");

			// go !
//			if (!zjxt_CimBuild.Measure.ConnectRtdb()) {
//				mlog.warn("连接实时库失败! IP:" + zjxt_CimBuild.Measure.rtdb_ip);
//				br.readLine();
//				br.close();
//				return;
//			}
			zjxt_Initialize.Init();
			if (zjxt_Initialize.InitError) {
				zjxt_msg.showwarn("初始化失败,请检查日志。");
				br.readLine();
				br.close();
				return;
			}

//			Timer timer = new Timer();
			SystemRun sRun = new SystemRun();
			sRun.ks = kbase.newStatelessKnowledgeSession();
//			sRun.skSession = skSession;
			
//			int interval = 4321;
//			if (JkParam.Instance().IsSmartPrj)
//				interval = 4567;
			int interval = 9991; //ms
			if (JkParam.Instance().IsSmartPrj)
				interval = 4567;
			interval = 19;  //s
			ScheduledExecutorService service = Executors  
	                .newSingleThreadScheduledExecutor();
			service.scheduleAtFixedRate(sRun, 1, interval, TimeUnit.SECONDS);
//			timer.schedule(sRun, 1000, interval);
			br.readLine();
			br.close();
			logger.close();
		} catch (Throwable t) {
			t.printStackTrace();
			mlog.error(t.getMessage());
		} finally {

		}
	}

	public static final void main(String[] args) {
		launch();
	}

	public static Resource DRL = null;

	//@Deprecated
	private static KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		if (DRL == null) {
			kbuilder.add(
					ResourceFactory.newClassPathResource(ZJXT_DRL, "utf-8"),
					ResourceType.DRL);
		} else {
			kbuilder.add(DRL, ResourceType.DRL);
		}

		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error : errors) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Could not parse knowledge.");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}

	public static void ShowDivideLine() {
		mlog.warn("=======================================================================================");
	}

	private static int timeTag1 = 6;
	private static int timeTag2 = 3;
	private static int timeTag3 = 0;
	private static int TickCount = 0;
	public static SimpleDateFormat XtDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private static class SystemRun implements Runnable {
//		public KnowledgeBase kb;
		public StatelessKnowledgeSession ks;
//		public StatelessKieSession skSession;

		// public List<PowerSystemResource> PsrList;
		// private Random random = new Random();
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (ks == null)
				return;
			try {
				if (zjxt_msg.State2 == 1) {
					mlog.warn("人工操作退出专家系统 " + zjxt_msg.GetDateTime());
//					cancel();
					System.exit(0);
					return;
				}
				timeTag1++;
				timeTag2++;
				TickCount++;
				if (timeTag1 >= 30) {
					timeTag1 = 0;
					zjxt_msg.RemoveMsg();
					Limit.Refresh();
//					timeTag3++;
//					if (timeTag3 >= 48) {
//						mlog.warn("未授权使用，程序已退出 " + zjxt_msg.GetDateTime());
////						cancel();
//						System.exit(0);
//						return;
//					}
				}
				
				ShowDivideLine();
				
				mlog.warn(zjxt_msg.GetDateTime() + " 执行周期.");
				zjxt_CimBuild.Measure.Refresh_ycyx();
				zjxt_State.Refresh(); // 设备状态刷新
				zjxt_ProtectionTable.Refresh(); // 设备保护刷新
				zjxt_State.AutoUnLock(); // 设备自动解锁检查
				zjxt_CimBuild.checkYKYTResult(); //检查调控反馈信息
				
				//if (timeTag2 >= 3) {
				//	timeTag2 = 0;
					zjxt_Cmd.CmdTraceDeal();
				//}
				
				
				
				if(TickCount == 3) { //每隔一分钟刷新一次
					TickCount = 0;
					System.out.println("刷新设备运行时间...");
					for(int i=0; i<zjxt_CimBuild.cbList.size(); i++) {
						PowerSystemResource powerSystemResource = zjxt_CimBuild.cbList.get(i);
						if(powerSystemResource instanceof Equipment) {
							Equipment equipment = (Equipment) powerSystemResource;
							if(!(equipment instanceof zBusbarSection)) {
								if(equipment.U>0) {
									equipment.prop.UpdateRunTime(1, 1); //投运一分钟
								} else {
									equipment.prop.UpdateRunTime(0, 1); //停运一分钟
								}
								
							}
						}
					}
				}
				
				
				//zjxt_CimBuild.loadX(); //加载电抗
				if(zjxt_kernel.jkParam.isJudgeDeadData)
				{
					zjxt_CimBuild.checkDeadData(); //判断死数据
				}
				zjxt_CimBuild.refreshNodeMesure();
				zjxt_CimBuild.list.clear();
//				zjxt_CimBuild.list.add(zjxt_CimBuild.cbList);
				mlog.warn("Calculate rules...\r\n");
				ks.execute(zjxt_CimBuild.cbList);
			} catch (Throwable t) {
				if(!"退出循环".equals(t.getCause().getMessage()))
				{
					mlog.warn("异常:" + t);
					System.exit(0); //退出虚拟机，让进程守护重新启动
				}
				extracted(t);
			}
		}

		private void extracted(Throwable e) {
			mlog.warn(e.toString());
		}

	}

}

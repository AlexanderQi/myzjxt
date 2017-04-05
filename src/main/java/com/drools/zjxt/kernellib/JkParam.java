/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drools.zjxt.kernellib;

import java.io.*;
import java.net.InetAddress;

import zjxt.zjxt_msg;

public class JkParam {

	public String cfg_host;
	public String cfg_server1, cfg_server2;// cur_server;
	public int cfg_104Port;
	public int cfg_insidePort;
	public int cfg_HotBackup = 0;
	public int cfg_ca = -1;
	public int cfg_rtdb = 0;
	public int cfg_file = 1;
	public boolean IsSmartPrj = false; // 是否是智能设备园区项目
	public boolean IsCreateRtdb = true;
	public boolean isJudgeDeadData = true; //是否判断死数据
	// public String UserPath;
	public String LocalHostName;
	public String CurrentPath;
	private static JkParam instance = null;

	public static JkParam Instance() {
		if (instance == null) {
			instance = new JkParam();
		}
		return instance;
	}

	private JkParam() {
		ReadEnv();
		ReadCfg();
	}

	public String dir;

	public void ReadEnv() {
		// UserPath = System.getenv("SC_JK");
		CurrentPath = System.getProperty("user.dir");
		LocalHostName = GetHostName();
		dir = System.getProperty("os.name") + " "
				+ System.getProperty("os.version") + "\njava "
				+ System.getProperty("java.version") + "\n"
				+ System.getProperty("user.name") + "\nUserPath=" + CurrentPath
				+ "\n";
		dir = LocalHostName + '\n' + dir;
		zjxt_msg.showwarn(dir);
	}

	public String GetHostName() {
		InetAddress address = null;
		String host = "";
		try {
			address = InetAddress.getLocalHost();
			host = address.getHostName();
		} catch (Exception e) {
			zjxt_msg.showwarn(e.toString());
		}
		return host;
	}

	public void ReadCfg() {
		File file = new File(CurrentPath + "/zjxt2.ini");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String txt;
			while ((txt = reader.readLine()) != null) {
				String[] info = txt.split("=");// 分隔读入的行
				if (info.length != 2) {
					continue;
				}
				if (info[0].equals("host")) {
					cfg_host = info[1];
				} else if (info[0].equals("srv1")) {
					cfg_server1 = info[1];
				} else if (info[0].equals("srv2")) {
					cfg_server2 = info[1];
				} else if (info[0].equals("hot")) {
					cfg_HotBackup = Integer.parseInt(info[1]);
				} else if (info[0].equals("ca")) {
					cfg_ca = Integer.parseInt(info[1]);
				} else if (info[0].equals("save2rtdb")) {
					cfg_rtdb = Integer.parseInt(info[1]);
				} else if (info[0].equals("save2file")) {
					cfg_file = Integer.parseInt(info[1]);
				} else if (info[0].equals("CreateRtdb")) {
					IsCreateRtdb = (Integer.parseInt(info[1]) == 1);
				} else if (info[0].equals("SmartPrj")) {
					IsSmartPrj = (Integer.parseInt(info[1]) == 1);
				} else if("isJudgeDeadData".equals(info[0])) {
					isJudgeDeadData = Boolean.parseBoolean(info[1]);
				}
			}
			reader.close();

		} catch (Exception e) {
			zjxt_msg.showwarn("ReadCfg()->", e);
		}
	}

	public boolean CheckLicense() {
		String userinfo = "";
		String keyinfo = "";
		File file = new File(CurrentPath + "/license.txt");
		try {
			if (!file.exists()) {
				FileWriter fw = new FileWriter(CurrentPath + "/license.txt");
				userinfo = dir;
				userinfo = AES_String.encrypt("License", userinfo);
				fw.write("user=" + userinfo);
				fw.flush();
				fw.close();
				return false;
			}

			BufferedReader reader = new BufferedReader(new FileReader(file));
			try {
				String txt;
				while ((txt = reader.readLine()) != null) {
					String[] info = txt.split("=");// 分隔读入的行
					if (info.length != 2) {
						continue;
					}
					if (info[0].equals("user")) {
						userinfo = info[1];
					} else if (info[0].equals("key")) {
						keyinfo = info[1];
					}
				}
				if (keyinfo.equals("")) {
					return false;
				}
				String _key = AES_String.decrypt("zjxt", keyinfo);
				if (!_key.equals(dir)) {
					return false;
				}
			} finally {
				reader.close();
			}

		} catch (Exception e) {
			zjxt_msg.showwarn("license->", e);
			return false;
		}

		return true;
	}
}

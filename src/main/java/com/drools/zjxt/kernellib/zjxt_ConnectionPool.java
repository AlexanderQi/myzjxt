package com.drools.zjxt.kernellib;

import java.sql.Connection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

public class zjxt_ConnectionPool {
	private static zjxt_ConnectionPool instance = null;
	public ComboPooledDataSource dataSource;
	private static String c3p0Properties = "com.mchange.v2.c3p0.cfg.xml";
	private String dbinfo = null;
	private static Lock lock = new ReentrantLock();
	public static int dbProvider = -1; // 1 oracle; 2 mysql;

	private zjxt_ConnectionPool() throws Exception {
		System.setProperty(c3p0Properties, "c3p0-config-zjxt2.xml");
		dataSource = new ComboPooledDataSource();
		dataSource.setDebugUnreturnedConnectionStackTraces(false);
		dbinfo = dataSource.getJdbcUrl();
		if (dbinfo.indexOf("mysql") > 0) {
			dbProvider = 2;
		} else {
			dbProvider = 1;
		}
	}

	public static zjxt_ConnectionPool Instance() throws Exception {
		lock.lock();
		if (instance == null) {
			instance = new zjxt_ConnectionPool();
		}
		lock.unlock();
		return instance;
	}

	public String getDbInfo() {
		return dbinfo;
	}

	public Connection getConnection() throws Exception {
		return dataSource.getConnection();
	}

	protected void finalize() throws Throwable {
		DataSources.destroy(dataSource);
		super.finalize();
	}

}

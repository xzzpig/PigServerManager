package com.github.xzzpig.pigapi.sql.mysql;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.github.xzzpig.pigapi.Debuger;
import com.github.xzzpig.pigapi.TClass;
import com.github.xzzpig.pigapi.TDownload;
import com.github.xzzpig.pigapi.sql.TSql;

public class TMySql extends TSql {

	public TMySql() {
		super();
		type = Type.MYSQL;
	}

	@Override
	protected void build() {
		try {
			try {
				TClass.loadJar("./lib");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Class.forName("com.mysql.jdbc.Driver");
			return;
		} catch (ClassNotFoundException e) {
			File jdbc = new File("./lib/jdbc.jar");
			try {
				new File("./lib").mkdirs();
				jdbc.createNewFile();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			Debuger.print("MYSQL所需驱动JDBC加载失败,将自动下载到" + jdbc.getAbsolutePath());
			try {
				TDownload download = new TDownload(
						"http://heanet.dl.sourceforge.net/project/pigtest0/jdbc.jar");
				download.isBarPrint(true);
				download.start(jdbc);
				while (!download.isFinished()) {
				}
				Debuger.print("jdbc.jar下载完成");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			Integer.valueOf("a");
		}
	}

	@Override
	public TMySql connect(String host, int port, String username,
			String password, String database) throws Exception {
		String url = "jdbc:mysql://" + host + ":3306/" + database + "?"
				+ "user=" + username + "&password=" + password
				+ "&useUnicode=true&characterEncoding=UTF8";
		conn = DriverManager.getConnection(url);
		stmt = conn.createStatement();

		return this;
	}

	@Override
	public void close() {
		try {
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

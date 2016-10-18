package com.github.xzzpig.pigservermanager.datas;

import com.github.xzzpig.pigapi.json.JSONObject;

public class ServerLog {
	private String level, time, log;

	public ServerLog(JSONObject log) {
		level = log.optString("level");
		time = log.optString("time");
		this.log = log.optString("log");
	}

	public String getLevel() {
		return level;
	}

	public String getTime() {
		return time;
	}

	public String getLog() {
		return log;
	}

	@Override
	public String toString() {
		return "[" + time + " " + level + "]" + log;
	}
}

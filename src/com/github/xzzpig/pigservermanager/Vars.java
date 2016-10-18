package com.github.xzzpig.pigservermanager;

import java.util.ArrayList;
import java.util.List;

import com.github.xzzpig.pigapi.PigData;
import com.github.xzzpig.pigservermanager.datas.ServerInfo;
import com.github.xzzpig.pigservermanager.datas.ServerLog;

import android.content.SharedPreferences;
import android.widget.Toast;

public class Vars {
	public static SharedPreferences preferences;
	public static SharedPreferences.Editor editor;
	public static PigData data;
	public static Toast lastToast;
	//public static Client client;
	public static String ip,port,id,pass;
	public static ServerInfo serverInfo;
	public static List<ServerLog> logs = new ArrayList<>();
}

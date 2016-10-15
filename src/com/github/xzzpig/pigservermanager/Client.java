package com.github.xzzpig.pigservermanager;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.github.xzzpig.pigapi.event.Event;
import com.github.xzzpig.pigapi.event.EventHandler;
import com.github.xzzpig.pigapi.event.Listener;
import com.github.xzzpig.pigapi.json.JSONObject;
import com.github.xzzpig.pigservermanager.datas.ServerInfo;
import com.github.xzzpig.pigservermanager.datas.ServerLog;

import android.os.Message;
import android.util.Log;

public class Client extends WebSocketClient implements Listener{
	
	public static boolean login;
	public static String reason;
	
	public Client(URI serverURI) {
		super(serverURI);
	}
	@Override
	public void onOpen(ServerHandshake handshakedata) {
		JSONObject json = new JSONObject();
		json.accumulate("command", "login");
		json.accumulate("id",Vars.id);
		json.accumulate("pass",Vars.pass);
		this.send(json.toString());
	}

	@Override
	public void onMessage(String message) {
		Log.d("PSM","GET:"+message);
		try {
			JSONObject json = new JSONObject(message);
			Event.callEvent(new ClientMessageEvent(json));
		} catch (Exception e) {
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
	}

	@Override
	public void onError(Exception ex) {
	}
	
	@EventHandler
	public void onLoginResponse(ClientMessageEvent event){
		JSONObject json = event.getMessage();
		String command = json.optString("command");
		if(!command.equalsIgnoreCase("loginResponse"))
			return;
		login = json.optBoolean("login");
		reason = json.optString("reason","success");
	}
	
	@EventHandler
	public void onGetServerInfo(ClientMessageEvent event){
		JSONObject json = event.getMessage();
		String command = json.optString("command");
		if(!command.equalsIgnoreCase("serverInfo"))
			return;
		Vars.serverInfo = new ServerInfo(json);
		try {
			Message message = new Message();
			message.what = HandleMessage.GetServerInfo.ordinal();
			InnerActivity.handler.handleMessage(message);
		} catch (Exception e) {
		}
	}
	
	@EventHandler
	public void onGetLog(ClientMessageEvent event){
		JSONObject json = event.getMessage();
		String command = json.optString("command");
		if(!command.equalsIgnoreCase("log"))
			return;
		ServerLog log = new ServerLog(json);
		Vars.logs.add(log);
		try {
			Message message = new Message();
			message.what = HandleMessage.GetNewLog.ordinal();
			message.obj = log;
			ConsoleActivity.handler.handleMessage(message);
		} catch (Exception e) {
		}
	}
	
	public void onGetOnlinePlayer(ClientMessageEvent event){
		JSONObject json = event.getMessage();
		String command = json.optString("command");
		if(!command.equalsIgnoreCase("onlinePlayers"))
			return;
		Vars.serverInfo.playerNumber = json.optInt("num");
		try {
			Message message = new Message();
			message.what = HandleMessage.GetServerInfo.ordinal();
			InnerActivity.handler.handleMessage(message);
		} catch (Exception e) {
		}
	}
}

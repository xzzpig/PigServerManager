package com.github.xzzpig.pigservermanager.datas;

import com.github.xzzpig.pigapi.json.JSONObject;

public class ServerInfo {
	public String ip,name;
	public int port,maxPlayer,playerNumber;
	public ServerInfo(JSONObject info) {
		ip = info.optString("ip");
		port = info.optInt("port");
		name = info.getString("name");
		maxPlayer = info.getInt("maxPlayer");
		playerNumber = info.getInt("playerNumber");
	}
	
	public String getIP(){
		return ip;
	}
	
	public String getServerName(){
		return name;
	}
	
	public int getPort(){
		return port;
	}
	
	public int getMaxPlayer(){
		return maxPlayer;
	}
	
	public int getPlayerNumber(){
		return playerNumber;
	}
	
	@Override
	public String toString() {
		return getServerName()+"服务器信息:\n\tIP:"+getIP()+":"+getPort()+"\n\t在线人数:"+getPlayerNumber()+"/"+getMaxPlayer();
	}
}

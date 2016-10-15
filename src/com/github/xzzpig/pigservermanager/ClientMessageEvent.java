package com.github.xzzpig.pigservermanager;

import com.github.xzzpig.pigapi.event.Event;
import com.github.xzzpig.pigapi.json.JSONObject;

public class ClientMessageEvent extends Event{
	private JSONObject jsonObject;
	public ClientMessageEvent(JSONObject message) {
		jsonObject = message;
	}
	public JSONObject getMessage() {
		return jsonObject;
	}
}

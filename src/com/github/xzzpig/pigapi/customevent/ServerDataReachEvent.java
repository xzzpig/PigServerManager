package com.github.xzzpig.pigapi.customevent;

import com.github.xzzpig.pigapi.PigData;
import com.github.xzzpig.pigapi.event.Event;
import com.github.xzzpig.pigapi.tcp.Client;

public class ServerDataReachEvent extends Event {
	private Client client;
	private Object data;

	public ServerDataReachEvent(Client client, Object data) {
		this.client = client;
		this.data = data;
	}

	public Client getClient() {
		return client;
	}

	public Object getData() {
		return data;
	}
	
	public PigData getPigData(){
		return new PigData(data.toString());
	}
}

package com.github.xzzpig.pigapi.customevent;

import com.github.xzzpig.pigapi.event.Event;
import com.github.xzzpig.pigapi.tcp.Client;
import com.github.xzzpig.pigapi.tcp.Server;

public class ClientConnectEvent extends Event {
	private Client client;
	private Server server;

	public ClientConnectEvent(Server server, Client client) {
		this.client = client;
		this.server = server;
	}

	public Client getClient() {
		return client;
	}

	public Server getServer() {
		return server;
	}
}

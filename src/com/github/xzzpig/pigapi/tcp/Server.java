package com.github.xzzpig.pigapi.tcp;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import com.github.xzzpig.pigapi.customevent.ClientConnectEvent;
import com.github.xzzpig.pigapi.event.Event;

public class Server {
	public static Server server;

	public ServerSocket ss;

	private List<Client> clients = new ArrayList<Client>();

	public Server(int port) throws Exception {
		Server.server = this;
		this.ss = new ServerSocket(port);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Client client = new Client(ss.accept());
						clients.add(client);
						Event.callEvent(new ClientConnectEvent(Server.this, client));
						System.out.println("新客户端连接");
					} catch (Exception e) {
						System.out.println("服务器接受客户端错误");
						if (Server.this.ss.isClosed())
							break;
					}
				}
			}
		}).start();
	}

	public List<Client> getClients() {
		return clients;
	}
}

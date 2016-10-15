package com.github.xzzpig.pigapi.tcp;

import java.net.Socket;

public abstract class AcceptData extends Thread {
	public AcceptData() {}
	
	public abstract AcceptData setSocket(Socket s);
	
	public abstract Socket getSocket();
	
	public abstract AcceptData setClient(Client c);
	
	public abstract Client getClient();
	@Override
	public abstract void run();
}

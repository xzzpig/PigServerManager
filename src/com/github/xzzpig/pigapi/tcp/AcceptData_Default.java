package com.github.xzzpig.pigapi.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.github.xzzpig.pigapi.Debuger;
import com.github.xzzpig.pigapi.customevent.ServerDataReachEvent;
import com.github.xzzpig.pigapi.event.Event;

public class AcceptData_Default extends AcceptData {
	public static int SIZE = 1024*1024;
	
	Socket s;
	private Client c;

	@Override
	public AcceptData setSocket(Socket s) {
		this.s = s;
		return this;
	}

	@Override
	public Socket getSocket() {
		return s;
	}

	@Override
	public void run() {
		try {
			s.setTcpNoDelay(true);
		} catch (Exception e) {
			Debuger.print(e);
			System.out.println("TcpNoDelay设置错误");
		}
		InputStream in = null;
		try {
			in = s.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (in != null || s.isConnected()) {
			try {
				byte[] data = new byte[SIZE];
				int length = in.read(data);
				String s = new String(data, 0, length);
				Event.callEvent(new ServerDataReachEvent(c, s));
			} catch (Exception e) {
			}
		}
	}

	@Override
	public AcceptData setClient(Client c) {
		this.c = c;
		this.s = c.s;
		return this;
	}

	@Override
	public Client getClient() {
		return c;
	}

}

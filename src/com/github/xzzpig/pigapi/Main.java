package com.github.xzzpig.pigapi;

import java.util.Scanner;

import com.github.xzzpig.pigapi.tcp.Client;
import com.github.xzzpig.pigapi.tcp.Server;

public class Main {
	public static void main(String[] args) {
		try {
			new Server(10727);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("服务器创建失败");
			return;

		}
		System.out.println("服务器创建成功");
		Scanner input = new Scanner(System.in);
		TData data = new TData();
		while (true) {
			try {
				System.out.print("next:");
				String str = input.nextLine();
				if (str.equalsIgnoreCase("/send")) {
					for (Client c : Client.clients) {
						c.sendData(data);
						data = new TData();
					}
					continue;
				}
				if (str.equalsIgnoreCase("/stop")) {
					break;
				}
				data.setString(str.split(":")[0], str.split(":")[1]);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		input.close();
		// try{
		// Client c = new Client("localhost",10727);
		// c.sendData("test");
		// c.sendData(new TData().setString("a","b"));
		//
		// }
		// catch(Exception e){
		// System.out.println("客户端连接失败");
		// }
	}
}

package com.github.xzzpig.pigservermanager;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import com.github.xzzpig.pigapi.event.Event;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class ClientService extends Service{ 
	
	
	public class ClientBinder extends Binder{
	}
	public class LoginBinder extends Binder{
		public boolean login_success,allfinish;
		public String error;
		boolean finish;
	}

	private Client client;
	
	@Override
	public IBinder onBind(Intent intent) {
		if(intent.getStringExtra("command").equalsIgnoreCase("login")){
			new Thread(new Runnable() {
				@Override
				public void run() {
					final Message message = new Message();
					message.what = HandleMessage.LoginMSG.ordinal();
					final Bundle bundle = new Bundle();
					String ip = Vars.ip;
					String port = Vars.port;
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								InetAddress address = InetAddress.getByName(Vars.ip);
								Vars.ip=address.getHostAddress();
								bundle.putBoolean("solve_ip",true);
								bundle.putBoolean("finish", true);
							} catch (UnknownHostException e) {
								bundle.putBoolean("login_success", false);
								bundle.putString("error","无法识别该IP(域名解析错误)");
								bundle.putBoolean("solve_ip",false);
								bundle.putBoolean("finish", true);
								return;
							}
						}
					}).start();
					long stop = System.currentTimeMillis()+5000;
					while ((!bundle.getBoolean("finish"))&&System.currentTimeMillis()<stop) {}
					if(bundle.getBoolean("solve_ip")==false){
						try {
							message.setData(bundle);
							MainActivity.loginHandler.sendMessage(message);
						} catch (Exception e2) {
						}
						return;
					}
					ip = Vars.ip;
					client = new Client(URI.create("ws://"+ip+":"+port));
					Event.registListener(client);
					client.connect();
					Log.d("PSM",ip+":"+port+"@"+Vars.id+"$"+Vars.pass);			
					while ((!client.isOpen())) {}
					while (Client.reason==null) {}
					if(!Client.login){
						bundle.putBoolean("login_success", false);
						bundle.putString("error",Client.reason);
						try {
							message.setData(bundle);
							MainActivity.loginHandler.sendMessage(message);
						} catch (Exception e2) {
						}
						return;
					}
					bundle.putBoolean("login_success", true);
					Vars.client = client;
					try {
						message.setData(bundle);
						MainActivity.loginHandler.sendMessage(message);
					} catch (Exception e2) {
					}
					return;
					
				}
			}).start();
//			Intent intent2 = new Intent(ClientService.this,InnerActivity.class);
//			startActivity(intent);
		}
		return new ClientBinder();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("PSM","Service Start");
	}
}

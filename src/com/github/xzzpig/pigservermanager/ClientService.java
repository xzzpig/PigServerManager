package com.github.xzzpig.pigservermanager;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import com.github.xzzpig.pigapi.event.Event;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class ClientService extends Service {

	private Client client;

	private ClientBroadcastReceiver broadcastReceiver;
	
	public class ClientBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getStringExtra("command").equalsIgnoreCase("senddata")) {
				client.send(intent.getStringExtra("data"));
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (intent.getStringExtra("command").equalsIgnoreCase("login")) {
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
								Vars.ip = address.getHostAddress();
								bundle.putBoolean("solve_ip", true);
								bundle.putBoolean("finish", true);
							} catch (UnknownHostException e) {
								bundle.putBoolean("login_success", false);
								bundle.putString("error", "无法识别该IP(域名解析错误)");
								bundle.putBoolean("solve_ip", false);
								bundle.putBoolean("finish", true);
								return;
							}
						}
					}).start();
					long stop = System.currentTimeMillis() + 5000;
					while ((!bundle.getBoolean("finish")) && System.currentTimeMillis() < stop) {
					}
					if (bundle.getBoolean("solve_ip") == false) {
						try {
							message.setData(bundle);
							MainActivity.loginHandler.sendMessage(message);
						} catch (Exception e2) {
						}
						return;
					}
					ip = Vars.ip;
					client = new Client(URI.create("ws://" + ip + ":" + port));
					Event.registListener(client);
					client.connect();
					Log.d("PSM", ip + ":" + port + "@" + Vars.id + "$" + Vars.pass);
					while ((!client.isOpen())) {
					}
					while (Client.reason == null) {
					}
					if (!Client.login) {
						bundle.putBoolean("login_success", false);
						bundle.putString("error", Client.reason);
						try {
							message.setData(bundle);
							MainActivity.loginHandler.sendMessage(message);
						} catch (Exception e2) {
						}
						return;
					}
					bundle.putBoolean("login_success", true);
					//Vars.client = client;
					try {
						message.setData(bundle);
						MainActivity.loginHandler.sendMessage(message);
					} catch (Exception e2) {
					}
					return;

				}
			}).start();
		}
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("PSM", "Service Create");
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(getString(R.string.ClientServiceBC));
		broadcastReceiver = new ClientBroadcastReceiver();
		registerReceiver(broadcastReceiver,intentFilter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("PSM", "Service Destroy");
		unregisterReceiver(broadcastReceiver);
	}
}

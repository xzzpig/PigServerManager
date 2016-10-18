package com.github.xzzpig.pigservermanager;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.github.xzzpig.pigapi.event.Event;
import com.github.xzzpig.pigapi.event.EventHandler;
import com.github.xzzpig.pigapi.event.Listener;
import com.github.xzzpig.pigapi.json.JSONObject;
import com.github.xzzpig.pigservermanager.datas.ServerInfo;
import com.github.xzzpig.pigservermanager.datas.ServerLog;

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

	private String ip, port, id, pass;

	private ClientBroadcastReceiver broadcastReceiver;

	public class ClientBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getStringExtra("command").equalsIgnoreCase("senddata")) {
				client.send(intent.getStringExtra("data"));
			} else if (intent.getStringExtra("command").equalsIgnoreCase("login")) {
				if (client != null && client.isOpen()) {
					Message message = new Message();
					message.what = HandleMessage.LoginMSG.ordinal();
					Bundle bundle = new Bundle();
					bundle.putBoolean("login_success", true);
					try {
						message.setData(bundle);
						MainActivity.loginHandler.sendMessage(message);
					} catch (Exception e2) {
					}
					return;
				}
				ip = intent.getStringExtra("ip");
				port = intent.getStringExtra("port");
				id = intent.getStringExtra("id");
				pass = intent.getStringExtra("pass");
				new Thread(new Runnable() {
					@Override
					public void run() {
						final Message message = new Message();
						message.what = HandleMessage.LoginMSG.ordinal();
						final Bundle bundle = new Bundle();
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									InetAddress address = InetAddress.getByName(ip);
									ip = address.getHostAddress();
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
						client = new Client(URI.create("ws://" + ip + ":" + port));
						Event.registListener(client);
						client.connect();
						Log.d("PSM", ip + ":" + port + "@" + id + "$" + pass);
						while ((!client.isOpen())) {
						}
						while (client.reason == null) {
						}
						if (!client.login) {
							bundle.putBoolean("login_success", false);
							bundle.putString("error", client.reason);
							try {
								message.setData(bundle);
								MainActivity.loginHandler.sendMessage(message);
							} catch (Exception e2) {
							}
							return;
						}
						bundle.putBoolean("login_success", true);
						try {
							message.setData(bundle);
							MainActivity.loginHandler.sendMessage(message);
						} catch (Exception e2) {
						}
						return;

					}
				}).start();
			}
		}
	}

	public class Client extends WebSocketClient implements Listener {

		public boolean login;
		public String reason;

		public Client(URI serverURI) {
			super(serverURI);
		}

		@Override
		public void onOpen(ServerHandshake handshakedata) {
			JSONObject json = new JSONObject();
			json.accumulate("command", "login");
			json.accumulate("id", id);
			json.accumulate("pass", pass);
			this.send(json.toString());
		}

		@Override
		public void onMessage(String message) {
			Log.d("PSM", "GET:" + message);
			try {
				JSONObject json = new JSONObject(message);
				Event.callEvent(new ClientMessageEvent(json));
			} catch (Exception e) {
			}
		}

		@Override
		public void onClose(int code, String reason, boolean remote) {
			ClientService.this.stopSelf();
		}

		@Override
		public void onError(Exception ex) {
		}

		@EventHandler
		public void onLoginResponse(ClientMessageEvent event) {
			JSONObject json = event.getMessage();
			String command = json.optString("command");
			if (!command.equalsIgnoreCase("loginResponse"))
				return;
			login = json.optBoolean("login");
			reason = json.optString("reason", "success");
		}

		@EventHandler
		public void onGetServerInfo(ClientMessageEvent event) {
			JSONObject json = event.getMessage();
			String command = json.optString("command");
			if (!command.equalsIgnoreCase("serverInfo"))
				return;
			Vars.serverInfo = new ServerInfo(json);
			try {
				Message message = new Message();
				message.what = HandleMessage.GetServerInfo.ordinal();
				InnerActivity.handler.handleMessage(message);
			} catch (Exception e) {
			}
		}

		@EventHandler
		public void onGetLog(ClientMessageEvent event) {
			JSONObject json = event.getMessage();
			String command = json.optString("command");
			if (!command.equalsIgnoreCase("log"))
				return;
			ServerLog log = new ServerLog(json);
			Vars.logs.add(log);
			try {
				Message message = new Message();
				message.what = HandleMessage.GetNewLog.ordinal();
				message.obj = log;
				ConsoleActivity.handler.handleMessage(message);
			} catch (Exception e) {
			}
		}

		public void onGetOnlinePlayer(ClientMessageEvent event) {
			JSONObject json = event.getMessage();
			String command = json.optString("command");
			if (!command.equalsIgnoreCase("onlinePlayers"))
				return;
			Vars.serverInfo.playerNumber = json.optInt("num");
			try {
				Message message = new Message();
				message.what = HandleMessage.GetServerInfo.ordinal();
				InnerActivity.handler.handleMessage(message);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("PSM", "Service Bind");

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(getString(R.string.ClientServiceBC));
		broadcastReceiver = new ClientBroadcastReceiver();
		registerReceiver(broadcastReceiver, intentFilter);
		Message message = new Message();
		message.what = HandleMessage.CanLogin.ordinal();
		try {
			MainActivity.loginHandler.handleMessage(message);
		} catch (Exception e) {
		}
		Log.d("PSM", "Service Create finish");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("PSM", "Service Destroy");
		unregisterReceiver(broadcastReceiver);
	}
}

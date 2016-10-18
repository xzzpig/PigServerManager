package com.github.xzzpig.pigservermanager;

import com.github.xzzpig.pigapi.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class InnerActivity extends Activity {
	public static TextView textView_ServerInfo;
	public static Button button_Console;
	public static Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inner);
		loadContents();
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if (msg.what==HandleMessage.GetServerInfo.ordinal()) {
					loadContents();
					textView_ServerInfo.setText(Vars.serverInfo.toString());
				}
			}
		};
		button_Console.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(InnerActivity.this,ConsoleActivity.class);
				startActivity(intent);
			}
		});
		JSONObject json = new JSONObject();
		json.accumulate("command", "serverInfo");
		Intent intent = new Intent();
		intent.setAction(getString(R.string.ClientServiceBC));
		intent.putExtra("command", "senddata");
		intent.putExtra("data", json.toString());
		sendBroadcast(intent);
		json = new JSONObject();
		json.accumulate("command", "oldLog");
		intent = new Intent();
		intent.setAction(getString(R.string.ClientServiceBC));
		intent.putExtra("command", "senddata");
		intent.putExtra("data", json.toString());
		sendBroadcast(intent);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.d("PSM","onStart2");
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("PSM","onRestart2");
	}
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("PSM","onResume2");
	}
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("PSM","onPause2");
	}
	@Override
	protected void onStop() {
		super.onStop();
		Log.d("PSM","onStop2");
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("PSM","onDestory2");
		/*
		Vars.client.close();
		Vars.client =null;
		Vars.data=null;
		Vars.id=null;
		Vars.ip=null;
		Vars.lastToast=null;
		Vars.logs = new ArrayList<>();
		Vars.pass=null;
		Vars.port=null;
		Vars.serverInfo=null;
		 */
	}
	
	private void loadContents(){
		textView_ServerInfo = (TextView) findViewById(R.id.TextView_ServerInfo);
		button_Console = (Button) findViewById(R.id.Button_Console);
	}
}

package com.github.xzzpig.pigservermanager;

import com.github.xzzpig.pigapi.json.JSONObject;
import com.github.xzzpig.pigservermanager.datas.ServerLog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class ConsoleActivity extends Activity {
	public static TextView textView_Log;
	public static EditText editText_Command;
	public static Button button_send;
	public static ScrollView scrollView_Log;
	public static Handler handler;

	public StringBuffer logs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.console);
		loadContents();
		logs = new StringBuffer();
		for (ServerLog _log : Vars.logs) {
			logs.append("\n" + _log);
		}
		textView_Log.setMovementMethod(ScrollingMovementMethod.getInstance());
		textView_Log.setText(logs.toString());
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == HandleMessage.GetNewLog.ordinal()) {
					loadContents();
					Log.d("PSM",msg.obj.toString());
					logs.append("\n" + msg.obj);
					textView_Log.setText(logs.toString());
					textView_Log.postInvalidate();
					editText_Command.setFocusableInTouchMode(true);
					editText_Command.requestFocus();
				}
			}
		};
		button_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String cmd = editText_Command.getText().toString();
				editText_Command.setText("");
				JSONObject json = new JSONObject();
				json.accumulate("command","command");
				json.accumulate("cmd", cmd);
				Vars.client.send(json.toString());
			}
		});
	}

	private void loadContents() {
		textView_Log = (TextView) findViewById(R.id.TextView_Log);
		editText_Command = (EditText) findViewById(R.id.EditText_Command);
		button_send = (Button) findViewById(R.id.Button_Send);
		//scrollView_Log = (ScrollView) findViewById(R.id.ScrollView_Log);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.d("PSM","onStart3");
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("PSM","onRestart3");
	}
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("PSM","onResume3");
	}
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("PSM","onPause3");
	}
	@Override
	protected void onStop() {
		super.onStop();
		Log.d("PSM","onStop3");
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("PSM","onDestory3");
	}
}

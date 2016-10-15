package com.github.xzzpig.pigservermanager;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;

import com.github.xzzpig.pigapi.PigData;
import com.github.xzzpig.pigapi.event.Event;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static Spinner spinner_Server;
	public static EditText editText_ID, editText_Password;
	public static Button button_Login, button_AddServer;
	public static CheckBox checkBox_savepass;
	public Builder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Vars.preferences = getSharedPreferences("data", MODE_PRIVATE);
		Vars.editor = Vars.preferences.edit();
		Vars.data = new PigData(Vars.preferences.getString("data", ""));
		builder = new AlertDialog.Builder(this);
		setContentView(R.layout.main);
		loadContents();
		button_AddServer.setOnClickListener(new AddServerClick());
		spinner_Server.setAdapter(new ServerAdapter());
		spinner_Server.setSelection(Vars.data.getInt("ip_last"));
		addEventForSpinner_Server();
		button_Login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final ProgressDialog loadingDialog=ProgressDialog.show(MainActivity.this, "Loading","正在解析服务器地址");
				//loadingDialog.setCancelable(true);
				//loadingDialog.setCanceledOnTouchOutside(true);
				//loadingDialog.show();
				String id = editText_ID.getText().toString(), pass = editText_Password.getText().toString();
				if (id.equalsIgnoreCase("")) {
					showToast("ID不能为空");
					loadingDialog.dismiss();
					return;
				}
				if (pass.equalsIgnoreCase("")) {
					showToast("密码不能为空");
					loadingDialog.dismiss();
					return;
				}
				Vars.data.set("id",id);
				if (checkBox_savepass.isChecked()){
					Vars.data.set("savepass", true);
					Vars.data.set("pass",pass);
				}else {
					Vars.data.set("savepass", false);
					Vars.data.remove("pass");
				}
				Voids.saveData();
				String[] ip_p = ((TextView)spinner_Server.getSelectedView()).getText().toString().split(":");
				if(ip_p.length<2){
					showToast("你所选的服务器IP不符合格式");
					loadingDialog.dismiss();
					return;
				}
				final String ip = ip_p[0];
				String port = ip_p[1];
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							InetAddress address = InetAddress.getByName(ip);
							Vars.ip=address.getHostAddress();
						} catch (UnknownHostException e) {
							Vars.ip="error";
							showToast("无法识别该IP(域名解析错误)");
							loadingDialog.dismiss();
							return;
						}
					}
				}).start();
				long stop = System.currentTimeMillis()+5000;
				while (Vars.ip==null&&System.currentTimeMillis()<stop) {}
				if(Vars.ip==null){
					showToast("域名解析超时");
					loadingDialog.dismiss();
					return;
				}else if(Vars.ip.equalsIgnoreCase("error")){
					loadingDialog.dismiss();
					return;
				}
				Vars.port = port;
				Vars.id = id;
				Vars.pass = pass;
				if(Vars.client!=null&&Vars.client.isOpen()){
					Intent intent = new Intent(MainActivity.this,InnerActivity.class);
					startActivity(intent);
					MainActivity.this.finish();
					return;
				}
				Vars.client = new Client(URI.create("ws://"+Vars.ip+":"+port));
				Event.registListener(Vars.client);
				loadingDialog.setMessage("正在连接服务器");
				Vars.client.connect();
				while ((!Vars.client.isOpen())&&loadingDialog.isShowing()) {}
				loadingDialog.setMessage("开始登录");
				while (Client.reason==null) {}
				loadingDialog.dismiss();
				if(!Client.login){
					showToast(Client.reason);
					return;
				}
				Intent intent = new Intent(MainActivity.this,InnerActivity.class);
				startActivity(intent);
				MainActivity.this.finish();
			}
		});
		button_Login.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});
		if(Vars.data.contianKey("id"))
			editText_ID.setText(Vars.data.getString("id"));
		if(Vars.data.contianKey("pass"))
			editText_Password.setText(Vars.data.getString("pass"));
		if(Vars.data.contianKey("savepass"))
			checkBox_savepass.setChecked(Vars.data.getBoolean("savepass"));
		Log.d("PSM","Data:\n"+Vars.data.getPrintString());
	}

	private void addEventForSpinner_Server() {
		spinner_Server.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Vars.data.set("ip_last", position);
				Voids.saveData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Vars.data.set("ip_last", 0);
				Voids.saveData();
			}
		});
		spinner_Server.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				List<String> ips = Vars.data.getList("ip");
				ips.remove(position);
				Vars.data.set("ip", ips);
				Voids.saveData();
				((BaseAdapter) spinner_Server.getAdapter()).notifyDataSetChanged();
				return true;
			}
		});
		spinner_Server.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if(spinner_Server.getSelectedItemPosition()<0)
					return false;
				List<String> ips = Vars.data.getList("ip");
				ips.remove(spinner_Server.getSelectedItemPosition());
				Vars.data.set("ip", ips);
				Voids.saveData();
				((BaseAdapter) spinner_Server.getAdapter()).notifyDataSetChanged();
				return true;
			}
		});
	}

	private void loadContents() {
		spinner_Server = (Spinner) findViewById(R.id.Spinner_Server);
		editText_ID = (EditText) findViewById(R.id.EditText_ID);
		editText_Password = (EditText) findViewById(R.id.EditText_Password);
		button_Login = (Button) findViewById(R.id.Button_Login);
		button_AddServer = (Button) findViewById(R.id.Button_AddServer);
		checkBox_savepass = (CheckBox) findViewById(R.id.CheckBox_SavePass);
	}
	
	class AddServerClick implements OnClickListener {
		@SuppressLint("InflateParams")
		@Override
		public void onClick(View v) {
			builder.setTitle("添加服务器");
			final View addServerLayout = getLayoutInflater().inflate(R.layout.addserver, null);
			builder.setView(addServerLayout);
			builder.setNegativeButton("保存", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					System.out.println("233333333333333333333333333333333333333333333333");
					EditText editText_ip = (EditText) addServerLayout.findViewById(R.id.EditText_IP);
					EditText editText_port = (EditText) addServerLayout.findViewById(R.id.EditText_Port);
					List<String> ips = Vars.data.getList("ip");
					ips.add(editText_ip.getText().toString() + ":" + editText_port.getText().toString());
					Vars.data.set("ip", ips);
					Voids.saveData();
					((BaseAdapter) spinner_Server.getAdapter()).notifyDataSetChanged();
					Toast.makeText(MainActivity.this, "长按服务器列表删除该服务器IP", Toast.LENGTH_SHORT).show();
				}
			});
			builder.setPositiveButton("取消", null);
			builder.show();
		}
	}

	class ServerAdapter extends BaseAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = new TextView(MainActivity.this);
			textView.setText(Vars.data.getList("ip").get(position));
			return textView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return Vars.data.getList("ip").get(position);
		}

		@Override
		public int getCount() {
			return Vars.data.getList("ip").size();
		}
	}
	
	private void showToast(String message) {
		if (Vars.lastToast != null)
			Vars.lastToast.cancel();
		Vars.lastToast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
		Vars.lastToast.show();
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.d("PSM","onStart1");
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("PSM","onRestart1");
	}
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("PSM","onResume1");
	}
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("PSM","onPause1");
	}
	@Override
	protected void onStop() {
		super.onStop();
		Log.d("PSM","onStop1");
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("PSM","onDestory1");
	}
}

package com.cresprit.alooh;


import com.cresprit.alooh.manager.SelectDeviceManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.cresprit.alooh.manager.UserManager;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;


public class FailRegDeviceActivity extends Activity{
	
	static private String TAG = "FailRegDeviceActivity";
	static private int DIALOG_CONNECT = 0; 
	static private int DIALOG_CLOSE_APP = 3;
	ImageButton btnWarn = null;
	SelectDeviceManager device = null;
	WifiManager wifiMgr = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fail_reg_dev_layout);
		
		device = SelectDeviceManager.getInstance(FailRegDeviceActivity.this);
		device.setDialogUpdateListener(listener);
		
		wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);		
		btnWarn = (ImageButton)findViewById(R.id.warning);
		
		btnWarn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
					finish();
					Intent intent = new Intent(FailRegDeviceActivity.this, AloohActivity.class);						
					startActivity(intent);

			}
			
		}
		);
	}

	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		showDialog(DIALOG_CLOSE_APP);
	}



	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		
		switch(id)
		{
		case 0:
		return new AlertDialog.Builder(FailRegDeviceActivity.this)
		.setTitle(device.getDeviceList().get(0).getDeviceName()+getResources().getString(R.string.will_connect_device))
		.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
						
						if(device.getDeviceList().get(0).getDeviceName().equals(wifiInfo.getSSID()))//선택된 디바이스와 이미 연결되어 있을 경우(ex.case retry)
						{
							Log.i(TAG, "Device was already connected....");
							device.sendAuthKeyToDevice(UserManager.getInstance(FailRegDeviceActivity.this).getAuthKey());
						}
						else
						{
							Log.i(TAG, "try to connect the device.....");
							device.setWifi(device.getDeviceList().get(0).getDeviceName());
							device.setBroadcastReceiver(2000);
						}
						
						showDialog(1);
						/* User clicked OK so do some stuff */
						// Intent intent = new Intent(this,
						// connect.class);
						// startActivity(intent);
					}
				})
				.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {

						/* User clicked OK so do some stuff */

					}
				}).create();
		
		case 1:

			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle(R.string.connecting);
			 dialog.setMessage(getResources().getString(R.string.noti_wait_moment));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			return dialog;			
		
		case 2:
			return new AlertDialog.Builder(FailRegDeviceActivity.this)
					.setTitle(R.string.noti)
					.setMessage(R.string.failed_to_connect_device)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();
			
		case 3:
			return new AlertDialog.Builder(FailRegDeviceActivity.this)
					.setTitle(getResources().getString(R.string.will_close_app))
					.setCancelable(false)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									finish();
								}
					})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked OK so do some stuff */

								}
					}).create();	
		}
		return super.onCreateDialog(id);
	}
	

	
	private IUpdateListener listener = new IUpdateListener()
	{
		@Override
		public void update(int status, boolean result) {
			// TODO Auto-generated method stub
			if(status == IUpdateListener.SHOW_DIALOG)
			{
				//showDialog();
			}
			else//status == IUpdateListener.REMOVE_DIALOG
			{
				removeDialog(1);
				if(result == false)
					showDialog(2);
				else
				{
					Intent intent = new Intent(FailRegDeviceActivity.this, SelectWifiActivity.class);
					startActivity(intent);
				}
			}
		}
	};
	
	
}
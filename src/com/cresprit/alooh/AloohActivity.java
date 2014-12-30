package com.cresprit.alooh;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.cresprit.alooh.R;
import com.cresprit.alooh.R.id;
import com.cresprit.alooh.R.layout;
import com.cresprit.alooh.manager.SelectDeviceManager;
import com.cresprit.alooh.manager.UserManager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.View;

public class AloohActivity extends Activity implements View.OnClickListener{
	
	private static String TAG = "AloohActivity";
	public static String DEVICE_NAME = "device_name";
	private ImageButton btnConnect;
	SelectDeviceManager device=null;
	Handler handler = new Handler();
	WifiManager wifiMgr = null;
	boolean isTimeoutFindDevice = false;
	static int retryCnt = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alooh_layout);
		wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		btnConnect = (ImageButton)findViewById(R.id.connect);
		btnConnect.setOnClickListener(this);
		
		device = SelectDeviceManager.getInstance(this);
		device.setDialogUpdateListener(listener);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.connect:
			isTimeoutFindDevice = false;
			showDialog(3);
			handler.postDelayed(searchingDevice, 2000);
			handler.postDelayed(timeoutSearchDevice, 30000);
			break;
		}
	}
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		Intent intent = new Intent(AloohActivity.this, SetDeviceNameActivity.class);
		intent.putExtra(DEVICE_NAME, UserManager.getInstance(AloohActivity.this).getDeviceName());
		startActivity(intent);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
	
		switch(id)
		{
		case 0:
		return new AlertDialog.Builder(AloohActivity.this)
				.setTitle(device.getDeviceList().get(0).getDeviceName()+getResources().getString(R.string.will_connect_device))
				.setCancelable(false)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
								
								if(device.getDeviceList().get(0).getDeviceName().equals(wifiInfo.getSSID()))//선택된 디바이스와 이미 연결되어 있을 경우(ex.case retry)
								{
									Log.i(TAG, "Device was already connected....");
									device.sendAuthKeyToDevice(UserManager.getInstance(AloohActivity.this).getAuthKey());
								}
								else
								{
									Log.i(TAG, "try to connect the device.....");
									device.setWifi(device.getDeviceList().get(0).getDeviceName());
									device.setBroadcastReceiver(1300);
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
			dialog.setCancelable(false);
			return dialog;
			
			
		
			
		case 3:
			ProgressDialog dialogFindDevice = new ProgressDialog(this);
			dialogFindDevice.setTitle(R.string.searching);
			dialogFindDevice.setMessage(getResources().getString(R.string.noti_wait_moment));
			dialogFindDevice.setIndeterminate(true);
			dialogFindDevice.setCancelable(false);
			return dialogFindDevice;

		case 4:
			return new AlertDialog.Builder(AloohActivity.this)
					.setTitle(R.string.noti)
					.setCancelable(false)
					.setMessage(R.string.failed_to_search_device)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();	
				
		case 5:
			return new AlertDialog.Builder(AloohActivity.this)
					.setTitle(R.string.noti)
					.setCancelable(false)
					.setMessage(R.string.failed_to_connect_device)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();	
		}
		return null;
	}

	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}

	private IUpdateListener listener = new IUpdateListener()
	{
		@Override
		public void update(int status, boolean result) {
			// TODO Auto-generated method stub
			Log.i("","+++++++++++++++++Callback listener +++++++++++++++++");
			if(status == IUpdateListener.SHOW_DIALOG)
			{
				//showDialog();
			}
			else//status == IUpdateListener.REMOVE_DIALOG
			{
				removeDialog(1);
				if(result == false)
				{
					showDialog(5);
				}
				else
				{	
					finish();
					Intent intent = new Intent(AloohActivity.this, SelectWifiActivity.class);
					startActivity(intent);
				}
			}
		}
	};

	private final Runnable timeoutSearchDevice = new Runnable()
  	{

 		@Override
 		public void run() {
 			// TODO Auto-generated method stub
 			isTimeoutFindDevice = true;
 		}
  		
  	};     
	
	private final Runnable searchingDevice = new Runnable()
  	{

 		@Override
 		public void run() {
 			// TODO Auto-generated method stub
 			device.getDeviceList().clear();
			device.getSearchAP();
			
			if(device.getDeviceList().size() > 0)
			{
				removeDialog(3);
				handler.removeCallbacks(timeoutSearchDevice);
				if(device.getDeviceList().size() == 1)
				{
					device.setSelectDevice(device.getDeviceList().get(0).getDeviceName());
					showDialog(0);
				}
				else
				{	
					finish();
					Intent intent = new Intent(AloohActivity.this, SelectDeviceActivity.class);						
					startActivity(intent);
				}
			}
			else
			{
				if(isTimeoutFindDevice == false)
					handler.postDelayed(searchingDevice, 2000);
				else
				{
					removeDialog(3);
					showDialog(4);
				}
					
			}
					
			
 		}
  		
  	};     
}

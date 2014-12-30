package com.cresprit.alooh;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import com.cresprit.alooh.R;
import com.cresprit.alooh.manager.SelectDeviceManager;
import com.cresprit.alooh.manager.SelectWifiManager;
import com.cresprit.alooh.manager.UserManager;
import com.cresprit.alooh.server_api.ServerAPIManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class InsertPwdActivity extends Activity{
	public static int DIALOG_REGIST_DEVICE=1;
	SelectWifiManager wifi = null;
	SelectDeviceManager device = null;
	ServerAPIManager serverApiMgr = null;
	Button confirmBtn=null;
	TextView ssidTv = null;
	EditText pwdEdt = null;
	String m_pPassword = null;
	String m_pSsid = null;
	int m_nPosition = 0;
	Handler handler = null;
	static boolean timeout = false;
	static boolean isTimeoutConnectWifi = false;
	static boolean isRetry = false;
	NetworkInfo networkInfo = null;
	WifiManager wifiMgr = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.insertpwd_layout);
		wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		wifi = SelectWifiManager.getInstance(InsertPwdActivity.this);
		wifi.setDialogUpdateListener(listener);
		
		serverApiMgr = new ServerAPIManager(InsertPwdActivity.this);
		serverApiMgr.setDialogUpdateListener(NetChangelistener);
		Intent intent = getIntent();
        Bundle intentData = intent.getExtras();
	        
        m_pSsid = intentData.getString("ssid");
        m_nPosition = intentData.getInt("position");

        handler = new Handler();
        ssidTv = (TextView)findViewById(R.id.ssid);
        ssidTv.setText(m_pSsid);
		confirmBtn = (Button)findViewById(R.id.confirmbtn);
		confirmBtn.setOnClickListener(new View.OnClickListener() {
			   public void onClick(View view) {
				   m_pPassword = pwdEdt.getText().toString();
				   if(m_pPassword == null || "".equals(m_pPassword))
				   {
					   showDialog(0);
				   }
				   else
				   {
					   showDialog(DIALOG_REGIST_DEVICE);
					   m_pPassword = pwdEdt.getText().toString();
					   
					   wifi.setWifiPassword(m_pPassword);
					   wifi.PostWifiConfiguration(m_pSsid);
				   }
			   }
			});
		
		ssidTv = (TextView)findViewById(R.id.ssid);
		pwdEdt = (EditText)findViewById(R.id.pwdedt);
	}
	
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		Intent intent = new Intent(InsertPwdActivity.this, SelectWifiActivity.class);
		startActivity(intent);
	}



	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		// TODO Auto-generated method stub
		switch(id)
		{
			case 0:
				return new AlertDialog.Builder(InsertPwdActivity.this)
	            .setTitle(R.string.noti)
	            .setMessage(R.string.insert_passwd)
	            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                    /* User clicked OK so do some stuff */
	
	                }
	            })
	            .create();			
			case 1:
				ProgressDialog dialog = new ProgressDialog(this);
				dialog.setTitle(R.string.connecting);
				dialog.setMessage(getResources().getString(R.string.noti_wait_moment));
				dialog.setIndeterminate(true);
				dialog.setCancelable(false);
				return dialog;
		}
		return super.onCreateDialog(id, args);
	}
	
//	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context arg0, Intent intent) {
//			// TODO Auto-generated method stub
//			String action = intent.getAction();
//			Log.i("TAG", "NETWORK_CHANGE:" + action );
//				
//				if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
//				{
//					String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
//					networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//					wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//					Log.d("","WifiManager.NETWORK_STATE_CHANGED_ACTION " + networkInfo);
//
//					
//					if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//						if(networkInfo.getState()!= null)
//						{
//							Log.d("","networkInfo.getState " + networkInfo.getState());
//						}
//						if (networkInfo.getState()!= null && networkInfo.getState().toString().equals("CONNECTED")){
//							WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
//						    String ssid = wifiInfo.getSSID();
//						    if(ssid != null)
//							if(ssid.contains("\""))
//							{
//								ssid = ssid.substring(1, ssid.length()-1);
//							}
//							Log.i("", "selectDevice Name:"+m_pSsid+"ssid :"+wifiInfo.getSSID()+" : ssid :"+ssid);
//							//if(m_pSsid.equals(ssid))
//							{
//								unregisterReceiver(mBroadcastReceiver);
//								//Log.i("", "selectDevice Name:"+m_pSsid+"ssid :"+wifiInfo.getSSID());
//									
//								timeout = false;
////								new Handler().postDelayed(new Runnable() {
////										
////								@Override
////								public void run() {
////									// TODO Auto-generated method stub
////									new RegistDeviceTask().execute();
////									}
////								}, 2000);
////										
////								new Handler().postDelayed(new Runnable() {
////											
////									@Override
////									public void run() {
////										// TODO Auto-generated method stub
////										timeout = true;
////									}
////								}, 30000);
//							}
//
//						}
//					}
//				}			
//		}
//	};
//	
	

	class RegistDeviceTask extends AsyncTask<String, Void, String> {
		String result=null;		
	@Override
	protected String doInBackground(String... params1) {
		JSONObject data = new JSONObject();
		JSONObject json = new JSONObject();
		
		try {
			data.put("auid"	, UserManager.getInstance(InsertPwdActivity.this).getAuid());
			data.put("name"	, UserManager.getInstance(InsertPwdActivity.this).getDeviceName());
			json.put("data", data);				
		} catch (JSONException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		HttpClient client = new DefaultHttpClient();
		final HttpParams params = client.getParams();
		
		HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
		HttpConnectionParams.setSoTimeout(params, 10 * 1000);

		HttpPost request = new HttpPost();
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Authorization", "Bearer "+UserManager.getInstance(InsertPwdActivity.this).getAuthKey());

		try {
			request.setURI(new URI(UserManager.SERVER_API_REGIST_DEVICE_NAME_CHECK_URL));
		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
					
		try {
			request.setEntity(new StringEntity(json.toString(), "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			HttpResponse response = client.execute(request);
			HttpEntity responseEntity = response.getEntity();
			Log.i("", "get http response: STATUS_CODE: " + response.getStatusLine().getStatusCode()+response.getStatusLine().getReasonPhrase());
			
			if(response.getStatusLine().getStatusCode() == 404)
				return null;
			
			if(response.getStatusLine().getStatusCode() == 200)
			{
				result= "200OK";
//				HttpEntity entity = response.getEntity();
//				String jsonStr = EntityUtils.toString(entity);
//				
//				try {
//					JSONObject resObj = new JSONObject(jsonStr);
//					JSONObject dataJSON = resObj.getJSONObject("data");
//					JSONObject deviceJSON = dataJSON.getJSONObject("device");
//					result  = deviceJSON.getString("name");
//	
//					Log.i("","result :"+result);
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;	

	}
	

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		
		super.onPreExecute();
	}



	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub

		if(timeout == true)
		{
			timeout = false;
			isRetry = true;
			removeDialog(DIALOG_REGIST_DEVICE);
			finish();
			Log.i("", "Timeout");
			Intent intent = new Intent(InsertPwdActivity.this, FailRegDeviceActivity.class);
			startActivity(intent);
		}
		else
		{
			if((result == null || "Device not found".equals(result)))
			{
				handler = new Handler();
			    handler.postDelayed(checkDevice, 3000);
			}
			else
			{
				removeDialog(DIALOG_REGIST_DEVICE);
				finish();
				Intent intent1 = new Intent(InsertPwdActivity.this, CompleteRegistActivity.class);
				startActivity(intent1);
			}
			super.onPostExecute(result);
		}
	}
}
	
	 



     private final Runnable checkDevice = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			new RegistDeviceTask().execute();
		}
    	 
     };
     
//     private final Runnable registReceiver = new Runnable()
//  	{
//
//  		@Override
//  		public void run() {
//  			// TODO Auto-generated method stub
//  			registerReceiver(mBroadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//  			registerReceiver(mBroadcastReceiver,new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
//  		}
//  		
//  	};
  	
  	private final Runnable timeoutConnectWifi = new Runnable()
  	{

 		@Override
 		public void run() {
 			// TODO Auto-generated method stub
 			isTimeoutConnectWifi = true;
 			Toast.makeText(InsertPwdActivity.this, R.string.failed_to_connect_wifi, Toast.LENGTH_LONG).show();
 			
 		}
  		
  	};     
  	
  	private IUpdateListener NetChangelistener = new IUpdateListener()
	{
		@Override
		public void update(int status, boolean result) {
			// TODO Auto-generated method stub
			Log.e("", "************************already UPDATED***************************");
			if(status == IUpdateListener.SHOW_DIALOG)
			{
				//showDialog();
			}
			else//status == IUpdateListener.REMOVE_DIALOG
			{
				removeDialog(DIALOG_REGIST_DEVICE);
				if(result == true)
				{
					finish();
					Intent intent = new Intent(InsertPwdActivity.this, CompleteRegistActivity.class);
					startActivity(intent);
				}	
				else
				{
					finish();
					Intent intent = new Intent(InsertPwdActivity.this, FailRegDeviceActivity.class);
					startActivity(intent);
				}
			}
		}
	};
	
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
				
				if(result == true)
				{
					WifiConfiguration wifiConfigUsed;
					   timeout = false;	 
					   new Handler().postDelayed(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								timeout = true;
							}
						}, 30000);
					   
					   if(UserManager.GetAppMode() == UserManager.APP_MODE_REGIST_DEVICE)
					   {
							new Handler().postDelayed(new Runnable() {
								
							@Override
							public void run() {
								// TODO Auto-generated method stub
								new RegistDeviceTask().execute();
								}
							}, 2000);
							
								
							
					   }
					   else//Only Change the Network Enviroment
					   {
						   new Handler().postDelayed(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									serverApiMgr.invokeServerAPI();
									}
								}, 2000);							   
					   }
				}
				else
				{
					removeDialog(1);
					Toast.makeText(InsertPwdActivity.this, R.string.error_wifi_passwd, Toast.LENGTH_LONG).show();
				}
			}
		}
	};
}
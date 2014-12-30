package com.cresprit.alooh.manager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
 
import com.cresprit.alooh.AloohActivity;
import com.cresprit.alooh.IUpdateListener;
import com.cresprit.alooh.R;
import com.cresprit.alooh.SelectWifiActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;
import android.net.wifi.WifiManager;

public class SelectDeviceManager{
	private String TAG = "SelectDeviceManager";
	private final Vector<DeviceInfo> deviceList = new Vector<DeviceInfo>();
	private ArrayList<ScanResult> apList = new ArrayList<ScanResult>();
	private ArrayList<WifiConfiguration> wificonfigList;
	private WifiConfiguration wifiConfigUsed;
	private static SelectDeviceManager __instance;
	private Context context;
	private WifiManager wifiMgr;
	private Handler handler = new Handler();
	private String mSelectDevice = null;
	private String mAuthKey = null;
	private static String mAuid = null;
	private IUpdateListener mListener = null;
	private boolean timeout=false;

	public static SelectDeviceManager getInstance(Context appContext) {
		if (__instance == null) {
			__instance = new SelectDeviceManager(appContext);
			
		}
		return __instance;
	}
	
	public SelectDeviceManager(Context ctx)
	{
		this.context=ctx;
		wifiMgr = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
		
	}
	
	public void setDialogUpdateListener(IUpdateListener _listener)
	{
		mListener = _listener;
	}
	public Vector<DeviceInfo> getDeviceList()
	{
		return deviceList;
	}
	
	
	public Vector<DeviceInfo> getSearchAP(DataSetObserver observer) {
		Log.i(TAG, "[DEVLIST:PERF] getLocalMediaServerList: Obj:" + observer );
		//mLocalMediaServerDataSetObserver = observer;
		
		for(int i=0; i < apList.size(); i++)
		{
			if(apList.get(i).SSID.contains("cres"))
			{
				deviceList.add(new DeviceInfo(apList.get(i).SSID, apList.get(i).BSSID));
			}
		}
		return deviceList;
	}
	public Vector<DeviceInfo> getSearchAP() {
		wifiMgr.startScan();
		apList = (ArrayList<ScanResult>) wifiMgr.getScanResults();
		
		for(int i=0; i < apList.size(); i++)
		{
			if(apList.get(i).SSID.contains("SNOW_"))
			{
				deviceList.add(new DeviceInfo(apList.get(i).SSID, apList.get(i).BSSID));
			}
		}
		return deviceList;
	}
	
	public int getWifiLevel(int _position)
	{
		return apList.get(_position).level;
	}
	
	public WifiConfiguration getWifiConfigUsed()
	{
		return wifiConfigUsed;
	}
	
	public void setSelectDevice(String _device)
	{
		mSelectDevice = _device;
	}
	
	public String getSelectDevice()
	{
		return mSelectDevice;
	}
	
	public void setWifi(String _ssid)
	{
		String ssidUsed;
		ssidUsed = wifiMgr.getConnectionInfo().getSSID();
		setSelectDevice(_ssid);
		
		String ssid = "\""+_ssid+"\"";
		ssidUsed = "\""+ssidUsed+"\"";
		wificonfigList = (ArrayList<WifiConfiguration>) wifiMgr.getConfiguredNetworks();
		WifiConfiguration config = new WifiConfiguration();
		
		//backup WifiConfiguration object for reconnecting
		
		for(WifiConfiguration item:wificonfigList)
		{
			if(ssidUsed.equals(item.SSID))
			{
				wifiConfigUsed = item;
			}
		}
		
		config.SSID = ssid;
		//config.BSSID = item.BSSID;
		String security = "WPA_PSK";
		String password = "\"87654321\"";
		config.preSharedKey = password;
		//config.preSharedKey = "87654321";
		config.allowedGroupCiphers.set(GroupCipher.TKIP);
		config.allowedGroupCiphers.set(GroupCipher.CCMP);
		
		config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);

		config.allowedPairwiseCiphers.set(PairwiseCipher.CCMP);
		config.allowedPairwiseCiphers.set(PairwiseCipher.TKIP);
		config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
		config.allowedProtocols.set(Protocol.RSN);
		config.networkId = wifiMgr.addNetwork(config);
		
		//setupSecurity(config, security, "\""+""+"\"");//87654321
		
		
		wifiMgr.enableNetwork(config.networkId, true);
		handler.postDelayed(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				sendAuthKeyToDevice(UserManager.getInstance(context).getAuthKey());
			}
		}, 2000);
		
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				timeout = true;
			}
		}, 30000);
	}
	
	public void setWifi(WifiConfiguration _apInfo)
	{
		_apInfo.networkId = wifiMgr.addNetwork(_apInfo);
		wifiMgr.enableNetwork(_apInfo.networkId, true);
	}
	
	public int getDeviceCount()
	{
		return deviceList.size();
	}
	
	public void sendAuthKeyToDevice(String _authKey)
	{
		mAuthKey = _authKey;
		new ConnectDevice().execute(mAuthKey);
	}
	
	public void setBroadcastReceiver(int delay)
	{
		handler.postDelayed(registReceiver, delay);
	}
	
	public void unsetBroadcastReceiver()
	{
		context.unregisterReceiver(mBroadcastReceiver);
	}
	
	public static void setAuid(String _auid)
	{
		mAuid = _auid;
	}
	
	public static String getAuid()
	{
		return mAuid;
	}
	class ConnectDevice extends AsyncTask<String, Void, String> {
		String auid = null;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//mListener.update(IUpdateListener.SHOW_DIALOG);
		}

		@Override
		protected String doInBackground(String... key) {
			// TODO Auto-generated method stub
			HttpClient client = new DefaultHttpClient();

			final HttpParams params = client.getParams();

			HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
			HttpConnectionParams.setSoTimeout(params, 10 * 1000);

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("key", key[0]));

			UrlEncodedFormEntity entityRequest = null;
			try {
				entityRequest = new UrlEncodedFormEntity(nameValuePairs,"UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			HttpPost request = new HttpPost();
			request.setHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
			request.setEntity(entityRequest);

			String url = "http://192.168.21.1:9001/key?";

			try {
				request.setURI(new URI(url));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			request.setParams(params);
			HttpResponse response = null;
			try {
				response = client.execute(request);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

					HttpEntity entity = response.getEntity();
					String jsonStr = EntityUtils.toString(entity);
					Log.i(TAG,"+++++++Success+++++++++++: "+jsonStr);
					return "success";
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String _auid) {
			// TODO Auto-generated method stub
			
			WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
			String ssid = wifiInfo.getSSID();
			
			if(ssid !=null && ssid.contains("\""))
			{
				ssid = ssid.substring(1, ssid.length()-1);
			}
			if (ssid != null && ssid.equals(getSelectDevice()) && _auid != null) {
				mListener.update(IUpdateListener.REMOVE_DIALOG, true);	
				timeout = false;
			}
			else
			{
				if(timeout == false)
				{
					handler.postDelayed(retryConnectDevice, 2000);
				}
				else
				{
					Log.i(TAG,"timeout : "+timeout);
					mListener.update(IUpdateListener.REMOVE_DIALOG, false);	
					
				}
			}

			super.onPostExecute(_auid);
		}

	};	
	
	private final Runnable retryConnectDevice = new Runnable()
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			new ConnectDevice().execute(mAuthKey);
		}
	};
	
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Log.i(TAG, "NETWORK_CHANGE:" + action);

			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				
				String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);

				Log.d(TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION "+ networkInfo);

				if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
				{
					if (networkInfo.getState() != null)
					{
						Log.d(TAG,"networkInfo.getState "+ networkInfo.getState());
					}
					
					if (networkInfo.getState() != null && networkInfo.getState().toString().contains("CONNECTED"))
					{
						WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
						
						String ssid = wifiInfo.getSSID();
						if(ssid != null)
						{
							if(ssid.contains("\""))
							{
								ssid = ssid.substring(1, ssid.length()-1);
							}
							Log.i(TAG,"selectDevice Name:"+ getDeviceList().get(0).getDeviceName()+"ssid :"+ssid);
							if (getDeviceList().get(0).getDeviceName().equals(ssid))
							{
								Log.i(TAG,"selectDevice Name:"+ getDeviceList().get(0).getDeviceName()+"ssid :"+wifiInfo.getSSID());
	
								unsetBroadcastReceiver();//장치와 연결이 성공하면 더이상 네트워크 관련 콜백 받지 않음
								
							}
						}
					}
				}
			}
		}
	};
	
	private final Runnable registReceiver = new Runnable()
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			context.registerReceiver(mBroadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
			context.registerReceiver(mBroadcastReceiver,new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
		}
		
	};
	
	public class DeviceInfo{
		String m_pName;
		String m_pDeviceId;

		
		public DeviceInfo(String _name, String _deviceId)
		{
			this.m_pName = _name;
			this.m_pDeviceId = _deviceId;
		}
		
		public String getDeviceName()
		{
			return this.m_pName;
		}
		
		public String getDeviceId()
		{
			return this.m_pDeviceId;
		}
	}
}





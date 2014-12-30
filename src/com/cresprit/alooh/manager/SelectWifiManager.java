package com.cresprit.alooh.manager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.http.Header;
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
import org.json.JSONException;
import org.json.JSONObject;

import com.cresprit.alooh.IUpdateListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class SelectWifiManager{
	private String TAG = "SelectDeviceManager";
	private ArrayList<WifiConfiguration> apList = new ArrayList<WifiConfiguration>();
	private ArrayList<WifiConfiguration> apListSorted  = new ArrayList<WifiConfiguration>();
	private ArrayList<DeviceInfo> apListSorting = new ArrayList<DeviceInfo>();
	
	private ArrayList<WifiConfiguration> wificonfigList;
	private static SelectWifiManager __instance;
	private Context context;
	private WifiManager wifiMgr;
	private Handler handler = new Handler();
	private String m_pPassword = null;
	private ArrayList<ScanResult> apList_scanResult;
	private WifiConfiguration config = null;
	WifiInfo wifiInfo=null;
	private IUpdateListener mListener = null;
	
	
	public static SelectWifiManager getInstance(Context appContext) {
		if (__instance == null) {
			__instance = new SelectWifiManager(appContext);
			
		}

		return __instance;
	}
	public SelectWifiManager(Context ctx)
	{
		this.context=ctx;
		wifiMgr = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
	}

	public void setDialogUpdateListener(IUpdateListener _listener)
	{
		mListener = _listener;
	}
	
	public ArrayList<WifiConfiguration> getDeviceList()
	{
		return apList;
	}
	
	
	public ArrayList<WifiConfiguration> getSearchAP(DataSetObserver observer) {
		Log.i(TAG, "[DEVLIST:PERF] getLocalMediaServerList: Obj:" + observer );
		//mLocalMediaServerDataSetObserver = observer;
		apList = (ArrayList<WifiConfiguration>) wifiMgr.getConfiguredNetworks();
		Vector test = new Vector();
		HashMap test1 = new HashMap();
		Collection test2 ;
		return apList;
	}
	public ArrayList<WifiConfiguration> getSearchAP() {
		apList = (ArrayList<WifiConfiguration>) wifiMgr.getConfiguredNetworks();
		
		return apList;
	}
	public int getWifiLevel(String _wifi)
	{
		apList_scanResult = (ArrayList<ScanResult>) wifiMgr.getScanResults();
		for(int i=0; i< apList_scanResult.size();i++)
		{
			if(_wifi.equals(apList_scanResult.get(i).SSID))
				return apList_scanResult.get(i).level;
		}
		return 0;
	}
	
	public void doWifiListSort()
	{
		apList_scanResult = (ArrayList<ScanResult>) wifiMgr.getScanResults();
		for(int i = 0;i<apList_scanResult.size();i++)
		{
			if(!apList_scanResult.get(i).SSID.contains("SNOW_"))
				apListSorting.add(new DeviceInfo(apList_scanResult.get(i).SSID, apList_scanResult.get(i).level));
		}
		Collections.sort(apListSorting, new Comparator(){

			@Override
			public int compare(Object lhs, Object rhs) {
				// TODO Auto-generated method stub
				DeviceInfo device1 = (DeviceInfo)lhs;
				DeviceInfo device2 = (DeviceInfo)rhs;
				
				if(device1.m_nLevel > device2.m_nLevel)
					return -1;
				else if(device1.m_nLevel < device2.m_nLevel)
					return 1;
				else					
					return 0;
			}
			
		});
		

		//for(int i = 0;i<apList_scanResult.size();i++)
		//	Log.i("",""+apListSorting.get(i).m_nLevel);
		
	
	}
	
	public ArrayList<DeviceInfo> getWifiListSorted()
	{
		return apListSorting;
	}
	
	public boolean setWifi(String _ssid, String _passwd)
	{
		String ssid = "\""+_ssid+"\"";
		wificonfigList = (ArrayList<WifiConfiguration>) wifiMgr.getConfiguredNetworks();
		WifiConfiguration config = new WifiConfiguration();
		boolean retValue = false;
		
		for(WifiConfiguration item:wificonfigList)
		{
			if(ssid.equals(item.SSID))
			{
				config = item;
				config.preSharedKey = "\""+_passwd+"\""; 
				config.networkId = wifiMgr.addNetwork(config);
				
				//setupSecurity(config, security, "\""+""+"\"");//87654321
				
				retValue = wifiMgr.enableNetwork(config.networkId, true);
				wifiMgr.reassociate();

			}
		}
		
		return retValue;
	}
	
	public String getWifiConfigProtocol(WifiConfiguration config)
	{
		String protocol= null;
		boolean bit = false;
		
		for(int i=0; i < config.allowedProtocols.length();i++)
		{
			bit = config.allowedProtocols.get(i);
			
			if(bit == true)
			{
				switch(i)
				{
					case WifiConfiguration.Protocol.RSN:
						protocol = "RSN";
						break;
						
					case WifiConfiguration.Protocol.WPA:
						protocol = "WPA";
						break;
				}
			}
		}
		
		return protocol;
	}
	
	public String getWifiConfigKeyMgmt(WifiConfiguration config)
	{
		String protocol= null;
		boolean bit = false;
		
		for(int i=0; i < config.allowedKeyManagement.length();i++)
		{
			bit = config.allowedProtocols.get(i);
			
			if(bit == true)
			{
				switch(i)
				{
					case WifiConfiguration.KeyMgmt.IEEE8021X:
						protocol = "IEEE8021X";
						break;
						
					case WifiConfiguration.KeyMgmt.NONE:
						protocol = "NONE";
						break;
						
					case WifiConfiguration.KeyMgmt.WPA_EAP:
						protocol = "WPA-EAP";
						break;
						
					case WifiConfiguration.KeyMgmt.WPA_PSK:
						protocol = "WPA-PSK";
						break;
				}
			}
		}
		
		return protocol;
	}

	public String getWifiConfigPairwiseCipher(WifiConfiguration config)
	{
		String protocol= null;
		boolean bit = false;
		
		for(int i=0; i < config.allowedPairwiseCiphers.length();i++)
		{
			bit = config.allowedPairwiseCiphers.get(i);
			
			if(bit == true)
			{
				switch(i)
				{
					case WifiConfiguration.PairwiseCipher.CCMP:
						protocol = "CCMP";
						Log.i("","Pairwise : "+protocol);
						break;
						
					case WifiConfiguration.PairwiseCipher.NONE:
						protocol = "NONE";
						Log.i("","Pairwise : "+protocol);
						break;
						
					case WifiConfiguration.PairwiseCipher.TKIP:
						protocol = "TKIP";
						Log.i("","Pairwise : "+protocol);
						break;						
				}
			}
		}
		
		return protocol;
	}
	

	public String getWifiConfigGroupCipher(WifiConfiguration config)
	{
		String protocol= "";
		boolean bit = false;
		
		for(int i=0; i < config.allowedGroupCiphers.length();i++)
		{
			bit = config.allowedGroupCiphers.get(i);
			
			if(bit == true)
			{
				switch(i)
				{
					case WifiConfiguration.GroupCipher.CCMP:
						if(!"".equals(protocol))
							protocol+=" ";
						protocol += "CCMP";
						Log.i("","GroupCipher : "+protocol);						
						break;
						
					case WifiConfiguration.GroupCipher.WEP104:
						if(!"".equals(protocol))
							protocol+=" ";
						protocol += "WEP104";
						Log.i("","GroupCipher : "+protocol);
						break;
						
					case WifiConfiguration.GroupCipher.TKIP:
						if(!"".equals(protocol))
							protocol+=" ";
						protocol += "TKIP";
						Log.i("","GroupCipher : "+protocol);
						break;						
						
					case WifiConfiguration.GroupCipher.WEP40:
						if(!"".equals(protocol))
							protocol+=" ";
						protocol += "WEP40";
						Log.i("","GroupCipher : "+protocol);
						break;						
				}
			}
		}
		
		return protocol;
	}

	public String getWifiConfigAuthAlgorithm(WifiConfiguration config)
	{
		return null;
	}

	
	public int getDeviceCount()
	{
		return apList.size();
	}
	 
	public void setWifiPassword(String _password)
	{
		this.m_pPassword = _password;
	}
	
	public void PostWifiConfiguration(String _ssid)
	{
		PostWifiConfigTask task = new PostWifiConfigTask();
		task.execute(_ssid);
	}
	class PostWifiConfigTask extends AsyncTask<String, Void, Boolean>{

		@Override
		protected Boolean doInBackground(String... param) {
			// TODO Auto-generated method stub
			WifiConfiguration selectedAP = null;
			WifiConfiguration config = new WifiConfiguration();//안드로이드에서 연결할 AP 데이터 
			int nProto = 0;
			int nPairwiseCiphers = 0;
			int nGroupCiphers;
			int nKeyManagement;
			String pSsid;
			String pTempSsid = null;
			String pProto = null;
			String pPairwiseCiphers = null;
			String pKeyManagement = null;
			String pGroupCiphers = null;
			String pPwd = null;	
			String pAuid = null;
			JSONObject auid= null;
			String _ssid = param[0];
			boolean result=false;
			
			for(int i=0;i<getDeviceList().size();i++)
			{
				pTempSsid = getDeviceList().get(i).SSID.substring(1, getDeviceList().get(i).SSID.length()-1);
				if(_ssid.equals(pTempSsid))
				{
					selectedAP = getDeviceList().get(i);
					Log.i(TAG,""+selectedAP.preSharedKey);
					Log.i(TAG,""+selectedAP.wepKeys);
				}
			}
			//selectedAP.SSID;
		
			wifiInfo = wifiMgr.getConnectionInfo();
			
			Log.i(TAG,"selectedAP");
			
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			if(selectedAP != null)
			{
				Log.i("","select AP : "+selectedAP.toString());
				pSsid = selectedAP.SSID;
				pProto = getWifiConfigProtocol(selectedAP);
				Log.i(TAG,selectedAP.allowedProtocols.toString()+"	"+pProto);
				
				pPairwiseCiphers = getWifiConfigPairwiseCipher(selectedAP);
				Log.i(TAG, selectedAP.allowedPairwiseCiphers.toString()+"	"+pPairwiseCiphers);
				
				pKeyManagement = getWifiConfigKeyMgmt(selectedAP);
				Log.i(TAG,selectedAP.allowedKeyManagement.toString()+"	"+pKeyManagement);		
				
				pGroupCiphers = getWifiConfigGroupCipher(selectedAP);
				Log.i(TAG,selectedAP.allowedGroupCiphers.toString()+"	"+pGroupCiphers);
				
				nameValuePairs.add(new BasicNameValuePair("ssid", pSsid.substring(1, pSsid.length()-1)));
				if(pProto != null & !"".equals(pProto))
					nameValuePairs.add(new BasicNameValuePair("proto", pProto));
				//nameValuePairs.add(new BasicNameValuePair("proto", "WPA"));
				if(pGroupCiphers != null & !"".equals(pGroupCiphers))			
					nameValuePairs.add(new BasicNameValuePair("group", pGroupCiphers));
				//nameValuePairs.add(new BasicNameValuePair("group", "CCMP"));
				
				if(pKeyManagement != null & !"".equals(pKeyManagement))		
					nameValuePairs.add(new BasicNameValuePair("key_mgmt", pKeyManagement));
				//nameValuePairs.add(new BasicNameValuePair("key_mgmt", "WPA_PSK"));
				if(pPairwiseCiphers != null & !"".equals(pPairwiseCiphers))		
					nameValuePairs.add(new BasicNameValuePair("pairwise", pPairwiseCiphers));
				//nameValuePairs.add(new BasicNameValuePair("pairwise", "CCMP"));
			}
			else
			{
				Log.i(TAG,"selectedAP null");
				nameValuePairs.add(new BasicNameValuePair("ssid",_ssid));
			}
			Log.i(TAG,"selectedAP after");
			
			
			HttpClient client = new DefaultHttpClient();
			
			final HttpParams params = client.getParams();

			//	20110317	thkim 사용자 삭제  timeout 값  수정
			HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
			HttpConnectionParams.setSoTimeout(params, 10 * 1000);
			
			nameValuePairs.add(new BasicNameValuePair("passwd", m_pPassword));
			
			if(UserManager.GetAppMode() == UserManager.APP_MODE_REGIST_DEVICE)
				nameValuePairs.add(new BasicNameValuePair("name", UserManager.getInstance(context).getDeviceName())); 
			
			UrlEncodedFormEntity entityRequest = null;
			try {
				entityRequest = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			HttpPost request = new HttpPost();
			request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			request.setEntity(entityRequest);
		
			String url = "http://192.168.21.1:9001/data?";
			

			try {
				request.setURI(new URI(url));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
				request.setParams(params);
				//request.setHeader("passwd","1111");
				HttpResponse response = null;

				try {
					response = client.execute(request);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					{
						
						HttpEntity entity = response.getEntity();
						Header[] header  = response.getHeaders("Auid");

						String test = header[0].toString();
						String[] aaa = test.split("Auid: ");
						
						Log.i(TAG,"Auid:"+aaa[1]+"::");
						SelectDeviceManager.setAuid(aaa[1]);

						
						if(selectedAP != null)
						{
							selectedAP.networkId = wifiMgr.addNetwork(selectedAP);
							//setupSecurity(config, security, "\""+""+"\"");//87654321
							result = wifiMgr.enableNetwork(selectedAP.networkId, true);		
							wifiMgr.reconnect();
						}
						else
						{
							config.SSID = "\""+_ssid+"\"";
							config.preSharedKey = "\""+m_pPassword+"\"";
							config.networkId = wifiMgr.addNetwork(config);
							result = wifiMgr.enableNetwork(config.networkId, true);		
							wifiMgr.reconnect();
						}
						Log.i(TAG, "enableNetwork Result : "+result);

					}
					else
					{
						Log.i(TAG, "Error to Send Supplicant Information   : "+response.getStatusLine().getStatusCode());
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
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			if(mListener != null)
			{
				mListener.update(IUpdateListener.REMOVE_DIALOG, result);
			}
			super.onPostExecute(result);
			
		}
		
	};
	
	
	
	
	public class DeviceInfo{
		String m_pName;
		String m_pDeviceId;
		int m_nLevel;
		
		public DeviceInfo(String _name, String _deviceId)
		{
			this.m_pName = _name;
			this.m_pDeviceId = _deviceId;
		}
		
		public DeviceInfo(String _ssid, int _level)
		{
			this.m_pDeviceId = _ssid;
			this.m_nLevel = _level;
		}
		
		public String getDeviceName()
		{
			return this.m_pName;
		}
		
		public String getDeviceID()
		{
			return this.m_pDeviceId;
		}
		
		public int getLevel()
		{
			return m_nLevel;
		}
	}
}





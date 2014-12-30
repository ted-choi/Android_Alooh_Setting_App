package com.cresprit.alooh.server_api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cresprit.alooh.IUpdateListener;
import com.cresprit.alooh.InsertPwdActivity;
import com.cresprit.alooh.R;
import com.cresprit.alooh.SetDeviceNameActivity;
import com.cresprit.alooh.manager.SelectDeviceManager;
import com.cresprit.alooh.manager.UserManager;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


public class ServerAPIManager
{
	static ServerAPIManager __instance = null;
	Context context;
	IUpdateListener mListener = null;
	WifiManager m_WifiMgr;
	String m_pDeviceNameToUpdate;
	String m_pIpAddressToUpdate;
	String m_pAccessKey;
	
	Handler handler = new Handler();
	private boolean timeout=false;
	
	public static ServerAPIManager getInstance(Context appContext) {
		if (__instance == null) {
			__instance = new ServerAPIManager(appContext);
			
		}
		return __instance;
	}
	
	public ServerAPIManager(Context ctx)
	{
		this.context=ctx;
		m_WifiMgr = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
		
	}
	
	public void setDialogUpdateListener(IUpdateListener _listener)
	{
		mListener = _listener;
	}
	
	public void invokeServerAPI()
	{
		new ServerApiTask().execute();
		handler.postDelayed(timeoutRunnable, 30000);
	}
	
	public void setUpdateItemsToUpdate(String _deviceName, String _ipAddress)
	{
		m_pDeviceNameToUpdate = _deviceName;
		m_pIpAddressToUpdate = _ipAddress;
	}
	
	public void setAccessKey(String _accessKey)
	{
		m_pAccessKey = _accessKey;
	}
	
	class ServerApiTask extends AsyncTask<String, Void, String> {
		String auid=null;
		String result = null;
		@Override
		protected String doInBackground(String... key) {
			// TODO Auto-generated method stub
			JSONObject data = new JSONObject();
			JSONObject json = new JSONObject();
			
	
			HttpClient client = new DefaultHttpClient();
			final HttpParams params = client.getParams();
			
			HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
			HttpConnectionParams.setSoTimeout(params, 10 * 1000);

			HttpGet request = new HttpGet();
			request.setHeader("Authorization", "Bearer "+UserManager.getInstance(context).getAuthKey());
			try {
				request.setURI(new URI(UserManager.SERVER_API_GET_DEVICE_LIST_URL));
			} catch (URISyntaxException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			try {
				HttpResponse response = client.execute(request);
				HttpEntity responseEntity = response.getEntity();
				Log.i("", "ServerApiTask http response: STATUS_CODE: " + response.getStatusLine().getStatusCode()+response.getStatusLine().getReasonPhrase());
				response.getStatusLine().getReasonPhrase();
				
				HttpEntity entity = response.getEntity();
				String jsonStr = EntityUtils.toString(entity);
				
				try {
					JSONObject resObj = new JSONObject(jsonStr);
					JSONObject resData = resObj.getJSONObject("data");
					JSONArray deviceArray = resData.getJSONArray("devices"); 
					
					for(int i=0; i<deviceArray.length();i++)
					{
						JSONObject device = deviceArray.getJSONObject(i);
						String name = device.getString("name");
						String feedId = device.getString("feed_id");
						String activationCode = device.getString("activation_code"); 
						String productName = device.getString("product_name");
						String auid = device.getString("serial");
						int status = device.getInt("status");
						String ipAddress = device.getString("ip");
						if(SelectDeviceManager.getAuid() != null)
						{
							if(SelectDeviceManager.getAuid().equals(auid))
							{
								if(status == 2)
								{
									Log.i("", "*****************************Find Item *********************************");
									setUpdateItemsToUpdate(name, ipAddress);
									return auid;
								}
							}
						}
						//SelectDeviceManager.getInstance(context)

					}
					Log.i("","resData : "+resData.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		protected void onPostExecute(String _result) {
			// TODO Auto-generated method stub

			if(timeout == false && _result == null)
			{
				handler.postDelayed(retryCheckUpdate, 2000);
			}
			else
			{
				if(timeout == true)
					mListener.update(IUpdateListener.REMOVE_DIALOG, false);
				else
					new ServerApiGetAccessKeyByNameTask().execute();
			}
			
			super.onPostExecute(_result);
		}
		
	};
	
	
	class ServerApiUndoStatusTask extends AsyncTask<String, Void, String> {
		String auid=null;
		String result = null;
		@Override
		protected String doInBackground(String... key) {
			// TODO Auto-generated method stub
			JSONObject data = new JSONObject();
			JSONObject json = new JSONObject();
			
			try {
				data.put("name"	, m_pDeviceNameToUpdate);
				data.put("status", "OFF");
				data.put("ip", m_pIpAddressToUpdate);
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
			request.setHeader("Authorization", "Bearer "+m_pAccessKey);

			try {
				request.setURI(new URI(UserManager.SERVER_API_UPDATE_DEVICE_URL));
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
				Log.i("", "ServerApiUndoStatusTask http response: STATUS_CODE: " + response.getStatusLine().getStatusCode()+response.getStatusLine().getReasonPhrase());
				response.getStatusLine().getReasonPhrase();
				
				HttpEntity entity = response.getEntity();
				String jsonStr = EntityUtils.toString(entity);
				if(response.getStatusLine().getStatusCode() == 200)
				result = "200";
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
		protected void onPostExecute(String _result) {
			// TODO Auto-generated method stub

			if(timeout == false && _result == null)
			{
				handler.postDelayed(retryUpdateDevice, 2000);
			}
			else
			{
				if(timeout == true)
					mListener.update(IUpdateListener.REMOVE_DIALOG, false);
				else
				{
					mListener.update(IUpdateListener.REMOVE_DIALOG, true);
				}
			}
			
			super.onPostExecute(_result);
		}
		
	};	

	class ServerApiGetAccessKeyByNameTask extends AsyncTask<String, Void, String> {
		String auid=null;
		String result = null;
		@Override
		protected String doInBackground(String... key) {
			// TODO Auto-generated method stub
			JSONObject data = new JSONObject();
			JSONObject json = new JSONObject();
			
			try {
				data.put("name"	, m_pDeviceNameToUpdate);
				json.put("type", "3");
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
			request.setHeader("Authorization", "Bearer "+UserManager.getInstance(context).getAuthKey());
			
			try {
				request.setURI(new URI(UserManager.SERVER_API_GET_DEVICE_URL));
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
				Log.i("", "ServerApiGetAccessKeyByNameTask http response: STATUS_CODE: " + response.getStatusLine().getStatusCode()+response.getStatusLine().getReasonPhrase());
				response.getStatusLine().getReasonPhrase();
				
				HttpEntity entity = response.getEntity();
				String jsonStr = EntityUtils.toString(entity);
				
					JSONObject resObj;
					try {
						resObj = new JSONObject(jsonStr);
						JSONObject resData = resObj.getJSONObject("data");
						JSONArray keyArray = resData.getJSONArray("keys");
						JSONObject keyItem = keyArray.getJSONObject(0);
						result = keyItem.getString("key");
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
		protected void onPostExecute(String _result) {
			// TODO Auto-generated method stub

			if(timeout == false && _result == null)
			{
				handler.postDelayed(retryGetAccessKey, 2000);
			}
			else
			{
				if(timeout == true)
					mListener.update(IUpdateListener.REMOVE_DIALOG, false);
				else
				{
					setAccessKey(_result);
					new ServerApiUndoStatusTask().execute();	
				}
			}
			
			super.onPostExecute(_result);
		}
		
	};	
	
	
	private final Runnable retryCheckUpdate = new Runnable()
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			new ServerApiTask().execute();
		}
	};	
	
	private final Runnable retryUpdateDevice = new Runnable()
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			new ServerApiUndoStatusTask().execute();
		}
	};

	private final Runnable retryGetAccessKey = new Runnable()
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			new ServerApiGetAccessKeyByNameTask().execute();
		}
	};
	
	private final Runnable timeoutRunnable = new Runnable()
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			timeout = true;
		}
	};
	
}
package com.cresprit.alooh;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

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

import com.cresprit.alooh.R;
import com.cresprit.alooh.manager.SelectWifiManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;



public class SelectWifiActivity  extends Activity implements OnItemClickListener{
	private static String TAG = "SelectDeviceActivity";
	private static int DIALOG_CLOSE_APP = 2; 
	ListView listV;
	ImageView seperate_top;
	ImageView seperate_bottom;
	SelectWifiAdapter adapter;
	SelectWifiManager wifi;
	int selectDevice = 0;
	ArrayList<WifiConfiguration> apList = new ArrayList<WifiConfiguration>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectwifi_layout);
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		wifi = SelectWifiManager.getInstance(this);
		wifi.getDeviceList().clear();
		wifi.getWifiListSorted().clear();
		
		apList = wifi.getSearchAP();
		
		adapter = SelectWifiAdapter.createSelectDeviceAdapter(SelectWifiActivity.this);
		
		seperate_top = (ImageView)findViewById(R.id.seperate_top);
		seperate_bottom = (ImageView)findViewById(R.id.seperate_bottom);
		
		if(wifi.getDeviceCount() > 0)
		{
			seperate_top.setVisibility(View.VISIBLE);
			seperate_bottom.setVisibility(View.VISIBLE);
		}
		listV = (ListView) findViewById(R.id.deviceListv);
		listV.setAdapter(adapter);
		adapter.registerDataSetObserver(mDeviceListDataSetObserver);
		listV.setOnItemClickListener(this);
		wifi.doWifiListSort();
	}

	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		showDialog(DIALOG_CLOSE_APP);
	}



	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		String ssid;
		finish();
		ssid = wifi.getWifiListSorted().get(position).getDeviceID();
		//ssid = device.getDeviceList().get(position).SSID;
		Intent intent = new Intent(SelectWifiActivity.this, InsertPwdActivity.class);
		intent.putExtra("ssid", ssid);
		intent.putExtra("position", position);
		startActivity(intent);
	}
	
    @Override
    protected Dialog onCreateDialog(int id) {
      
    		switch(id)
    		{
    		
    		case 0:
            return new AlertDialog.Builder(SelectWifiActivity.this)
                .setTitle(R.string.will_connect_wifi)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	showDialog(1);		
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked OK so do some stuff */
                    		
                    }
                })
                .create();
            
    		case 1:
    				 ProgressDialog dialog = new ProgressDialog(this);
    				 dialog.setTitle(R.string.connecting);
    				 dialog.setIndeterminate(true);
    				 dialog.setCancelable(true);
    				 return dialog;

    		case 2:
    			return new AlertDialog.Builder(SelectWifiActivity.this)
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
			return null; 
    }	
	private DataSetObserver mDeviceListDataSetObserver = new  DataSetObserver() {

		@Override
		public void onChanged() {
			super.onChanged();
		}

		@Override
		public void onInvalidated() {
			Log.e(TAG, "ListView:DataSetObserver.onChanged()");
			super.onInvalidated();
		}
		
	};

}
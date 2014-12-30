package com.cresprit.alooh;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

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
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.cresprit.alooh.R;
import com.cresprit.alooh.manager.SelectDeviceManager;
import com.cresprit.alooh.manager.UserManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;

public class SelectDeviceActivity extends Activity implements		OnItemClickListener {
	private static String TAG = "SelectDeviceActivity";
	private static int DIALOG_CLOSE_APP = 3;
	ListView listV;
	ImageView seperate_top;
	ImageView seperate_bottom;
	SelectDeviceAdapter adapter;
	SelectDeviceManager device;
	int selectDevice = 0;
	Handler handler = null;
	static int retryCnt = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectdevice_layout);

		device = SelectDeviceManager.getInstance(this);
		device.setDialogUpdateListener(listener);
		device.getDeviceList().clear();
		device.getSearchAP();

		adapter = SelectDeviceAdapter.createSelectDeviceAdapter(SelectDeviceActivity.this);

		seperate_top = (ImageView) findViewById(R.id.seperate_top);
		seperate_bottom = (ImageView) findViewById(R.id.seperate_bottom);

		if (device.getDeviceCount() > 0) {
			seperate_top.setVisibility(View.VISIBLE);
			seperate_bottom.setVisibility(View.VISIBLE);
		}
		listV = (ListView) findViewById(R.id.deviceListv);
		listV.setAdapter(adapter);
		adapter.registerDataSetObserver(mDeviceListDataSetObserver);
		listV.setOnItemClickListener(this);
		handler = new Handler();
	}

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		startActivity(new Intent(SelectDeviceActivity.this, AloohActivity.class));
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		selectDevice = position;
		showDialog(0);
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {

		case 0:
			return new AlertDialog.Builder(SelectDeviceActivity.this)
					.setTitle(device.getDeviceList().get(selectDevice).getDeviceName()+getResources().getString(R.string.will_connect_device))
					.setCancelable(false)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									device.setWifi(device.getDeviceList().get(selectDevice).getDeviceName());
									device.setBroadcastReceiver(1000);
									
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
		
		case 2:
			return new AlertDialog.Builder(SelectDeviceActivity.this)
					.setTitle(R.string.noti)
					.setCancelable(false)
					.setMessage(R.string.failed_to_connect_device)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									finish();
									startActivity(new Intent(SelectDeviceActivity.this, AloohActivity.class));
								}
							}).create();	
			
		case 3:
			return new AlertDialog.Builder(SelectDeviceActivity.this)
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

	private DataSetObserver mDeviceListDataSetObserver = new DataSetObserver() {

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
					finish();
					Intent intent = new Intent(SelectDeviceActivity.this, SelectWifiActivity.class);
					startActivity(intent);
				}
			}
		}
	};
	
}

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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.cresprit.alooh.R;
import com.cresprit.alooh.manager.UserManager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class SetDeviceNameActivity extends Activity{
	Button confirmBtn=null;
	Button chgNetBtn = null;
	TextView ssidTv = null;
	EditText deviceNameEdt = null;
	String m_pDeviceName = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		String tempDeviceName = intent.getStringExtra(AloohActivity.DEVICE_NAME);
		setContentView(R.layout.setdevicename_layout);
		
		
		confirmBtn = (Button)findViewById(R.id.confirmbtn);
		confirmBtn.setOnClickListener(new View.OnClickListener() {
			   public void onClick(View view) {
				   m_pDeviceName = deviceNameEdt.getText().toString();

				   if(m_pDeviceName == null || "".equals(m_pDeviceName))
				   {
					   showDialog(0);
				   }
				   else
				   {
					   m_pDeviceName = deviceNameEdt.getText().toString();
					   UserManager.getInstance(SetDeviceNameActivity.this).setDeviceName(m_pDeviceName);
					   new PostAuthKey().execute();
				   }
			   }
			});
		
//		chgNetBtn = (Button)findViewById(R.id.changenetworkbtn);
//		chgNetBtn.setOnClickListener(new View.OnClickListener() {
//			   public void onClick(View view) {
//				   finish();
//				   Intent intent1 = new Intent(SetDeviceNameActivity.this, AloohActivity.class); 
//				   startActivity(intent1);
//			   }
//			});
		ssidTv = (TextView)findViewById(R.id.ssid);
		deviceNameEdt = (EditText)findViewById(R.id.devicename_edt);
		if(tempDeviceName != null && !"".equals(tempDeviceName))
		{
			deviceNameEdt.setText(tempDeviceName);
			deviceNameEdt.setSelection(tempDeviceName.length());
		}
	}
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		showDialog(2);
	}


	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		// TODO Auto-generated method stub
		switch(id)
		{
		case 0:
			return new AlertDialog.Builder(SetDeviceNameActivity.this)
            .setTitle(R.string.noti)
            .setMessage(R.string.insert_device_name)
            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                			
                    /* User clicked OK so do some stuff */

                }
            })
            .create();			
			
		case 1:
			 ProgressDialog dialog = new ProgressDialog(this);
			 dialog.setTitle(R.string.regist_device);
			 dialog.setMessage(getResources().getString(R.string.noti_wait_moment));
			 dialog.setIndeterminate(true);
			 dialog.setCancelable(false);
			 return dialog;
			
		case 2:
			return new AlertDialog.Builder(SetDeviceNameActivity.this)
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
		
		return super.onCreateDialog(id, args);
	}
	class PostAuthKey extends AsyncTask<String, Void, String> {
		String auid=null;
		String result = null;
		@Override
		protected String doInBackground(String... key) {
			// TODO Auto-generated method stub
			JSONObject data = new JSONObject();
			JSONObject json = new JSONObject();
			
			try {
				data.put("auid"	, UserManager.getInstance(SetDeviceNameActivity.this).getAuid());
				data.put("name"	, UserManager.getInstance(SetDeviceNameActivity.this).getDeviceName());
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
			request.setHeader("Authorization", "Bearer "+UserManager.getInstance(SetDeviceNameActivity.this).getAuthKey());

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
				response.getStatusLine().getReasonPhrase();
				
				HttpEntity entity = response.getEntity();
				String jsonStr = EntityUtils.toString(entity);
				
				try {
					JSONObject resObj = new JSONObject(jsonStr);
					result = resObj.getString("error");
					//username = resData.getString("id");
					Log.i("","result : "+result);
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

			if(_result != null)
			{
				finish();
				Intent intent1 = new Intent(SetDeviceNameActivity.this, AloohActivity.class); 
				startActivity(intent1);
			}
			else
			{
				Toast.makeText(SetDeviceNameActivity.this, R.string.noti_same_device_name, Toast.LENGTH_SHORT).show();
			}
			
			super.onPostExecute(_result);
		}
		
	};
	
	
	
}
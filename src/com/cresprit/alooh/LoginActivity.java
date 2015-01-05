package com.cresprit.alooh;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import com.cresprit.alooh.SignUpActivity.SendEmailTask;
import com.cresprit.alooh.manager.UserManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements View.OnClickListener{
	private static final String PROPERTY_USER_ID = "userid";
	private static final String PROPERTY_SERVER_SETTING = "server_setting";
	private static int DIALOG_LOGIN = 0;
	private static int DIALOG_CLOSE = 1;
	private static int DIALOG_WIFI_WARNING = 2;
	private static int DIALOG_SNOW_WARNING =3;
	private static int DIALOG_SERVER_SETTING = 4;
	
	private Button btnLogin;
	private TextView tvSignup;
	private TextView tvFindPwd;
	private EditText edtId;
	private EditText edtPasswd;
	private UserManager userMgr=null;
	private String m_pId;
	private String m_pPasswd;
	private SharedPreferences mSharedPreferences;
	private WifiManager wifiMgr = null;
	private int selectServer = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		
		edtId = (EditText)findViewById(R.id.id_edt);
		mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
		edtId.setText(getUserId());
		edtId.setSelection(edtId.getText().length());
		selectServer = getServerSetting();
		UserManager.ServerSetting(selectServer);
		
		edtPasswd = (EditText)findViewById(R.id.passwd_edt);
		edtPasswd.setText("");
		
		btnLogin = (Button)findViewById(R.id.loginbtn);
		btnLogin.setOnClickListener(this);
		
		tvSignup = (TextView)findViewById(R.id.signup_tv);
		tvSignup.setOnClickListener(this);
		
		tvFindPwd = (TextView)findViewById(R.id.find_passwd_tv);
		tvFindPwd.setOnClickListener(this);

		wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(v.getId())
		{
		case R.id.loginbtn:
			String ssid = null;
			m_pId = edtId.getText().toString();
			m_pPasswd = edtPasswd.getText().toString();
			
			if(m_pId.length() == 0)
			{
				Toast.makeText(this, R.string.insert_id, Toast.LENGTH_SHORT).show();
				break;
			}

			if(m_pId.equals("cresprit0101"))
			{
				Log.i("",""+getServerSetting());
				Log.i("",""+UserManager.SERVER_API_LOGIN_URL);
				showDialog(DIALOG_SERVER_SETTING);
				break;
			}
			
			if(m_pPasswd.length() == 0)
			{
				Toast.makeText(this, R.string.insert_passwd, Toast.LENGTH_SHORT).show();
				break;				
			}
			
			
			
			if(wifiMgr == null ||wifiMgr.getWifiState() != WifiManager.WIFI_STATE_ENABLED 
					|| wifiMgr.getDhcpInfo() == null)
			{
				Toast.makeText(this, R.string.noti_check_wifi_status, Toast.LENGTH_SHORT).show();
				break;				
			}
			else
			{
				ssid = wifiMgr.getConnectionInfo().getSSID();
				if(ssid == null)
				{
					Toast.makeText(this, R.string.noti_check_wifi_status, Toast.LENGTH_SHORT).show();
					break;
				}
			}
			setUserId(m_pId);

			userMgr = UserManager.getInstance(LoginActivity.this);
			
			
			if(ssid.contains("SNOW_"))
			{
				showDialog(DIALOG_SNOW_WARNING);
				break;
			}
			
			//UserManager.ServerSetting(getServerSetting());
//			if(getServerSetting() == UserManager.ALOOH_SERVER)
//				Toast.makeText(this, "ALOOH Server", Toast.LENGTH_SHORT).show();
//			else
//				Toast.makeText(this, "PINO Server", Toast.LENGTH_SHORT).show();
			
			LoginTask loginTask = new LoginTask();
			loginTask.execute("");
			break;
			
		case R.id.signup_tv:
			intent = new Intent(LoginActivity.this, SignUpActivity.class);
			startActivity(intent);
			break;
			
		case R.id.find_passwd_tv:
			intent = new Intent(LoginActivity.this, FindPasswdActivity.class);
			startActivity(intent);				
			break;
		}
	}

	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		showDialog(DIALOG_CLOSE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option, menu);
		return super.onCreateOptionsMenu(menu);
	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// TODO Auto-generated method stub
//		//if(item.getItemId() == R.id.setting)
//		{
//			Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
//			startActivity(intent);
//		}
//		return super.onOptionsItemSelected(item);
//	}

	public String getUserId()
	{
		return getString( PROPERTY_USER_ID, "" );
	}
	
	public void setUserId(String id)
	{
		setString(PROPERTY_USER_ID, id);
	}	
	
	public int getServerSetting()
	{
		if( mSharedPreferences == null )
		{
			Log.i("","ERROR");			
			return 0;
			
		}
		return mSharedPreferences.getInt(PROPERTY_SERVER_SETTING, 0);
	}
	
	public void setServerSetting(int _server)
	{
		if( mSharedPreferences == null )
		{
			Log.i("","ERROR");
			return;
		}
		Log.i("","Server Setting : "+_server);
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		Log.i("",editor.putInt(PROPERTY_SERVER_SETTING, _server).toString());
		Log.i("","commit result :"+editor.commit());
		UserManager.ServerSetting(_server);
	}
	
	public String getString(String key, String defaultValue )
	{
		if( mSharedPreferences == null )
			return defaultValue;

		return mSharedPreferences.getString(key, defaultValue);
		
	}
	
	public void setString(String key, String value)
	{
		if( mSharedPreferences == null )
			return;
		
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString(key, value);

		editor.commit();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		
		if(id == DIALOG_LOGIN)
		{
			 ProgressDialog dialog = new ProgressDialog(this);
			 dialog.setTitle(R.string.login);
			 dialog.setMessage(getResources().getString(R.string.noti_wait_moment));
			 dialog.setIndeterminate(true);
			 dialog.setCancelable(false);
			 return dialog;
		}
		else if(id == DIALOG_CLOSE)
			return new AlertDialog.Builder(LoginActivity.this)
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
		else if(id == DIALOG_SNOW_WARNING)
			return new AlertDialog.Builder(LoginActivity.this)
					.setTitle(getResources().getString(R.string.noti))
					.setMessage(R.string.warn_wifi_impossible)
					.setCancelable(false)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
								}
					}).create();
		else if(id == DIALOG_WIFI_WARNING)
			return new AlertDialog.Builder(LoginActivity.this)
					.setTitle(getResources().getString(R.string.noti))
					.setMessage(R.string.noti_check_wifi_status)
					.setCancelable(false)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
								}
					}).create();		
		else if(id == DIALOG_SERVER_SETTING)
			 return new AlertDialog.Builder(LoginActivity.this)
					.setTitle("서버설정")
					.setCancelable(false)
					.setSingleChoiceItems(R.array.select_server, getServerSetting(), new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int whichButton) {
					    	setServerSetting(whichButton);
					        /* User clicked on a radio button do some stuff */
					    }
					})
					.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int whichButton) {
					    	Log.i("","OK : "+selectServer);
					        /* User clicked Yes so do some stuff */
					    }
					})
					.create();
		return super.onCreateDialog(id);
	}	
		
	class LoginTask extends AsyncTask<String, Void, String> {
	
		String key=null;
		int responseCode = 0;
		@Override
	protected String doInBackground(String... params1) {
		JSONObject data = new JSONObject();
		JSONObject json = new JSONObject();
		
		try {
			data.put("email"	, m_pId);
			data.put("password", m_pPasswd);
			json.put("data", data);				
		} catch (JSONException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		HttpClient client = new DefaultHttpClient();
		final HttpParams params = client.getParams();
		
		HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);
		HttpConnectionParams.setSoTimeout(params, 30 * 1000);

		HttpPost request = new HttpPost();
		request.setHeader("Content-Type", "application/json");
		Log.i("",""+UserManager.SERVER_API_LOGIN_URL);
		try {
			request.setURI(new URI(UserManager.SERVER_API_LOGIN_URL));
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
			responseCode = response.getStatusLine().getStatusCode();
			if(responseCode == 406 || responseCode == 401 || responseCode == 404 || responseCode == 500)
			{
				key = Integer.toString(responseCode);			
				return key;
			}
			
			HttpEntity entity = response.getEntity();
			String jsonStr = EntityUtils.toString(entity);
			try {
				JSONObject resObj = new JSONObject(jsonStr);
				JSONObject resData = resObj.getJSONObject("data");
				Log.i("","resData : "+resData.toString());
				key = resData.getString("key");

				Log.i("","key : "+key);
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
		return key;	

	}



	@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
		showDialog(DIALOG_LOGIN);
			super.onPreExecute();
		}



	@Override
	protected void onPostExecute(String key) {
		removeDialog(DIALOG_LOGIN);

		if(key == null)
			Toast.makeText(LoginActivity.this, "네트워크 에러", Toast.LENGTH_SHORT).show();
		else if("401".equals(key))
			Toast.makeText(LoginActivity.this, R.string.incorrect_login, Toast.LENGTH_SHORT).show();
		else if("406".equals(key))
			Toast.makeText(LoginActivity.this, R.string.noti_relogin_auth_email, Toast.LENGTH_SHORT).show();
		else if("404".equals(key))
			Toast.makeText(LoginActivity.this, R.string.incorrect_login, Toast.LENGTH_SHORT).show();
		else if("500".equals(key))
			Toast.makeText(LoginActivity.this, "서버에러"+key, Toast.LENGTH_SHORT).show();
		else
		{
			UserManager.getInstance(LoginActivity.this).setAuthKey(key);
			Log.i("","*************KEY : "+key+"*********************");
			//Intent intent = new Intent(LoginActivity.this, AloohActivity.class);	
			finish();
			Intent intent = new Intent(LoginActivity.this, SetDeviceNameActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, 0);
		}
		
		super.onPostExecute(key);
	}
}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}
	
}

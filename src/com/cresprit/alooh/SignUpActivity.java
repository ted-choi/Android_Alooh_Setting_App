package com.cresprit.alooh;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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
import com.cresprit.alooh.manager.UserManager;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends Activity implements View.OnClickListener, OnCheckedChangeListener{
	private int DIALOG_CREATE_USER = 0;
	private Button btnAuthEmail;
	private TextView tvTerms;
	private EditText edtId;
	private EditText edtPasswd;
	private EditText edtConfirmPasswd;
	private String m_pId;
	private String m_pPasswd;
	private String m_pConfirmPasswd;
	
	private CheckBox cbTerms;
	private boolean isChecked=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_layout);
		
		btnAuthEmail = (Button)findViewById(R.id.auth_emailbtn);
		btnAuthEmail.setOnClickListener(this);
		
		edtId = (EditText)findViewById(R.id.id_edt);
		
		edtPasswd = (EditText)findViewById(R.id.passwd_edt);
		
		edtConfirmPasswd = (EditText)findViewById(R.id.confirm_passwd_edt);
		
		tvTerms = (TextView)findViewById(R.id.terms_tv);
		tvTerms.setOnClickListener(this);
				
		cbTerms = (CheckBox)findViewById(R.id.check_terms);
		cbTerms.setOnCheckedChangeListener(this);
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		
		switch(v.getId())
		{
		case R.id.auth_emailbtn:
			m_pId = edtId.getText().toString();
			m_pPasswd = edtPasswd.getText().toString();
			m_pConfirmPasswd = edtConfirmPasswd.getText().toString();
			
			if(m_pId.length() == 0)
			{
				Toast.makeText(this, R.string.insert_id, Toast.LENGTH_SHORT).show();
				break;
			}
			
			boolean result = Pattern.matches("[\\w\\~\\-\\.]+@[\\w\\~\\-]+(\\.[\\w\\~\\-]+)+",m_pId.trim());
			
			if(result == false)
			{
				Toast.makeText(this, R.string.invaild_email, Toast.LENGTH_SHORT).show();
				break;
			}
			
		
			if(m_pPasswd.length() == 0)
			{
				Toast.makeText(this, R.string.insert_passwd, Toast.LENGTH_SHORT).show();
				break;				
			}
			
			if(m_pPasswd.length() > 15 || m_pPasswd.length() < 6)
			{
				Toast.makeText(this, R.string.invaild_password, Toast.LENGTH_SHORT).show();
				break;				
			}	
			
			if(m_pConfirmPasswd.length() == 0)
			{
				Toast.makeText(this, R.string.insert_passwd, Toast.LENGTH_SHORT).show();
				break;				
			}

			if(!m_pPasswd.equals(m_pConfirmPasswd))
			{
				Toast.makeText(this, R.string.error_different_passwd_eachother, Toast.LENGTH_SHORT).show();
				break;
			}
			
			if(isChecked==false)		
			{
				Toast.makeText(SignUpActivity.this, R.string.request_agree, Toast.LENGTH_SHORT).show();
				break;
			}
			SendEmailTask asynctask = new SendEmailTask();
			asynctask.execute("");

			
			break;
		
		case R.id.terms_tv:
			intent = new Intent(SignUpActivity.this, WebActivity.class);
			startActivityForResult(intent, 0);			
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 0)
		{
			if(data != null)
			{
				boolean result = data.getBooleanExtra(WebActivity.JSSCRIPT_RESULT, false);
				if(result == true)
					cbTerms.setChecked(true);
			}
		}
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		//if(item.getItemId() == R.id.setting)
		{
			Intent intent = new Intent(SignUpActivity.this, SettingActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		
		if(id == DIALOG_CREATE_USER)
		{
			 ProgressDialog dialog = new ProgressDialog(this);
			 dialog.setTitle(R.string.regist_user);
			 dialog.setMessage(getResources().getString(R.string.noti_wait_moment));
			 dialog.setIndeterminate(true);
			 dialog.setCancelable(false);
			 return dialog;
		}
		
		return super.onCreateDialog(id);
	}	
	
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean _isChecked) {
		// TODO Auto-generated method stub
		isChecked = _isChecked;
		Log.i("", ""+_isChecked);
	}
	
	class SendEmailTask extends AsyncTask<String, Void, Integer> {
		String username=null;
		String email = null;
	@Override
	protected Integer doInBackground(String... params1) {
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
		
		HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
		HttpConnectionParams.setSoTimeout(params, 10 * 1000);

		HttpPost request = new HttpPost();
		request.setHeader("Content-Type", "application/json");

		try {
			request.setURI(new URI(UserManager.SERVER_API_CREATE_USER_URL));
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
			
			return response.getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;	

	}
	


	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		showDialog(DIALOG_CREATE_USER);
		super.onPreExecute();
	}



	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		
		removeDialog(DIALOG_CREATE_USER);
		
		if(result == 201)
		{
			Toast.makeText(SignUpActivity.this, m_pId+getResources().getString(R.string.noti_send_email), Toast.LENGTH_LONG).show();
			finish();
		}
		else if(result == 409)
			Toast.makeText(SignUpActivity.this, R.string.alreay_used_email, Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(SignUpActivity.this, R.string.error_fail_create_user, Toast.LENGTH_SHORT).show();
		super.onPostExecute(result);
	}


}
}

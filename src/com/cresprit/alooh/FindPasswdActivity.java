package com.cresprit.alooh;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

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






import com.cresprit.alooh.LoginActivity.LoginTask;
import com.cresprit.alooh.manager.UserManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FindPasswdActivity extends Activity implements View.OnClickListener{
	private int DIALOG_RESET_USER = 0;
	private int DIALOG_COMPLETE = 1;
	EditText edtId = null;
	Button btnReset = null;
	String m_pId = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.findpasswd_layout);
		edtId = (EditText)findViewById(R.id.id_edt);
		edtId.setText("");
		
		btnReset = (Button)findViewById(R.id.resetbtn);
		btnReset.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.resetbtn:
				m_pId = edtId.getText().toString();
				
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
				
				ResetUserTask task = new ResetUserTask();
				task.execute("");
				break;
		}
	}
	
	class ResetUserTask extends AsyncTask<String, Void, Integer> {
		
		String key=null;
		int responseCode = 0;
		@Override
	protected Integer doInBackground(String... params1) {
		JSONObject data = new JSONObject();
		JSONObject json = new JSONObject();
		
		try {
			data.put("email", m_pId);
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

		try {
			request.setURI(new URI(UserManager.SERVER_API_RESET_USER_URL));
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
		
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseCode;	
	}


	@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
		showDialog(DIALOG_RESET_USER);
			super.onPreExecute();
		}



	@Override
	protected void onPostExecute(Integer resCode) {
		removeDialog(DIALOG_RESET_USER);

		if(resCode == 401)
			Toast.makeText(FindPasswdActivity.this, R.string.error_different_passwd_eachother, Toast.LENGTH_SHORT).show();
		else if(resCode == 406)
			Toast.makeText(FindPasswdActivity.this, R.string.noti_relogin_auth_email, Toast.LENGTH_SHORT).show();
		else if(resCode == 404)
			Toast.makeText(FindPasswdActivity.this, R.string.noti_not_find_email, Toast.LENGTH_SHORT).show();
		else if(resCode == 500)
			Toast.makeText(FindPasswdActivity.this, R.string.error_server+key, Toast.LENGTH_SHORT).show();
		else if(resCode == 200)
			showDialog(DIALOG_COMPLETE);
		else
			Toast.makeText(FindPasswdActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
		
		super.onPostExecute(resCode);
	}
}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		if(id == DIALOG_RESET_USER)
		{
			 ProgressDialog dialog = new ProgressDialog(this);
			 dialog.setTitle(R.string.connecting);
			 dialog.setMessage(getResources().getString(R.string.noti_wait_moment));
			 dialog.setIndeterminate(true);
			 dialog.setCancelable(false);
			 return dialog;
		}
		else if(id == DIALOG_COMPLETE)
		{
			return new AlertDialog.Builder(FindPasswdActivity.this)
			.setTitle(R.string.noti)
			.setMessage(R.string.noti_sent_email)
			.setCancelable(false)
			.setPositiveButton(R.string.confirm,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							finish();
						}
					})
					.create();
		}
		
		return super.onCreateDialog(id);
	}
}


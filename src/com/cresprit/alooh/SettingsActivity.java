package com.cresprit.alooh;

import com.cresprit.alooh.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.LinearLayout;

public class SettingsActivity extends Activity implements OnClickListener{
	private LinearLayout layMyProfile;
	private LinearLayout laySwVersion;
	private LinearLayout layQuickGuide;
	
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		layMyProfile = (LinearLayout)findViewById(R.id.myprofile_lay);
		laySwVersion = (LinearLayout)findViewById(R.id.sw_version_lay);
		layQuickGuide = (LinearLayout)findViewById(R.id.quick_guide_lay);
		
		layMyProfile.setOnClickListener(this);
		laySwVersion.setOnClickListener(this);
		layQuickGuide.setOnClickListener(this);
		
		
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId())
		{
		case R.id.myprofile_lay:
			break;
			
		case R.id.sw_version_lay:
			break;
			
		case R.id.quick_guide_lay:
			Intent intent = new Intent(SettingsActivity.this, QuickGuideActivity.class);
			startActivity(intent);
			break;
		}
	}
	
	
}
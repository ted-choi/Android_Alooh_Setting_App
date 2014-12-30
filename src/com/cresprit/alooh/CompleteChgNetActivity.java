package com.cresprit.alooh;

import com.cresprit.alooh.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


public class CompleteChgNetActivity extends Activity implements View.OnClickListener{
	private static int DIALOG_CLOSE_APP = 0;
	ImageButton ibtnNext;
	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.complete_layout);
		
		ibtnNext = (ImageButton)findViewById(R.id.connect);
		ibtnNext.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.connect:
			Intent intent = new Intent(CompleteChgNetActivity.this, QuickGuideActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		showDialog(DIALOG_CLOSE_APP);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		if(id == DIALOG_CLOSE_APP)
		return new AlertDialog.Builder(CompleteChgNetActivity.this)
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
		return super.onCreateDialog(id);
	}
	
	
}
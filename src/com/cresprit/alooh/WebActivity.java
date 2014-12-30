package com.cresprit.alooh;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebActivity extends Activity{
	final static public String JSSCRIPT_RESULT = "com.cresprit.alooh.WebActivity.jsscript_result";
	WebView web;
	WebSettings setting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.webview);
		web = (WebView)findViewById(R.id.webview);
		
		web.loadUrl("http://api.alooh.io:50001/demo/#/terms");
		web.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
		
		web.setInitialScale(50);
		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setBuiltInZoomControls(true);
		web.getSettings().setDisplayZoomControls(false);
		web.getSettings().setSupportZoom(true);
		web.getSettings().setDefaultZoom(ZoomDensity.FAR);
		web.getSettings().setUseWideViewPort(true);
		web.requestFocus();			
		web.setWebViewClient(new WebViewClient() {
	         
	    });

		web.setWebChromeClient(new WebChromeClient() {
			
			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, JsResult result) {
				// TODO Auto-generated method stub
				
				return super.onJsConfirm(view, url, message, result);
			}

			@Override
			public boolean onJsAlert (WebView view, String url, String message, JsResult _result) {
				web.getSettings().setSupportZoom(false);
				  new AlertDialog.Builder(WebActivity.this)
		            .setMessage(message)
		            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							web.clearCache(true);
							web.destroy();
							Intent result = new Intent();
							result.putExtra(JSSCRIPT_RESULT, true);
							setResult(0, result);
							
							
							finish();
						}
	                }).show();
				
				return true;
			}
		});
	}
	
	
}
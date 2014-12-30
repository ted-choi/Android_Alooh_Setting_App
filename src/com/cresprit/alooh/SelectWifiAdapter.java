package com.cresprit.alooh;

import com.cresprit.alooh.R;
import com.cresprit.alooh.manager.SelectWifiManager;
import com.cresprit.alooh.manager.SelectDeviceManager.DeviceInfo;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



class SelectWifiAdapter extends BaseAdapter{

	private Context context;
	private SelectWifiManager deviceManager;
	
	public SelectWifiAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		deviceManager = SelectWifiManager.getInstance(context);
		
	}
	public static SelectWifiAdapter createSelectDeviceAdapter(Context context) {
		return new SelectWifiAdapter(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int count=0;
		count = deviceManager.getWifiListSorted().size();
		//notifyDataSetChanged();
		return count;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		
		if(deviceManager.getWifiListSorted().size() == 0)
			return null;
		else
			return deviceManager.getWifiListSorted().get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	public int getPowerPercentage(int power) {
	     int i = 0;
	     if (power <= -100) {
	            i = 0;
	     } else {
	            i = 100 + power;
	     }

	     return i;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View v = null;
		TextView deviceNameV;
		ImageView strengthIV;
		String value ="";

		
		String deviceName;
		String deviceId;
		
		DeviceHolder holder;

		if( convertView == null ) {
			v = LayoutInflater.from(context).inflate(R.layout.wifi_listview, null);
			holder = new DeviceHolder(v);
			v.setTag(holder);
		}
		else {
			v = convertView;
			holder = (DeviceHolder) v.getTag();
		}

		deviceName = deviceManager.getWifiListSorted().get(position).getDeviceID();
		
		deviceNameV = holder.getDeviceNameView();
		deviceNameV.setText(deviceName);		
		strengthIV  = holder.getStrengthView();
		int result1 = deviceManager.getWifiListSorted().get(position).getLevel();
		
		int percentage = getPowerPercentage(result1);
		BitmapDrawable bDrawable= null;
		if(percentage > 75)
		{
			bDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.wifi4); 
		}
		else if(percentage <= 75 && percentage > 50)
		{
			bDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.wifi3);
		}
		else if(percentage <= 50 && percentage > 25)
		{
			bDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.wifi2);
		}
		else
		{
			bDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.wifi1);
		}
		strengthIV.setImageDrawable(bDrawable);
		
		return v;
	}
	
	class DeviceHolder {
		View v;
		
		TextView name;
		ImageView strength;
		
		DeviceHolder(View v){
			this.v = v;
		}

		TextView getDeviceNameView(){
			if (name == null) {
				name = (TextView)v.findViewById(R.id.device_name);
			}
			return name;
		}
		
		ImageView getStrengthView(){
			if(strength == null) {
				strength = (ImageView)v.findViewById(R.id.wifi_strength);
			}
			return strength;
		}
	}
	
}
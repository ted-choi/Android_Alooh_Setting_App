package com.cresprit.alooh;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cresprit.alooh.R;
import com.cresprit.alooh.manager.SelectDeviceManager;
import com.cresprit.alooh.manager.SelectDeviceManager.DeviceInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



class SelectDeviceAdapter extends BaseAdapter{

	private Context context;
	private SelectDeviceManager deviceManager;
	
	public SelectDeviceAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		deviceManager = SelectDeviceManager.getInstance(context);
		
	}
	public static SelectDeviceAdapter createSelectDeviceAdapter(Context context) {
		return new SelectDeviceAdapter(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int count=0;
		count = deviceManager.getDeviceList().size();
		//notifyDataSetChanged();
		return count;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		
		if(deviceManager.getDeviceList().size() == 0)
			return null;
		else
			return deviceManager.getDeviceList().get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View v = null;
		TextView deviceNameV;
		TextView deviceIdV;
		String value ="";
		//JSONObject data;
		JSONObject data_streams;
		JSONArray data_points;
		JSONObject data;
		JSONArray jvalue;
		JSONObject jvalue2;
		
		String deviceName;
		String deviceId;
		
		DeviceHolder holder;

		if( convertView == null ) {
			v = LayoutInflater.from(context).inflate(R.layout.listview, null);
			holder = new DeviceHolder(v);
			v.setTag(holder);
		}
		else {
			v = convertView;
			holder = (DeviceHolder) v.getTag();
		}

		deviceName = deviceManager.getDeviceList().get(position).getDeviceName();
		deviceId = deviceManager.getDeviceList().get(position).getDeviceId();
		
		deviceNameV = holder.getDeviceNameView();
		deviceNameV.setText(deviceName);		
		deviceIdV  = holder.getDeviceIdView();
		deviceIdV.setText(deviceId);		
		return v;
	}
	
	class DeviceHolder {
		View v;
		
		TextView name;
		TextView id;
		
		DeviceHolder(View v){
			this.v = v;
		}

		TextView getDeviceNameView(){
			if (name == null) {
				name = (TextView)v.findViewById(R.id.device_name);
			}
			return name;
		}
		
		TextView getDeviceIdView(){
			if(id == null) {
				id = (TextView)v.findViewById(R.id.device_id);
			}
			return id;
		}
	}
	
}
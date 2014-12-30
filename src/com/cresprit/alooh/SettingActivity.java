package com.cresprit.alooh;


import com.cresprit.alooh.R;
import com.cresprit.alooh.SelectWifiAdapter.DeviceHolder;
import com.cresprit.alooh.manager.UserManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;


public class SettingActivity extends Activity{
	ListView list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//adapter = SelectWifiAdapter.createSelectDeviceAdapter(SelectWifiActivity.this);
		setContentView(R.layout.setting_layout);
		list = (ListView)findViewById(R.id.settingListv);
		
		
		list.setAdapter(new SettingAdapter(this));
		  
	}
	


	private class SettingAdapter extends BaseAdapter {
        public SettingAdapter(Context context) {
            mContext = context;
        }

        /**
         * The number of items in the list is determined by the number of speeches
         * in our array.
         * 
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
            return mTitles.length;
        }

        /**
         * Since the data comes from an array, just returning the index is
         * sufficent to get at the data. If we were using a more complex data
         * structure, we would return whatever object represents one row in the
         * list.
         * 
         * @see android.widget.ListAdapter#getItem(int)
         */
        public Object getItem(int position) {
            return position;
        }

        /**
         * Use the array index as a unique id.
         * 
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         * Make a SpeechView to hold each row.
         * 
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
        	ItemView holder;
        	View v;
    		if( convertView == null ) {
    			v = LayoutInflater.from(SettingActivity.this).inflate(R.layout.setting_listview, null);
    			holder = new ItemView(v);
    			v.setTag(holder);
    		}
    		else {
    			v = convertView;
    			holder = (ItemView) v.getTag();
    		}


    		switch(position)
    		{
    		case 0:
        		holder.getTitleView().setText(mTitles[position]);
    			holder.getContentView().setText(UserManager.getInstance(mContext).getUserId());
    			break;
    			
    		case 1:
        		holder.getTitleView().setText(mTitles[position]);
    			holder.getContentView().setText("ver 0.9");
    			break;
    		case 2:
    			holder.getContentView().setVisibility(View.GONE);
    			holder.getTitleView().setVisibility(View.GONE);
    			holder.getSingleTitleView().setVisibility(View.VISIBLE);
    			holder.getSingleTitleView().setText("Äü °¡ÀÌµå");
    			break;
    		}
            return v;
        }

        /**
         * Remember our context so we can use it when constructing views.
         */
        private Context mContext;
        
        /**
         * Our data, part 1.
         */
        private String[] mTitles = 
        {
                "My Profile",   
                "SW Version",
                "Äü °¡ÀÌµå",       
        };
        
        /**
         * Our data, part 2.
         */
    
        
         class ItemView {
    		View v;
    		
    		TextView title;
    		TextView content;
    		TextView titleOnly;
    		
    		ItemView(View v){
    			this.v = v;
    		}

    		TextView getTitleView(){
    			if (title == null) {
    				title = (TextView)v.findViewById(R.id.title);
    			}
    			return title;
    		}
    		
    		TextView getContentView(){
    			if(content == null) {
    				content = (TextView)v.findViewById(R.id.content);
    			}
    			return content;
    		}
    		
    		TextView getSingleTitleView(){
    			if (titleOnly == null) {
    				titleOnly = (TextView)v.findViewById(R.id.titleonly);
    			}
    			return titleOnly;
    		}
    	}
    }
}
package com.cresprit.alooh.manager;

import android.content.Context;
import android.util.Log;


public class UserManager{
	
	public static String SERVER_API_LOGIN_URL = null;
	public static String SERVER_API_CREATE_USER_URL = null;
	public static String SERVER_API_RESET_USER_URL = null;
	public static String SERVER_API_REGIST_DEVICE_NAME_CHECK_URL = null;
	public static String SERVER_API_REGIST_DEVICE_AUID_URL = null;
	public static String SERVER_API_GET_DEVICE_LIST_URL = null;
	public static String SERVER_API_GET_DEVICE_URL = null;
	public static String SERVER_API_UPDATE_DEVICE_URL = null;
	public static String SERVER_URI = null;
	
	public final static String PINO_API_LOGIN_URL = "http://pino.io:50001/api/v1/session";
	public final static String PINO_API_CREATE_USER_URL = "http://pino.io:50001/api/v1/user/new";
	public final static String PINO_API_RESET_USER_URL = "http://pino.io:50001/api/v1/user/reset";
	public final static String PINO_API_REGIST_DEVICE_NAME_CHECK_URL = "http://pino.io:50001/api/v1/devices/check";
	public final static String PINO_API_GET_DEVICE_LIST_URL = "http://pino.io:50001/api/v1/devices/name/list";
	public final static String PINO_API_GET_DEVICE_URL = "http://pino.io:50001/api/v1/devices/name";
	public final static String PINO_API_UPDATE_DEVICE_URL = "http://pino.io:50001/api/v1/devices/update";
	public final static String PINO_API_REGIST_DEVICE_AUID_URL = "http://pino.io:50001/api/v1/products/542271621846451c43bae192/device/new";
	public final static String PINO_URI = "http://pino.io:50001/demo";
	
	public final static String ALOOH_API_LOGIN_URL = "http://api.alooh.io:50001/api/v1/session";
	public final static String ALOOH_API_CREATE_USER_URL = "http://api.alooh.io:50001/api/v1/user/new";
	public final static String ALOOH_API_RESET_USER_URL = "http://api.alooh.io:50001/api/v1/user/reset";
	public final static String ALOOH_API_REGIST_DEVICE_NAME_CHECK_URL = "http://api.alooh.io:50001/api/v1/devices/check";
	public final static String ALOOH_API_GET_DEVICE_LIST_URL = "http://api.alooh.io:50001/api/v1/devices/name/list";
	public final static String ALOOH_API_GET_DEVICE_URL = "http://api.alooh.io:50001/api/v1/devices/name";	
	public final static String ALOOH_API_REGIST_DEVICE_AUID_URL = "http://api.alooh.io:50001/api/v1/products/542271621846451c43bae192/device/new";	
	public final static String ALOOH_URI = "http://api.alooh.io:50001/demo";
	public final static String ALOOH_API_UPDATE_DEVICE_URL = "http://api.alooh.io:50001/api/v1/devices/update";
	
	public final static int ALOOH_SERVER = 0;
	public final static int PINO_SERVER = 1;
	
	public final static int APP_MODE_REGIST_DEVICE = 0;
	public final static int APP_MODE_CHANGE_NETWORK = 1;
	
	private static String m_pId;
	private static String m_pAuthKey;
	private static String m_pAuid;
	private static String m_pDeviceName;
	private static int m_AppMode;
	
	private static UserManager __instance;
	
	String m_pPasswd;
	Context context;
	
	
	public static UserManager getInstance(Context appContext) {
		if (__instance == null) {
			__instance = new UserManager(appContext);
			
		}

		return __instance;
	}
	
	public UserManager(Context ctx)
	{
		this.context=ctx;
	}	
	
	public UserManager()
	{
		
	}
	
	public UserManager(String _id, String _passwd){
		m_pId = _id;
		this.m_pPasswd = _passwd;
	}
	
	public static String getUserID()
	{
		return m_pId;
	}
	
	public void setUserId(String _id){
		this.m_pId = _id;
	}
	
	public String getUserId(){
		return this.m_pId;
	}
	
	public void setAuthKey(String _key)
	{
		this.m_pAuthKey = _key;
	}
	
	public String getAuthKey()
	{
		return this.m_pAuthKey;
	}
	
	public void setAuid(String _auid)
	{
		this.m_pAuid = _auid;
	}
	
	public String getAuid()
	{
		return this.m_pAuid;
	}
	
	public void setDeviceName(String _deviceName)
	{
		this.m_pDeviceName = _deviceName;
	}
	
	public String getDeviceName()
	{
		return this.m_pDeviceName;
	}
	
	public static void ServerSetting(int select)
	{
		if(select == 0)
		{
			SERVER_API_LOGIN_URL = ALOOH_API_LOGIN_URL;
			SERVER_API_CREATE_USER_URL = ALOOH_API_CREATE_USER_URL;
			SERVER_API_RESET_USER_URL = ALOOH_API_RESET_USER_URL;
			SERVER_API_REGIST_DEVICE_NAME_CHECK_URL = ALOOH_API_REGIST_DEVICE_NAME_CHECK_URL;
			SERVER_API_REGIST_DEVICE_AUID_URL = ALOOH_API_REGIST_DEVICE_AUID_URL;
			SERVER_API_GET_DEVICE_LIST_URL = ALOOH_API_GET_DEVICE_LIST_URL;
			SERVER_API_GET_DEVICE_URL  = ALOOH_API_GET_DEVICE_URL;
			SERVER_API_UPDATE_DEVICE_URL = ALOOH_API_UPDATE_DEVICE_URL;
			SERVER_URI = ALOOH_URI;			
			Log.i("","Server : ALOOH !!!");
		}
		else
		{
			SERVER_API_LOGIN_URL = PINO_API_LOGIN_URL;
			SERVER_API_CREATE_USER_URL = PINO_API_CREATE_USER_URL;
			SERVER_API_RESET_USER_URL = PINO_API_RESET_USER_URL;
			SERVER_API_REGIST_DEVICE_NAME_CHECK_URL = PINO_API_REGIST_DEVICE_NAME_CHECK_URL;
			SERVER_API_REGIST_DEVICE_AUID_URL = PINO_API_REGIST_DEVICE_AUID_URL;
			SERVER_API_GET_DEVICE_LIST_URL = PINO_API_GET_DEVICE_LIST_URL;
			SERVER_API_GET_DEVICE_URL  = PINO_API_GET_DEVICE_URL;
			SERVER_API_UPDATE_DEVICE_URL = PINO_API_UPDATE_DEVICE_URL;
			SERVER_URI = PINO_URI;
			Log.i("","Server : PINO !!!");
		}
	}
	
	public static int GetAppMode()
	{
		return m_AppMode;
	}
	
	public static void SetAppMode(int _mode)
	{
		m_AppMode = _mode;
	}
}
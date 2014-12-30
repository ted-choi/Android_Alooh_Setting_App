package com.cresprit.alooh.server_api;

public interface IServerAPIListener{
	public Object todoInPostExcute(Object _obj);
	public Object todoInBackground(Object _obj);
	public Object todoInPreExcute(Object _obj);
	
	
}
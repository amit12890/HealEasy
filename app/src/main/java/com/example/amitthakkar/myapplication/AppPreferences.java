package com.example.amitthakkar.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;

public class AppPreferences {
	
	SharedPreferences preferences;
	Context context;
	final String App_PREFERENCE = "app_preference";
	final String SHOW_LOGIN_SCREEN = "showLoginScreen";
	final String COOKIE_NAME = "cookie_name";
	final String COOKIE_VALUE = "cookie_value";
	final String COOKIE_DOMAIN = "cookie_domain";	
	final String USERNAME = "username";
	final String PASSWORD = "password";
	final String IS_LOGGEDIN = "is_loggined";
	final String F_NAME ="first_name";
	final String L_NAME ="last_name";
	final String EMAIL ="email";
	final String LATITUDE = "latitude";
	final String LONGITUDE = "longitude";




	public AppPreferences(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		preferences = context.getSharedPreferences(App_PREFERENCE, Context.MODE_PRIVATE);

	}

	public String getCookieDomain()
	{
		return preferences.getString(COOKIE_DOMAIN, "");
	}
	
	public String getCookieName()
	{
		return preferences.getString(COOKIE_NAME, "");
	}
	
	public String getCookieValue()
	{
		return preferences.getString(COOKIE_VALUE, "");
	}
	
	public void setCookies(String cookieName, String cookieValue, String cookieDomain)
	{
		Editor editor = preferences.edit();		
		editor.putString(COOKIE_NAME, cookieName);
		editor.putString(COOKIE_VALUE, cookieValue);
		editor.putString(COOKIE_DOMAIN, cookieDomain);
		editor.commit();
	}
	
	public String getUserName()
	{
		return preferences.getString(USERNAME, "");
	}
	
	public void setUserName(String userName){
		Editor editor = preferences.edit();		
		editor.putString(USERNAME, userName);
		editor.commit();
	}

	public String getFirstName()
	{
		return preferences.getString(F_NAME, "");
	}

	public void setFirstName(String firstName){
		Editor editor = preferences.edit();
		editor.putString(F_NAME, firstName);
		editor.commit();
	}

	public String getLastName()
	{
		return preferences.getString(L_NAME, "");
	}

	public void setLastName(String lastName){
		Editor editor = preferences.edit();
		editor.putString(L_NAME, lastName);
		editor.commit();
	}

	public String getEmail()
	{
		return preferences.getString(EMAIL, "");
	}

	public void setEmail(String email){
		Editor editor = preferences.edit();
		editor.putString(EMAIL, email);
		editor.commit();
	}
	
	public String getPassword()
	{
		return preferences.getString(PASSWORD, "");
	}
	
	public void setPassword(String password){
		Editor editor = preferences.edit();		
		editor.putString(PASSWORD, password);
		editor.commit();
	}

	public String getIsLoggedIn()
	{
		return preferences.getString(IS_LOGGEDIN, "0");
	}

	public void setIsLoggedIn(String is_loggined){
		Editor editor = preferences.edit();
		editor.putString(IS_LOGGEDIN, is_loggined);
		editor.commit();
	}

	public String getLatitude()
	{
		return preferences.getString(LATITUDE, "22.6919");
	}

	public void setLatitude(String latitude){
		Editor editor = preferences.edit();
		editor.putString(LATITUDE, latitude);
		editor.commit();
	}

	public String getLongitude()
	{
		return preferences.getString(LONGITUDE, "72.8632");
	}

	public void setLongitude(String latitude){
		Editor editor = preferences.edit();
		editor.putString(LONGITUDE, latitude);
		editor.commit();
	}

}

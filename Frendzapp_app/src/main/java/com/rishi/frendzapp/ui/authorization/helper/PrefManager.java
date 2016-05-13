package com.rishi.frendzapp.ui.authorization.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class PrefManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "FrendzApp";

    // All Shared Preferences Keys
    private static final String KEY_IS_WAITING_FOR_SMS = "IsWaitingForSms";
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_NEW_USER = "isLoggedIn";
    private static final String KEY_STATUS = "status";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_ISREMEMBER = "isRemember";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIsWaitingForSms(boolean isWaiting) {
        editor.putBoolean(KEY_IS_WAITING_FOR_SMS, isWaiting);
        editor.commit();
    }

    public boolean isWaitingForSms() {
        return pref.getBoolean(KEY_IS_WAITING_FOR_SMS, false);
    }

    public void setMobileNumber(String mobileNumber) {
        editor.putString(KEY_MOBILE_NUMBER, mobileNumber);
        editor.commit();
    }

    public String getMobileNumber() {
        return pref.getString(KEY_MOBILE_NUMBER, null);
    }

    public  void setIsremember(boolean isremember){
        editor.putBoolean(KEY_ISREMEMBER, isremember);
        editor.commit();
    }

    public boolean getIsremember(){
        return pref.getBoolean(KEY_ISREMEMBER, false);
    }

    public void createLogin(String mobile ,String password, String status,String user_status) {
        editor.putString(KEY_STATUS, status);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_NEW_USER, user_status);
        editor.commit();
    }

    public int isNewUser() {
        return pref.getInt(KEY_NEW_USER, 0);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("status", pref.getString(KEY_STATUS, null));
        profile.put("password", pref.getString(KEY_PASSWORD, null));
        profile.put("mobile", pref.getString(KEY_MOBILE, null));
        return profile;
    }
}

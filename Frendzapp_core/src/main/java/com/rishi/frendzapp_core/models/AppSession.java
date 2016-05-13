package com.rishi.frendzapp_core.models;

import android.text.TextUtils;

import com.quickblox.auth.QBAuth;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.server.BaseService;
import com.rishi.frendzapp_core.utils.ConstsCore;
import com.rishi.frendzapp_core.utils.PrefsHelper;
import com.quickblox.users.model.QBUser;
import com.rishi.frendzapp_core.utils.ErrorUtils;

import java.io.Serializable;
import java.util.Date;

public class AppSession implements Serializable {

    private static final Object lock = new Object();
    private static AppSession activeSession;
    private final LoginType loginType;
    private QBUser user;
    private String sessionToken;
    private long sessionExpirationDate;

    private AppSession(LoginType loginType, QBUser user, String sessionToken,long sessionExpirationDate) {
        this.loginType = loginType;
        this.user = user;
        this.sessionToken = sessionToken;
        this.sessionExpirationDate = sessionExpirationDate;
        save();
    }

    public static void startSession(LoginType loginType, QBUser user, String sessionToken,long sessionExpirationDate) {
        activeSession = new AppSession(loginType, user, sessionToken,sessionExpirationDate);
    }

    private static AppSession getActiveSession() {
        synchronized (lock) {
            return activeSession;
        }
    }

    public static AppSession load() {
        PrefsHelper helper = PrefsHelper.getPrefsHelper();
        String loginTypeRaw = helper.getPref(PrefsHelper.PREF_LOGIN_TYPE, LoginType.LOGIN.toString());
        int userId = helper.getPref(PrefsHelper.PREF_USER_ID, ConstsCore.NOT_INITIALIZED_VALUE);
        String userFullName = helper.getPref(PrefsHelper.PREF_USER_FULL_NAME, ConstsCore.EMPTY_STRING);
        String sessionToken = helper.getPref(PrefsHelper.PREF_SESSION_TOKEN, ConstsCore.EMPTY_STRING);
        long sessionexpirationTime=0;
        try {
            sessionexpirationTime = BaseService.getBaseService().getTokenExpirationDate().getTime();
        }catch (BaseServiceException e){
            ErrorUtils.logError(e);
        }
        QBUser qbUser = new QBUser();
        qbUser.setId(userId);
        qbUser.setFullName(userFullName);
        LoginType loginType = LoginType.valueOf(loginTypeRaw);
        return new AppSession(loginType, qbUser, sessionToken,sessionexpirationTime);
    }

    public static void saveRememberMe(boolean value) {
        PrefsHelper.getPrefsHelper().savePref(PrefsHelper.PREF_REMEMBER_ME, value);
    }


//    public static boolean isSessionExistOrNotExpired(long expirationTime) {
//        try {
//           // BaseService baseService = QBAuth.getBaseService();
//           // String token = baseService.getToken();
//          //  System.out.println("ExpireDate:" + baseService.getTokenExpirationDate());
//           // BaseService.createFromExistentToken();
//            String token = PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN);
//            System.out.println("expirationDate:"+PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_EXPIRE_DATE));
//            if (token == null) {
//                System.out.println("Tokan is null");
//                return false;
//            }
//
//            //Date tokenExpirationDate = baseService.getTokenExpirationDate();
//          //  long tokenExpirationDate = PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_EXPIRE_DATE);
//           // long tokenLiveOffset = tokenExpirationDate.getTime() - System.currentTimeMillis();
//            long tokenLiveOffset = tokenExpirationDate - System.currentTimeMillis();
//            System.out.println("tokenLiveOffset:"+tokenLiveOffset);
//            //return tokenLiveOffset > expirationTime;
//            if (tokenLiveOffset > expirationTime) {
//                BaseService.createFromExistentToken(token, new Date(tokenExpirationDate));
//                return true;
//            }
//        } catch (Exception e) {
//            ErrorUtils.logError(e);
//        }
//        return false;
//    }

    public static boolean isSessionExistOrNotExpired(long expirationTime) {
        try {
            BaseService baseService = QBAuth.getBaseService();
            String token = baseService.getToken();
            if (token == null) {
                return false;
            }
            Date tokenExpirationDate = baseService.getTokenExpirationDate();
            long tokenLiveOffset = tokenExpirationDate.getTime() - System.currentTimeMillis();
            return tokenLiveOffset > expirationTime;
        } catch (BaseServiceException e) {
            ErrorUtils.logError(e);
        }
        return false;
    }

    public static AppSession getSession() {
        AppSession activeSession = AppSession.getActiveSession();
        if (activeSession == null) {
            activeSession = AppSession.load();
        }
        return activeSession;
    }

    public void closeAndClear() {
        PrefsHelper helper = PrefsHelper.getPrefsHelper();
        helper.delete(PrefsHelper.PREF_USER_EMAIL);
        helper.delete(PrefsHelper.PREF_LOGIN_TYPE);
        helper.delete(PrefsHelper.PREF_SESSION_TOKEN);
        helper.delete(PrefsHelper.PREF_SESSION_EXPIRE_DATE);
        helper.delete(PrefsHelper.PREF_USER_ID);
        activeSession = null;
    }

    public QBUser getUser() {
        return user;
    }

    public void save() {
        PrefsHelper prefsHelper = PrefsHelper.getPrefsHelper();
        prefsHelper.savePref(PrefsHelper.PREF_LOGIN_TYPE, loginType.toString());
        prefsHelper.savePref(PrefsHelper.PREF_SESSION_TOKEN, sessionToken);
        prefsHelper.savePref(PrefsHelper.PREF_SESSION_EXPIRE_DATE, sessionExpirationDate);
        saveUser(user, prefsHelper);
    }

    public void updateUser(QBUser user) {
        this.user = user;
        saveUser(this.user, PrefsHelper.getPrefsHelper());
    }

    private void saveUser(QBUser user, PrefsHelper prefsHelper) {
        prefsHelper.savePref(PrefsHelper.PREF_USER_ID, user.getId());
        prefsHelper.savePref(PrefsHelper.PREF_USER_EMAIL, user.getLogin());
        prefsHelper.savePref(PrefsHelper.PREF_USER_FULL_NAME, user.getFullName());
        prefsHelper.savePref(PrefsHelper.PREF_USER_PASSWORD, user.getPassword());
    }

    public boolean isSessionExist() {
        return loginType != null && !TextUtils.isEmpty(sessionToken);
    }

    public LoginType getLoginType() {
        return loginType;
    }
}
package com.rishi.frendzapp_core.qb.helpers;

import android.content.Context;
import android.text.TextUtils;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.rishi.frendzapp_core.models.AppSession;
import com.rishi.frendzapp_core.models.LoginType;
import com.rishi.frendzapp_core.models.UserCustomData;
import com.rishi.frendzapp_core.utils.Utils;

import java.io.File;

public class QBAuthHelper extends BaseHelper {

    private static final String TAG_ANDROID = "android";

    public QBAuthHelper(Context context) {
        super(context);
    }

    public QBUser login(QBUser inputUser) throws QBResponseException, BaseServiceException {
        QBUser user;
        QBAuth.createSession();
        String password = inputUser.getPassword();
        user = QBUsers.signIn(inputUser);

        // TODO: temp block
        if (!isUpdatedUserCustomData(user)) {
            user.setOldPassword(password);
            updateUser(user);
        }
        // end todo

        String token = QBAuth.getBaseService().getToken();
        long expirationTime = QBAuth.getBaseService().getTokenExpirationDate().getTime();
        user.setPassword(password);
        AppSession.startSession(LoginType.LOGIN, user, token, expirationTime);
        return user;
    }

    public QBUser login(String socialProvider, String accessToken,
                        String accessTokenSecret) throws QBResponseException, BaseServiceException {
        QBUser user;
        QBSession session = QBAuth.createSession();
        user = QBUsers.signInUsingSocialProvider(socialProvider, accessToken, accessTokenSecret);
        user.setPassword(session.getToken());

        // TODO: temp block
        if (!isUpdatedUserCustomData(user)) {
            user.setOldPassword(session.getToken());
            updateUser(user);
        }
        // end todo

        user.setPassword(session.getToken());
        String token = QBAuth.getBaseService().getToken();
        long expirationTime = QBAuth.getBaseService().getTokenExpirationDate().getTime();
        AppSession.startSession(LoginType.FACEBOOK, user, token,expirationTime);
        return user;
    }

    public QBUser signup(QBUser inputUser, File file) throws QBResponseException, BaseServiceException {
        QBUser user;
        UserCustomData userCustomData = new UserCustomData();

        QBAuth.createSession();
        String password = inputUser.getPassword();
        inputUser.setOldPassword(password);
        inputUser.setCustomData(Utils.customDataToString(userCustomData));

        StringifyArrayList<String> stringifyArrayList = new StringifyArrayList<String>();
        stringifyArrayList.add(TAG_ANDROID);
        inputUser.setTags(stringifyArrayList);

        user = QBUsers.signUpSignInTask(inputUser);

        if (file != null) {
            QBFile qbFile = QBContent.uploadFileTask(file, true, (String) null);
            userCustomData.setAvatar_url(qbFile.getPublicUrl());
            inputUser.setCustomData(Utils.customDataToString(userCustomData));
            user = QBUsers.updateUser(inputUser);
        }

        user.setCustomDataClass(UserCustomData.class);
        user.setPassword(password);
        String token = QBAuth.getBaseService().getToken();
        long expirationTime = QBAuth.getBaseService().getTokenExpirationDate().getTime();
        AppSession.startSession(LoginType.LOGIN, user, token,expirationTime);
        return user;
    }

    public void logout() throws QBResponseException {
        AppSession activeSession = AppSession.getSession();
        if (activeSession != null) {
            activeSession.closeAndClear();
        }
       // Session.getActiveSession().closeAndClearTokenInformation();
        QBAuth.deleteSession();
    }

    public QBUser updateUser(QBUser inputUser) throws QBResponseException {
        QBUser user;

        String password = inputUser.getPassword();
        String oldPassword = inputUser.getOldPassword();

        UserCustomData userCustomDataNew = getUserCustomData(inputUser);
        inputUser.setCustomData(Utils.customDataToString(userCustomDataNew));

        if (LoginType.LOGIN.equals(AppSession.getSession().getLoginType())) {
            inputUser.setPassword(password);
            inputUser.setOldPassword(oldPassword);
        } else if (LoginType.FACEBOOK.equals(AppSession.getSession().getLoginType())){
            inputUser.setPassword(null);
            inputUser.setOldPassword(null);
        }

        user = QBUsers.updateUser(inputUser);

        if (LoginType.LOGIN.equals(AppSession.getSession().getLoginType())) {
            user.setPassword(password);
        }

        return user;
    }


    public QBUser updateUser(QBUser user, File file) throws QBResponseException {
        QBUser newUser = new QBUser();

        QBFile qbFile = QBContent.uploadFileTask(file, true, (String) null);
        newUser.setId(user.getId());
        newUser.setWebsite(qbFile.getPublicUrl());
        newUser.setFileId(qbFile.getId());

        UserCustomData userCustomData = getUserCustomData(user);
        userCustomData.setAvatar_url(qbFile.getPublicUrl());
        newUser.setCustomData(Utils.customDataToString(userCustomData));

        return updateUser(newUser);
    }

    public String upLoadAudioStatus(File file) throws QBResponseException {

        QBFile qbFile = QBContent.uploadFileTask(file, true, (String) null);

        return qbFile.getPublicUrl();
    }

    public QBFile uploadVideo(File file) throws QBResponseException {

        QBFile qbFile = QBContent.uploadFileTask(file, true, (String) null);

        return qbFile;
    }

    public QBFile uploadAudio(File file) throws QBResponseException {

        QBFile qbFile = QBContent.uploadFileTask(file, true, (String) null);

        return qbFile;
    }

    // TODO: temp method
    private UserCustomData getUserCustomData(QBUser user) {
        if (TextUtils.isEmpty(user.getCustomData())) {
            UserCustomData userCustomData = new UserCustomData();
            userCustomData.setAvatar_url(user.getWebsite());
            return userCustomData;
        }

        UserCustomData userCustomDataNew = null;
        UserCustomData userCustomDataOld = null;

        userCustomDataOld = Utils.customDataToObject(user.getCustomData());

        if (userCustomDataOld != null) {
            userCustomDataNew = userCustomDataOld;
        } else {
            userCustomDataNew = new UserCustomData();
        }

        if (!TextUtils.isEmpty(user.getWebsite())) {
            userCustomDataNew.setAvatar_url(user.getWebsite());
        }

        return userCustomDataNew;
    }


    // TODO: temp method
    private boolean isUpdatedUserCustomData(QBUser user) {
        if (TextUtils.isEmpty(user.getCustomData())) {
            return false;
        }

        UserCustomData userCustomDataOld = Utils.customDataToObject(user.getCustomData());

        if (userCustomDataOld != null) {
            return true;
        } else {
            return false;
        }
    }

    public void resetPassword(String email) throws QBResponseException {
        QBAuth.createSession();
        QBUsers.resetPassword(email);
    }
}
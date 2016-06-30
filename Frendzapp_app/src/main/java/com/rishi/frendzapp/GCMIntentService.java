package com.rishi.frendzapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.rishi.frendzapp.ui.authorization.app.MyApplication;
import com.rishi.frendzapp_core.utils.ConstsCore;
import com.rishi.frendzapp_core.utils.PrefsHelper;
import com.quickblox.users.model.QBUser;
import com.rishi.frendzapp_core.core.gcm.NotificationHelper;
import com.rishi.frendzapp_core.models.PushMessage;
import com.rishi.frendzapp.ui.splash.SplashActivity;

public class GCMIntentService extends IntentService {

    public final static int NOTIFICATION_ID = 1;
    public final static long VIBRATOR_DURATION = 1500;

    private NotificationManager notificationManager;
    private String message;
    private String dialogId;
    private int userId;
    private PrefsHelper helper;

    public GCMIntentService() {
        super("GcmIntentService");
    }

    String TAG = GCMIntentService.class.getSimpleName();

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                parseMessage(extras);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void parseMessage(Bundle extras) {
        if (extras.getString(NotificationHelper.MESSAGE) != null) {
            message = extras.getString(NotificationHelper.MESSAGE);
        }

        if (extras.getString(NotificationHelper.USER_ID) != null) {
            userId = Integer.parseInt(extras.getString(NotificationHelper.USER_ID));
        }

        if (extras.getString(NotificationHelper.DIALOG_ID) != null) {
            dialogId = extras.getString(NotificationHelper.DIALOG_ID);
        }

        sendNotification();
    }

    private void sendNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, SplashActivity.class);

        saveOpeningDialogData(userId, dialogId);
        PendingIntent contentIntent = PendingIntent.getActivity(this, ConstsCore.ZERO_INT_VALUE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(
                R.drawable.mainlogo).setContentTitle(getString(R.string.push_title)).setStyle(
                new NotificationCompat.BigTextStyle().bigText(message)).setContentText(message);

        builder.setAutoCancel(true);
        builder.setContentIntent(contentIntent);
        builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        playNotificationSound();
    }

    private void saveOpeningDialogData(int userId, String dialogId) {
        PrefsHelper prefsHelper = PrefsHelper.getPrefsHelper();
        if (userId != ConstsCore.ZERO_INT_VALUE && !TextUtils.isEmpty(dialogId)) {
            prefsHelper.savePref(PrefsHelper.PREF_PUSH_MESSAGE_USER_ID, userId);
            prefsHelper.savePref(PrefsHelper.PREF_PUSH_MESSAGE_DIALOG_ID, dialogId);

            prefsHelper.savePref(PrefsHelper.PREF_PUSH_MESSAGE_NEED_TO_OPEN_DIALOG, true);
        } else {
            prefsHelper.savePref(PrefsHelper.PREF_PUSH_MESSAGE_NEED_TO_OPEN_DIALOG, false);
        }
    }
    // Playing notification sound
    public void playNotificationSound() {
        try {
            helper = new PrefsHelper(getApplicationContext());
            String sound = helper.getPref(PrefsHelper.PREF_NOTIFICATION_NAME).toString();
            System.out.println("sound:"+sound);
            sound.toLowerCase();
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + App.getInstance().getApplicationContext().getPackageName() + "/raw/"+sound.toLowerCase());
            System.out.println("uri:"+alarmSound);
            Ringtone r = RingtoneManager.getRingtone(App.getInstance().getApplicationContext(), alarmSound);
            r.play();
            Boolean con = helper.getPref(PrefsHelper.PREF_NOTIFICATION_VIBRATE);
            if (con){
                Vibrator vibs = (Vibrator) App.getInstance().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibs.vibrate(1000);
                Log.d(TAG,"Vibrate in if:"+helper.getPref(PrefsHelper.PREF_NOTIFICATION_VIBRATE));
            }else {
            Log.d(TAG,"Vibrate in else:"+helper.getPref(PrefsHelper.PREF_NOTIFICATION_VIBRATE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendBroadcast(PushMessage message) {
        Intent intent = new Intent();
        intent.setAction(NotificationHelper.ACTION_VIDEO_CALL);
        QBUser qbUser = new QBUser();
        qbUser.setId(message.getUserId());
        intent.putExtra(ConstsCore.USER, qbUser);
        sendBroadcast(intent);
    }
}
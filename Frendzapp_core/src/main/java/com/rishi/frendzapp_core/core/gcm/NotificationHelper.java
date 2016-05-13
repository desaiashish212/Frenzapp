package com.rishi.frendzapp_core.core.gcm;

import android.util.Log;

import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;

import java.util.List;

public class NotificationHelper {

    public static final String ACTION_VIDEO_CALL = "com.rishi.frendzapp.VIDEO_CALL";
    public static final String ACTION_AUDIO_CALL = "com.rishi.frendzapp.AUDIO_CALL";
    public static final String MESSAGE = "message";
    public static final String DIALOG_ID = "dialog_id";
    public static final String USER_ID = "user_id";

    public static final String CALL_TYPE = "call";

    public static QBEvent createPushEvent(List<Integer> userIdsList, String message, String messageType) {
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        userIds.addAll(userIdsList);
        QBEvent event = new QBEvent();
        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.PRODUCTION);
        event.setNotificationType(QBNotificationType.PUSH);
        event.setMessage(message);
        return event;
    }
}
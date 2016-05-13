package com.rishi.frendzapp_core.qb.commands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.quickblox.core.exception.QBResponseException;
import com.rishi.frendzapp_core.models.AppSession;
import com.quickblox.users.model.QBUser;
import com.rishi.frendzapp_core.core.command.ServiceCommand;
import com.rishi.frendzapp_core.qb.helpers.QBMultiChatHelper;
import com.rishi.frendzapp_core.qb.helpers.QBPrivateChatHelper;
import com.rishi.frendzapp_core.service.QBService;
import com.rishi.frendzapp_core.service.QBServiceConsts;

public class QBInitChatsCommand extends ServiceCommand {

    private QBPrivateChatHelper privateChatHelper;
    private QBMultiChatHelper multiChatHelper;

    public QBInitChatsCommand(Context context, QBPrivateChatHelper privateChatHelper,
            QBMultiChatHelper multiChatHelper, String successAction, String failAction) {
        super(context, successAction, failAction);
        this.privateChatHelper = privateChatHelper;
        this.multiChatHelper = multiChatHelper;
    }

    public static void start(Context context) {
        Intent intent = new Intent(QBServiceConsts.INIT_CHATS_ACTION, null, context, QBService.class);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws QBResponseException {
        QBUser user;

        if (extras == null) {
            user = AppSession.getSession().getUser();
        } else {
            user = (QBUser) extras.getSerializable(QBServiceConsts.EXTRA_USER);
        }

        privateChatHelper.init(user);
        multiChatHelper.init(user);

        return extras;
    }
}
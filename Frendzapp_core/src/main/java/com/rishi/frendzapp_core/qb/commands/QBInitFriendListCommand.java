package com.rishi.frendzapp_core.qb.commands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.quickblox.core.exception.QBResponseException;
import com.rishi.frendzapp_core.core.command.ServiceCommand;
import com.rishi.frendzapp_core.qb.helpers.QBFriendListHelper;
import com.rishi.frendzapp_core.qb.helpers.QBPrivateChatHelper;
import com.rishi.frendzapp_core.service.QBService;
import com.rishi.frendzapp_core.service.QBServiceConsts;

public class QBInitFriendListCommand extends ServiceCommand {

    private QBFriendListHelper friendListHelper;
    private QBPrivateChatHelper privateChatHelper;

    public QBInitFriendListCommand(Context context, QBFriendListHelper friendListHelper, QBPrivateChatHelper privateChatHelper, String successAction,
            String failAction) {
        super(context, successAction, failAction);
        this.friendListHelper = friendListHelper;
        this.privateChatHelper = privateChatHelper;
    }

    public static void start(Context context) {
        Intent intent = new Intent(QBServiceConsts.INIT_FRIEND_LIST_ACTION, null, context, QBService.class);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws QBResponseException {
        friendListHelper.init(privateChatHelper);
        return extras;
    }
}
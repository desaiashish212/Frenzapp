package com.rishi.frendzapp_core.qb.commands;

import android.content.Context;
import android.content.Intent;

import com.rishi.frendzapp_core.core.command.CompositeServiceCommand;
import com.rishi.frendzapp_core.service.QBService;
import com.rishi.frendzapp_core.service.QBServiceConsts;

public class QBLoginAndJoinDialogsCommand extends CompositeServiceCommand {

    public QBLoginAndJoinDialogsCommand(Context context, String successAction, String failAction) {
        super(context, successAction, failAction);
    }

    public static void start(Context context) {
        Intent intent = new Intent(QBServiceConsts.LOGIN_AND_JOIN_CHAT_ACTION, null, context, QBService.class);
        context.startService(intent);
    }
}

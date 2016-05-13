package com.rishi.frendzapp_core.qb.commands;

import android.content.Context;
import android.content.Intent;

import com.quickblox.users.model.QBUser;
import com.rishi.frendzapp_core.core.command.CompositeServiceCommand;
import com.rishi.frendzapp_core.service.QBService;
import com.rishi.frendzapp_core.service.QBServiceConsts;

public class QBLoginWithSocialCommand extends CompositeServiceCommand {

    public QBLoginWithSocialCommand(Context context, String successAction, String failAction) {
        super(context, successAction, failAction);
    }

    public static void start(Context context, QBUser user) {
        Intent intent = new Intent(QBServiceConsts.LOGIN_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_USER, user);
        context.startService(intent);
    }
}
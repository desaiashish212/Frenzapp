package com.rishi.frendzapp.ui.chats.dialogs;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;

import com.quickblox.chat.model.QBDialog;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.chats.BaseSelectableFriendListActivity;
import com.rishi.frendzapp.ui.chats.GroupDialogActivity;
import com.rishi.frendzapp_core.core.command.Command;
import com.rishi.frendzapp_core.db.managers.UsersDatabaseManager;
import com.rishi.frendzapp_core.models.AppSession;
import com.rishi.frendzapp_core.models.User;
import com.rishi.frendzapp_core.qb.commands.QBCreateGroupDialogCommand;
import com.rishi.frendzapp_core.qb.helpers.QBMultiChatHelper;
import com.rishi.frendzapp_core.service.QBService;
import com.rishi.frendzapp_core.service.QBServiceConsts;
import com.rishi.frendzapp_core.utils.ErrorUtils;

import java.util.ArrayList;

public class NewDialogActivity extends BaseSelectableFriendListActivity implements NewDialogCounterFriendsListener {

    private QBMultiChatHelper multiChatHelper;

    public static void start(Context context) {
        Intent intent = new Intent(context, NewDialogActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addActions();
    }

    @Override
    public void onConnectedToService(QBService service) {
        if (multiChatHelper == null) {
            multiChatHelper = (QBMultiChatHelper) service.getHelper(QBService.MULTI_CHAT_HELPER);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActions();
    }

    @Override
    protected Cursor getFriends() {
        return UsersDatabaseManager.getAllFriends(this);
    }

    @Override
    protected void onFriendsSelected(ArrayList<User> selectedFriends) {
        createChat(selectedFriends);
    }

    protected void removeActions() {
        removeAction(QBServiceConsts.CREATE_GROUP_CHAT_SUCCESS_ACTION);
        removeAction(QBServiceConsts.CREATE_GROUP_CHAT_FAIL_ACTION);
    }

    protected void addActions() {
        addAction(QBServiceConsts.CREATE_GROUP_CHAT_SUCCESS_ACTION, new CreateChatSuccessAction());
        addAction(QBServiceConsts.CREATE_GROUP_CHAT_FAIL_ACTION, failAction);
        updateBroadcastActionList();
    }

    private void createChat(ArrayList<User> friendList) {
        showProgress();
        System.out.println("friendList"+friendList.toString());
        String groupName = createChatName(friendList);
        QBCreateGroupDialogCommand.start(this, groupName, friendList);
    }

    private String createChatName(ArrayList<User> friendList) {
        String userFullname = AppSession.getSession().getUser().getFullName();
        String friendsFullnames = TextUtils.join(", ", friendList);
        return userFullname + ", " + friendsFullnames;
    }

//    private void sendNotificationToGroup(QBDialog dialog) {
//        try {
//            multiChatHelper.sendNotificationToFriends(dialog, MessagesNotificationType.CREATE_DIALOG,
//                    ChatUtils.getOccupantIdsWithoutUser(dialog.getOccupants()));
//        } catch (QBResponseException e) {
//            ErrorUtils.logError(e);
//        }
//    }

    private class CreateChatSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            hideProgress();
            QBDialog dialog = (QBDialog) bundle.getSerializable(QBServiceConsts.EXTRA_DIALOG);
            if (dialog.getRoomJid() != null) {
                GroupDialogActivity.start(NewDialogActivity.this, dialog,null);
//                sendNotificationToGroup(dialog);
                finish();
            } else {
                ErrorUtils.showError(NewDialogActivity.this, getString(R.string.dlg_fail_create_groupchat));
            }
        }
    }
}
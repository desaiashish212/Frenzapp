package com.rishi.frendzapp.ui.importfriends;

import android.app.Activity;
import com.rishi.frendzapp_core.models.InviteFriend;
import com.rishi.frendzapp_core.qb.commands.QBImportFriendsCommand;
import com.rishi.frendzapp_core.utils.ConstsCore;
import com.rishi.frendzapp.utils.EmailUtils;

import java.util.ArrayList;
import java.util.List;

public class ImportFriends {

    public Activity activity;
    private List<InviteFriend> friendsFacebookList;
    private List<InviteFriend> friendsContactsList;
    private int expectedFriendsCallbacks;
    private int realFriendsCallbacks;

    public ImportFriends(Activity activity) {
        this.activity = activity;
       // friendsFacebookList = new ArrayList<InviteFriend>();
        friendsContactsList = new ArrayList<InviteFriend>();
    }

    public void startGetFriendsListTask(boolean isGetFacebookFriends) {
        expectedFriendsCallbacks++;
        friendsContactsList = EmailUtils.getContactsWithEmail(activity);
        if (isGetFacebookFriends) {
            expectedFriendsCallbacks++;
            //getFacebookFriendsList();

        }
        fiendsReceived();
    }

    private List<String> getIdsList(List<InviteFriend> friendsList) {
        if (friendsList.isEmpty()) {
            return new ArrayList<String>();
        }
        List<String> idsList = new ArrayList<String>();
        for (InviteFriend friend : friendsList) {
            idsList.add(friend.getId());
        }
        return idsList;
    }

    public void fiendsReceived() {
        realFriendsCallbacks++;
        if (realFriendsCallbacks == expectedFriendsCallbacks) {
            realFriendsCallbacks = ConstsCore.ZERO_INT_VALUE;
            QBImportFriendsCommand.start(activity, getIdsList(friendsContactsList));
        }
        System.out.println("FriendsReceived:"+friendsContactsList);
    }

  /*  private void getFacebookFriendsList() {
        Request.newMyFriendsRequest(Session.getActiveSession(), new Request.GraphUserListCallback() {

            @Override
            public void onCompleted(List<GraphUser> users, Response response) {
                for (com.facebook.model.GraphUser user : users) {
                    friendsFacebookList.add(new InviteFriend(user.getId(), user.getName(), user.getLink(),
                            InviteFriend.VIA_FACEBOOK_TYPE, null, false));
                }
                fiendsReceived();
            }
        }).executeAsync();
    }*/
}
package com.rishi.frendzapp.call;

import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseFragment;
import com.rishi.frendzapp.ui.friends.FriendDetails;
import com.rishi.frendzapp.ui.friends.FriendOperationListener;
import com.rishi.frendzapp.ui.mediacall.CallActivity;
import com.rishi.frendzapp.utils.KeyboardUtils;
import com.rishi.frendzapp_core.core.command.Command;
import com.rishi.frendzapp_core.db.managers.UsersDatabaseManager;
import com.rishi.frendzapp_core.db.tables.FriendTable;
import com.rishi.frendzapp_core.db.tables.UserTable;
import com.rishi.frendzapp_core.models.AppSession;
import com.rishi.frendzapp_core.models.FriendGroup;
import com.rishi.frendzapp_core.models.User;
import com.rishi.frendzapp_core.qb.commands.QBAddFriendCommand;
import com.rishi.frendzapp_core.qb.commands.QBFindUsersCommand;
import com.rishi.frendzapp_core.qb.commands.QBReloginCommand;
import com.rishi.frendzapp_core.service.QBServiceConsts;
import com.rishi.frendzapp_core.utils.ConstsCore;
import com.rishi.frendzapp_core.utils.DialogUtils;
import com.rishi.frendzapp_core.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FriendsListCallFragment extends BaseFragment implements SearchView.OnQueryTextListener, AbsListView.OnScrollListener {

    private static final int SEARCH_DELAY = 1000;

    private State state;
    private String constraint;
    private ExpandableListView friendsListView;
    private TextView emptyListTextView;
    private FriendsListCallAdapter friendsListAdapter;
    private Toast errorToast;
    private ContentObserver userTableContentObserver;
    private ContentObserver friendTableContentObserver;
    private FriendOperationAction friendOperationAction;
    private Resources resources;
    private Timer searchTimer;
    private int firstVisiblePositionList;
    private View listLoadingView;
    private boolean loadingMore;
    private int page = -1; // first loading
    private int totalEntries;
    private int loadedItems;
    private int lastItemInScreen;
    private int totalItemCountInList;

    private List<FriendGroup> friendGroupList;
    private FriendGroup friendGroupAllFriends;
    private FriendGroup friendGroupAllUsers;

    public static FriendsListCallFragment newInstance() {
        return new FriendsListCallFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        checkVisibilityEmptyLabel();

        if (page == -1) {
            friendsListView.removeFooterView(listLoadingView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeActions();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unregisterContentObservers();
    }

    private void registerContentObservers() {
        userTableContentObserver = new ContentObserver(new Handler()) {

            @Override
            public void onChange(boolean selfChange) {
                updateAllFriendsData();
            }
        };

        friendTableContentObserver = new ContentObserver(new Handler()) {

            @Override
            public void onChange(boolean selfChange) {
                updateAllFriendsData();
            }
        };

        baseActivity.getContentResolver().registerContentObserver(UserTable.CONTENT_URI, true,
                userTableContentObserver);
        baseActivity.getContentResolver().registerContentObserver(FriendTable.CONTENT_URI, true,
                friendTableContentObserver);
    }

    private void updateAllFriendsData() {
        firstVisiblePositionList = friendsListView.getFirstVisiblePosition();
        updateAllFriends();
        initFriendAdapter();

        if (!TextUtils.isEmpty(constraint)) {
            performQueryTextChange();
        }

        checkVisibilityEmptyLabel();

        friendsListView.setSelection(firstVisiblePositionList);
    }

    private void unregisterContentObservers() {
        baseActivity.getContentResolver().unregisterContentObserver(userTableContentObserver);
        baseActivity.getContentResolver().unregisterContentObserver(friendTableContentObserver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getString(R.string.nvd_title_call);
        state = State.FRIENDS_LIST;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = layoutInflater.inflate(R.layout.fragment_friend_list, container, false);

        resources = getResources();
        friendOperationAction = new FriendOperationAction();
        searchTimer = new Timer();
        friendGroupList = new ArrayList<FriendGroup>();

        initUI(rootView);
        initListeners();
        registerContentObservers();
        addActions();
        initFriendList();

        return rootView;
    }

    private void initUI(View view) {
        friendsListView = (ExpandableListView) view.findViewById(R.id.friends_expandablelistview);
        listLoadingView = baseActivity.getLayoutInflater().inflate(R.layout.view_load_more, null);
        friendsListView.addFooterView(listLoadingView);
        emptyListTextView = (TextView) view.findViewById(R.id.empty_list_textview);
    }

    private void initListeners() {
        friendsListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyboardUtils.hideKeyboard(baseActivity);
                return false;
            }
        });

        friendsListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // nothing do
                return true;
            }
        });

        friendsListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                User selectedUser = (User) friendsListAdapter.getChild(groupPosition, childPosition);
                boolean isFriend = UsersDatabaseManager.isFriendInBaseWithPending(baseActivity,
                        selectedUser.getUserId());
                if (isFriend) {
                   // showFilterPopup(v, selectedUser);
                    initiatePopupWindow(selectedUser);
                    //startFriendDetailsActivity(selectedUser.getUserId());
                }
                return false;
            }
        });
    }

    private void initFriendList() {
        friendGroupList.clear();

        initAllFriends();
        initAllUsers();
        initFriendAdapter();
    }

    private void initAllFriends() {
        int countFriends = UsersDatabaseManager.getAllFriendsCountWithPending(baseActivity);
        friendGroupAllFriends = new FriendGroup(FriendGroup.GROUP_POSITION_MY_CONTACTS,null);
        friendGroupAllFriends.setUserList(new ArrayList<User>(countFriends));

        if (countFriends > ConstsCore.ZERO_INT_VALUE) {
            List<User> friendList = UsersDatabaseManager.getAllFriendsList(baseActivity);
            friendGroupAllFriends.addUserList(friendList);
        }

        friendGroupList.add(friendGroupAllFriends);
    }

    private void initAllUsers() {
        friendGroupAllUsers = new FriendGroup(FriendGroup.GROUP_POSITION_ALL_USERS, resources.getString(
                R.string.frl_column_header_name_all_users));
        friendGroupAllUsers.setUserList(new ArrayList<User>());

        friendGroupList.add(friendGroupAllUsers);
    }

    private void updateAllFriends() {
        int countFriends = UsersDatabaseManager.getAllFriendsCountWithPending(baseActivity);
        friendGroupAllFriends.getUserList().clear();

        if (countFriends > ConstsCore.ZERO_INT_VALUE) {
            List<User> friendList = UsersDatabaseManager.getAllFriendsList(baseActivity);
            friendGroupAllFriends.addUserList(friendList);
        }
    }

    private void initFriendAdapter() {
        sortLists();

        friendsListAdapter = new FriendsListCallAdapter(baseActivity, friendOperationAction, friendGroupList);
        friendsListAdapter.setSearchCharacters(constraint);
        friendsListView.setAdapter(friendsListAdapter);
        friendsListView.setGroupIndicator(null);
        friendsListView.setOnScrollListener(this);

        expandAll();
    }

    private void sortLists() {
        UserComparator userComparator = new UserComparator();
        Collections.sort(friendGroupList.get(FriendGroup.GROUP_POSITION_ALL_USERS).getUserList(),
                userComparator);
        Collections.sort(friendGroupList.get(FriendGroup.GROUP_POSITION_MY_CONTACTS).getUserList(),
                userComparator);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        constraint = query;
        KeyboardUtils.hideKeyboard(baseActivity);
        friendsListAdapter.filterData(query);
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (state.equals(State.GLOBAL_LIST)) {
            constraint = query;
            page = 1; // first page for loading items

            initFriendList();

            if (!TextUtils.isEmpty(constraint)) {
                performQueryTextChange();
            } else {
                baseActivity.hideActionBarProgress();
            }

            checkUsersListLoader();
        }

        return true;
    }

    private void performQueryTextChange() {
        friendsListAdapter.filterData(constraint);
        expandAll();
    }

    private void removeActions() {
        baseActivity.removeAction(QBServiceConsts.ADD_FRIEND_SUCCESS_ACTION);
        baseActivity.removeAction(QBServiceConsts.ADD_FRIEND_FAIL_ACTION);

        baseActivity.removeAction(QBServiceConsts.FIND_USERS_SUCCESS_ACTION);
        baseActivity.removeAction(QBServiceConsts.FIND_USERS_FAIL_ACTION);
    }

    private void addActions() {
        baseActivity.addAction(QBServiceConsts.ADD_FRIEND_SUCCESS_ACTION, new AddFriendSuccessAction());
        baseActivity.addAction(QBServiceConsts.ADD_FRIEND_FAIL_ACTION, failAction);


        baseActivity.updateBroadcastActionList();
    }

    private void startFriendDetailsActivity(int userId) {
        FriendDetails.start(baseActivity, userId);
    }

    private void expandAll() {
        int count = friendsListAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            friendsListView.expandGroup(i);
        }
    }

    private void addToFriendList(final int userId) {
        baseActivity.showProgress();
        QBAddFriendCommand.start(baseActivity, userId);
        KeyboardUtils.hideKeyboard(baseActivity);
    }

    private void checkUsersListLoader() {
        searchTimer.cancel();
        searchTimer = new Timer();
        searchTimer.schedule(new SearchTimerTask(), SEARCH_DELAY);
        baseActivity.showActionBarProgress();
    }

    private void findUsers() {
        if (TextUtils.isEmpty(constraint)) {
            return;
        }

        loadingMore = true;

        QBFindUsersCommand.start(baseActivity, AppSession.getSession().getUser(), constraint, page);
    }


    private void checkVisibilityEmptyLabel() {
        if (state == State.GLOBAL_LIST) {
            emptyListTextView.setVisibility(View.GONE);
        } else {
            int countFriends = UsersDatabaseManager.getAllFriendsCountWithPending(baseActivity);

            if ((countFriends + friendGroupAllUsers.getUserList().size()) > ConstsCore.ZERO_INT_VALUE) {
                emptyListTextView.setVisibility(View.GONE);
            } else {
                emptyListTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            if ((lastItemInScreen == totalItemCountInList) && !loadingMore && state == State.GLOBAL_LIST) {
                if (TextUtils.isEmpty(constraint)) {
                    return;
                }

                firstVisiblePositionList = totalItemCountInList - 1;
                int currentPage = (page - 1);
                loadedItems = currentPage * ConstsCore.FL_FRIENDS_PER_PAGE;

                if (ConstsCore.FL_FRIENDS_PER_PAGE < totalEntries) {
                    loadMoreItems();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemInScreen = firstVisibleItem + visibleItemCount;
        totalItemCountInList = totalItemCount;
    }

    private void loadMoreItems() {
        if (!friendGroupList.isEmpty()) {
            if (loadedItems < totalEntries) {
                friendsListView.addFooterView(listLoadingView);
                findUsers();
                page++;
            }
        } else {
            friendsListView.addFooterView(listLoadingView);
            findUsers();
            page++;
        }
    }


    private enum State {FRIENDS_LIST, GLOBAL_LIST}

    private class SearchTimerTask extends TimerTask {

        @Override
        public void run() {
            findUsers();
        }
    }


    private class FriendOperationAction implements FriendOperationListener {

        @Override
        public void onAddUserClicked(int userId) {
            addToFriendList(userId);
        }
    }

    private class AddFriendSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            int userId = bundle.getInt(QBServiceConsts.EXTRA_FRIEND_ID);

            User addedUser = UsersDatabaseManager.getUserById(baseActivity, userId);
            friendGroupAllFriends.getUserList().add(addedUser);
            friendGroupAllUsers.getUserList().remove(addedUser);
            initFriendAdapter();

            baseActivity.hideProgress();
        }
    }


    private class UserComparator implements Comparator<User> {

        @Override
        public int compare(User firstUser, User secondUser) {
            if (firstUser.getFullName() == null || secondUser.getFullName() == null) {
                return 0;
            }

            return String.CASE_INSENSITIVE_ORDER.compare(firstUser.getFullName(), secondUser.getFullName());
        }
    }
/*
    private void showFilterPopup(View v,final User user) {
        PopupMenu popup = new PopupMenu(baseActivity, v);

        popup.getMenuInflater().inflate(R.layout.view_call_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.voice_call:
                        if (baseActivity.isConnectionEnabled()) {
                            callToUser(user, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO);
                        } else {
                            Toast.makeText(baseActivity, getString(R.string.feature_unavailable), Toast.LENGTH_LONG).show();
                        }
                        Toast.makeText(baseActivity, "Keyword!", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.video_call:
                        if (baseActivity.isConnectionEnabled()) {
                            callToUser(user, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO);
                        } else {
                            Toast.makeText(baseActivity, getString(R.string.feature_unavailable), Toast.LENGTH_LONG).show();
                        }
                        Toast.makeText(baseActivity, "Popularity!", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popup.show();
    }

*/
    private PopupWindow pwindo;

    private void initiatePopupWindow(final User user) {
//        LinearLayout videoCall;
//        LinearLayout voiceCall;
        ImageView videoCall;
        ImageView voiceCall;

        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) baseActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.view_call_popup_one,null);
            pwindo = new PopupWindow(layout,ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            pwindo.setOutsideTouchable(true);
            pwindo.setFocusable(true);
            // Removes default background.
            pwindo.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            // Removes default background.
//            videoCall = (LinearLayout) layout.findViewById(R.id.linearLayout_video_call);
//            voiceCall = (LinearLayout) layout.findViewById(R.id.linearLayout_voice_call);
            videoCall = (ImageView) layout.findViewById(R.id.btn_video_call);
            voiceCall = (ImageView) layout.findViewById(R.id.btn_voice_call);



            voiceCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.dismiss();
                    if (baseActivity.isConnectionEnabled()) {
                        callToUser(user, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO);
                    } else {
                        Toast.makeText(baseActivity, getString(R.string.feature_unavailable), Toast.LENGTH_LONG).show();
                    }
                }
            });

            videoCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.dismiss();
                    if (baseActivity.isConnectionEnabled()) {
                        callToUser(user, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO);
                    } else {
                        Toast.makeText(baseActivity, getString(R.string.feature_unavailable), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    private void callToUser(User friend, QBRTCTypes.QBConferenceType callType) {
        if (friend.getUserId() != AppSession.getSession().getUser().getId()) {
            if (checkFriendStatus(friend.getUserId())) {
                if(QBChatService.getInstance().isLoggedIn()) {
                    CallActivity.start(baseActivity, friend, callType);
                }else {
                    QBReloginCommand.start(baseActivity);
//                    Toast.makeText(this, getString(R.string.dlg_fail_connection), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean checkFriendStatus(int userId) {
        boolean isFriend = UsersDatabaseManager.isFriendInBase(baseActivity, userId);
        if (isFriend) {
            return true;
        } else {
            DialogUtils.showLong(baseActivity, getResources().getString(R.string.dlg_user_is_not_friend));
            return false;
        }
    }
}
package com.rishi.frendzapp.ui.chats.dialogs;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseFragment;
import com.rishi.frendzapp.ui.chats.GroupDialogActivity;
import com.rishi.frendzapp.ui.chats.PrivateDialogActivity;
import com.rishi.frendzapp_core.core.command.Command;
import com.rishi.frendzapp_core.db.managers.ChatDatabaseManager;
import com.rishi.frendzapp_core.db.managers.UsersDatabaseManager;
import com.rishi.frendzapp_core.db.tables.UserTable;
import com.rishi.frendzapp_core.models.ParcelableQBDialog;
import com.rishi.frendzapp_core.models.User;
import com.rishi.frendzapp_core.service.QBServiceConsts;
import com.rishi.frendzapp_core.utils.ChatUtils;
import com.rishi.frendzapp_core.utils.ConstsCore;
import com.rishi.frendzapp_core.utils.DialogUtils;
import com.rishi.frendzapp_core.utils.PrefsHelper;

import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class GroupFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DIALOGS_LOADER_ID = 0;

    //private ListView dialogsListView;
    private GridView dialogsListView;
    private GroupAdapter groupAdapter;
    private TextView emptyListTextView;
    private ContentObserver userTableContentObserver;
    private Cursor dialogsCursor;
    private int selectedPositionList;

    public static GroupFragment newInstance() {
        return new GroupFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getString(R.string.nvd_title_chats);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_dialogs_list, container, false);

        if(savedInstanceState != null){
            selectedPositionList = savedInstanceState.getInt(ConstsCore.LAST_CLICKED_DIALOG);
        }

        initUI(view);
        initListeners();
        Crouton.cancelAllCroutons();

        addActions();
        initCursorLoaders();

        registerForContextMenu(dialogsListView);
        registerContentObservers();

        return view;
    }

    private void registerContentObservers() {
        userTableContentObserver = new ContentObserver(new Handler()) {

            @Override
            public void onChange(boolean selfChange) {
                initCursorLoaders();
            }
        };

        baseActivity.getContentResolver().registerContentObserver(UserTable.CONTENT_URI, true,
                userTableContentObserver);
    }

    private void unregisterContentObservers() {
        baseActivity.getContentResolver().unregisterContentObserver(userTableContentObserver);
    }

    private void initCursorLoaders() {
        if (isAdded()) {
            getLoaderManager().initLoader(DIALOGS_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return ChatDatabaseManager.getAllDialogGroupsCursorLoader(baseActivity);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dialogsCursor) {
        this.dialogsCursor = dialogsCursor;
        initChatsDialogs(dialogsCursor);
        checkVisibilityEmptyLabel();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void initUI(View view) {
        setHasOptionsMenu(true);
        dialogsListView = (GridView) view.findViewById(R.id.chats_listview);
        emptyListTextView = (TextView) view.findViewById(R.id.empty_list_textview);
    }

    private void initListeners() {
        dialogsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                Crouton.clearCroutonsForActivity(getActivity());

                // Check cursor
                Cursor selectedChatCursor = null;
                if (groupAdapter.getCursor().getCount() > ConstsCore.ZERO_INT_VALUE) {
                    selectedChatCursor = (Cursor) groupAdapter.getItem(position);
                }

                QBDialog dialog = ChatDatabaseManager.getDialogFromCursor(selectedChatCursor);
                if (dialog.getType() == QBDialogType.PRIVATE) {
                    startPrivateChatActivity(dialog);
                } else {
                    startGroupChatActivity(dialog);
                }

                selectedPositionList = parent.getFirstVisiblePosition();
            }
        });



//        dialogsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                selectedPositionList = dialogsListView.getFirstVisiblePosition();
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });
    }

    private void checkVisibilityEmptyLabel() {
        emptyListTextView.setVisibility(groupAdapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        if (groupAdapter != null) {
            checkVisibilityEmptyLabel();
        }

        restoreLastUsedListPosition();

        super.onResume();
    }

    private void restoreLastUsedListPosition() {
        dialogsListView.setSelection(selectedPositionList);

        // Erase last used list position to prevent int using next time
        selectedPositionList = ConstsCore.ZERO_INT_VALUE;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unregisterContentObservers();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dialogs_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                startNewDialogPage();
                break;
        }
        return true;
    }

    private void initChatsDialogs(Cursor newCursor) {
        if (groupAdapter == null) {
            groupAdapter = new GroupAdapter(baseActivity, dialogsCursor);

            groupAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    checkVisibilityEmptyLabel();
                }
            });
            dialogsListView.setAdapter(groupAdapter);
        } else {
            groupAdapter.swapCursor(newCursor);
//            if(selectedPositionList > ConstsCore.ZERO_INT_VALUE){
//                dialogsListView.setSelection(selectedPositionList);
//            }
        }

    }

    private void startPrivateChatActivity(QBDialog dialog) {
        int occupantId = ChatUtils.getOccupantIdFromList(dialog.getOccupants());
        User occupant = groupAdapter.getOccupantById(occupantId);
        if (!TextUtils.isEmpty(dialog.getDialogId())) {
            PrivateDialogActivity.start(baseActivity, occupant, dialog,null);
        }
    }

    private void startGroupChatActivity(QBDialog dialog) {
        GroupDialogActivity.start(baseActivity, dialog,null);
    }

    private void startNewDialogPage() {
        boolean isFriends = UsersDatabaseManager.getAllFriends(baseActivity)
                .getCount() > ConstsCore.ZERO_INT_VALUE;
        if (isFriends) {
            NewDialogActivity.start(baseActivity);
        } else {
            DialogUtils.showLong(baseActivity, getResources().getString(
                    R.string.ndl_no_friends_for_new_chat));
        }
    }

    private void addActions() {
        baseActivity.addAction(QBServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION,
                new LoadChatsDialogsSuccessAction());
        baseActivity.addAction(QBServiceConsts.LOAD_CHATS_DIALOGS_FAIL_ACTION, failAction);
        baseActivity.updateBroadcastActionList();
    }

    private class LoadChatsDialogsSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            ArrayList<ParcelableQBDialog> parcelableDialogsList = bundle.getParcelableArrayList(
                    QBServiceConsts.EXTRA_CHATS_DIALOGS);
            if (parcelableDialogsList == null) {
                emptyListTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstsCore.LAST_CLICKED_DIALOG, selectedPositionList);
    }
}
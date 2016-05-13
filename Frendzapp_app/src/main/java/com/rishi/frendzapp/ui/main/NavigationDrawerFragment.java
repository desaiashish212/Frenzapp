package com.rishi.frendzapp.ui.main;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rishi.frendzapp.ui.views.RoundedImageView;
import com.rishi.frendzapp.utils.Consts;
import com.rishi.frendzapp_core.db.managers.ChatDatabaseManager;
import com.quickblox.users.model.QBUser;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp_core.models.AppSession;
import com.rishi.frendzapp_core.qb.commands.QBLogoutCommand;
import com.rishi.frendzapp_core.service.QBServiceConsts;
import com.rishi.frendzapp.ui.base.BaseFragment;
import com.rishi.frendzapp.ui.dialogs.ConfirmDialog;
import com.rishi.frendzapp_core.utils.PrefsHelper;
import com.rishi.frendzapp_core.utils.Utils;

import java.util.Arrays;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class NavigationDrawerFragment extends BaseFragment {

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static DrawerLayout drawerLayout;
    private static View fragmentContainerView;
    private Resources resources;
    private ListView drawerListView;
    private TextView fullNameTextView;
    private TextView phonenumber;
    private RoundedImageView profileImage;

    private NavigationDrawerCallbacks navigationDrawerCallbacks;
    private NavigationDrawerCounterListener navigationDrawerCounterListener;
    private ActionBarDrawerToggle drawerToggle;
    private int currentSelectedPosition = 0;
    private boolean fromSavedInstanceState;
    private boolean userLearnedDrawer;
    private NavigationDrawerAdapter navigationDrawerAdapter;
    private BroadcastReceiver countUnreadDialogsBroadcastReceiver;

    public static boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        resources = getResources();

        initPrefValues();

        if (savedInstanceState != null) {
            currentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            fromSavedInstanceState = true;
        }

        selectItem(currentSelectedPosition);

        initLocalBroadcastManagers();
    }

    private void initLocalBroadcastManagers() {
        countUnreadDialogsBroadcastReceiver = new CountUnreadDialogsBroadcastReceiver();

        LocalBroadcastManager.getInstance(baseActivity).registerReceiver(countUnreadDialogsBroadcastReceiver,
                new IntentFilter(QBServiceConsts.GOT_CHAT_MESSAGE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        initUI(rootView);
        initListeners();
        initNavigationAdapter();

        drawerListView.setItemChecked(currentSelectedPosition, true);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        navigationDrawerCallbacks = (NavigationDrawerCallbacks) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        baseActivity.getActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        QBUser user = AppSession.getSession().getUser();
        if (user != null)
        {
            fullNameTextView.setText(user.getFullName());
            phonenumber.setText(user.getLogin());
            displayAvatarImage(Utils.customDataToObject(user.getCustomData()).getAvatar_url(), profileImage);
        }
        addActions();
    }
    private void displayAvatarImage(String s, ImageView imageview)
    {
        ImageLoader.getInstance().displayImage(s, imageview, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        navigationDrawerCallbacks = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public void setUp(int fragmentId, final DrawerLayout drawerLayout) {
        fragmentContainerView = baseActivity.findViewById(fragmentId);
        this.drawerLayout = drawerLayout;

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        ActionBar actionBar = baseActivity.getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(getActivity().getLayoutInflater().inflate(R.layout.actionbar, null));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        drawerToggle = new QMActionBarDrawerToggle(baseActivity, drawerLayout, R.drawable.ic_drawer,
                R.string.nvd_open, R.string.nvd_close);

        if (!userLearnedDrawer && !fromSavedInstanceState) {
            drawerLayout.openDrawer(fragmentContainerView);
        }

        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });

        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void initPrefValues() {
        PrefsHelper prefsHelper = PrefsHelper.getPrefsHelper();
        userLearnedDrawer = prefsHelper.getPref(PrefsHelper.PREF_USER_LEARNED_DRAWER, false);

        // Set base value of droverLayout as opposite to userLearnerDrawer
        // Made it for next behaviour: if drawer will be opened then we shouldn't show croutons
        prefsHelper.savePref(PrefsHelper.PREF_CROUTONS_DISABLED, !userLearnedDrawer);
    }

    private void selectItem(int position) {
        currentSelectedPosition = position;
        if (drawerListView != null) {
            drawerListView.setItemChecked(position, true);
        }
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(fragmentContainerView);
        }
        if (navigationDrawerCallbacks != null) {
            navigationDrawerCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    private void initNavigationAdapter() {
        navigationDrawerAdapter = new NavigationDrawerAdapter(baseActivity, getNavigationDrawerItems(),getNavigationDrawerIcon());
        drawerListView.setAdapter(navigationDrawerAdapter);
        navigationDrawerCounterListener = navigationDrawerAdapter;
    }

    private void initUI(View rootView) {
        drawerListView = (ListView) rootView.findViewById(R.id.navigation_listview);
        fullNameTextView = (TextView)rootView.findViewById(R.id.username);
        phonenumber = (TextView)rootView.findViewById(R.id.header_phone);
        profileImage = (RoundedImageView)rootView.findViewById(R.id.profile_image);
    }

    private void initListeners() {
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                selectItem(position);
            }
        });
/*
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
*/    }


    private List<String> getNavigationDrawerItems() {
        String[] itemsArray = resources.getStringArray(R.array.nvd_items_array);
        return Arrays.asList(itemsArray);
    }

    private TypedArray getNavigationDrawerIcon() {
        TypedArray navMenuIcons;
        navMenuIcons = getResources().obtainTypedArray(R.array.nvd_icon_array);
        return navMenuIcons;
    }

    private void logout() {
        ConfirmDialog dialog = ConfirmDialog.newInstance(R.string.dlg_logout, R.string.dlg_confirm);
        dialog.setPositiveButton(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Stop porcess push notifications after logout
                PrefsHelper.getPrefsHelper().savePref(PrefsHelper.PREF_PUSH_NOTIFICATIONS_ON_LOGOUT, true);

                // Start clear messages that was not sent
                ChatDatabaseManager.deleteAllNotSendMessages(getActivity().getApplicationContext());

                baseActivity.showProgress();
                //FacebookHelper.logout();

                // Clear crouton queue
                Crouton.cancelAllCroutons();

                QBLogoutCommand.start(baseActivity);
            }
        });
        dialog.show(getFragmentManager(), null);
    }

    private void addActions() {
        baseActivity.addAction(QBServiceConsts.LOGOUT_FAIL_ACTION, failAction);
        baseActivity.updateBroadcastActionList();
    }

    private void saveUserLearnedDrawer() {
        PrefsHelper.getPrefsHelper().savePref(PrefsHelper.PREF_USER_LEARNED_DRAWER, true);
    }

    private int getCountUnreadDialogs() {
        return ChatDatabaseManager.getCountUnreadDialogs(baseActivity);
    }

    public interface NavigationDrawerCallbacks {

        void onNavigationDrawerItemSelected(int position);
    }

    public interface NavigationDrawerCounterListener {

        public void onUpdateCountUnreadDialogs(int count);
    }

    private class CountUnreadDialogsBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                navigationDrawerCounterListener.onUpdateCountUnreadDialogs(getCountUnreadDialogs());
            }
        }
    }

    private class QMActionBarDrawerToggle extends ActionBarDrawerToggle {

        public QMActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, int drawerImageRes,
                int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes,
                    closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);

            // Clear croutons
            PrefsHelper.getPrefsHelper().savePref(PrefsHelper.PREF_CROUTONS_DISABLED, true);
            Crouton.clearCroutonsForActivity(getActivity());

            baseActivity.invalidateOptionsMenu();

            if (!userLearnedDrawer) {
                userLearnedDrawer = true;
                saveUserLearnedDrawer();
            }

            navigationDrawerCounterListener.onUpdateCountUnreadDialogs(getCountUnreadDialogs());
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            baseActivity.invalidateOptionsMenu();
            PrefsHelper.getPrefsHelper().savePref(PrefsHelper.PREF_CROUTONS_DISABLED, false);
        }
    }


}
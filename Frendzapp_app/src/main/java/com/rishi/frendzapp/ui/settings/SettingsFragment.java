package com.rishi.frendzapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.quickblox.users.model.QBUser;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.applock.HomePage;
import com.rishi.frendzapp.ui.event.EventActivity;
import com.rishi.frendzapp.ui.profile.Profile;
import com.rishi.frendzapp_core.models.AppSession;
import com.rishi.frendzapp.ui.base.BaseFragment;
import com.rishi.frendzapp_core.utils.PrefsHelper;

public class SettingsFragment extends BaseFragment {

    private Button profileButton;
    private Switch pushNotificationSwitch;
    private Button eventsButton;
    private Button applockButton;
    private TextView versionView;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        initUI(rootView);

        pushNotificationSwitch.setChecked(getPushNotifications());
        QBUser user = AppSession.getSession().getUser();
        if (user == null || null == user.getFacebookId()) {
           // rootView.findViewById(R.id.change_password_linearlyout).setVisibility(View.VISIBLE);
        } else {
            //rootView.findViewById(R.id.change_password_linearlyout).setVisibility(View.GONE);
        }

       // versionView.setText(getString(R.string.stn_version, Utils.getAppVersionName(baseActivity)));

        initListeners();

        //        TipsManager.showTipIfNotShownYet(this, getActivity().getString(R.string.tip_settings));

        return rootView;
    }

    private void initUI(View rootView) {
        profileButton = (Button) rootView.findViewById(R.id.profile_button);
        pushNotificationSwitch = (Switch) rootView.findViewById(R.id.push_notification_switch);
        eventsButton = (Button) rootView.findViewById(R.id.events_button);
        applockButton = (Button) rootView.findViewById(R.id.applock_button);
      //  versionView = (TextView) rootView.findViewById(R.id.version_textview);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        title = getString(R.string.nvd_title_settings);
    }

    private void initListeners() {
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile.start(baseActivity);
            }
        });

        pushNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                savePushNotification(isChecked);
            }
        });

        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity();
            }
        });

        applockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomePage.start(baseActivity);
            }
        });
    }

    private void startEventActivity() {
        EventActivity.start(baseActivity);
    }
/*
    private void logout() {
        ConfirmDialog dialog = ConfirmDialog.newInstance(R.string.dlg_logout, R.string.dlg_confirm);
        dialog.setPositiveButton(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                baseActivity.showProgress();
                QBLogoutCommand.start(getActivity());
            }
        });
        dialog.show(getFragmentManager(), null);
    }
*/
    private void savePushNotification(boolean value) {
        PrefsHelper.getPrefsHelper().savePref(PrefsHelper.PREF_PUSH_NOTIFICATIONS, !value);
    }

    private boolean getPushNotifications() {
        return !PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_PUSH_NOTIFICATIONS, false);
    }

}
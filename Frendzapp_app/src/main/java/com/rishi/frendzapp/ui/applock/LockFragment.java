package com.rishi.frendzapp.ui.applock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.core.lock.AppLock;
import com.rishi.frendzapp.core.lock.LockManager;
import com.rishi.frendzapp.ui.base.BaseFragment;

public class LockFragment extends BaseFragment {
    public static final String TAG = "HomePage";
    private Button btOnOff;
    private Button btChange;
    private final int RESULT_OK = -1;
    public static LockFragment newInstance() {
        return new LockFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frendzapp_lock,container,false);

        btOnOff = (Button) v.findViewById(R.id.bt_on_off);
        btOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = LockManager.getInstance().getAppLock().isPasscodeSet() ? AppLock.DISABLE_PASSLOCK
                        : AppLock.ENABLE_PASSLOCK;
                Intent intent = new Intent(baseActivity, AppLockActivity.class);
                intent.putExtra(AppLock.TYPE, type);
                startActivityForResult(intent, type);
            }
        });

        btChange = (Button) v.findViewById(R.id.bt_change);
        btChange.setText(R.string.change_passcode);
        btChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity, AppLockActivity.class);
                intent.putExtra(AppLock.TYPE, AppLock.CHANGE_PASSWORD);
                intent.putExtra(AppLock.MESSAGE,
                        getString(R.string.enter_old_passcode));
                startActivityForResult(intent, AppLock.CHANGE_PASSWORD);
            }
        });

        updateUI();

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //title = getString(R.string.nvd_title_settings);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AppLock.DISABLE_PASSLOCK:
                break;
            case AppLock.ENABLE_PASSLOCK:
            case AppLock.CHANGE_PASSWORD:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(baseActivity, getString(R.string.setup_passcode),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        updateUI();
    }

    private void updateUI() {
        if (LockManager.getInstance().getAppLock().isPasscodeSet()) {
            btOnOff.setText(R.string.disable_passcode);
            btChange.setEnabled(true);
        } else {
            btOnOff.setText(R.string.enable_passcode);
            btChange.setEnabled(false);
        }
    }
}


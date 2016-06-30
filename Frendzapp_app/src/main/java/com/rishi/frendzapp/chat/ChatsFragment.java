package com.rishi.frendzapp.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseFragment;
import com.rishi.frendzapp_core.utils.PrefsHelper;

/**
 * Created by Admin on 04-06-2015.
 */
public class ChatsFragment extends BaseFragment {

    public static ChatsFragment newInstance() {
        return new ChatsFragment();
    }

    PrefsHelper helper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat,container,false);
        LinearLayout text_auto_download=(LinearLayout)v.findViewById(R.id.Linear_auto_download);

        final CheckBox chk_enter_is_send=(CheckBox)v.findViewById(R.id.chk_enter_is_send);
        helper = new PrefsHelper(getActivity());

        if (helper.isPrefExists(PrefsHelper.PREF_IS_SEND_CHACKED)){
            if (helper.getPref(PrefsHelper.PREF_IS_SEND_CHACKED)){
                chk_enter_is_send.setChecked(true);
            }else {
                chk_enter_is_send.setChecked(false);
            }
        }

        text_auto_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoDownloadActivity.start(baseActivity);
            }
        });

        //this code is used for the enter resend

        chk_enter_is_send.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public
            void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    helper.savePref(PrefsHelper.PREF_IS_SEND_CHACKED,true);
                }
                else {
                    helper.savePref(PrefsHelper.PREF_IS_SEND_CHACKED,false);
                }
            }
        });


        return v;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
      //  title = getString(R.string.nvd_title_settings);
    }


}

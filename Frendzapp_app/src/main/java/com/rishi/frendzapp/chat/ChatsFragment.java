package com.rishi.frendzapp.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseFragment;

/**
 * Created by Admin on 04-06-2015.
 */
public class ChatsFragment extends BaseFragment {

    public static ChatsFragment newInstance() {
        return new ChatsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat,container,false);
        LinearLayout text_auto_download=(LinearLayout)v.findViewById(R.id.Linear_auto_download);

        RelativeLayout Relative_enter_is_Send=(RelativeLayout)v.findViewById(R.id.Relative_enter_is_Send);
        final CheckBox chk_enter_is_send=(CheckBox)v.findViewById(R.id.chk_enter_is_send);

        text_auto_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inti=new Intent(getActivity(),AutoDownloadActivity.class);
                startActivity(inti);
                getActivity().finish();
            }
        });

        //this code is used for the enter resend

        Relative_enter_is_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chk_enter_is_send.isChecked())
                {
                    chk_enter_is_send.setChecked(false);
                }
                else
                {
                    chk_enter_is_send.setChecked(true);
                }
            }
        });


        return v;
    }
}

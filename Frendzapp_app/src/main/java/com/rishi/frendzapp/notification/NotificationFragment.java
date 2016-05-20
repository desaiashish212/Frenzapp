package com.rishi.frendzapp.notification;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;





import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseFragment;
import com.rishi.frendzapp_core.utils.PrefsHelper;


import java.util.ArrayList;

/**
 * Created by Dharendra on 26-Apr-16.
 */
public class NotificationFragment extends BaseFragment {

    ArrayList<String> actionsArrayList;
    PrefsHelper helper;
    MediaPlayer mp;

    String[] Notification_names = new String[] {
                                   "Notification_V1","Notification_V1_Bottom","Notification_V2","Notification_V2_Eloe",
                                    "Notification_V3","Notification_V4","Notification_V5","Notification_V6","Notification_V7","Notification_V8",
                                    "Notification_V9", "Notification_V10", "Notification_V11", "Notification_V12", "Notification_V13",
                                    "Notification_V14", "Notification_V15", "Notification_V16", "Notification_V17", "Notification_V18", "Notification_V19"
                                    };

    int [] Notification_Sounds= new int[]{
                            R.raw.notification_v1,R.raw.notification_v1_bottom,R.raw.notification_v2,R.raw.notification_v2_eloe,
                            R.raw.notification_v3,R.raw.notification_v4,R.raw.notification_v5,R.raw.notification_v6,R.raw.notification_v7,
                            R.raw.notification_v8,R.raw.notification_v9,R.raw.breakforth,R.raw.code,R.raw.demonstrative,R.raw.jubilation,
                            R.raw.munchausen,R.raw.suppressed,R.raw.nicecut, R.raw.sisfus,R.raw.mayhaveyourttention,R.raw.isawyou
                            };
    String [] Notification_Resource= new String[]{
            "notification_v1","notification_v1_bottom","notification_v2","notification_v2_eloe",
            "notification_v3","notification_v4","notification_v5","notification_v6","notification_v7",
            "notification_v8","notification_v9","breakforth","code","demonstrative","jubilation",
            "munchausen","suppressed","nicecut", "sisfus","mayhaveyourttention","isawyou"
    };

    int pos;
    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification,container,false);
        final CheckBox chk_one=(CheckBox)v.findViewById(R.id.chk_one);
        final LinearLayout Notification_Tone=(LinearLayout)v.findViewById(R.id.Linear_two);
        final TextView set_Notification_Tone=(TextView)v.findViewById(R.id.set_Tone);
        final CheckBox chk_mute = (CheckBox) v.findViewById(R.id.chk_mute);
        final CheckBox chk_vibrate = (CheckBox) v.findViewById(R.id.vibrate_chkbox);
        helper = new PrefsHelper(getActivity());


        if (helper.isPrefExists(PrefsHelper.PREF_CONVERSATION_TONE)){
            if (helper.getPref(PrefsHelper.PREF_CONVERSATION_TONE)){
                chk_one.setChecked(true);
            }else {
                chk_one.setChecked(false);
            }
        }

        if (helper.isPrefExists(PrefsHelper.PREF_NOTIFICATION_VIBRATE)){
            if (helper.getPref(PrefsHelper.PREF_NOTIFICATION_VIBRATE)){
                chk_vibrate.setChecked(true);
            }else {
                chk_vibrate.setChecked(false);
            }
        }

        if (helper.isPrefExists(PrefsHelper.PREF_NOTIFICATION_NAME)){
            set_Notification_Tone.setText(helper.getPref(PrefsHelper.PREF_NOTIFICATION_NAME).toString());
        }

        chk_mute.setChecked(getPushNotifications());

        //
        chk_one.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public
            void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    helper.savePref(PrefsHelper.PREF_CONVERSATION_TONE,true);
                }
                else {
                    helper.savePref(PrefsHelper.PREF_CONVERSATION_TONE,false);
                }

            }
        });

        chk_vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public
            void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    helper.savePref(PrefsHelper.PREF_NOTIFICATION_VIBRATE,true);
                    Vibrator vibs = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibs.vibrate(1000);
                }
                else {
                    helper.savePref(PrefsHelper.PREF_NOTIFICATION_VIBRATE,false);
                }
            }
        });



        actionsArrayList=new ArrayList<String>();
        actionsArrayList.add("Action 1");
        actionsArrayList.add("Action 2");

        // this code is used for the notification tone

        Notification_Tone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater
                        = (LayoutInflater)getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View popupView = layoutInflater.inflate(R.layout.view_popup_notification_tone
                        , null);


                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.showAtLocation(popupView,Gravity.CENTER,0,0);

                ListView listView=(ListView)popupView.findViewById(R.id.list);
                TextView txt_set=(TextView)popupView.findViewById(R.id.txt_set);
                TextView txt_Center=(TextView)popupView.findViewById(R.id.txt_cancel);
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, Notification_names)
                {

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view =super.getView(position, convertView, parent);

                        TextView textView=(TextView) view.findViewById(android.R.id.text1);

            /*YOUR CHOICE OF COLOR*/
                        textView.setTextColor(Color.BLACK);

                        return view;
                    }

                });

                // ListView on item selected listener.
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        pos = position;
                        stopPlaying();
                        //Below code is used for set the tone positionwise in array
                                mp = MediaPlayer.create(getActivity(), Notification_Sounds[position]);
                                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                //Below code is used for set the toneName positionwise in array
                                mp.start();
                            }
                        });
                txt_set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        set_Notification_Tone.setText(Notification_names[pos]);
                        stopPlaying();
                        helper.savePref(PrefsHelper.PREF_NOTIFICATION_NAME,Notification_names[pos]);
                        helper.savePref(PrefsHelper.PREF_NOTIFICATION,Notification_Resource[pos]);
                        popupWindow.dismiss();
                    }
                });


                txt_Center.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopPlaying();
                       popupWindow.dismiss();
                    }
                });

               popupWindow.showAsDropDown(Notification_Tone, 50, -30);
            }

            });


        chk_mute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public
            void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    savePushNotification(true);
                }
                else {
                    savePushNotification(false);
                }

            }
        });


        return v;
    }


    private void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }


    }

    private void savePushNotification(boolean value) {
        PrefsHelper.getPrefsHelper().savePref(PrefsHelper.PREF_PUSH_NOTIFICATIONS, !value);
    }

    private boolean getPushNotifications() {
        return !PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_PUSH_NOTIFICATIONS, false);
    }


}

package com.rishi.frendzapp.chat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseLogeableActivity;
import com.rishi.frendzapp_core.utils.PrefsHelper;

/**
 * Created by Dharendra on 28-Apr-16.
 */
public class AutoDownloadActivity extends BaseLogeableActivity{

    private CheckBox chk_one;
    private CheckBox chk_two;
    private CheckBox chk_three;
    private CheckBox chk_four;
    PrefsHelper helper;

    public static void start(Context context) {
        Intent intent = new Intent(context, AutoDownloadActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_auto_download);
        init();
        initListner();
        // you clicked on the images
        if (helper.isPrefExists(PrefsHelper.PREF_IMAGE)) {
            if (helper.getPref(PrefsHelper.PREF_IMAGE)) {
                chk_one.setChecked(true);
            } else {
                chk_one.setChecked(false);
            }
        }
        // you clicked on the audios
        if (helper.isPrefExists(PrefsHelper.PREF_VIDEOS)) {
            if (helper.getPref(PrefsHelper.PREF_VIDEOS)) {
                chk_two.setChecked(true);
            } else {
                chk_two.setChecked(false);
            }
        }

        if (helper.isPrefExists(PrefsHelper.PREF_AUDIO)) {
            if (helper.getPref(PrefsHelper.PREF_AUDIO)) {
                chk_three.setChecked(true);
            } else {
                chk_three.setChecked(false);
            }
        }
        //you clicked on the videos
        if (helper.isPrefExists(PrefsHelper.PREF_DOCUMENTS)) {
            if (helper.getPref(PrefsHelper.PREF_DOCUMENTS)) {
                chk_four.setChecked(true);
            } else {
                chk_four.setChecked(false);
            }
        }
}

    public void init(){
        chk_one=(CheckBox)findViewById(R.id.chk_images);
        chk_two=(CheckBox)findViewById(R.id.chk_audeos);
        chk_three=(CheckBox)findViewById(R.id.chk_videos);
        chk_four=(CheckBox)findViewById(R.id.chk_doc);
        helper = new PrefsHelper(this);
    }

    public void initListner(){

        chk_one.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public
            void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    helper.savePref(PrefsHelper.PREF_IMAGE,true);
                }
                else {
                    helper.savePref(PrefsHelper.PREF_IMAGE,false);
                }

            }
        });

        chk_two.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public
            void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    helper.savePref(PrefsHelper.PREF_VIDEOS,true);
                }
                else {
                    helper.savePref(PrefsHelper.PREF_VIDEOS,false);
                }

            }
        });

        chk_three.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public
            void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    helper.savePref(PrefsHelper.PREF_AUDIO,true);
                }
                else {
                    helper.savePref(PrefsHelper.PREF_AUDIO,false);
                }

            }
        });

        chk_four.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public
            void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    helper.savePref(PrefsHelper.PREF_DOCUMENTS,true);
                }
                else {
                    helper.savePref(PrefsHelper.PREF_DOCUMENTS,false);
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}


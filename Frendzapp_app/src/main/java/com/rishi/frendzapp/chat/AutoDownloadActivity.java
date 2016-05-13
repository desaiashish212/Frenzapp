package com.rishi.frendzapp.chat;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseLogeableActivity;

/**
 * Created by Dharendra on 28-Apr-16.
 */
public class AutoDownloadActivity extends BaseLogeableActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_auto_download);
        RelativeLayout relative_one=(RelativeLayout)findViewById(R.id.relative_one);
        RelativeLayout relative_two=(RelativeLayout)findViewById(R.id.relative_two);

        RelativeLayout relative_three=(RelativeLayout)findViewById(R.id.relative_three);

        RelativeLayout relative_four=(RelativeLayout)findViewById(R.id.relative_four);
        final CheckBox chk_one=(CheckBox)findViewById(R.id.chk_images);
        final CheckBox chk_two=(CheckBox)findViewById(R.id.chk_audeos);
        final CheckBox chk_three=(CheckBox)findViewById(R.id.chk_videos);
        final CheckBox chk_four=(CheckBox)findViewById(R.id.chk_doc);

        // you clicked on the images
        relative_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chk_one.isChecked())
                {
                    chk_one.setChecked(false);
                }
                else {
                    chk_one.setChecked(true);
                }
            }
        });
        // you clicked on the audeos
        relative_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chk_two.isChecked())
                {
                    chk_two.setChecked(false);
                }
                else {
                    chk_two.setChecked(true);
                }
            }
        });

        //you clicked on the videos
        relative_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chk_three.isChecked())
                {
                    chk_three.setChecked(false);
                }
                else {
                    chk_three.setChecked(true);
                }
            }
        });


        //you clicked on the documents
        relative_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chk_four.isChecked())
                {
                    chk_four.setChecked(false);
                }
                else {
                    chk_four.setChecked(true);
                }
            }
        });

}


}


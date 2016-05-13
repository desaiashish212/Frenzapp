package com.rishi.frendzapp.ui.selfiemode;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.rishi.frendzapp.R;

/**
 * Created by AD on 23-Mar-16.
 */
public class SelfiePreview extends Activity {

    private ImageView selfiePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initListner();
    }

    public void init(){
        selfiePreview = (ImageView) findViewById(R.id.selfiePreview);
    }

    public void initListner(){

    }
}

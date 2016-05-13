package com.rishi.frendzapp.invite;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseFragment;

/**
 * Created by Dharendra on 26-Apr-16.
 */
public class ShareFragment extends BaseFragment {

    public static ShareFragment newInstance() {
        return new ShareFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View v = inflater.inflate(R.layout.fragment_share,container,false);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
        return null;
    }
}

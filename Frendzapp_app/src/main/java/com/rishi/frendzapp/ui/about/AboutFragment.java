package com.rishi.frendzapp.ui.about;

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
public class AboutFragment extends BaseFragment {
    public static AboutFragment newInstance() {
        return new AboutFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about,container,false);
        return v;
    }
}

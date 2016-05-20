package com.rishi.frendzapp.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseFragment;

/**
 * Created by Dharendra on 26-Apr-16.
 */
public class AccountFragment extends BaseFragment {

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_account,container,false);
        LinearLayout linear_one=(LinearLayout)v.findViewById(R.id.Linear_one);
        LinearLayout linear_two=(LinearLayout)v.findViewById(R.id.Linear_two);


        linear_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountPaymentInfoActivity.start(baseActivity);
            }
        });

        linear_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"you clicked on the delete",Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}

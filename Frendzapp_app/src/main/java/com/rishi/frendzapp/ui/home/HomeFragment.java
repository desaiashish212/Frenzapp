package com.rishi.frendzapp.ui.home;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.call.FriendsListCallFragment;
import com.rishi.frendzapp.core.lock.LockManager;
import com.rishi.frendzapp.ui.base.BaseFragment;
import com.rishi.frendzapp.ui.chats.dialogs.DialogsFragment;
import com.rishi.frendzapp.ui.chats.dialogs.GroupFragment;
import com.rishi.frendzapp.ui.event.EventActivity;
import com.rishi.frendzapp.ui.friends.FriendsListFragment;
import com.rishi.frendzapp.ui.main.MainActivity;
import com.rishi.frendzapp.ui.offer.OfferActivity;
import com.rishi.frendzapp.ui.profile.Profile;
import com.rishi.frendzapp.ui.selfiemode.SelfieModeActivity;

public class HomeFragment extends BaseFragment {

    private ImageButton call;
    private ImageButton chat;
    private ImageButton contact;
    private ImageButton events;
    private ImageButton gallery;
    private ImageButton group;
    private ImageButton offers;
    private ImageButton profile;
    private ImageButton selfie;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
       // title = getString(R.string.nvd_title_settings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initUI(view);
        initListeners();

        return view;
    }

    private void initListeners() {
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment view = FriendsListFragment.newInstance();
                FragmentTransaction fragmenttransaction = getFragmentManager().beginTransaction();
                fragmenttransaction.replace(R.id.container, view);
                fragmenttransaction.addToBackStack(null);
                fragmenttransaction.commit();
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment view = DialogsFragment.newInstance();
                FragmentTransaction fragmenttransaction = getFragmentManager().beginTransaction();
                fragmenttransaction.replace(R.id.container, view);
                fragmenttransaction.addToBackStack(null);
                fragmenttransaction.commit();
            }
        });

        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Group");
                Fragment view = GroupFragment.newInstance();
                FragmentTransaction fragmenttransaction = getFragmentManager().beginTransaction();
                fragmenttransaction.replace(R.id.container, view);
                fragmenttransaction.addToBackStack(null);
                fragmenttransaction.commit();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Gallery");
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Profile");
                startActivity(new Intent(getActivity(), Profile.class));
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Call");
                Fragment view = FriendsListCallFragment.newInstance();
                FragmentTransaction fragmenttransaction = getFragmentManager().beginTransaction();
                fragmenttransaction.replace(R.id.container, view);
                fragmenttransaction.addToBackStack(null);
                fragmenttransaction.commit();
            }
        });

        selfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Selfie");
                startActivity(new Intent(getActivity(), SelfieModeActivity.class));
            }
        });

        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("events");
                startActivity(new Intent(getActivity(), EventActivity.class));
            }
        });

        offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Offer");
                startActivity(new Intent(getActivity(), OfferActivity.class));
            }
        });

    }

    private void initUI(View view) {
        contact = (ImageButton)view.findViewById(R.id.contact);
        chat = (ImageButton)view.findViewById(R.id.chat);
        group = (ImageButton)view.findViewById(R.id.group);
        gallery = (ImageButton)view.findViewById(R.id.gallery);
        profile = (ImageButton)view.findViewById(R.id.profile);
        call = (ImageButton)view.findViewById(R.id.call);
        selfie = (ImageButton)view.findViewById(R.id.selfie);
        events = (ImageButton)view.findViewById(R.id.event);
        offers = (ImageButton)view.findViewById(R.id.offers);
    }

}
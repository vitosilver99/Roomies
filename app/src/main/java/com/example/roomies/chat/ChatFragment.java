package com.example.roomies.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.roomies.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class ChatFragment extends Fragment {

    private static final String ARG_USER_ID = "param1";
    private static final String ARG_CASA_ID = "param2";

    public String casaId;
    public String userId;

    public ChatFragment() {
        // Required empty public constructor
    }


    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, param1);
        args.putString(ARG_CASA_ID, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            casaId = getArguments().getString(ARG_CASA_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_chat, container, false);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout_chat);
        ViewPager viewPager = view.findViewById(R.id.viewPager_chat);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        Bundle args_chat= new Bundle();
        args_chat.putCharSequence(ARG_USER_ID, userId);
        args_chat.putCharSequence(ARG_CASA_ID, casaId);
        UsersFragment usersFragment = new UsersFragment();
        usersFragment.setArguments(args_chat);

        viewPagerAdapter.addFragment(new ChatSecondFragment(),"Chats");
        viewPagerAdapter.addFragment(usersFragment,"Users");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(FragmentManager fm){
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title)
        {
            fragments.add(fragment);
            titles.add(title);
        }

        public CharSequence getPageTitle(int position){
            return titles.get(position);
        }
    }



}


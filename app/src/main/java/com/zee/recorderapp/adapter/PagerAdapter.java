package com.zee.recorderapp.adapter;


import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.zee.recorderapp.R;

import java.util.ArrayList;

public class PagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> fragmentsTitle = new ArrayList<>();

    public PagerAdapter(Context context, FragmentManager fm) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
    }

    public void addFragment(Fragment fragments , String fragmentsTitle)
    {
        this.fragments.add(fragments);
        this.fragmentsTitle.add(fragmentsTitle) ;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return  fragmentsTitle.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
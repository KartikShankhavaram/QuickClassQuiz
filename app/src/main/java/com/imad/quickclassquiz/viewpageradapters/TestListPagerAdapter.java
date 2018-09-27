package com.imad.quickclassquiz.viewpageradapters;

import android.annotation.SuppressLint;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.imad.quickclassquiz.fragments.StartedTestsTeacherFragment;
import com.imad.quickclassquiz.fragments.UpcomingTestsTeacherFragment;

import java.util.HashMap;

public class TestListPagerAdapter extends FragmentPagerAdapter {

    private HashMap<Integer, Fragment> fragmentHashMap;

    @SuppressLint("UseSparseArrays")
    public TestListPagerAdapter(FragmentManager fm) {
        super(fm);
        if(fragmentHashMap == null) {
            fragmentHashMap = new HashMap<>();
            initializeMap();
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0: return fragmentHashMap.get(0);
            case 1: return fragmentHashMap.get(1);
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0: return "Upcoming Tests";
            case 1: return "Ongoing/Completed Tests";
            default: return "";
        }
    }

    private void initializeMap() {
        if(fragmentHashMap != null) {
            fragmentHashMap.put(0, UpcomingTestsTeacherFragment.newInstance());
            fragmentHashMap.put(1, StartedTestsTeacherFragment.newInstance());
        }
    }
}

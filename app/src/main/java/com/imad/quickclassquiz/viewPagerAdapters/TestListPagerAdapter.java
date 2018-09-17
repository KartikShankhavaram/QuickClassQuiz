package com.imad.quickclassquiz.viewPagerAdapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.imad.quickclassquiz.fragments.StartedTestsTeacherFragment;
import com.imad.quickclassquiz.fragments.UpcomingTestsTeacherFragment;

public class TestListPagerAdapter extends FragmentPagerAdapter {

    public TestListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0: return UpcomingTestsTeacherFragment.newInstance();
            case 1: return StartedTestsTeacherFragment.newInstance();
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
}

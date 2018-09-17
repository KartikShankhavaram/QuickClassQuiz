package com.imad.quickclassquiz.viewPagerAdapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AddQuestionPagerAdapter extends FragmentPagerAdapter {
    public AddQuestionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }

//    ArrayList<AddQuestionFragment> list = new ArrayList<>();
//
//    public AddQuestionPagerAdapter(FragmentManager fm) {
//        super(fm);
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        return list.get(position);
//    }
//
//    @Override
//    public int getCount() {
//        return list.size();
//    }
//
//    public void setFragmentList(ArrayList<AddQuestionFragment> list) {
//        this.list = list;
//        notifyDataSetChanged();
//    }
//
//    public ArrayList<AddQuestionFragment> getFragmentList() {
//        return list;
//    }
}

package com.imad.quickclassquiz.viewpageradapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.imad.quickclassquiz.fragments.StudentQuestionFragment;

import java.util.ArrayList;

public class TestQuestionPagerAdapter extends FragmentPagerAdapter {
    public TestQuestionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private ArrayList<StudentQuestionFragment> list = new ArrayList<>();

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void setFragmentList(ArrayList<StudentQuestionFragment> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public ArrayList<StudentQuestionFragment> getFragmentList() {
        return list;
    }
}

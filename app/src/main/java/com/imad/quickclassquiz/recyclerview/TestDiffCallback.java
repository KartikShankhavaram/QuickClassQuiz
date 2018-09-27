package com.imad.quickclassquiz.recyclerview;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.imad.quickclassquiz.datamodel.Test;

import java.util.List;

public class TestDiffCallback extends DiffUtil.Callback {

    private final List<Test> mOldTestList;
    private final List<Test> mNewTestList;

    public TestDiffCallback(List<Test> mOldTestList, List<Test> mNewTestList) {
        this.mOldTestList = mOldTestList;
        this.mNewTestList = mNewTestList;
    }

    @Override
    public int getOldListSize() {
        return mOldTestList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewTestList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldTestList.get(oldItemPosition).getTestId().equals(mNewTestList.get(newItemPosition).getTestId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldTestList.get(oldItemPosition).equals(mNewTestList.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}

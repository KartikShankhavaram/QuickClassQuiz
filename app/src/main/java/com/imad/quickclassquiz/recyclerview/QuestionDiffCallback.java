package com.imad.quickclassquiz.recyclerview;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.imad.quickclassquiz.datamodel.Question;

import java.util.List;

public class QuestionDiffCallback extends DiffUtil.Callback {

    private final List<Question> mOldQuestionList;
    private final List<Question> mNewQuestionList;

    public QuestionDiffCallback(List<Question> mOldQuestionList, List<Question> mNewQuestionList) {
        this.mOldQuestionList = mOldQuestionList;
        this.mNewQuestionList = mNewQuestionList;
    }

    @Override
    public int getOldListSize() {
        return mOldQuestionList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewQuestionList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldQuestionList.get(oldItemPosition).getQuestionId().equals(mNewQuestionList.get(newItemPosition).getQuestionId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldQuestionList.get(oldItemPosition).equals(mNewQuestionList.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}

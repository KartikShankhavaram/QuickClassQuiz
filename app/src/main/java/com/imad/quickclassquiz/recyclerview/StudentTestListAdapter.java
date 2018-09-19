package com.imad.quickclassquiz.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.datamodel.Test;

import java.util.ArrayList;
import java.util.List;

public class StudentTestListAdapter extends RecyclerView.Adapter<StudentTestListAdapter.StudentTestListViewHolder> {

    Context mContext;
    ArrayList<Test> list = new ArrayList<>();
    LayoutInflater inflater;
    View rootView;

    public StudentTestListAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public StudentTestListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        rootView = inflater.inflate(R.layout.card_test_student, parent, false);
        return new StudentTestListViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentTestListViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setListContent(List<Test> list) {
        final TestDiffCallback diffCallback = new TestDiffCallback(this.list, list);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.list.clear();
        this.list.addAll(list);
        diffResult.dispatchUpdatesTo(this);
    }

    public class StudentTestListViewHolder extends RecyclerView.ViewHolder {

        TextView testNameTextView, testDescTextView, questionCountTextView;
        Button testStartButton;

        public StudentTestListViewHolder(View itemView) {
            super(itemView);
            testNameTextView = itemView.findViewById(R.id.testNameTextView);
            testDescTextView = itemView.findViewById(R.id.testDescTextView);
            questionCountTextView = itemView.findViewById(R.id.questionCountTextView);
            testStartButton = itemView.findViewById(R.id.testStartButton);
        }
    }
}

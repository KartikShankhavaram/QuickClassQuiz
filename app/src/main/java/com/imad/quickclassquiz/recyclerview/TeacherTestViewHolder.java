package com.imad.quickclassquiz.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.imad.quickclassquiz.R;

public class TeacherTestViewHolder extends RecyclerView.ViewHolder {

    private TextView testNameTextView;
    private TextView testDescTextView;
    private Button editTestButton;
    private Button startTestButton;
    private TextView testAddTimeTextView;

    public TeacherTestViewHolder(View itemView) {
        super(itemView);
        testNameTextView = itemView.findViewById(R.id.testNameTextView);
        testDescTextView = itemView.findViewById(R.id.testDescTextView);
        editTestButton = itemView.findViewById(R.id.testEditButton);
        startTestButton = itemView.findViewById(R.id.testStartButton);
        testAddTimeTextView = itemView.findViewById(R.id.testAddDateTextView);
    }

    public TextView getTestNameTextView() {
        return testNameTextView;
    }

    public TextView getTestDescTextView() {
        return testDescTextView;
    }

    public Button getEditTestButton() {
        return editTestButton;
    }

    public Button getStartTestButton() {
        return startTestButton;
    }

    public TextView getTestAddTimeTextView() {
        return testAddTimeTextView;
    }

}

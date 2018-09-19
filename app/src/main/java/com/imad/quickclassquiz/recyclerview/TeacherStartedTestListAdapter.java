package com.imad.quickclassquiz.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.activities.StartTestActivity;
import com.imad.quickclassquiz.activities.QuestionListActivity;
import com.imad.quickclassquiz.datamodel.Test;
import com.imad.quickclassquiz.utils.StaticValues;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class TeacherStartedTestListAdapter extends RecyclerView.Adapter<TeacherStartedTestListAdapter.TeacherStartedTestViewHolder> {

    private ArrayList<Test> testArrayList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater inflater;

    public TeacherStartedTestListAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public TeacherStartedTestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.card_started_test_teacher, parent, false);
        JodaTimeAndroid.init(mContext);
        return new TeacherStartedTestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherStartedTestViewHolder holder, int position) {
        if(testArrayList.get(position).getAccessCode() == null && testArrayList.get(position).getMasterCode() == null)
            return;

        TextView testNameTextView = holder.getTestNameTextView();
        TextView testDescTextView = holder.getTestDescTextView();
        Button viewQuestionsButton = holder.getViewQuestionsButton();
        Button viewCodesButton = holder.getViewCodesButton();
        TextView testStartDateTextView = holder.getTestStartDateTextView();

        String testName = testArrayList.get(position).getTestName();
        String testDesc = testArrayList.get(position).getTestDesc();

        testNameTextView.setText(testName);
        testDescTextView.setText(testDesc);
        viewQuestionsButton.setOnClickListener(v -> {
            Intent viewQuestions = new Intent(mContext, QuestionListActivity.class);
            viewQuestions.putExtra("test", testArrayList.get(position));
            viewQuestions.putExtra("started", true);
            StaticValues.setCurrentTest(testArrayList.get(position));
            mContext.startActivity(viewQuestions);
        });
        viewCodesButton.setOnClickListener(v -> {
            Intent viewCodes = new Intent(mContext, StartTestActivity.class);
            viewCodes.putExtra("test", testArrayList.get(position));
            viewCodes.putExtra("generated", true);
            mContext.startActivity(viewCodes);
        });
        String timestamp = testArrayList.get(position).getStartedAt();
        DateTime dt = new DateTime(timestamp);
        DateTimeFormatter format = DateTimeFormat.forPattern("'Started on 'MMM d' at 'h:mm a");
        String time = format.print(dt);
        testStartDateTextView.setText(time);
    }

    public void setListContent(List<Test> testArrayList) {
        final TestDiffCallback diffCallback = new TestDiffCallback(this.testArrayList, testArrayList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.testArrayList.clear();
        this.testArrayList.addAll(testArrayList);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return testArrayList.size();
    }

    public class TeacherStartedTestViewHolder extends RecyclerView.ViewHolder {

        private TextView testNameTextView;
        private TextView testDescTextView;
        private Button viewQuestionsButton;
        private Button viewCodesButton;
        private TextView testStartDateTextView;

        public TeacherStartedTestViewHolder(View itemView) {
            super(itemView);
            testNameTextView = itemView.findViewById(R.id.testNameTextView);
            testDescTextView = itemView.findViewById(R.id.testDescTextView);
            viewQuestionsButton = itemView.findViewById(R.id.viewQuestionsButton);
            viewCodesButton = itemView.findViewById(R.id.viewCodesButton);
            testStartDateTextView = itemView.findViewById(R.id.testStartDateTextView);
        }

        public TextView getTestNameTextView() {
            return testNameTextView;
        }

        public TextView getTestDescTextView() {
            return testDescTextView;
        }

        public Button getViewQuestionsButton() {
            return viewQuestionsButton;
        }

        public Button getViewCodesButton() {
            return viewCodesButton;
        }

        public TextView getTestStartDateTextView() {
            return testStartDateTextView;
        }
    }
}

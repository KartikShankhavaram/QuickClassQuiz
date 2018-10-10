package com.imad.quickclassquiz.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.activities.QuestionListActivity;
import com.imad.quickclassquiz.datamodel.Test;
import com.imad.quickclassquiz.utils.StaticValues;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class StudentCompletedTestAdapter extends RecyclerView.Adapter<StudentCompletedTestAdapter.TeacherStartedTestViewHolder> {

    private ArrayList<Test> testArrayList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater inflater;

    public StudentCompletedTestAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public StudentCompletedTestAdapter.TeacherStartedTestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.card_student_completed_test, parent, false);
        JodaTimeAndroid.init(mContext);
        return new StudentCompletedTestAdapter.TeacherStartedTestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TeacherStartedTestViewHolder holder, int position) {
        TextView testNameTextView = holder.getTestNameTextView();
        TextView testDescTextView = holder.getTestDescTextView();
        Button viewQuestionsButton = holder.getViewQuestionsButton();
        TextView questionCountTextView = holder.getQuestionCountTextView();

        Test test = testArrayList.get(position);

        String testName = test.getTestName();
        String testDesc = test.getTestDesc();

        testNameTextView.setText(testName);
        testDescTextView.setText(testDesc);

        String questionText = test.getQuestionCount() <= 1 ? " question" : " questions";
        String questionCount = Integer.toString(test.getQuestionCount());

        if (questionCount.equals("0"))
            questionCountTextView.setText("No questions");
        else {
            SpannableStringBuilder str = new SpannableStringBuilder(test.getQuestionCount() + questionText);
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, questionCount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            questionCountTextView.setText(str);
        }

        viewQuestionsButton.setOnClickListener(v -> {

            Intent viewQuestions = new Intent(mContext, QuestionListActivity.class);
            viewQuestions.putExtra("test", test);
            viewQuestions.putExtra("started", true);
            viewQuestions.putExtra("completed", true);
            StaticValues.setCurrentTest(test);
            mContext.startActivity(viewQuestions);
        });

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

        private TextView questionCountTextView;

        public TeacherStartedTestViewHolder(View itemView) {
            super(itemView);
            testNameTextView = itemView.findViewById(R.id.testNameTextView);
            testDescTextView = itemView.findViewById(R.id.testDescTextView);
            viewQuestionsButton = itemView.findViewById(R.id.showQuestions);
            questionCountTextView = itemView.findViewById(R.id.questionCountTextView);
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

        public TextView getQuestionCountTextView() {
            return questionCountTextView;
        }

    }
}

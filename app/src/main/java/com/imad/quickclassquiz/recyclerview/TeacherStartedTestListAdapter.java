package com.imad.quickclassquiz.recyclerview;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.activities.TeacherStartTestActivity;
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
               TextView testNameTextView = holder.getTestNameTextView();
        TextView testDescTextView = holder.getTestDescTextView();
        Button viewQuestionsButton = holder.getViewQuestionsButton();
        Button viewCodesButton = holder.getViewCodesButton();
        TextView testStartDateTextView = holder.getTestStartDateTextView();
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
            StaticValues.setCurrentTest(test);
            mContext.startActivity(viewQuestions);
        });
        viewCodesButton.setOnClickListener(v -> {
            Intent viewCodes = new Intent(mContext, TeacherStartTestActivity.class);
            viewCodes.putExtra("test", test);
            viewCodes.putExtra("generated", true);
            mContext.startActivity(viewCodes);
        });
        String timestamp = test.getStartedAt();
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
        private TextView questionCountTextView;

        public TeacherStartedTestViewHolder(View itemView) {
            super(itemView);
            testNameTextView = itemView.findViewById(R.id.testNameTextView);
            testDescTextView = itemView.findViewById(R.id.testDescTextView);
            viewQuestionsButton = itemView.findViewById(R.id.viewQuestionsButton);
            viewCodesButton = itemView.findViewById(R.id.viewCodesButton);
            testStartDateTextView = itemView.findViewById(R.id.testStartDateTextView);
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

        public Button getViewCodesButton() {
            return viewCodesButton;
        }

        public TextView getTestStartDateTextView() {
            return testStartDateTextView;
        }

        public TextView getQuestionCountTextView() {
            return questionCountTextView;
        }
    }
}

package com.imad.quickclassquiz.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.imad.quickclassquiz.dataModel.Test;
import com.imad.quickclassquiz.utils.StaticValues;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class TeacherUpcomingTestListAdapter extends RecyclerView.Adapter<TeacherUpcomingTestListAdapter.TeacherUpcomingTestViewHolder> {

    private ArrayList<Test> testArrayList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater inflater;

    public TeacherUpcomingTestListAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public TeacherUpcomingTestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.card_upcoming_test_teacher, parent, false);
        JodaTimeAndroid.init(mContext);
        return new TeacherUpcomingTestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherUpcomingTestViewHolder holder, int position) {
        TextView testNameTextView = holder.getTestNameTextView();
        TextView testDesctextView = holder.getTestDescTextView();
        Button editTestButton = holder.getEditTestButton();
        Button startTestButton = holder.getStartTestButton();
        TextView testAddDateTextView = holder.getTestAddTimeTextView();

        String testName = testArrayList.get(position).getTestName();
        String testDesc = testArrayList.get(position).getTestDesc();

        testNameTextView.setText(testName);
        testDesctextView.setText(testDesc);
        editTestButton.setOnClickListener(v -> {
            Intent editTest = new Intent(mContext, QuestionListActivity.class);
            editTest.putExtra("test", testArrayList.get(position));
            editTest.putExtra("started", false);
            StaticValues.setCurrentTest(testArrayList.get(position));
            mContext.startActivity(editTest);
        });
        startTestButton.setOnClickListener(v -> {
            Intent startTest = new Intent(mContext, StartTestActivity.class);
            startTest.putExtra("test", testArrayList.get(position));
            startTest.putExtra("generated", false);
            new AlertDialog.Builder(mContext)
                    .setCancelable(false)
                    .setTitle("Start test")
                    .setMessage(String.format("Are you sure you want to start the test '%s'?", testName))
                    .setPositiveButton("Yes", (dialog, which) -> {
                        mContext.startActivity(startTest);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {

                    })
                    .show();
        });
        String timestamp = testArrayList.get(position).getCreatedAt();
        DateTime dt = new DateTime(timestamp);
        DateTimeFormatter format = DateTimeFormat.forPattern("'Added on 'MMM d' at 'h:mm a");
        String time = format.print(dt);
        testAddDateTextView.setText(time);

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

    public class TeacherUpcomingTestViewHolder extends RecyclerView.ViewHolder {

        private TextView testNameTextView;
        private TextView testDescTextView;
        private Button editTestButton;
        private Button startTestButton;
        private TextView testAddTimeTextView;

        public TeacherUpcomingTestViewHolder(View itemView) {
            super(itemView);
            testNameTextView = itemView.findViewById(R.id.testNameEditText);
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
}

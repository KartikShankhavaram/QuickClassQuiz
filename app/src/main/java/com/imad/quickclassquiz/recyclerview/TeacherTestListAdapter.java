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
import android.widget.Toast;

import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.StartTestActivity;
import com.imad.quickclassquiz.activities.QuestionListActivity;
import com.imad.quickclassquiz.dataModel.Test;
import com.imad.quickclassquiz.utils.StaticValues;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class TeacherTestListAdapter extends RecyclerView.Adapter<TeacherTestViewHolder> {

    private ArrayList<Test> testArrayList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater inflater;

    public TeacherTestListAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public TeacherTestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.cardview_test_teacher, parent, false);
        JodaTimeAndroid.init(mContext);
        return new TeacherTestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherTestViewHolder holder, int position) {
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
            StaticValues.setCurrentTest(testArrayList.get(position));
            mContext.startActivity(editTest);
        });
        startTestButton.setOnClickListener(v -> {
            Intent startTest = new Intent(mContext, StartTestActivity.class);
            startTest.putExtra("test", testArrayList.get(position));
            if(testArrayList.get(position).getAccessCode() != null && testArrayList.get(position).getMasterCode() != null) {
                Toast.makeText(mContext, "This test has already been started!", Toast.LENGTH_SHORT).show();
                mContext.startActivity(startTest);
            } else {
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
            }
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
}

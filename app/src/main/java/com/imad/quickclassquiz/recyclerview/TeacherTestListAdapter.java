package com.imad.quickclassquiz.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.activities.QuestionListActivity;
import com.imad.quickclassquiz.dataModel.Question;
import com.imad.quickclassquiz.dataModel.Test;

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

        testNameTextView.setText(testArrayList.get(position).getTestName());
        testDesctextView.setText(testArrayList.get(position).getTestDesc());
        editTestButton.setOnClickListener(v -> {
            Intent editTest = new Intent(mContext, QuestionListActivity.class);
            editTest.putExtra("test", testArrayList.get(position));
            mContext.startActivity(editTest);
        });
        startTestButton.setOnClickListener(v -> {
            Toast.makeText(mContext, "Start button clicked for " + testArrayList.get(position).getTestId(), Toast.LENGTH_SHORT).show();
        });
        String timestamp = testArrayList.get(position).getTestTimestamp();
        DateTime dt = new DateTime(timestamp);
        DateTimeFormatter format = DateTimeFormat.forPattern("'Added on 'MMM d' at 'h:mm a");
        String time = format.print(dt);
        testAddDateTextView.setText(time);

    }

    public void setListContent(ArrayList<Test> testArrayList) {
        if(!equalLists(this.testArrayList, testArrayList)) {
            this.testArrayList = testArrayList;
            notifyItemRangeChanged(0, testArrayList.size());
        }
    }

    @Override
    public int getItemCount() {
        return testArrayList.size();
    }

    public boolean equalLists(List<Test> a, List<Test> b) {
        return a == null && b == null || a != null && b != null && a.size() == b.size() && a.equals(b);
    }
}

package com.imad.quickclassquiz.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.dataModel.Test;

import java.util.ArrayList;

public class TeacherTestAdapter extends RecyclerView.Adapter<TeacherTestViewHolder> {

    private ArrayList<Test> testArrayList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater inflater;

    public TeacherTestAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public TeacherTestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.cardview_test_teacher, parent, false);
        return new TeacherTestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherTestViewHolder holder, int position) {
        TextView testNameTextView = holder.getTestNameTextView();
        TextView testDesctextView = holder.getTestDescTextView();
        Button editTestButton = holder.getEditTestButton();
        Button startTestButton = holder.getStartTestButton();

        testNameTextView.setText(testArrayList.get(position).getTestName());
        testDesctextView.setText(testArrayList.get(position).getTestDesc());
        editTestButton.setOnClickListener(v -> {
            Toast.makeText(mContext, "Edit button clicked for " + testArrayList.get(position).getTestName(), Toast.LENGTH_SHORT).show();
        });
        startTestButton.setOnClickListener(v -> {
            Toast.makeText(mContext, "Start button clicked for " + testArrayList.get(position).getTestName(), Toast.LENGTH_SHORT).show();
        });
    }

    public void setListContent(ArrayList<Test> testArrayList) {
        this.testArrayList = testArrayList;
        notifyItemRangeChanged(0, testArrayList.size());

    }

    @Override
    public int getItemCount() {
        return testArrayList.size();
    }
}

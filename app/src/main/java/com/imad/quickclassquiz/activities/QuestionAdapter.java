package com.imad.quickclassquiz.activities;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.dataModel.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyViewHolder> {
    @NonNull
    private List<Question> questionList;

    public QuestionAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }

    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Question obj = questionList.get(position);
        holder.question.setText(obj.getQuestion());
        holder.option1.setText(obj.getOption1());
        holder.option2.setText(obj.getOption2());
        holder.option3.setText(obj.getOption3());
        holder.option4.setText(obj.getOption4());
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView question,option1,option2,option3,option4;
        public RadioButton radio1,radio2,radio3,radio4;

        public MyViewHolder(View convertView) {
            super(convertView);
            question = convertView.findViewById(R.id.question);
            option1 = convertView.findViewById(R.id.option1Text);
            option2 = convertView.findViewById(R.id.option2Text);
            option3 = convertView.findViewById(R.id.option3Text);
            option4 = convertView.findViewById(R.id.option4Text);
            radio1 = convertView.findViewById(R.id.option1Radio);
            radio2 = convertView.findViewById(R.id.option2Radio);
            radio3 = convertView.findViewById(R.id.option3Radio);
            radio4 = convertView.findViewById(R.id.option4Radio);
        }
    }

    public void filterList(ArrayList<Question> filteredList) {
        questionList = filteredList;
        notifyDataSetChanged();
    }
}

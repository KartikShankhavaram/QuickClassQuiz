package com.imad.quickclassquiz.recyclerview;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
                .inflate(R.layout.question_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Question obj = questionList.get(position);

        String options[] = {obj.getOption1(), obj.getOption2(), obj.getOption3(), obj.getOption4()};
        TextView optionView[] = {holder.option1, holder.option2, holder.option3, holder.option4};

        for(int i = 0; i < options.length; i++) {
            if(options[i].equals(obj.getCorrectOption())) {
                optionView[i].setTypeface(optionView[i].getTypeface(), Typeface.BOLD);
                break;
            }
        }

        holder.question.setText(obj.getQuestion());
        holder.option1.setText(String.format("A. %s", obj.getOption1()));
        holder.option2.setText(String.format("B. %s", obj.getOption2()));
        holder.option3.setText(String.format("C. %s", obj.getOption3()));
        holder.option4.setText(String.format("D. %s", obj.getOption4()));


    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView question,option1,option2,option3,option4;

        public MyViewHolder(View convertView) {
            super(convertView);
            question = convertView.findViewById(R.id.testNameTextView);
            option1 = convertView.findViewById(R.id.option1);
            option2 = convertView.findViewById(R.id.option2);
            option3 = convertView.findViewById(R.id.option3);
            option4 = convertView.findViewById(R.id.option4);
        }
    }

    public void filterList(ArrayList<Question> filteredList) {
        questionList = filteredList;
        notifyDataSetChanged();
    }
}

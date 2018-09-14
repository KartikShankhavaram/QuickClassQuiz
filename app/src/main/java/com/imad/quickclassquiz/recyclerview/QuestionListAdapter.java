package com.imad.quickclassquiz.recyclerview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.activities.EditQuestion;
import com.imad.quickclassquiz.dataModel.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.MyViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<Question> list = new ArrayList<>();
    private FirebaseFirestore firestore;
    private View rootView;

    public QuestionListAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        rootView = inflater.inflate(R.layout.question_list_item, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Question obj = list.get(position);

        String options[] = {obj.getOption1(), obj.getOption2(), obj.getOption3(), obj.getOption4()};
        TextView optionView[] = {holder.option1, holder.option2, holder.option3, holder.option4};

        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(obj.getCorrectOption())) {
                optionView[i].setTypeface(optionView[i].getTypeface(), Typeface.BOLD);
                break;
            }
        }

        holder.question.setText(obj.getQuestion());
        holder.option1.setText(String.format("A. %s", obj.getOption1()));
        holder.option2.setText(String.format("B. %s", obj.getOption2()));
        holder.option3.setText(String.format("C. %s", obj.getOption3()));
        holder.option4.setText(String.format("D. %s", obj.getOption4()));

        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Deleting question");
        progressDialog.setMessage("Please wait while we delete this question...");

        holder.deleteButton.setOnClickListener((View v) -> {
            String url = String.format("tests/%s/questions/%s", obj.getTestId(), obj.getQuestionId());
            new AlertDialog.Builder(mContext)
                    .setCancelable(false)
                    .setTitle("Delete question")
                    .setMessage("Are you sure you want to delete this question?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        progressDialog.show();
                        firestore.document(url).delete().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                this.list.remove(position);
                                notifyItemRemoved(position);
                            } else {
                                Toast.makeText(mContext, "Couldn't delete question. Try again.", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        });
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {

                    })
                    .show();
        });

        holder.editButton.setOnClickListener(v -> {
            // TODO Add code to edit a question
           // Snackbar.make(rootView, "Add code to edit this question", Snackbar.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, EditQuestion.class);
            intent.putExtra("Question",list.get(position));
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setListContent(List<Question> list) {
        final QuestionDiffCallback diffCallback = new QuestionDiffCallback(this.list, list);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.list.clear();
        this.list.addAll(list);
        diffResult.dispatchUpdatesTo(this);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView question, option1, option2, option3, option4;
        public ImageButton deleteButton, editButton;

        public MyViewHolder(View view) {
            super(view);
            question = view.findViewById(R.id.testNameEditText);
            option1 = view.findViewById(R.id.option1);
            option2 = view.findViewById(R.id.option2);
            option3 = view.findViewById(R.id.option3);
            option4 = view.findViewById(R.id.option4);
            deleteButton = view.findViewById(R.id.deleteQuestionButton);
            editButton = view.findViewById(R.id.editQuestionButton);
        }
    }
}

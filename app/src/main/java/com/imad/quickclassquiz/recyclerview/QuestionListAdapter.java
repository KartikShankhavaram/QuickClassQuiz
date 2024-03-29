package com.imad.quickclassquiz.recyclerview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.activities.EditQuestionActivity;
import com.imad.quickclassquiz.datamodel.Question;
import com.imad.quickclassquiz.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.MyViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<Question> list = new ArrayList<>();
    private HashMap<String, Object> attemptedAnswers = new HashMap<>();
    private FirebaseFirestore firestore;
    private View rootView;
    private Boolean editable;
    private Boolean showAttempted;

    public QuestionListAdapter(Context mContext, Boolean editable) {
        this.mContext = mContext;
        this.editable = editable;
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

        for (int i = 0; i < optionView.length; i++) {
            boolean optionCorrect = false;
            if (options[i].equals(obj.getCorrectOption())) {
                optionView[i].setTypeface(optionView[i].getTypeface(), Typeface.BOLD);
                optionView[i].setTextSize(18);
                optionCorrect = true;
            }
            if (attemptedAnswers.containsKey(obj.getQuestionId()) && options[i].equals(attemptedAnswers.get(obj.getQuestionId()))) {
                if(optionCorrect)
                    optionView[i].setTextColor(mContext.getResources().getColor(R.color.colorTaskCompleted));
                else
                    optionView[i].setTextColor(mContext.getResources().getColor(R.color.colorTaskIncomplete));
            }
        }


        holder.question.setText(obj.getQuestion());
        holder.option1.setText(String.format("A. %s", obj.getOption1()));
        holder.option2.setText(String.format("B. %s", obj.getOption2()));
        holder.option3.setText(String.format("C. %s", obj.getOption3()));
        holder.option4.setText(String.format("D. %s", obj.getOption4()));

        if (editable) {
            ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setTitle("Deleting question");
            progressDialog.setMessage("Please wait while we delete this question...");

            holder.deleteButton.setOnClickListener((View v) -> {
                new NetworkUtils(internet -> {
                    if (internet) {
                        String url = String.format("tests/%s/questions/%s", obj.getTestId(), obj.getQuestionId());
                        String testUrl = String.format("tests/%s", obj.getTestId());
                        new AlertDialog.Builder(mContext)
                                .setCancelable(false)
                                .setTitle("Delete question")
                                .setMessage("Are you sure you want to delete this question?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    progressDialog.show();
                                    firestore.runTransaction(transaction -> {
                                        DocumentSnapshot test = transaction.get(firestore.document(testUrl));
                                        int questionCount = (int) Math.round(test.getDouble("questionCount"));
                                        transaction.delete(firestore.document(url));
                                        transaction.update(firestore.document(testUrl), "questionCount", questionCount - 1);

                                        return null;
                                    }).addOnCompleteListener(task -> {
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
                    } else {
                        Toast.makeText(mContext, "No internet available.", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            holder.editButton.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, EditQuestionActivity.class);
                intent.putExtra("Question", list.get(position));
                mContext.startActivity(intent);
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
            holder.editButton.setVisibility(View.GONE);
        }
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

    public void setAttemptedAnswersHashMap(HashMap<String, Object> attemptedAnswers) {
        this.attemptedAnswers = attemptedAnswers;
        notifyDataSetChanged();
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

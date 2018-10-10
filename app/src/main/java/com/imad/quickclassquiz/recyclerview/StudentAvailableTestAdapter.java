package com.imad.quickclassquiz.recyclerview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.activities.StudentStartTestActivity;
import com.imad.quickclassquiz.datamodel.Test;
import com.imad.quickclassquiz.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class StudentAvailableTestAdapter extends RecyclerView.Adapter<StudentAvailableTestAdapter.StudentAvailableTestViewHolder> {

    Context mContext;
    ArrayList<Test> list = new ArrayList<>();
    LayoutInflater inflater;
    View rootView;
    FirebaseFirestore firestore;

    public StudentAvailableTestAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public StudentAvailableTestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        rootView = inflater.inflate(R.layout.card_test_student, parent, false);
        return new StudentAvailableTestViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAvailableTestViewHolder holder, int position) {
        TextView testNameTextView = holder.testNameTextView;
        TextView testDescTextView = holder.testDescTextView;
        TextView questionCountTextView = holder.questionCountTextView;
        CardView studentTestCardView = holder.studentTestCardView;

        Test test = list.get(position);
        testNameTextView.setText(test.getTestName());
        testDescTextView.setText(test.getTestDesc());

        String questionText = test.getQuestionCount() <= 1 ? " question" : " questions";
        String questionCount = Integer.toString(test.getQuestionCount());

        if (questionCount.equals("0"))
            questionCountTextView.setText("No questions");
        else {
            SpannableStringBuilder str = new SpannableStringBuilder(test.getQuestionCount() + questionText);
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, questionCount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            questionCountTextView.setText(str);
        }

        studentTestCardView.setOnClickListener(v -> {
            new NetworkUtils(internet -> {
                if (internet) {
                    checkIfAlreadyAttempted(test);
                } else {
                    Toast.makeText(mContext, "No internet connection.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setListContent(List<Test> list) {
        final TestDiffCallback diffCallback = new TestDiffCallback(this.list, list);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.list.clear();
        this.list.addAll(list);
        diffResult.dispatchUpdatesTo(this);
    }

    private void checkIfAlreadyAttempted(Test test) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setTitle("Please wait");
        dialog.setMessage("Checking for previous attempts...");
        dialog.show();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);
        String url = String.format(Locale.ENGLISH, "tests/%s/attempts", test.getTestId());
        firestore.collection(url).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                    if (account.getId().equals(snapshot.getId())) {
                        dialog.dismiss();
                        Toast.makeText(mContext, "You have already attempted this test.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Intent toStartTest = new Intent(mContext, StudentStartTestActivity.class);
                toStartTest.putExtra("test", test);
                mContext.startActivity(toStartTest);
            } else {
                Toast.makeText(mContext, "Please try again.", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
    }

    public class StudentAvailableTestViewHolder extends RecyclerView.ViewHolder {

        TextView testNameTextView, testDescTextView, questionCountTextView;
        CardView studentTestCardView;

        public StudentAvailableTestViewHolder(View itemView) {
            super(itemView);
            testNameTextView = itemView.findViewById(R.id.testNameTextView);
            testDescTextView = itemView.findViewById(R.id.testDescTextView);
            questionCountTextView = itemView.findViewById(R.id.questionCountTextView);
            studentTestCardView = itemView.findViewById(R.id.studentTestCardView);
        }
    }
}

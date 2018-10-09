package com.imad.quickclassquiz.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.WriteBatch;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.datamodel.Question;
import com.imad.quickclassquiz.datamodel.ScoreModel;
import com.imad.quickclassquiz.datamodel.Test;
import com.imad.quickclassquiz.utils.NetworkUtils;
import com.imad.quickclassquiz.utils.TimestampUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EvaluationActivity extends AppCompatActivity {

    HashMap<String, String> attemptedAnswersMap = new HashMap<>();
    ArrayList<Question> questions = new ArrayList<>();
    private String roll;
    private String name;
    private ProgressBar progressBar;
    private Button retry;
    private TextView textView;
    FirebaseFirestore firestore;
    GoogleSignInAccount account;
    Toolbar toolbar;
    Test test;
    private String url = "";
    int score = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        progressBar = findViewById(R.id.progressBar);
        retry = findViewById(R.id.retry);
        textView = findViewById(R.id.textView);
        toolbar = findViewById(R.id.EvaluationTaskToolBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Evalution");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        retry.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        account = GoogleSignIn.getLastSignedInAccount(this);
        assert account != null;
        String email =account.getEmail();
        assert email != null;
        String[] parts = email.split("@");
        roll = parts[0];
        name = account.getDisplayName();
        test = getIntent().getParcelableExtra("Test");
        url = String.format("tests/%s/scores", test.getTestId());
        attemptedAnswersMap = (HashMap<String, String>) Objects.requireNonNull(getIntent().getExtras()).getSerializable("HashMap");
        questions = getIntent().getParcelableArrayListExtra("Question");
        for (Map.Entry<String, String> entry : attemptedAnswersMap.entrySet()) {
            if(checkAnswer(entry.getKey(),entry.getValue())){
                score = score + 4;
            }
        }
        new NetworkUtils(internet -> {
            if(internet){
                sendToDatabase();
            }else{
                progressBar.setVisibility(View.GONE);
                textView.setText("No Internet");
                retry.setEnabled(true);
            }
        });
        retry.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            new NetworkUtils(internet -> {
                if(internet){
                    retry.setEnabled(false);
                    sendToDatabase();
                }else{
                    retry.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                }
            });
        });
    }

    private void sendToDatabase() {
        String currentTime = TimestampUtils.getISO8601StringForCurrentDate();
        ScoreModel scoreModel = new ScoreModel(name,roll,String.valueOf(score), currentTime);
        firestore = FirebaseFirestore.getInstance();

        WriteBatch batch = firestore.batch();

        batch.set(firestore.collection(url).document( account.getId().toString()), scoreModel);
        batch.commit().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(this, "Successfully submitted", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                textView.setText("Successfully submitted");
            } else {
                Toast.makeText(this, "Submission Failed", Toast.LENGTH_SHORT).show();
                textView.setText("Submission Failed");
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean checkAnswer(String questionId, String attemptedAnswer) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            java.util.Optional<Question> questionOptional = questions.stream().filter(question -> questionId.equals(question.getQuestionId())).findFirst();
            return questionOptional.map(question -> question.getCorrectOption().equals(attemptedAnswer)).orElse(false);
        } else {
            Optional<Question> questionOptional = FluentIterable.from(questions).firstMatch(question -> questionId.equals(question.getQuestionId()));
            if (questionOptional.isPresent()) {
                return questionOptional.get().getCorrectOption().equals(attemptedAnswer);
            } else {
                return false;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EvaluationActivity.this,StudentTestListActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

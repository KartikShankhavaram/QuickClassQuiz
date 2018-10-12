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
/* As name supposed this activity is for evalution after this activity student has completed the test succesfully*/
public class EvaluationActivity extends AppCompatActivity {

    HashMap<String, Object> attemptedAnswersMap = new HashMap<>();
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
        retry.setVisibility(View.GONE);
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
        attemptedAnswersMap = (HashMap<String, Object>) Objects.requireNonNull(getIntent().getExtras()).getSerializable("HashMap");
        questions = getIntent().getParcelableArrayListExtra("Question");
        for (Map.Entry<String, Object> entry : attemptedAnswersMap.entrySet()) {
            if(checkAnswer(entry.getKey(),(String)entry.getValue())){
                score = score + 1;
            }
        }
        String currentTime = TimestampUtils.getISO8601StringForCurrentDate();
        ScoreModel scoreModel = new ScoreModel(name, roll, Integer.toString(score), currentTime, attemptedAnswersMap);
        new NetworkUtils(internet -> {
            if(internet){
                sendToDatabase(scoreModel);
            }else{
                progressBar.setVisibility(View.GONE);
                textView.setText("No Internet");
                retry.setVisibility(View.VISIBLE);
            }
        });
        retry.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            new NetworkUtils(internet -> {
                if(internet){
                    retry.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    sendToDatabase(scoreModel);
                }else{
                    retry.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });
        });
    }

    private void sendToDatabase(ScoreModel scoreModel) {
        firestore = FirebaseFirestore.getInstance();

        WriteBatch batch = firestore.batch();
        batch.set(firestore.collection(url).document(account.getId()), scoreModel);
        batch.commit().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                String str = "Congrats, " + account.getDisplayName() + " You have submitted Successfully you can view result after 30 minutes in completed test";
                Toast.makeText(this, "Successfully submitted", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                textView.setText(str);
                retry.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Submission Failed", Toast.LENGTH_SHORT).show();
                textView.setText("Submission Failed");
                progressBar.setVisibility(View.GONE);
                retry.setVisibility(View.VISIBLE);
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

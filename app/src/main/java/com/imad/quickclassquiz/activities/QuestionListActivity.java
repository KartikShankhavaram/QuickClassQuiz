package com.imad.quickclassquiz.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.datamodel.Question;
import com.imad.quickclassquiz.datamodel.ScoreModel;
import com.imad.quickclassquiz.datamodel.Test;
import com.imad.quickclassquiz.recyclerview.QuestionListAdapter;
import com.imad.quickclassquiz.utils.NetworkUtils;
import com.imad.quickclassquiz.utils.StaticValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
/* In this activity we are fetching all the question and their options,user's marked answer,their score
* and populating them in UI so that student can view them in completed test fragement*/
public class QuestionListActivity extends AppCompatActivity {

    @BindView(R.id.addQuestionFAB)
    FloatingActionButton addQuestionButton;
    @BindView(R.id.questionListRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.questionListToolbar)
    Toolbar toolbar;
    @BindView(R.id.questionListSwipeRefresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.noQuestionsTextView)
    TextView noQuestionsTextView;
    @BindView(R.id.scoreTextView)
    TextView scoreTextView;

    ActionBar actionBar;

    FirebaseFirestore firestore;
    QuestionListAdapter adapter;

    String testUrl;
    Test test;
    ArrayList<Question> currentQuestionList;

    ScoreModel scoreModel;

    boolean shouldShowScore = false;

    boolean scoreFetched = false;
    boolean questionsFetched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);

        ButterKnife.bind(this);

        firestore = FirebaseFirestore.getInstance();

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            test = intent.getParcelableExtra("test");
            if (intent.getBooleanExtra("started", true)) {
                addQuestionButton.hide();
                adapter = new QuestionListAdapter(this, false);
                noQuestionsTextView.setText("This test does not contain any questions.");
            } else {
                adapter = new QuestionListAdapter(this, true);
                addQuestionButton.show();
            }

            shouldShowScore = intent.getBooleanExtra("completed", false);

            if (test != null) {
                testUrl = String.format("tests/%s/questions", test.getTestId());
            }
        }

        if (!shouldShowScore) {
            scoreTextView.setVisibility(View.GONE);
            scoreFetched = true;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        noQuestionsTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        refreshLayout.setOnRefreshListener(() -> {
            new NetworkUtils(internet -> {
                if (internet) {
                    fetchQuestions();
                    if(shouldShowScore)
                    fetchScore();
                } else {
                    refreshLayout.setRefreshing(false);
                    scoreTextView.setText("Marks: Could not fetch data");
                    Toast.makeText(this, "No internet available.", Toast.LENGTH_SHORT).show();
                }
            });

        });

        addQuestionButton.setOnClickListener(v -> {
            Intent addQuestion = new Intent(QuestionListActivity.this, AddQuestionActivity.class);
            addQuestion.putExtra("test", test);
            startActivity(addQuestion);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        new NetworkUtils(internet -> {
            if (internet) {
                fetchQuestions();
                if(shouldShowScore)
                fetchScore();
            } else {
                scoreTextView.setText("Marks: Could not fetch data");
                Toast.makeText(this, "No internet available.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Test test;
        if ((test = StaticValues.getCurrentTest()) != null) {
            this.test = test;
            actionBar.setTitle(test.getTestName());
        }
    }

    private void fetchQuestions() {
        refreshLayout.setRefreshing(true);
        questionsFetched = false;
        ArrayList<Question> list = new ArrayList<>();
        adapter.setListContent(list);
        firestore.collection(testUrl).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    Question question = documentSnapshot.toObject(Question.class);
                    list.add(question);
                }
                currentQuestionList = list;
                adapter.setListContent(list);
                if (list.size() == 0) {
                    noQuestionsTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    noQuestionsTextView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(this, "Failed to fetch.", Toast.LENGTH_SHORT).show();
            }
            questionsFetched = true;
            checkForCompletion();
        });
    }

    private void fetchScore() {
        refreshLayout.setRefreshing(true);
        scoreFetched = false;
        String scoreUrl = String.format(Locale.ENGLISH, "tests/%s/scores", test.getTestId());
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        firestore.collection(scoreUrl).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    ScoreModel score = documentSnapshot.toObject(ScoreModel.class);
                    if(score != null && documentSnapshot.getId().equals(account.getId())) {
                        scoreTextView.setText(String.format(Locale.ENGLISH, "Marks: %s/%d", score.getUserScore(), test.getQuestionCount()));
                        scoreFetched = true;
                        scoreModel = score;
                        checkForCompletion();
                        return;
                    }
                }
                scoreTextView.setText("Marks: Not Attempted");
            } else {
                scoreTextView.setText("Marks: Could not fetch data");
                Toast.makeText(this, "Failed to fetch.", Toast.LENGTH_SHORT).show();
            }
            scoreFetched = true;
            checkForCompletion();
        });
    }

    private void checkForCompletion() {
        if(questionsFetched && scoreFetched) {
            if(scoreModel != null) {
                HashMap<String, Object> attemptedAnswers = scoreModel.getAttemptedAnswers();
                if(attemptedAnswers != null) {
                    adapter.setAttemptedAnswersHashMap(attemptedAnswers);
                }
            }
            refreshLayout.setRefreshing(false);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        Intent intent;
        if ((intent = getIntent()) == null || !intent.getBooleanExtra("started", true)) {
            inflater.inflate(R.menu.question_list_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Deleting test");
        progressDialog.setMessage("Please wait while this test is deleted...");
        switch (item.getItemId()) {
            case R.id.editTestDetailsMenu:
                Intent updateTest = new Intent(this, UpdateTestActivity.class);
                updateTest.putExtra("test", test);
                startActivity(updateTest);
                return true;
            case R.id.deleteTestMenu:
                if (currentQuestionList != null) {
                    if (currentQuestionList.size() != 0)
                        Toast.makeText(this, "You must first delete all the questions before deleting a test.", Toast.LENGTH_SHORT).show();
                    else {
                        new NetworkUtils(internet -> {
                            if (internet) {
                                String testUrl = String.format("tests/%s", test.getTestId());
                                new AlertDialog.Builder(this)
                                        .setCancelable(false)
                                        .setTitle("Delete test")
                                        .setMessage("Are you sure you want to delete this test?")
                                        .setPositiveButton("Yes", (dialog, which) -> {
                                            progressDialog.show();
                                            firestore.document(testUrl).delete().addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(this, "Deleted successfully!", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    startActivity(new Intent(this, TeacherTestListActivity.class));
                                                    finish();
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(this, "Failed to delete.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        })
                                        .setNegativeButton("Cancel", (dialog, which) -> {

                                        })
                                        .show();
                            } else {
                                Toast.makeText(this, "No internet available.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StaticValues.clearTest();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

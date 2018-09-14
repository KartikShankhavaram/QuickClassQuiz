package com.imad.quickclassquiz.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.dataModel.Question;
import com.imad.quickclassquiz.dataModel.Test;
import com.imad.quickclassquiz.recyclerview.QuestionListAdapter;
import com.imad.quickclassquiz.utils.StaticValues;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

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

    FirebaseFirestore firestore;
    QuestionListAdapter adapter;

    String testUrl;
    Test test;

    ArrayList<Question> currentQuestionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);

        ButterKnife.bind(this);

        firestore = FirebaseFirestore.getInstance();

        setSupportActionBar(toolbar);

        adapter = new QuestionListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new LandingAnimator());

        noQuestionsTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        if (intent != null) {
            test = intent.getParcelableExtra("test");
        }
        if (test != null) {
            testUrl = String.format("tests/%s/questions", test.getTestId());
            Log.e("testUrl", testUrl);
        }

        refreshLayout.setOnRefreshListener(() -> fetchQuestions());

        addQuestionButton.setOnClickListener(v -> {
            Intent addQuestion = new Intent(QuestionListActivity.this, AddQuestionActivity.class);
            addQuestion.putExtra("test", test);
            startActivity(addQuestion);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchQuestions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Test test;
        if ((test = StaticValues.getCurrentTest()) != null) {
            this.test = test;
            getSupportActionBar().setTitle(test.getTestName());
        }
    }

    private void fetchQuestions() {
        refreshLayout.setRefreshing(true);
        ArrayList<Question> list = new ArrayList<>();
        adapter.setListContent(list);
        firestore.collection(testUrl).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    Question question = documentSnapshot.toObject(Question.class);
                    Log.e("question", question.getQuestion());
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
            refreshLayout.setRefreshing(false);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.question_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ProgressDialog progressDialog = new ProgressDialog(this);
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
                                            startActivity(new Intent(this, TestListActivity.class));
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
}

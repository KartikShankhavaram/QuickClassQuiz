package com.imad.quickclassquiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.dataModel.Question;
import com.imad.quickclassquiz.dataModel.Test;
import com.imad.quickclassquiz.recyclerview.QuestionListAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);

        ButterKnife.bind(this);

        firestore = FirebaseFirestore.getInstance();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

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
        if (test != null && actionBar != null) {
            actionBar.setTitle(test.getTestName());
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
    protected void onResume() {
        super.onResume();
        fetchQuestions();
    }

    private void fetchQuestions() {
        refreshLayout.setRefreshing(true);
        ArrayList<Question> list = new ArrayList<>();
        firestore.collection(testUrl).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    Question question = documentSnapshot.toObject(Question.class);
                    Log.e("question", question.getQuestion());
                    list.add(question);
                }
                adapter.setListContent(list);
                if(list.size() == 0) {
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
}

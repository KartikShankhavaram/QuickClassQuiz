package com.imad.quickclassquiz.activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.imad.quickclassquiz.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentTestListActivity extends AppCompatActivity {

    @BindView(R.id.activeTestListRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.activeTestListSwipeRefresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.noActiveTestsTextView)
    TextView noActiveTestsTextView;

    @BindView(R.id.studentTestListToolbar)
    Toolbar toolbar;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_test_list);

        ButterKnife.bind(this);

        firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build());

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Available Tests");
        }

        noActiveTestsTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);



    }
}

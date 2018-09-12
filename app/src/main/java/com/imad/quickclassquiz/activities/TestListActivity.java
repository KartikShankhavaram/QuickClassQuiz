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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.dataModel.Test;
import com.imad.quickclassquiz.recyclerview.TeacherTestAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestListActivity extends AppCompatActivity {

    @BindView(R.id.testListToolbar)
    Toolbar toolbar;
    @BindView(R.id.addTestFAB)
    FloatingActionButton addTestButton;
    @BindView(R.id.testListRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout refreshLayout;

    FirebaseFirestore firestore;
    TeacherTestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Upcoming tests");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TeacherTestAdapter(this);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        refreshLayout.setOnRefreshListener(() -> onRefresh());

        onRefresh();

        addTestButton.setOnClickListener(v -> {
            startActivity(new Intent(TestListActivity.this, AddTestActivity.class));
        });
    }

    private void onRefresh() {
        refreshLayout.setRefreshing(true);
        ArrayList<Test> teacherTestList = new ArrayList<>();
        adapter.setListContent(teacherTestList);
        CollectionReference testsCollection = firestore.collection("tests");
        testsCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            teacherTestList.add(documentSnapshot.toObject(Test.class));
                        }
                        adapter.setListContent(teacherTestList);
                    }
                    refreshLayout.setRefreshing(false);
                });
    }
}

package com.imad.quickclassquiz.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.datamodel.Test;
import com.imad.quickclassquiz.recyclerview.StudentTestListAdapter;
import com.imad.quickclassquiz.utils.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

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
    StudentTestListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_test_list);

        ButterKnife.bind(this);

        firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build());

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Available Tests");
        }

        noActiveTestsTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        adapter = new StudentTestListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new LandingAnimator());
        recyclerView.setAdapter(new AlphaInAnimationAdapter(adapter));

        refreshLayout.setOnRefreshListener(() -> {
            new NetworkUtils(internet -> {
                if (internet) {
                    fetchAvailableTests();
                } else {
                    Toast.makeText(this, "No internet available.", Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                }
            });
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new NetworkUtils(internet -> {
            if (internet) {
                fetchAvailableTests();
            } else {
                Toast.makeText(this, "No internet available.", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public void fetchAvailableTests() {
        refreshLayout.setRefreshing(true);
        ArrayList<Test> list = new ArrayList<>();
        adapter.setListContent(list);
        firestore.collection("tests")
                .whereEqualTo("visible", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Test test = snapshot.toObject(Test.class);
                            list.add(test);
                        }
                        adapter.setListContent(list);
                        if (list.size() == 0) {
                            noActiveTestsTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            noActiveTestsTextView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch tests.", Toast.LENGTH_SHORT).show();
                    }
                    refreshLayout.setRefreshing(false);
                });
    }
}

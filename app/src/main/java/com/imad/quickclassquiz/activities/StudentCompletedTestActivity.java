package com.imad.quickclassquiz.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.datamodel.Test;
import com.imad.quickclassquiz.recyclerview.StudentCompletedTestAdapter;
import com.imad.quickclassquiz.utils.NetworkUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public class StudentCompletedTestActivity extends AppCompatActivity {

    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    TextView noStartedTestsTextView;
    StudentCompletedTestAdapter adapter;
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_completed_test);
        refreshLayout = findViewById(R.id.completedTestListSwipeRefresh);
        recyclerView = findViewById(R.id.completedTestListRecyclerView);
        noStartedTestsTextView = findViewById(R.id.noCompletedTestsTextView);
        firestore = FirebaseFirestore.getInstance();
        adapter = new StudentCompletedTestAdapter(this);
        refreshLayout.setOnRefreshListener(() -> {
            new NetworkUtils(internet -> {
                if(internet) {
                    fetchTests();
                } else {
                    Toast.makeText(StudentCompletedTestActivity.this, "No internet available.", Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                }
            });
        });
        fetchTests();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new LandingAnimator());
        recyclerView.setAdapter(new AlphaInAnimationAdapter(adapter));
        noStartedTestsTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

    }


    public void fetchTests() {
        refreshLayout.setRefreshing(true);
        DateTime today = new DateTime();
        ArrayList<Test> teacherTestList = new ArrayList<>();
        adapter.setListContent(teacherTestList);
        CollectionReference testsCollection = firestore.collection("tests");
        testsCollection.whereGreaterThan("accessCode", "")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            Test test = documentSnapshot.toObject(Test.class);
                            DateTime dt = new DateTime(test.getStartedAt());
                            if(today.minusMinutes(30).isAfter(dt)) {
                                teacherTestList.add(test);
                            }
                        }
                        Toast.makeText(StudentCompletedTestActivity.this, String.valueOf(teacherTestList.size()), Toast.LENGTH_SHORT).show();
                        adapter.setListContent(teacherTestList);
                        if (teacherTestList.size() == 0) {
                            noStartedTestsTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            noStartedTestsTextView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                    refreshLayout.setRefreshing(false);
                });
    }
}

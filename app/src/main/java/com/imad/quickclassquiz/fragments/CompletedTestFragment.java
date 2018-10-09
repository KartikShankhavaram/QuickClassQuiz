package com.imad.quickclassquiz.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class CompletedTestFragment extends Fragment {
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    TextView noStartedTestsTextView;
    StudentCompletedTestAdapter adapter;
    FirebaseFirestore firestore;

    public CompletedTestFragment() {
        // Required empty public constructor
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        adapter = new StudentCompletedTestAdapter(getContext());
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_completed_test_fragement, container, false);
        refreshLayout = rootView.findViewById(R.id.completedTestListSwipeRefresh);
        recyclerView = rootView.findViewById(R.id.completedTestListRecyclerView);
        noStartedTestsTextView = rootView.findViewById(R.id.noCompletedTestsTextView);

        refreshLayout.setOnRefreshListener(() -> {
            new NetworkUtils(internet -> {
                if(internet) {
                    fetchTests();
                } else {
                    Toast.makeText(getContext(), "No internet available.", Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                }
            });
        });
        fetchTests();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new LandingAnimator());
        recyclerView.setAdapter(new AlphaInAnimationAdapter(adapter));
        noStartedTestsTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        return rootView;
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

package com.imad.quickclassquiz.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.dataModel.Test;
import com.imad.quickclassquiz.recyclerview.TeacherStartedTestListAdapter;
import com.imad.quickclassquiz.utils.FilterTests;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartedTestsTeacherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartedTestsTeacherFragment extends Fragment {

    @BindView(R.id.startedTestListSwipeRefresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.startedTestListRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noStartedTestsTextView)
    TextView noStartedTestsTextView;

    TeacherStartedTestListAdapter adapter;
    FirebaseFirestore firestore;

    public StartedTestsTeacherFragment() {
        // Required empty public constructor
    }


    public static StartedTestsTeacherFragment newInstance() {
        StartedTestsTeacherFragment fragment = new StartedTestsTeacherFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TeacherStartedTestListAdapter(getContext());
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_started_tests_teacher, container, false);
        ButterKnife.bind(this, rootView);
        refreshLayout.setOnRefreshListener(() -> {
            fetchTests();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new LandingAnimator());
        recyclerView.setAdapter(new AlphaInAnimationAdapter(adapter));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchTests();
    }

    private void fetchTests() {
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
                        ArrayList<Test> filteredList = FilterTests.getStartedTestList(teacherTestList);
                        adapter.setListContent(filteredList);
                        Log.e("Test list", filteredList.toString());
                        if (filteredList.size() == 0) {
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

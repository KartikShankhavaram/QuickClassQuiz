package com.imad.quickclassquiz.fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.imad.quickclassquiz.recyclerview.TeacherUpcomingTestListAdapter;
import com.imad.quickclassquiz.utils.NetworkUtils;
import com.imad.quickclassquiz.utils.StaticValues;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpcomingTestsTeacherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpcomingTestsTeacherFragment extends Fragment {

    @BindView(R.id.upcomingTestListSwipeRefresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.upcomingTestListRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noUpcomingTestsTextView)
    TextView noUpcomingTestsTextView;

    TeacherUpcomingTestListAdapter adapter;
    FirebaseFirestore firestore;

    public UpcomingTestsTeacherFragment() {
        // Required empty public constructor
    }

    public static UpcomingTestsTeacherFragment newInstance() {
        UpcomingTestsTeacherFragment fragment = new UpcomingTestsTeacherFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TeacherUpcomingTestListAdapter(getContext());
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upcoming_tests_teacher, container, false);
        ButterKnife.bind(this, rootView);
        refreshLayout.setOnRefreshListener(() -> {
            new NetworkUtils(internet -> {
                if (internet) {
                    StaticValues.setShouldRefresh(true);
                    fetchTests();
                } else {
                    Toast.makeText(getContext(), "No internet available.", Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                }
            });
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new LandingAnimator());
        adapter.setOnTestVisibilityChangedListener(() -> {
            StaticValues.setShouldRefresh(true);
            fetchTests();
        });
        recyclerView.setAdapter(new AlphaInAnimationAdapter(adapter));
        noUpcomingTestsTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        new NetworkUtils(internet -> {
            if (internet) {
                fetchTests();
            } else {
                Toast.makeText(getContext(), "No internet available.", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });

    }

    public void fetchTests() {
        refreshLayout.setRefreshing(true);
        ArrayList<Test> teacherTestList = new ArrayList<>();
        adapter.setListContent(teacherTestList);
        CollectionReference testsCollection = firestore.collection("tests");
        testsCollection.whereEqualTo("accessCode", null)
                .whereEqualTo("masterCode", null)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            teacherTestList.add(documentSnapshot.toObject(Test.class));
                        }
                        adapter.setListContent(teacherTestList);
                        if (teacherTestList.size() == 0) {
                            noUpcomingTestsTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            noUpcomingTestsTextView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch tests.", Toast.LENGTH_SHORT).show();
                    }
                    refreshLayout.setRefreshing(false);
                });
    }

}

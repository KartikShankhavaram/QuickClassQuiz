package com.imad.quickclassquiz.fragments;


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
import com.imad.quickclassquiz.recyclerview.TeacherStartedTestListAdapter;
import com.imad.quickclassquiz.utils.NetworkUtils;
import com.imad.quickclassquiz.utils.StaticValues;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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

    private boolean _hasLoadedOnce = false;

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
            new NetworkUtils(internet -> {
               if(internet) {
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
        recyclerView.setAdapter(new AlphaInAnimationAdapter(adapter));
        noStartedTestsTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(_hasLoadedOnce)
            new NetworkUtils(internet -> {
                if(internet) {
                    fetchTests();
                } else {
                    Toast.makeText(getContext(), "No internet available.", Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                }
            });
    }

    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(true);

        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isFragmentVisible_ && !_hasLoadedOnce) {
                new NetworkUtils(internet -> {
                    if(internet) {
                        fetchTests();
                    } else {
                        Toast.makeText(getContext(), "No internet available.", Toast.LENGTH_SHORT).show();
                        refreshLayout.setRefreshing(false);
                    }
                });
                _hasLoadedOnce = true;
            }
        }
    }

    public void fetchTests() {
        refreshLayout.setRefreshing(true);
        ArrayList<Test> teacherTestList = new ArrayList<>();
        adapter.setListContent(teacherTestList);
        CollectionReference testsCollection = firestore.collection("tests");
        testsCollection.whereGreaterThan("accessCode", "")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            teacherTestList.add(documentSnapshot.toObject(Test.class));
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

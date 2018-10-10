package com.imad.quickclassquiz.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.datamodel.Test;
import com.imad.quickclassquiz.recyclerview.StudentAvailableTestAdapter;
import com.imad.quickclassquiz.utils.NetworkUtils;

import org.joda.time.DateTime;

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


public class AvailableTestFragment extends Fragment {



    @BindView(R.id.activeTestListRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.activeTestListSwipeRefresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.noActiveTestsTextView)
    TextView noActiveTestsTextView;

    FirebaseFirestore firestore;
    StudentAvailableTestAdapter adapter;

    public AvailableTestFragment() {
        // Required empty public constructor
    }


    public static AvailableTestFragment newInstance() {
        AvailableTestFragment fragment = new AvailableTestFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new StudentAvailableTestAdapter(getContext());
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_available_tests_student, container, false);
        ButterKnife.bind(this, rootView);


        noActiveTestsTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new LandingAnimator());
        recyclerView.setAdapter(new AlphaInAnimationAdapter(adapter));

        refreshLayout.setOnRefreshListener(() -> {
            new NetworkUtils(internet -> {
                if (internet) {

                    fetchAvailableTests();
                } else {
                    Toast.makeText(getContext(), "No internet available.", Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                }
            });
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        new NetworkUtils(internet -> {
            if (internet) {

                fetchAvailableTests();
            } else {
                Toast.makeText(getContext(), "No internet available.", Toast.LENGTH_SHORT).show();
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
                            if (test.getStartedAt() == null || new DateTime(test.getStartedAt()).plusMinutes(30).isAfter(new DateTime()))
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
                        Toast.makeText(getContext(), "Failed to fetch tests.", Toast.LENGTH_SHORT).show();
                    }
                    refreshLayout.setRefreshing(false);
                });
    }
}

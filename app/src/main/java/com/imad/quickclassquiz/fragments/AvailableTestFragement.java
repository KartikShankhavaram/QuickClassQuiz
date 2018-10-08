package com.imad.quickclassquiz.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class AvailableTestFragement extends Fragment {

    @BindView(R.id.studentTestListToolbar)
    Toolbar toolbar;

    @BindView(R.id.activeTestListRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.activeTestListSwipeRefresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.noActiveTestsTextView)
    TextView noActiveTestsTextView;



    FirebaseFirestore firestore;
    StudentTestListAdapter adapter;




    public AvailableTestFragement() {
        // Required empty public constructor
    }


    public static AvailableTestFragement newInstance() {
        AvailableTestFragement fragment = new AvailableTestFragement();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new StudentTestListAdapter(getContext());
        firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_available_test_fragement, container, false);
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

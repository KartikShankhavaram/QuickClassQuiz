package com.imad.quickclassquiz.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.customcomponents.SwipeButton;
import com.imad.quickclassquiz.datamodel.Question;
import com.imad.quickclassquiz.viewpageradapters.TestQuestionPagerAdapter;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestFragment extends Fragment {

    @BindView(R.id.testViewPager)
    ViewPager testViewPager;
    @BindView(R.id.swipeButton)
    SwipeButton submitButton;
    @BindView(R.id.questionNumberTextView)
    TextView questionNumberTextView;
    @BindView(R.id.attemptedQuestionCountTextView)
    TextView attemptedQuestionCountTextView;
    @BindView(R.id.timerTextView)
    TextView timertextView;

    ArrayList<Question> questions = new ArrayList<>();

    public TestFragment() {
        // Required empty public constructor
    }

    public static TestFragment newInstance(ArrayList<Question> list) {
        TestFragment fragment = new TestFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("questions", list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questions = getArguments().getParcelableArrayList("questions");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        ButterKnife.bind(this, view);

        TestQuestionPagerAdapter adapter = new TestQuestionPagerAdapter(getChildFragmentManager());
        adapter.setFragmentList(getFragmentList());
        testViewPager.setAdapter(adapter);
        questionNumberTextView.setText("1");
        testViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                questionNumberTextView.setText(String.format(Locale.ENGLISH, "%d", position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }

    private ArrayList<StudentQuestionFragment> getFragmentList() {
        ArrayList<StudentQuestionFragment> fragmentList = new ArrayList<>();
        int i = 1;
        for (Question question : questions) {
            fragmentList.add(StudentQuestionFragment.newInstance(question, i++));
        }
        return fragmentList;
    }


}

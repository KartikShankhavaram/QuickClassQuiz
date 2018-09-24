package com.imad.quickclassquiz.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.customcomponents.SwipeButton;
import com.imad.quickclassquiz.datamodel.AttemptedQuestionsMessage;
import com.imad.quickclassquiz.datamodel.Question;
import com.imad.quickclassquiz.viewpageradapters.TestQuestionPagerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

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
    TextView timerTextView;
    int timerColor = 0;
    int numberOfAttemptedQuestions = 0;

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

        setAttemptedText(String.format(Locale.ENGLISH, "%d / %d", numberOfAttemptedQuestions, questions.size()));
        new CountDownTimer(50000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                DateTime timeLeft = new DateTime(millisUntilFinished);
                DateTimeFormatter a = DateTimeFormat.forPattern("m:ss");
                timerTextView.setText(a.print(timeLeft));
                if (millisUntilFinished > 30000) {
                    timerTextView.setTextColor(getResources().getColor(R.color.colorNormalRemainingTime));
                } else {
                    if (timerColor == 0) {
                        timerColor = 1;
                        timerTextView.setTextColor(getResources().getColor(R.color.colorLessRemainingTime));
                    } else {
                        timerColor = 0;
                        timerTextView.setTextColor(getResources().getColor(android.R.color.black));
                    }
                }
            }

            @Override
            public void onFinish() {
                timerTextView.setText("0:00");
            }
        }.start();

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

        submitButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Implement submit functionality.", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private ArrayList<StudentQuestionFragment> getFragmentList() {
        ArrayList<StudentQuestionFragment> fragmentList = new ArrayList<>();
        int i = 1;
        for (Question question : questions) {
            fragmentList.add(StudentQuestionFragment.newInstance(question, i++));
        }
        return fragmentList;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuestionAttempt(AttemptedQuestionsMessage message) {
        if (message.wasQuestionAttempted()) {
            numberOfAttemptedQuestions++;
        } else {
            numberOfAttemptedQuestions--;
        }
        int totalQuestions = questions.size();
        setAttemptedText(String.format(Locale.ENGLISH, "%d / %d", numberOfAttemptedQuestions, totalQuestions));
    }

    public void setAttemptedText(String s) {
        int start = s.length() + 1;
        String total = s + "\nattempted";
        SpannableString ss = new SpannableString(total);
        ss.setSpan(new RelativeSizeSpan(0.5f), start, total.length(), SPAN_INCLUSIVE_INCLUSIVE);
        attemptedQuestionCountTextView.setText(ss);
    }

}

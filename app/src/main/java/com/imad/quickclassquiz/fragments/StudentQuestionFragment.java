package com.imad.quickclassquiz.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.datamodel.AttemptedQuestionsMessage;
import com.imad.quickclassquiz.datamodel.Question;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentQuestionFragment extends Fragment {

    @BindView(R.id.question)
    TextView questionTextView;
    @BindView(R.id.option1TextView)
    TextView option1TextView;
    @BindView(R.id.option2TextView)
    TextView option2TextView;
    @BindView(R.id.option3TextView)
    TextView option3TextView;
    @BindView(R.id.option4TextView)
    TextView option4TextView;
    @BindView(R.id.option1Radio)
    RadioButton option1RadioButton;
    @BindView(R.id.option2Radio)
    RadioButton option2RadioButton;
    @BindView(R.id.option3Radio)
    RadioButton option3RadioButton;
    @BindView(R.id.option4Radio)
    RadioButton option4RadioButton;
    @BindView(R.id.clearChoiceButton)
    Button clearChoiceButton;

    private Question question;
    private int questionNo;

    private boolean firstCheckDone = false;

    public StudentQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param question Question to display in fragment.
     * @return A new instance of fragment StudentQuestionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentQuestionFragment newInstance(Question question, int questionNo) {
        StudentQuestionFragment fragment = new StudentQuestionFragment();
        Bundle args = new Bundle();
        args.putParcelable("question", question);
        args.putInt("questionNo", questionNo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = getArguments().getParcelable("question");
            questionNo = getArguments().getInt("questionNo", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_question, container, false);
        ButterKnife.bind(this, view);

        questionTextView.setText(question.getQuestion());
        option1TextView.setText(question.getOption1());
        option2TextView.setText(question.getOption2());
        option3TextView.setText(question.getOption3());
        option4TextView.setText(question.getOption4());

        clearChoiceButton.setOnClickListener(v -> {
            clearChoices();
        });

        clearChoiceButton.setEnabled(false);

        option1RadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !firstCheckDone) {
                EventBus.getDefault().post(new AttemptedQuestionsMessage(true));
                firstCheckDone = true;
            }
            if (isChecked) {
                option2RadioButton.setChecked(false);
                option3RadioButton.setChecked(false);
                option4RadioButton.setChecked(false);
                clearChoiceButton.setEnabled(true);
            }
        });

        option2RadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !firstCheckDone) {
                EventBus.getDefault().post(new AttemptedQuestionsMessage(true));
                firstCheckDone = true;
            }
            if (isChecked) {
                option1RadioButton.setChecked(false);
                option3RadioButton.setChecked(false);
                option4RadioButton.setChecked(false);
                clearChoiceButton.setEnabled(true);
            }
        });

        option3RadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !firstCheckDone) {
                EventBus.getDefault().post(new AttemptedQuestionsMessage(true));
                firstCheckDone = true;
            }
            if (isChecked) {
                option1RadioButton.setChecked(false);
                option2RadioButton.setChecked(false);
                option4RadioButton.setChecked(false);
                clearChoiceButton.setEnabled(true);
            }
        });

        option4RadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !firstCheckDone) {
                EventBus.getDefault().post(new AttemptedQuestionsMessage(true));
                firstCheckDone = true;
            }
            if (isChecked) {
                option1RadioButton.setChecked(false);
                option2RadioButton.setChecked(false);
                option3RadioButton.setChecked(false);
                clearChoiceButton.setEnabled(true);
            }
        });

        return view;
    }

    private void clearChoices() {
        option1RadioButton.setChecked(false);
        option2RadioButton.setChecked(false);
        option3RadioButton.setChecked(false);
        option4RadioButton.setChecked(false);
        EventBus.getDefault().post(new AttemptedQuestionsMessage(false));
        firstCheckDone = false;
        clearChoiceButton.setEnabled(false);
    }


}

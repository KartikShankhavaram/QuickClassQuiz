package com.imad.quickclassquiz.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.activities.TestActivity;
import com.imad.quickclassquiz.datamodel.Test;

import org.joda.time.DateTime;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;


public class CodeEntryFragment extends Fragment {

    @BindView(R.id.accessCodeInputLayout)
    TextInputLayout accessCodeInputLayout;
    @BindView(R.id.accessCodeEditText)
    TextInputEditText accessCodeEditText;
    @BindView(R.id.accessTestButton)
    Button accessCodeButton;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.accessCodeTimerTextView)
    TextView accessCodeTimerTextView;

    FirebaseFirestore firestore;
    Test test;

    public CodeEntryFragment() {
        // Required empty public constructor
    }

    public static CodeEntryFragment newInstance(Test test) {
        CodeEntryFragment fragment = new CodeEntryFragment();
        Bundle args = new Bundle();
        args.putParcelable("test", test);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            test = getArguments().getParcelable("test");
        }
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_code_entry, container, false);
        ButterKnife.bind(this, view);

        accessCodeButton.setEnabled(false);
        progressBar.setVisibility(View.GONE);

        accessCodeInputLayout.setErrorEnabled(true);

        accessCodeButton.setOnClickListener(v -> {
            String enteredCode = accessCodeEditText.getText().toString();
            if (enteredCode.length() != 6) {
                accessCodeInputLayout.setError("Invalid number of characters.");
            } else {
                verifyCode(enteredCode);
            }
        });

        accessCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                accessCodeButton.setEnabled(s.length() == 6);
                accessCodeInputLayout.setError(null);
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void verifyCode(String enteredCode) {
        int PASSWORD_WRONG = 0;
        int PASSWORD_CORRECT = 1;
        int TIME_EXPIRED = 2;
        setLoadingState(true);
        String testUrl = "tests/" + test.getTestId();
        String attemptUrl = "tests/" + test.getTestId() + "/attempts/" + GoogleSignIn.getLastSignedInAccount(getContext()).getId();
        DocumentReference testRef = firestore.document(testUrl);
        DocumentReference attemptRef = firestore.document(attemptUrl);
        Log.e("attemptUrl", attemptUrl);
        firestore.runTransaction(transaction -> {
            DocumentSnapshot testDetails = transaction.get(testRef);
            Test test = testDetails.toObject(Test.class);
            String accessCode = test.getAccessCode();
            transaction.update(testRef, "accessCode", accessCode);
            DateTime startedAt = new DateTime(test.getStartedAt());
            if (startedAt.plusMinutes(3).isBeforeNow()) {
                return TIME_EXPIRED;
            }
            if (TextUtils.equals(enteredCode, accessCode)) {
                transaction.update(attemptRef, "started", true);
                return PASSWORD_CORRECT;
            } else {
                return PASSWORD_WRONG;
            }
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() == TIME_EXPIRED) {
                    Toast.makeText(getContext(), "Time to enter test has expired.", Toast.LENGTH_SHORT).show();
                    ((TestActivity) getContext()).finish();
                } else if (task.getResult() == PASSWORD_CORRECT) {
                    Toast.makeText(getContext(), "Access Code validated!", Toast.LENGTH_SHORT).show();
                    ((TestActivity) getContext()).switchFragment(TestActivity.TEST_FRAGMENT);
                } else {
                    Toast.makeText(getContext(), "Incorrect Code. Please try again.", Toast.LENGTH_SHORT).show();
                    accessCodeInputLayout.setError("Incorrect code");
                }
            } else {
                Toast.makeText(getContext(), "Could not validate code. Try again.", Toast.LENGTH_SHORT).show();
                task.getException().printStackTrace();
            }
            setLoadingState(false);
        });
    }

    private void setLoadingState(boolean state) {
        if (state) {
            accessCodeInputLayout.setEnabled(false);
            accessCodeButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            accessCodeInputLayout.setEnabled(true);
            accessCodeButton.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }
}

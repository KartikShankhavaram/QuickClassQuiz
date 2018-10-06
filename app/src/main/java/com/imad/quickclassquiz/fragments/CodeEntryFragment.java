package com.imad.quickclassquiz.fragments;

import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.activities.TestActivity;
import com.imad.quickclassquiz.datamodel.Test;

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

    private void verifyCode(String enteredCode) {
//        if (test.getAccessCode() == null) {
            setLoadingState(true);
            String url = "tests/" + test.getTestId();
            firestore.document(url)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Test test = task.getResult().toObject(Test.class);
                            String accessCode = test.getAccessCode();
                            if (TextUtils.equals(enteredCode, accessCode)) {
                                Toast.makeText(getContext(), "Access Code validated!", Toast.LENGTH_SHORT).show();
                                ((TestActivity)getContext()).switchFragment(TestActivity.TEST_FRAGMENT);
                            } else {
                                Toast.makeText(getContext(), "Incorrect Code. Please try again.", Toast.LENGTH_SHORT).show();
                                accessCodeInputLayout.setError("Incorrect code");
                            }
                        } else {
                            Toast.makeText(getContext(), "Could not validate code. Try again.", Toast.LENGTH_SHORT).show();
                        }
                        setLoadingState(false);
                    });
//        }
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

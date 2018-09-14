package com.imad.quickclassquiz.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.dataModel.Test;
import com.imad.quickclassquiz.utils.TimestampUtils;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddTestActivity extends AppCompatActivity {

    @BindView(R.id.testNameEditText)
    TextInputEditText testNameEditText;
    @BindView(R.id.testDescEditText)
    TextInputEditText testDescEditText;
    @BindView(R.id.submitTestInfoButton)
    Button submitTestInfoButton;
    @BindView(R.id.cancel)
    Button cancelButton;
    @BindView(R.id.testNameInputLayout)
    TextInputLayout testNameInputLayout;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setFinishOnTouchOutside(false);

        setContentView(R.layout.activity_add_test);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        setTitle("Create a test");

        ButterKnife.bind(this);

        firestore = FirebaseFirestore.getInstance();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Test");

        CollectionReference testsCollection = firestore.collection("tests");

        submitTestInfoButton.setEnabled(false);

        cancelButton.setOnClickListener(v -> {
            finish();
        });

        testNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                submitTestInfoButton.setEnabled(!TextUtils.isEmpty(s.toString().trim()));
            }
        });

        submitTestInfoButton.setOnClickListener(v -> {
            String testName = testNameEditText.getText().toString().trim();
            String testDesc = testDescEditText.getText().toString().trim();
            if (TextUtils.isEmpty(testName))
                testNameInputLayout.setError("This field is mandatory.");
            else {
                String timeStamp = TimestampUtils.getISO8601StringForCurrentDate();
                String testId = UUID.randomUUID().toString();
                Test test = new Test(testId, testName, testDesc, timeStamp);
                progressDialog.setMessage("Please wait while we create your test...");
                progressDialog.show();
                hideKeyboard();
                testsCollection.document(testId).set(test).addOnCompleteListener(ref -> {
                    if (ref.isSuccessful()) {
                        Log.e("Test added", String.format("New Test with name -> %s and ref -> %s", testName, testId));
                        Toast.makeText(this, "Test created successfully!", Toast.LENGTH_SHORT).show();
                        Intent toQuestionList = new Intent(AddTestActivity.this, QuestionListActivity.class);
                        toQuestionList.putExtra("test", test);
                        progressDialog.dismiss();
                        startActivity(toQuestionList);
                        finish();
                    } else {
                        Log.e("Test add failed", "FAILED with error -> " + ref.getException().getMessage());
                        Toast.makeText(this, "Test creation failed. Please try again.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void hideKeyboard() {
        Activity activity = this;
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

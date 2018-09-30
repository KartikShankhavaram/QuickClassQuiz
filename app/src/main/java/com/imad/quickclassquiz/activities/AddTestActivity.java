package com.imad.quickclassquiz.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.datamodel.Test;
import com.imad.quickclassquiz.utils.KeyboardUtils;
import com.imad.quickclassquiz.utils.NetworkUtils;
import com.imad.quickclassquiz.utils.StaticValues;
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
        firestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build());

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
                new NetworkUtils(internet -> {
                    if(internet) {
                        String timeStamp = TimestampUtils.getISO8601StringForCurrentDate();
                        String testId = UUID.randomUUID().toString();
                        Test test = new Test(testId, testName, testDesc, timeStamp);
                        progressDialog.setMessage("Please wait while we create your test...");
                        progressDialog.show();
                        KeyboardUtils.hideKeyboard(AddTestActivity.this);
                        testsCollection.document(testId).set(test).addOnCompleteListener(ref -> {
                            if (ref.isSuccessful()) {
                                Log.e("Test added", String.format("New Test with name -> %s and ref -> %s", testName, testId));
                                Toast.makeText(this, "Test created successfully!", Toast.LENGTH_SHORT).show();
                                Intent toQuestionList = new Intent(AddTestActivity.this, QuestionListActivity.class);
                                toQuestionList.putExtra("test", test);
                                toQuestionList.putExtra("started", false);
                                StaticValues.setCurrentTest(test);
                                progressDialog.dismiss();
                                startActivity(toQuestionList);
                                finish();
                            } else {
                                Log.e("Test add failed", "FAILED with error -> " + ref.getException().getMessage());
                                Toast.makeText(this, "Test creation failed. Please try again.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    } else {
                        Toast.makeText(this, "No internet available.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}

package com.imad.quickclassquiz.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateTestActivity extends AppCompatActivity {

    @BindView(R.id.testNameEditText)
    TextInputEditText testNameEditText;
    @BindView(R.id.testDescEditText)
    TextInputEditText testDescEditText;
    @BindView(R.id.updateTestInfoButton)
    Button updateTestInfoButton;
    @BindView(R.id.cancel)
    Button cancelButton;

    FirebaseFirestore firestore;

    Test test;
    boolean nameChanged = false;
    boolean descChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_update_test);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setTitle("Update test details");

        ButterKnife.bind(this);

        firestore = FirebaseFirestore.getInstance();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Updating Test");

        CollectionReference testsCollection = firestore.collection("tests");

        Intent intent = getIntent();
        if (intent != null && (test = intent.getParcelableExtra("test")) != null) {
            testNameEditText.setText(test.getTestName());
            testDescEditText.setText(test.getTestDesc());
            testNameEditText.requestFocus();
        }

        updateTestInfoButton.setEnabled(false);

        testNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                nameChanged = !s.toString().trim().equals(test.getTestName().trim());
                updateTestInfoButton.setEnabled(!TextUtils.isEmpty(s.toString().trim()) && (nameChanged || descChanged));
            }
        });

        testDescEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                descChanged = !s.toString().trim().equals(test.getTestDesc().trim());
                updateTestInfoButton.setEnabled(nameChanged || descChanged);
            }
        });

        cancelButton.setOnClickListener(v -> {
            finish();
        });

        updateTestInfoButton.setOnClickListener(v -> {
            new NetworkUtils(internet -> {
                if(internet) {
                    String testName = testNameEditText.getText().toString().trim();
                    String testDesc = testDescEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(testName))
                        testNameEditText.setError("This field is mandatory.");
                    else {
                        progressDialog.setMessage("Please wait while we update the details...");
                        progressDialog.show();
                        test.setTestName(testName);
                        test.setTestDesc(testDesc);
                        KeyboardUtils.hideKeyboard(UpdateTestActivity.this);
                        testsCollection.document(test.getTestId()).set(test).addOnCompleteListener(ref -> {
                            if (ref.isSuccessful()) {
                                Log.e("Test updated", String.format("Test with name -> %s and ref -> %s updated!", testName, test.getTestId()));
                                Toast.makeText(this, "Test updated successfully!", Toast.LENGTH_SHORT).show();
                                StaticValues.setCurrentTest(test);
                                progressDialog.dismiss();
                                finish();
                            } else {
                                Log.e("Test update failed", "FAILED with error -> " + ref.getException().getMessage());
                                Toast.makeText(this, "Test updation failed. Please try again.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "No internet available.", Toast.LENGTH_SHORT).show();
                }
            });

        });
    }
}

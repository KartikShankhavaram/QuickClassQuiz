package com.imad.quickclassquiz.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TimeUtils;
import android.widget.Button;

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

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);

        ButterKnife.bind(this);

        firestore = FirebaseFirestore.getInstance();

        CollectionReference testsCollection = firestore.collection("tests");

        submitTestInfoButton.setOnClickListener(v -> {
            String testName = testNameEditText.getText().toString().trim();
            String testDesc = testDescEditText.getText().toString().trim();
            String timeStamp = TimestampUtils.getISO8601StringForCurrentDate();
            String testId = UUID.randomUUID().toString();
            Test test = new Test(testId, testName, testDesc, timeStamp);
            testsCollection.add(test).addOnCompleteListener(ref -> {
                if (ref.isSuccessful())
                    Log.e("Test added", String.format("New Test with name -> %s and ref -> %s", testName, ref.getResult().getId()));
                else
                    Log.e("Test add failed", "FAILED with error -> " + ref.getException().getMessage());
            });
        });

    }
}

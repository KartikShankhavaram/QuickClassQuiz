package com.imad.quickclassquiz.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.dataModel.Question;
import com.imad.quickclassquiz.dataModel.Test;
import com.imad.quickclassquiz.utils.KeyboardUtils;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddQuestionActivity extends AppCompatActivity {

    @BindView(R.id.questionEditText)
    EditText title;

    @BindView(R.id.option1EditText)
    EditText option1EditText;

    @BindView(R.id.option2EditText)
    EditText option2EditText;

    @BindView(R.id.option3EditText)
    EditText option3EditText;

    @BindView(R.id.option4EditText)
    EditText option4EditText;

    @BindView(R.id.option1Radio)
    RadioButton option1radio;

    @BindView(R.id.option2Radio)
    RadioButton option2radio;

    @BindView(R.id.option3Radio)
    RadioButton option3radio;

    @BindView(R.id.option4Radio)
    RadioButton option4radio;

    @BindView(R.id.saveQuestion)
    FloatingActionButton saveQuestion;

    @BindView(R.id.addQuestionToolbar)
    Toolbar toolbar;

    @BindView(R.id.option1InputLayout)
    TextInputLayout inputLayout1;

    @BindView(R.id.option2InputLayout)
    TextInputLayout inputLayout2;

    @BindView(R.id.option3InputLayout)
    TextInputLayout inputLayout3;

    @BindView(R.id.option4InputLayout)
    TextInputLayout inputLayout4;

    String correctAnswer = "";
    ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    String url = "";
    String testId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        ButterKnife.bind(this);

        firestore = FirebaseFirestore.getInstance();

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add a question");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        Test test;
        if (intent != null && (test = intent.getParcelableExtra("test")) != null) {
            url = String.format("tests/%s/questions", test.getTestId());
            testId = test.getTestId();
        }

        progressDialog = new ProgressDialog(this);

        saveQuestion.setOnClickListener(v -> saveToDatabase());
    }

    private void saveToDatabase() {
        if(title.getText().length() == 0){
            title.requestFocus();
            title.setError("Can't be empty");
            return;
        }
        if(option1EditText.getText().length() == 0){
            option1EditText.requestFocus();
            inputLayout1.setError("Can't be empty");
            return;
        }
        if(option2EditText.getText().length() == 0){
            option2EditText.requestFocus();
            inputLayout2.setError("Can't be empty");
            return;
        }
        if(option3EditText.getText().length() == 0){
            option3EditText.requestFocus();
            inputLayout3.setError("Can't be empty");
            return;
        }
        if(option4EditText.getText().length() == 0){
            option4EditText.requestFocus();
            inputLayout4.setError("Can't be empty");
            return;
        }

        String question = title.getText().toString().trim();
        String option1 = option1EditText.getText().toString().trim();
        String option2 = option2EditText.getText().toString().trim();
        String option3 = option3EditText.getText().toString().trim();
        String option4 = option4EditText.getText().toString().trim();

        if (option1radio.isChecked()) {
            correctAnswer = option1;
        } else if (option2radio.isChecked()) {
            correctAnswer = option2;
        } else if (option3radio.isChecked()) {
            correctAnswer = option3;
        } else if (option4radio.isChecked()) {
            correctAnswer = option4;
        }

        if (correctAnswer.length() == 0) {
            Toast.makeText(this, "One Option Must Be Selected", Toast.LENGTH_SHORT).show();
            return;
        }

        KeyboardUtils.hideKeyboard(this);

        progressDialog.setTitle("Save Question");
        progressDialog.setMessage("Please wait while the question is added to the test...");
        progressDialog.show();

        Question questionObj = new Question(testId, UUID.randomUUID().toString(), question, option1, option2, option3, option4, correctAnswer);
        firestore.collection(url)
                .document(questionObj.getQuestionId())
                .set(questionObj)
                .addOnCompleteListener(res -> {
                    if (res.isSuccessful()) {
                        Toast.makeText(this, "Question added successfully to the test.", Toast.LENGTH_SHORT).show();
                        clearInput();
                    } else {
                        Toast.makeText(this, "Could not add the question to the test. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                });
    }

    private void clearInput() {
        title.setText("");
        option1EditText.setText("");
        option2EditText.setText("");
        option3EditText.setText("");
        option4EditText.setText("");
        if (option1radio.isChecked()) {
            option1radio.setChecked(false);
        } else if (option2radio.isChecked()) {
            option2radio.setChecked(false);
        } else if (option3radio.isChecked()) {
            option3radio.setChecked(false);
        } else {
            option4radio.setChecked(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

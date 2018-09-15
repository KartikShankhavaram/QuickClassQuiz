package com.imad.quickclassquiz.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.dataModel.Question;
import com.imad.quickclassquiz.utils.KeyboardUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditQuestionActivity extends AppCompatActivity {

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

    Question question;
    ProgressDialog progressDialog;
    String testId;
    String questionId;
    FirebaseFirestore firestore;
    String url = "";
    private String correctAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Update a question");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build());
        Intent intent = getIntent();
        if (intent != null) {
            question = intent.getParcelableExtra("Question");
            testId = question.getTestId();
            questionId = question.getQuestionId();
            url = String.format("tests/%s/questions", testId);
            title.setText(question.getQuestion());
            option1EditText.setText(question.getOption1());
            option2EditText.setText(question.getOption2());
            option3EditText.setText(question.getOption3());
            option4EditText.setText(question.getOption4());
            if (question.getOption1().equals(question.getCorrectOption())) {
                option1radio.setChecked(true);
            } else if (question.getOption2().equals(question.getCorrectOption())) {
                option2radio.setChecked(true);
            } else if (question.getOption3().equals(question.getCorrectOption())) {
                option3radio.setChecked(true);
            } else {
                option4radio.setChecked(true);
            }

        }
        progressDialog = new ProgressDialog(this);

        saveQuestion.setOnClickListener(v -> updateQuestion());
    }

    private void updateQuestion() {
        if (title.getText().length() == 0) {
            title.requestFocus();
            title.setError("Can't be empty");
            return;
        }
        if (option1EditText.getText().length() == 0) {
            option1EditText.requestFocus();
            inputLayout1.setError("Can't be empty");
            return;
        }
        if (option2EditText.getText().length() == 0) {
            option2EditText.requestFocus();
            inputLayout2.setError("Can't be empty");
            return;
        }
        if (option3EditText.getText().length() == 0) {
            option3EditText.requestFocus();
            inputLayout3.setError("Can't be empty");
            return;
        }
        if (option4EditText.getText().length() == 0) {
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

        progressDialog.setTitle("Update Question");
        progressDialog.setMessage("Please wait while the question is updated...");

        Question questionObj = new Question(testId, questionId, question, option1, option2, option3, option4, correctAnswer);
        if (!this.question.equals(questionObj)) {
            progressDialog.show();
            firestore.collection(url)
                    .document(questionId)
                    .set(questionObj)
                    .addOnCompleteListener(res -> {
                        if (res.isSuccessful()) {
                            Toast.makeText(this, "Question updated successfully.", Toast.LENGTH_SHORT).show();
                            this.onBackPressed();
                        } else {
                            Toast.makeText(this, "Could not update the question.. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    });
        } else {
            Toast.makeText(this, "Question updated successfully.", Toast.LENGTH_SHORT).show();
            this.onBackPressed();
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

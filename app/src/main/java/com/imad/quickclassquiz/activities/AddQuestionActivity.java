package com.imad.quickclassquiz.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.dataModel.Question;

public class AddQuestionActivity extends AppCompatActivity {

    private static String TAG;
    FirebaseFirestore firestore;
    CollectionReference testsCollection;
    private EditText title;
    private EditText option1;
    private EditText option2;
    private EditText option3;
    private EditText option4;
    private RadioButton option1radio;
    private RadioButton option2radio;
    private RadioButton option3radio;
    private RadioButton option4radio;
    private FloatingActionButton saveQuestion;
    private String correctAnswer = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        TAG = getPackageName();
        firestore = FirebaseFirestore.getInstance();
        testsCollection = firestore.collection("tests");
        title = findViewById(R.id.testNameTextView);
        saveQuestion = findViewById(R.id.saveQuestion);
        option1 = findViewById(R.id.option1EditText);
        option2 = findViewById(R.id.option2EditText);
        option3 = findViewById(R.id.option3EditText);
        option4 = findViewById(R.id.option4EditText);
        option1radio = findViewById(R.id.option1Radio);
        option2radio = findViewById(R.id.option2Radio);
        option3radio = findViewById(R.id.option3Radio);
        option4radio = findViewById(R.id.option4Radio);

        saveQuestion.setOnClickListener(v -> saveToDatabase());
    }

    private void saveToDatabase() {
        if (title.getText().length() == 0) {
            title.requestFocus();
            title.setError("Can't be empty");
            return;
        }
        if (option1.getText().length() == 0) {
            option1.requestFocus();
            option1.setError("Can't be empty");
            return;
        }
        if (option2.getText().length() == 0) {
            option2.requestFocus();
            option2.setError("Can't be empty");
            return;
        }
        if (option3.getText().length() == 0) {
            option3.requestFocus();
            option3.setError("Can't be empty");
            return;
        }
        if (option4.getText().length() == 0) {
            option4.requestFocus();
            option4.setError("Can't be empty");
            return;
        }
        String question = title.getText().toString().trim();
        String ooption1 = option1.getText().toString().trim();
        String ooption2 = option2.getText().toString().trim();
        String ooption3 = option3.getText().toString().trim();
        String ooption4 = option4.getText().toString().trim();

        if (option1radio.isChecked()) {
            correctAnswer = ooption1;
        } else if (option2radio.isChecked()) {
            correctAnswer = ooption2;
        } else if (option3radio.isChecked()) {
            correctAnswer = ooption3;
        }
        if (option4radio.isChecked()) {
            correctAnswer = ooption4;
        }

        if (correctAnswer.length() == 0) {
            Toast.makeText(this, "One Option Must Be Selected", Toast.LENGTH_SHORT).show();
            return;
        }

        Question question1 = new Question(1, question, ooption1, ooption2, ooption3, ooption4, correctAnswer);
        testsCollection.document("Question")
                .set(question1)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    title.setText("");
                    option1.setText("");
                    option2.setText("");
                    option3.setText("");
                    option4.setText("");
                    if (option1radio.isChecked()) {
                        option1radio.setChecked(false);
                    } else if (option2radio.isChecked()) {
                        option2radio.setChecked(false);
                    } else if (option3radio.isChecked()) {
                        option3radio.setChecked(false);
                    } else {
                        option4radio.setChecked(false);
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }
}

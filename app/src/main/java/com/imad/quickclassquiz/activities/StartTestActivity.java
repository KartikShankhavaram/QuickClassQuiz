package com.imad.quickclassquiz.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.datamodel.Test;
import com.imad.quickclassquiz.utils.NetworkUtils;
import com.imad.quickclassquiz.utils.RandomCodeGenerator;
import com.imad.quickclassquiz.utils.TimestampUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartTestActivity extends AppCompatActivity {

    @BindView(R.id.testNameTextView)
    TextView testNameTextView;

    @BindView(R.id.testDescTextView)
    TextView testDescTextView;

    @BindView(R.id.accessCodeTextView)
    TextView accessCodeTextView;

    @BindView(R.id.masterCodetextView)
    TextView masterCodeTextView;

    @BindView(R.id.startedAtTextView)
    TextView startedAtTextView;

    @BindView(R.id.deleteCodesButton)
    Button deleteCodesButton;

    FirebaseFirestore firestore;
    Test test;
    String url;
    String finalUrl;
    Boolean generated = false;
    String accessCode;
    String masterCode;
    String time;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_start_test);

        ButterKnife.bind(this);

        firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build());

        Intent intent = getIntent();
        if (intent != null && (test = intent.getParcelableExtra("test")) != null) {
            testNameTextView.setText(test.getTestName());
            testDescTextView.setText(test.getTestDesc());
            url = String.format("tests/%s", test.getTestId());
            generated = intent.getBooleanExtra("generated", false);
        }

        deleteCodesButton.setOnClickListener(v -> {
            new NetworkUtils(internet -> {
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("Delete codes");
                dialog.setMessage("Please wait while the codes are deleted...");
                if (internet) {
                    dialog.show();
                    DocumentReference testRef = firestore.document("tests/" + test.getTestId());
                    firestore.runTransaction(transaction -> {
                        transaction.update(testRef, "accessCode", null);
                        transaction.update(testRef, "masterCode", null);
                        transaction.update(testRef, "startedAt", null);
                        return null;
                    }).addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            this.onBackPressed();
                            Toast.makeText(this, "Codes successfully deleted.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Could not delete codes.", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    });
                } else {
                    Toast.makeText(this, "No internet available.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        if (generated) {
            accessCodeTextView.setText(test.getAccessCode());
            masterCodeTextView.setText(test.getMasterCode());
            String startedAt = test.getStartedAt();

            DateTime dt = new DateTime(startedAt);
            DateTimeFormatter format = DateTimeFormat.forPattern("'Started on 'MMM d' at 'h:mm a");
            String time = format.print(dt);
            startedAtTextView.setText(time);
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Generating codes");
            progressDialog.setMessage("Please wait while the access and master codes are generated...");

            progressDialog.show();

            accessCode = RandomCodeGenerator.getRandomCode(6);
            masterCode = RandomCodeGenerator.getRandomCode(6);

            test.setAccessCode(accessCode);
            test.setMasterCode(masterCode);
            String startedAt = TimestampUtils.getISO8601StringForCurrentDate();
            test.setStartedAt(startedAt);

            DateTime dt = new DateTime(startedAt);
            DateTimeFormatter format = DateTimeFormat.forPattern("'Started on 'MMM d' at 'h:mm a");
            time = format.print(dt);

            new NetworkUtils(internet -> {
                if (internet) {
                    firestore.document(url)
                            .set(test)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Codes generated successfully!", Toast.LENGTH_SHORT).show();
                                    accessCodeTextView.setText(accessCode);
                                    masterCodeTextView.setText(masterCode);
                                    startedAtTextView.setText(time);
                                    progressDialog.dismiss();
                                } else {
                                    Toast.makeText(this, "Error in generating codes.", Toast.LENGTH_SHORT).show();
                                    accessCodeTextView.setText("Error!");
                                    masterCodeTextView.setText("Error!");
                                    startedAtTextView.setText("Could not start test");
                                    progressDialog.dismiss();
                                }
                            });
                } else {
                    Toast.makeText(this, "No internet available.", Toast.LENGTH_SHORT).show();
                    accessCodeTextView.setText("Error!");
                    masterCodeTextView.setText("Error!");
                    startedAtTextView.setText("Could not start test");
                    progressDialog.dismiss();
                }
            });

        }
    }
}

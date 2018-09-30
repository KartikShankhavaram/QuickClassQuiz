package com.imad.quickclassquiz.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.datamodel.Test;
import com.imad.quickclassquiz.utils.NetworkUtils;
import com.imad.quickclassquiz.utils.RandomCodeGenerator;
import com.imad.quickclassquiz.utils.TimestampUtils;

import net.frakbot.jumpingbeans.JumpingBeans;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class TeacherStartTestActivity extends AppCompatActivity {

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

    @BindView(R.id.noOfSubmissionsTextView)
    TextView noOfSubmissionsTextView;

    FirebaseFirestore firestore;
    Test test;
    String url;
    String finalUrl;
    Boolean generated = false;
    String accessCode;
    String masterCode;
    String time;
    ProgressDialog progressDialog;
    JumpingBeans jumpingBeans;
    boolean fetchingData = false;
    int submissionCount = 0;
    ArrayList<String> scoresReference = new ArrayList<>();

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
            if (submissionCount != 0) {
                String count = Integer.toString(submissionCount);
                String message = String.format("%s student%s already submitted. Do you want to delete the codes and clear all the submissions?", count, submissionCount == 1 ? " has" : "s have");
                SpannableStringBuilder ss = new SpannableStringBuilder(message);
                ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, count.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                new AlertDialog.Builder(this)
                        .setTitle("Delete Codes")
                        .setMessage(message)
                        .setPositiveButton("Delete", (dialogInterface, i) -> {
                            deleteCodes(true);
                        })
                        .setNegativeButton("Cancel", (dialogInterface, i) -> {
                        })
                        .show();
            } else {
                deleteCodes(false);
            }
        });

        noOfSubmissionsTextView.setOnClickListener(v -> {
            if (!fetchingData)
                new NetworkUtils(internet -> {
                    if (internet) {
                        showNumberOfSubmissions();
                    } else {
                        noOfSubmissionsTextView.setText("No internet available. Click here to try again.");
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
            progressDialog.setCanceledOnTouchOutside(false);
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

    @Override
    protected void onStart() {
        super.onStart();
        new NetworkUtils(internet -> {
            if (internet) {
                showNumberOfSubmissions();
            } else {
                noOfSubmissionsTextView.setText("No internet available. Click here to try again.");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (jumpingBeans != null)
            jumpingBeans.stopJumping();
    }

    private void showNumberOfSubmissions() {
        CollectionReference ref = firestore.collection(String.format(Locale.ENGLISH, "tests/%s/scores", test.getTestId()));
        noOfSubmissionsTextView.setText("Fetching number of submissions");
        jumpingBeans = JumpingBeans.with(noOfSubmissionsTextView).appendJumpingDots().build();
        fetchingData = true;
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                scoresReference = new ArrayList<>();
                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                    scoresReference.add(snapshot.getId());
                }
                submissionCount = task.getResult().size();
                String count = Integer.toString(submissionCount);
                String countString = count + " submissions till now.";
                SpannableStringBuilder ss = new SpannableStringBuilder(countString);
                ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, count.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                ss.setSpan(new RelativeSizeSpan(1.5f), 0, count.length(), SPAN_INCLUSIVE_INCLUSIVE);
                noOfSubmissionsTextView.setText(ss);
            } else {
                noOfSubmissionsTextView.setText("Could not fetch number of submissions. Click here to try again.");
            }
            fetchingData = false;
            jumpingBeans.stopJumping();
        });
    }

    private void deleteCodes(boolean deleteScores) {
        new NetworkUtils(internet -> {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Delete codes");
            dialog.setMessage("Please wait while the codes are deleted...");
            dialog.setCanceledOnTouchOutside(false);
            if (internet) {
                dialog.show();
                String testRefString = "tests/" + test.getTestId();
                DocumentReference testRef = firestore.document(testRefString);
                firestore.runTransaction(transaction -> {
                    transaction.update(testRef, "accessCode", null);
                    transaction.update(testRef, "masterCode", null);
                    transaction.update(testRef, "startedAt", null);
                    if (deleteScores) {
                        for (String scoreRefString : scoresReference) {
                            DocumentReference scoreRef = firestore.document(testRefString + "/scores/" + scoreRefString);
                            transaction.delete(scoreRef);
                        }
                    }
                    return null;
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
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
    }
}

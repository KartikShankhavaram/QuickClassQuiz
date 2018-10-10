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
import org.joda.time.DateTimeZone;
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

    @BindView(R.id.noOfAttemptStartedTextView)
    TextView noOfAttemptStartedTextView;

    FirebaseFirestore firestore;
    Test test;
    String url;
    Boolean generated = false;
    String accessCode;
    String masterCode;
    String time;
    ProgressDialog progressDialog;
    JumpingBeans jumpingBeans[] = new JumpingBeans[2];
    boolean fetchingSubmissionData = false;
    boolean fetchingAttemptData = false;
    int submissionCount = 0;
    int attemptCount = 0;
    ArrayList<String> scoresReference = new ArrayList<>();
    ArrayList<String> attemptsReference = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_start_test);

        ButterKnife.bind(this);

        firestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        if (intent != null && (test = intent.getParcelableExtra("test")) != null) {
            testNameTextView.setText(test.getTestName());
            testDescTextView.setText(test.getTestDesc());
            url = String.format("tests/%s", test.getTestId());
            generated = intent.getBooleanExtra("generated", false);
        }

        if(new DateTime(test.getStartedAt()).plusMinutes(30).isBeforeNow()) {
            deleteCodesButton.setEnabled(false);
        }

        deleteCodesButton.setOnClickListener(v -> {
            if (submissionCount != 0 || attemptCount != 0) {
                String sCount = Integer.toString(submissionCount);
                String aCount = Integer.toString(attemptCount);
                String message = String.format("%s student%s already submitted and %s student%s started the test. Do you want to delete the codes and clear all the submissions?", sCount, submissionCount == 1 ? " has" : "s have", aCount, attemptCount == 1 ? " has" : "s have");
                SpannableStringBuilder ss = new SpannableStringBuilder(message);
                ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, sCount.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), message.indexOf("and") + 4, message.indexOf("and") + 4 + aCount.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                new AlertDialog.Builder(this)
                        .setTitle("Delete Codes")
                        .setMessage(ss)
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
            if (!fetchingSubmissionData)
                new NetworkUtils(internet -> {
                    if (internet) {
                        showNumberOfSubmissions();
                    } else {
                        noOfSubmissionsTextView.setText("No internet available. Click here to try again.");
                    }
                });
        });

        noOfAttemptStartedTextView.setOnClickListener(v -> {
            if (!fetchingAttemptData)
                new NetworkUtils(internet -> {
                    if (internet) {
                        showNoOfAttempts();
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
            DateTimeFormatter format = DateTimeFormat.forPattern("'Started on 'MMM d' at 'h:mm a").withZone(DateTimeZone.forID("Asia/Kolkata"));
            String time = format.print(dt);
            startedAtTextView.setText(time);
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setTitle("Generating codes");
            progressDialog.setMessage("Please wait while the access and master codes are generated...");

            progressDialog.show();

            accessCode = RandomCodeGenerator.getRandomCode(6, test.getTestId());
            masterCode = RandomCodeGenerator.getRandomCode(6, test.getTestId());

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
                showNoOfAttempts();
            } else {
                noOfSubmissionsTextView.setText("No internet available. Click here to try again.");
                noOfAttemptStartedTextView.setText("No internet available. Click here to try again.");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (JumpingBeans beans : jumpingBeans) {
            if (beans != null) {
                beans.stopJumping();
            }
        }
    }

    private void showNumberOfSubmissions() {
        CollectionReference ref = firestore.collection(String.format(Locale.ENGLISH, "tests/%s/scores", test.getTestId()));
        noOfSubmissionsTextView.setText("Fetching number of submissions ");
        jumpingBeans[0] = JumpingBeans.with(noOfSubmissionsTextView).appendJumpingDots().build();
        fetchingSubmissionData = true;
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                scoresReference = new ArrayList<>();
                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                    scoresReference.add(snapshot.getId());
                }
                submissionCount = task.getResult().size();
                String count = Integer.toString(submissionCount);
                String countString = count + " submission" + (submissionCount == 1 ? "" : "s") + " till now.";
                SpannableStringBuilder ss = new SpannableStringBuilder(countString);
                ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, count.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                ss.setSpan(new RelativeSizeSpan(1.5f), 0, count.length(), SPAN_INCLUSIVE_INCLUSIVE);
                noOfSubmissionsTextView.setText(ss);
            } else {
                noOfSubmissionsTextView.setText("Could not fetch number of submissions. Click here to try again.");
            }
            fetchingSubmissionData = false;
            jumpingBeans[0].stopJumping();
        });
    }

    private void showNoOfAttempts() {
        CollectionReference ref = firestore.collection(String.format(Locale.ENGLISH, "tests/%s/attempts", test.getTestId()));
        noOfAttemptStartedTextView.setText("Fetching number of students who have started test ");
        jumpingBeans[1] = JumpingBeans.with(noOfAttemptStartedTextView).appendJumpingDots().build();
        fetchingAttemptData = true;
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                attemptsReference = new ArrayList<>();
                attemptCount = 0;
                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                    attemptsReference.add(snapshot.getId());
                    if((boolean) snapshot.get("started")) {
                        attemptCount++;
                    }
                }
                String count = Integer.toString(attemptCount);
                String countString = String.format(Locale.ENGLISH, "%s student%s started the test.", attemptCount, attemptCount == 1 ? " has" : "s have");
                SpannableStringBuilder ss = new SpannableStringBuilder(countString);
                ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, count.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                ss.setSpan(new RelativeSizeSpan(1.5f), 0, count.length(), SPAN_INCLUSIVE_INCLUSIVE);
                noOfAttemptStartedTextView.setText(ss);
            } else {
                noOfAttemptStartedTextView.setText("Could not fetch data. Click here to try again.");
            }
            fetchingAttemptData = false;
            jumpingBeans[1].stopJumping();
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
                        for (String attemptRefString : attemptsReference) {
                            DocumentReference attemptRef = firestore.document(testRefString + "/attempts/" + attemptRefString);
                            transaction.delete(attemptRef);
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

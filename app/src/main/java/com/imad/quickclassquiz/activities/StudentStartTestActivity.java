package com.imad.quickclassquiz.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.datamodel.Question;
import com.imad.quickclassquiz.datamodel.Test;
import com.imad.quickclassquiz.utils.NetworkUtils;

import net.frakbot.jumpingbeans.JumpingBeans;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentStartTestActivity extends AppCompatActivity {

    @BindView(R.id.rulesListView)
    ListView rulesListView;
    @BindView(R.id.airplaneModeEnabledTextView)
    TextView airplaneModeEnabledTextView;
    @BindView(R.id.questionsFetchedStatusTextView)
    TextView questionsFetchedStatusTextView;
    @BindView(R.id.beginTestButton)
    Button beginTestButton;

    FirebaseFirestore firestore;
    boolean airplaneModeEnabled = false;
    boolean questionsFetched = false;
    ArrayList<Question> questions = new ArrayList<>();
    Test test;
    JumpingBeans jumpingBeans;
    String tick;
    String cross;

    IntentFilter intentFilter;
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_start_test);

        tick = getResources().getString(R.string.tick);
        cross = getResources().getString(R.string.cross);

        ButterKnife.bind(this);
        firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build());

        handleAirplaneModeStatus();

        Intent intent = getIntent();
        if (intent != null) {
            test = intent.getParcelableExtra("test");
        }

        beginTestButton.setOnClickListener(v -> {
            Intent toTest = new Intent(this, TestActivity.class);
            toTest.putExtra("questions", questions);
            toTest.putExtra("test", test);
            startActivity(toTest);
        });

        String rules[] = getResources().getStringArray(R.array.rules);
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rules);
        rulesListView.setAdapter(adapter);

        questionsFetchedStatusTextView.setOnClickListener(v -> {
            if (!questionsFetched)
                fetchTestQuestions();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForCompletion();
        fetchTestQuestions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (jumpingBeans != null) {
            jumpingBeans.stopJumping();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(receiver);
    }

    public void handleAirplaneModeStatus() {
        if ((airplaneModeEnabled = isAirplaneModeOn(this))) {
            airplaneModeEnabledTextView.setText(tick + " Airplane Mode Enabled.");
            airplaneModeEnabledTextView.setTextColor(getResources().getColor(R.color.colorTaskCompleted));
        } else {
            airplaneModeEnabledTextView.setText(cross + " Airplane Mode Disabled. Enable it to continue to the test.");
            airplaneModeEnabledTextView.setTextColor(getResources().getColor(R.color.colorTaskIncomplete));
        }

        intentFilter = new IntentFilter("android.intent.action.AIRPLANE_MODE");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
                airplaneModeEnabled = isAirplaneModeOn;
                if (isAirplaneModeOn) {
                    airplaneModeEnabledTextView.setText(tick + " Airplane Mode Enabled.");
                    airplaneModeEnabledTextView.setTextColor(getResources().getColor(R.color.colorTaskCompleted));
                } else {
                    airplaneModeEnabledTextView.setText(cross + " Airplane Mode Disabled. Enable it to continue to the test.");
                    airplaneModeEnabledTextView.setTextColor(getResources().getColor(R.color.colorTaskIncomplete));
                }
                checkForCompletion();
            }
        };

        this.registerReceiver(receiver, intentFilter);
    }

    private boolean isAirplaneModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    public void fetchTestQuestions() {
        new NetworkUtils(internet -> {
            if (internet) {
                if (test != null) {
                    String url = String.format("tests/%s/questions", test.getTestId());
                    questionsFetchedStatusTextView.setTextColor(getResources().getColor(R.color.colorTaskExecuting));
                    questionsFetchedStatusTextView.setText("Fetching questions");
                    jumpingBeans = JumpingBeans.with(questionsFetchedStatusTextView).appendJumpingDots().build();
                    firestore.collection(url)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        Question question = snapshot.toObject(Question.class);
                                        questions.add(question);
                                    }
                                    Log.e("questions", questions.toString());
                                    questionsFetched = true;
                                    questionsFetchedStatusTextView.setTextColor(getResources().getColor(R.color.colorTaskCompleted));
                                    questionsFetchedStatusTextView.setText(tick + " Questions fetched!");
                                } else {
                                    questionsFetched = false;
                                    questionsFetchedStatusTextView.setTextColor(getResources().getColor(R.color.colorTaskIncomplete));
                                    questionsFetchedStatusTextView.setText(cross + " Could not fetch questions. Click here to try again.");
                                }
                                checkForCompletion();
                                jumpingBeans.stopJumping();
                            });
                } else {
                    questionsFetched = false;
                    checkForCompletion();
                    questionsFetchedStatusTextView.setTextColor(getResources().getColor(R.color.colorTaskIncomplete));
                    questionsFetchedStatusTextView.setText(cross + " Could not fetch questions. Click here to try again.");
                }
            } else {
                questionsFetched = false;
                checkForCompletion();
                questionsFetchedStatusTextView.setTextColor(getResources().getColor(R.color.colorTaskIncomplete));
                questionsFetchedStatusTextView.setText(cross + " No internet connection. Enable it and then click here to try again.");
            }
        });

    }

    private void checkForCompletion() {
        if (airplaneModeEnabled && questionsFetched) {
            Log.e("Button", "enabled");
            beginTestButton.setEnabled(true);
        } else {
            Log.e("Button", "disabled");
            beginTestButton.setEnabled(false);
        }
    }
}
package com.imad.quickclassquiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.imad.quickclassquiz.R;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore firestore;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        Toast.makeText(this, String.format("Window %s focus!", hasFocus ? "has" : "lost"), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .setHostedDomain("lnmiit.ac.in")
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.button).setOnClickListener(v -> {
            mGoogleSignInClient.signOut().addOnCompleteListener(aVoid -> {
                Toast.makeText(this, "Signed out!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            });
        });

        Intent intent = getIntent();

        Button showTestsButton = findViewById(R.id.showTestsButton);
        Button completedTest = findViewById(R.id.completedTest);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            String role = "";
            String rollNo = "";
            if (intent != null && intent.getStringExtra("from").equals("login")) {
                final boolean isTeacher = intent.getBooleanExtra("teacher", false);
                rollNo = intent.getStringExtra("rollNumber");
                Log.e("intent", intent.getBooleanExtra("teacher", false) + "");
                if (isTeacher) {
                    role = "teacher";
                    completedTest.setEnabled(false);
                }
                else
                    role = "student";
                showTestsButton.setOnClickListener(v -> {
                    if(isTeacher)
                        startActivity(new Intent(this, TeacherTestListActivity.class));
                    else
                        startActivity(new Intent(this, StudentTestListActivity.class));

                });
                completedTest.setOnClickListener(v ->{
                    startActivity(new Intent(MainActivity.this,StudentCompletedTest.class));
                });
            }
            if (TextUtils.isEmpty(rollNo))
                ((TextView) findViewById(R.id.nameTextView)).setText(String.format("Hello %s!\nYou're a %s!", account.getDisplayName(), role));
            else
                ((TextView) findViewById(R.id.nameTextView)).setText(String.format("Hello %s!\nYou're a %s!\nYour roll number is %s!", account.getDisplayName(), role, rollNo));
        } else {
            showTestsButton.setEnabled(false);
        }

//        CollectionReference testsCollection = firestore.collection("tests");
//
//        String uuid = UUID.randomUUID().toString();
//        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
//        Test test = new Test(uuid, "IMAD test", "Test on intents", timestamp);
//
//        Question question = new Question(1, "AAA", "fdfsd", "fff", "ffdd", "qqq", "fdfsd");
//        Question question1 = new Question(2, "BBB", "fdfsd", "fff", "ffdd", "qqq", "fdfsd");
//        // Add a new document with a generated ID
//        testsCollection.add(test).addOnSuccessListener(documentReference -> {
//            documentReference.collection("questions").add(question).addOnSuccessListener(d -> {
//                Log.i(getPackageName(), "Added with ref " + d.getId());
//            });
//            documentReference.collection("questions").add(question1).addOnSuccessListener(d -> {
//                Log.i(getPackageName(), "Added with ref " + d.getId());
//            });
//        });

//        String uuid1 = UUID.randomUUID().toString();
//        String timestamp1 = Long.toString(System.currentTimeMillis() / 1000);
//        Test test1 = new Test(uuid1, "IMAD test", "Test on intents", timestamp1);
//
//        Question question1 = new Question(1, "AAA", "fdfsd", "fff", "ffdd", "qqq", "fdfsd");
//        // Add a new document with a generated ID
//        testsCollection.add(test1).addOnSuccessListener(documentReference -> {
//            documentReference.collection("questions").add(question1).addOnSuccessListener(d -> {
//                Log.i(getPackageName(), "Added with ref " + d.getId());
//            });
//        });
    }

    public void moveFirestoreDocument(DocumentReference fromPath, final DocumentReference toPath) {
        fromPath.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    toPath.set(document.getData())
                            .addOnSuccessListener(aVoid -> {
                                Log.d(getPackageName(), "DocumentSnapshot successfully written!");
                                fromPath.delete()
                                        .addOnSuccessListener(aVoid1 -> Log.d(getPackageName(), "DocumentSnapshot successfully deleted!"))
                                        .addOnFailureListener(e -> Log.w(getPackageName(), "Error deleting document", e));
                            })
                            .addOnFailureListener(e -> Log.w(getPackageName(), "Error writing document", e));
                } else {
                    Log.d(getPackageName(), "No such document");
                }
            } else {
                Log.d(getPackageName(), "get failed with ", task.getException());
            }
        });
    }

}

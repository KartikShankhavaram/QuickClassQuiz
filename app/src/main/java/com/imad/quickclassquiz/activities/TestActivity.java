package com.imad.quickclassquiz.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.dataModel.Question;
import com.imad.quickclassquiz.dataModel.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    private static String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TAG = getPackageName();
        firestore = FirebaseFirestore.getInstance();

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

        Log.i(TAG, "Adding document");

        String uuid = UUID.randomUUID().toString();
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        Test test = new Test(uuid, "IMAD test", "Test on intents", timestamp);

        Question question = new Question(1, "AAA", "fdfsd", "fff", "ffdd", "qqq", "fdfsd");
        // Add a new document with a generated ID
        CollectionReference testsCollection = firestore.collection("tests");
//        testsCollection.add(test).addOnSuccessListener(documentReference -> {
//            documentReference.collection("questions").add(question).addOnSuccessListener(d -> {
//                Log.i(TAG, "Added with ref " + d.getId());
//            });
//        });

        ArrayList<Test> testList = new ArrayList<>();

        testsCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            testList.add(documentSnapshot.toObject(Test.class));
//                            Map<String, Object> accessCode = new HashMap<>();
//                            accessCode.put("accessCode", "66543hu8");
//                            testsCollection.document(documentSnapshot.getId()).update(accessCode).addOnSuccessListener(d -> {
//                                Log.e(TAG, "Added access code to "  +documentSnapshot.getId());
//                            });
                        }
                    }
                });

        CountDownTimer timer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                testsCollection.document("Cbq5o4vsLqi6SGZdohsd").get().addOnCompleteListener(snapshot -> {
                    if (snapshot.isSuccessful()) {
                        Test a = snapshot.getResult().toObject(Test.class);
                        if (a != null) {
                            if (a.getAccessCode() != null) {
                                Log.e(TAG, "Access code is " + a.getAccessCode());
                            } else {
                                Log.e(TAG, "Access code is null.");
                            }
                        }
                    }
                });

            }
        }.start();

//        firestore.collection("users")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });


    }
}

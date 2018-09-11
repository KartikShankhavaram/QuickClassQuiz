package com.imad.quickclassquiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.dataModel.Question;
import com.imad.quickclassquiz.dataModel.Test;
import com.imad.quickclassquiz.recyclerview.TeacherTestAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore firestore;
    CollectionReference testsCollection;
    RecyclerView mRecyclerView;
    QuestionAdapter questionAdapter;
    private List<Question> questionList = new ArrayList<Question>();
    private String TAG;

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        Toast.makeText(this, String.format("Window %s focus!", hasFocus ? "has" : "lost"), Toast.LENGTH_SHORT).show();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addQuestion:
                startActivity( new Intent(MainActivity.this,AddQuestion.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    protected void onStart() {
        questionList.clear();
        getData();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TAG = getPackageName();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        questionAdapter = new QuestionAdapter(questionList);
        firestore = FirebaseFirestore.getInstance();
        testsCollection = firestore.collection("tests");
        mRecyclerView.setAdapter(questionAdapter);
        getData();
//        firestore = FirebaseFirestore.getInstance();
//
//        testListRecyclerView = findViewById(R.id.testListRecyclerView);
//        testListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        final TeacherTestAdapter adapter = new TeacherTestAdapter(this);
//        testListRecyclerView.setAdapter(adapter);
//
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .setHostedDomain("lnmiit.ac.in")
//                .build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        findViewById(R.id.button).setOnClickListener(v -> {
//            mGoogleSignInClient.signOut().addOnCompleteListener(aVoid -> {
//                Toast.makeText(this, "Signed out!", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(this, LoginActivity.class));
//            });
//        });
//
//        Intent intent = getIntent();
//
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//
//        if(account != null) {
//            String role = "";
//            if (intent != null && intent.getStringExtra("from").equals("login")) {
//                Log.e("intent", intent.getBooleanExtra("teacher", false) + "");
//                if(intent.getBooleanExtra("teacher", false))
//                    role = "teacher";
//                else
//                    role = "student";
//            }
//            ((TextView)findViewById(R.id.nameTextView)).setText(String.format("Hello %s!\nYou're a %s!", account.getDisplayName(), role));
//        }
//
//        ArrayList<Test> teacherTestList = new ArrayList<>();
//
//        CollectionReference testsCollection = firestore.collection("tests");
//        testsCollection.get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                            teacherTestList.add(documentSnapshot.toObject(Test.class));
//                        }
//                        adapter.setListContent(teacherTestList);
//                    }
//                });
    }
    private void getData(){
       testsCollection.get()
               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if (task.isSuccessful()) {
                           for (QueryDocumentSnapshot document : task.getResult()) {
                              questionList.add(document.toObject(Question.class));
                              questionAdapter.notifyDataSetChanged();
                           }
                       } else {
                           Log.d(TAG, "Error getting documents: ", task.getException());
                       }
                   }
               });
    }
}

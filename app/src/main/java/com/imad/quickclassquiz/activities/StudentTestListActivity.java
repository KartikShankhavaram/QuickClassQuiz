package com.imad.quickclassquiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.fragments.AvailableTestFragment;
import com.imad.quickclassquiz.fragments.CompletedTestFragment;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class StudentTestListActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore firestore;
    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private AvailableTestFragment availableTestFragment;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        Toast.makeText(this, String.format("Window %s focus!", hasFocus ? "has" : "lost"), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_test_list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String name = "";
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            name = account.getDisplayName();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Welcome,  " + name);
        }

        frameLayout = findViewById(R.id.main_frame);
        bottomNavigationView = findViewById(R.id.main_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) this);
        Fragment fragment = new AvailableTestFragment();
        loadFragment(fragment);

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.Available:
                    //Toast.makeText(getApplicationContext(),"Available",Toast.LENGTH_SHORT).show();
                    fragment = new AvailableTestFragment();
                    break;

                case R.id.Completed:
                    //Toast.makeText(getApplicationContext(),"Completed",Toast.LENGTH_SHORT).show();
                    fragment = new CompletedTestFragment();
                    break;
            }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction;
            transaction = manager.beginTransaction();
            transaction.replace(R.id.main_frame, fragment);
            transaction.commit();
            return true;
        }
        return false;
    }
}


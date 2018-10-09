package com.imad.quickclassquiz.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.fragments.AvailableTestFragment;
import com.imad.quickclassquiz.fragments.CompletedTestFragment;
import com.imad.quickclassquiz.utils.StaticValues;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore firestore;
    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;

    private AvailableTestFragment availableTestFragment;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        Toast.makeText(this, String.format("Window %s focus!", hasFocus ? "has" : "lost"), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Kick Out Notification";
            String description = "This notification informs you when you have been kicked out of the test for leaving the app.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getPackageName(), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}


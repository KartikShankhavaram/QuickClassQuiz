package com.imad.quickclassquiz.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.utils.NetworkUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        createNotificationChannel();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build());

        ProgressBar progressBar = findViewById(R.id.progressBar);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        new NetworkUtils(internet -> {
            if(internet) {
                if(account != null) {
                    String email = account.getEmail();
                    String[] parts = email.split("@");
                    String username = parts[0];
                    CollectionReference teachers = firestore.collection("teachers");
                    teachers.get().addOnCompleteListener(task -> {
                        boolean found = false;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (account.getEmail() != null && account.getEmail().equals(document.get("email"))) {
                                    found = true;
                                    startActivity(new Intent(SplashActivity.this, TeacherTestListActivity.class)
                                            .putExtra("teacher", true)
                                            .putExtra("rollNumber", "")
                                            .putExtra("from", "login"));
                                    finish();
                                }
                            }
                            if(!found) {
                                startActivity(new Intent(SplashActivity.this, StudentTestListActivity.class)
                                        .putExtra("teacher", false)
                                        .putExtra("rollNumber", username)
                                        .putExtra("from", "login"));
                                finish();
                            }
                        }
                    });
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                }
            } else {
                Toast.makeText(this, "There is no internet connection. Connect to internet and open the app again.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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

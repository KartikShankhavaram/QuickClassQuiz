package com.imad.quickclassquiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.imad.quickclassquiz.R;

public class MainActivity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if(account != null) {
            String email = account.getEmail();
            String[] parts = email.split("@");
            String username = parts[0];
            boolean isStudent = username.matches("[1|2]\\du[c|e|m][s|c|m|e]\\d\\d\\d");
            String role = "";
            if(isStudent)
                role = "student";
            else
                role = "teacher";
            ((TextView)findViewById(R.id.nameTextView)).setText(String.format("Hello %s!\nYou're a %s!", account.getDisplayName(), role));
        }
    }
}

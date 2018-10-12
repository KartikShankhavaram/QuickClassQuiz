package com.imad.quickclassquiz.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.utils.NetworkUtils;

import androidx.appcompat.app.AppCompatActivity;
/* this is the login activity in which only LNMIIT domain ID user is allowed and we are checking
* who is registere as faculty and who is students*/
public class LoginActivity extends AppCompatActivity {

    private static String TAG;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1337;

    SignInButton signInButton;
    ProgressDialog progress;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TAG = getPackageName();

        signInButton = findViewById(R.id.sign_in_button);

        firestore = FirebaseFirestore.getInstance();

        progress = new ProgressDialog(this);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage("Please wait while we sign you in...");
        progress.setTitle("Sign in");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        signInButton.setSize(SignInButton.SIZE_WIDE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .setHostedDomain("lnmiit.ac.in")
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(v -> {
            new NetworkUtils(internet -> {
                if(internet) {
                    progress.show();
                    signIn();
                } else {
                    Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            progress.dismiss();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            String email = account.getEmail();
            String[] parts = email.split("@");
            String username = parts[0];
            CollectionReference teachers = firestore.collection("teachers");
            teachers.get().addOnCompleteListener(task -> {
                boolean found = false;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (email != null && email.equals(document.get("email"))) {
                            Log.e(TAG, "document email -> " + document.get("email"));
                            Log.e(TAG, "email comparison -> " + email.equals(document.get("email")));
                            progress.dismiss();
                            found = true;
                            startActivity(new Intent(LoginActivity.this, TeacherTestListActivity.class)
                                    .putExtra("teacher", true)
                                    .putExtra("rollNumber", "")
                                    .putExtra("from", "login"));
                            finish();
                        }
                    }
                    if(!found) {
                        progress.dismiss();
                        if(!email.matches("[1|2]\\du[c|e|m][s|c|m|e]\\d\\d\\d.lnmiit.ac.in")) {
                            Toast.makeText(this, "Please enter your email in the format \"16ucs088@lnmiit.ac.in\".", Toast.LENGTH_SHORT).show();
                            mGoogleSignInClient.signOut();
                        } else {
                            startActivity(new Intent(LoginActivity.this, StudentTestListActivity.class)
                                    .putExtra("teacher", false)
                                    .putExtra("rollNumber", username)
                                    .putExtra("from", "login"));
                            finish();
                        }
                    }
                }
            });

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }
}

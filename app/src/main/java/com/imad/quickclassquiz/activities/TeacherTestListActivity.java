package com.imad.quickclassquiz.activities;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.datamodel.Test;
import com.imad.quickclassquiz.fragments.StartedTestsTeacherFragment;
import com.imad.quickclassquiz.fragments.UpcomingTestsTeacherFragment;
import com.imad.quickclassquiz.utils.StaticValues;
import com.imad.quickclassquiz.viewpageradapters.TestListPagerAdapter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
/* This activity contails two tab layouts(Upcoming tests and Ongoing/Completed test) any of these
 * fragment contains their respective test list if a teacher starts a test(In upcoming fragment) after made it public then
  * it will move into the completes test fragment and access code will be generated*/
public class TeacherTestListActivity extends AppCompatActivity {

    @BindView(R.id.testListToolbar)
    Toolbar toolbar;
    @BindView(R.id.addTestFAB)
    FloatingActionButton addTestButton;
    @BindView(R.id.testListPager)
    ViewPager testListViewPager;
    @BindView(R.id.testListTabLayout)
    TabLayout testListTabLayout;

    FirebaseFirestore firestore;
    int REQUEST_CODE = 1337;

    Test toDownload;

    DownloadManager downloadManager;
    long downloadRefId;
    boolean currentlyDownloading = false;
    BroadcastReceiver onCompleteReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_test_list);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        String name = "";
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            name = account.getDisplayName();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        actionBar.setTitle("Tests");

        TestListPagerAdapter pagerAdapter = new TestListPagerAdapter(getSupportFragmentManager());
        testListViewPager.setAdapter(pagerAdapter);

        testListTabLayout.setupWithViewPager(testListViewPager);

        testListViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                TestListPagerAdapter adapter = (TestListPagerAdapter) testListViewPager.getAdapter();
                switch (position) {
                    case 0:
                        addTestButton.show();
                        if (StaticValues.getShouldRefresh()) {
                            UpcomingTestsTeacherFragment fragment = (UpcomingTestsTeacherFragment) adapter.getItem(0);
                            fragment.fetchTests();
                            StaticValues.setShouldRefresh(false);
                        }
                        break;
                    case 1:
                        addTestButton.hide();
                        if (StaticValues.getShouldRefresh()) {
                            StartedTestsTeacherFragment fragment = (StartedTestsTeacherFragment) adapter.getItem(1);
                            fragment.fetchTests();
                            StaticValues.setShouldRefresh(false);
                        }
                        break;
                    default:
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        addTestButton.setOnClickListener(v -> startActivity(new Intent(TeacherTestListActivity.this, AddTestActivity.class)));

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        onCompleteReceiver = new BroadcastReceiver() {

            public void onReceive(Context ctxt, Intent intent) {

                long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                Log.e("INSIDE", "" + referenceId);
                if (referenceId == downloadRefId) {
                    Log.e("FILE", "Downloaded!");
                    currentlyDownloading = false;
                }
            }
        };

        registerReceiver(onCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onCompleteReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (permissions[0].equals(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                startDownload();
            }
        } else {
            Toast.makeText(this, "Storage permissions are required for downloading files.", Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadTestReport(Test test) {
//        if (currentlyDownloading) {
//            Toast.makeText(this, "You can only downloading one report at a time.", Toast.LENGTH_SHORT).show();
//            return;
//        }
        toDownload = test;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setTitle("Storage Permission required")
                        .setMessage("Storage permissions are required to download files.")
                        .setPositiveButton("Give Permissions", (dialog, i) -> ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE))
                        .setCancelable(false)
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        } else {
            startDownload();
            Toast.makeText(this, "Downloading file...", Toast.LENGTH_SHORT).show();
        }
    }

    public void startDownload() {
        Uri downloadUri = Uri.parse("https://us-central1-quick-class-quiz.cloudfunctions.net/getTestReport");
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(true);
        String filename = constructFilename(toDownload);
        request.setTitle(filename);
        request.setDescription("Downloading report for " + toDownload.getTestName());
        request.addRequestHeader("testId", toDownload.getTestId());
        request.addRequestHeader("filename", filename);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/" + filename);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        currentlyDownloading = true;
        downloadRefId = downloadManager.enqueue(request);
    }

    private String constructFilename(Test test) {
        String time = DateTimeFormat.forPattern("YYYYMMdd").print(new DateTime(test.getStartedAt()));
        String name = toCamelCase(test.getTestName());
        return String.format(Locale.ENGLISH, "%s-%s-Report.csv", time, name);
    }

    private String toCamelCase(String a) {
        String words[] = a.split(" ");
        String newWord = "";
        for (String word : words) {
            word = Character.toUpperCase(word.charAt(0)) + word.substring(1);
            newWord = newWord.concat(word);
        }
        return newWord;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.log_out_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.logOut:
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .setHostedDomain("lnmiit.ac.in")
                        .build();

                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                mGoogleSignInClient.signOut().addOnCompleteListener(aVoid -> {
                    Toast.makeText(this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.imad.quickclassquiz.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.datamodel.Question;
import com.imad.quickclassquiz.datamodel.Test;
import com.imad.quickclassquiz.fragments.CodeEntryFragment;
import com.imad.quickclassquiz.fragments.TestFragment;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {

    public final static int ACCESS_CODE_ENTRY_FRAGMENT = 1337;
    public final static int TEST_FRAGMENT = 1338;
    @BindView(R.id.testFragmentHolder)
    FrameLayout testFragmentHolder;
    ArrayList<Question> questions = new ArrayList<>();
    Test test;
    boolean proceedingToSubmit;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!(hasFocus || proceedingToSubmit)) {
            Toast.makeText(this, "Because of your leaving the app during the test, you have been kicked out.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, StudentTestListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this, getPackageName())
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("You have been kicked out")
                    .setContentText("You have been kicked out of the test because you left the app during the test.")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("You have been kicked out of the test because you left the app during the test."))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(13371, nBuilder.build());

            finish();
        }
//        Toast.makeText(this, String.format("Window %s focus!", (hasFocus ? "has" : "lost")), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(flags);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Code below is to handle presses of Volume up or Volume down.
        // Without this, after pressing volume buttons, the navigation bar will
        // show up and won't hide
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                decorView.setSystemUiVisibility(flags);
            }
            Log.e("System visibility", visibility + "");
        });

        setContentView(R.layout.activity_test);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            questions = intent.getParcelableArrayListExtra("questions");
            test = intent.getParcelableExtra("test");
        }

        switchFragment(ACCESS_CODE_ENTRY_FRAGMENT);
    }

    public boolean switchFragment(int fragmentType) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction;
        Fragment fragment;
        switch (fragmentType) {
            case ACCESS_CODE_ENTRY_FRAGMENT:
                transaction = manager.beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragment = CodeEntryFragment.newInstance(test);
                transaction.replace(R.id.testFragmentHolder, fragment);
                transaction.commit();
                return true;
            case TEST_FRAGMENT:
                transaction = manager.beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragment = TestFragment.newInstance(questions, test);
                transaction.replace(R.id.testFragmentHolder, fragment);
                transaction.commit();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_MENU) {
            Toast.makeText(this, "Navigation button pressed.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void setProceedingToSubmit(boolean proceedingToSubmit) {
        this.proceedingToSubmit = proceedingToSubmit;
    }
}

package com.imad.quickclassquiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {

    public final static int ACCESS_CODE_ENTRY_FRAGMENT = 1337;
    public final static int TEST_FRAGMENT = 1338;
    @BindView(R.id.testFragmentHolder)
    FrameLayout testFragmentHolder;
    ArrayList<Question> questions = new ArrayList<>();
    Test test;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Toast.makeText(this, String.format("Window %s focus!", (hasFocus ? "has" : "lost")), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            questions = intent.getParcelableArrayListExtra("questions");
            test = intent.getParcelableExtra("test");
        }

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

        switchFragment(ACCESS_CODE_ENTRY_FRAGMENT);
    }

    public boolean switchFragment(int fragmentType) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction;
        Fragment fragment;
        switch (fragmentType) {
            case ACCESS_CODE_ENTRY_FRAGMENT:
                transaction = manager.beginTransaction();
                transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                fragment = CodeEntryFragment.newInstance(test);
                transaction.replace(R.id.testFragmentHolder, fragment);
                transaction.commit();
                return true;
            case TEST_FRAGMENT:
                transaction = manager.beginTransaction();
                transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                fragment = TestFragment.newInstance(questions,test);
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
}

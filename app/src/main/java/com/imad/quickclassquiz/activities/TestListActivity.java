package com.imad.quickclassquiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.recyclerview.TeacherStartedTestListAdapter;
import com.imad.quickclassquiz.viewPagerAdapters.TestListPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestListActivity extends AppCompatActivity {

    @BindView(R.id.testListToolbar)
    Toolbar toolbar;
    @BindView(R.id.addTestFAB)
    FloatingActionButton addTestButton;
    @BindView(R.id.testListPager)
    ViewPager testListViewPager;
    @BindView(R.id.testListTabLayout)
    TabLayout testListTabLayout;

    FirebaseFirestore firestore;
    TeacherStartedTestListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
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

                switch (position) {
                    case 0:
                        addTestButton.show();
                        break;

                    default:
                        addTestButton.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        addTestButton.setOnClickListener(v -> {
            startActivity(new Intent(TestListActivity.this, AddTestActivity.class));
        });
    }

}

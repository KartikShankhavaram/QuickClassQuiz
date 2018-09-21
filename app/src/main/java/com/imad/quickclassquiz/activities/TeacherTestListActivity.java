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
import com.imad.quickclassquiz.R;
import com.imad.quickclassquiz.fragments.StartedTestsTeacherFragment;
import com.imad.quickclassquiz.fragments.UpcomingTestsTeacherFragment;
import com.imad.quickclassquiz.recyclerview.TeacherStartedTestListAdapter;
import com.imad.quickclassquiz.utils.StaticValues;
import com.imad.quickclassquiz.viewpageradapters.TestListPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    TeacherStartedTestListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_question);

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
                TestListPagerAdapter adapter = (TestListPagerAdapter)testListViewPager.getAdapter();
                switch (position) {
                    case 0:
                        addTestButton.show();
                        if(StaticValues.getShouldRefresh()) {
                            UpcomingTestsTeacherFragment fragment = (UpcomingTestsTeacherFragment) adapter.getItem(0);
                            fragment.fetchTests();
                            StaticValues.setShouldRefresh(false);
                        }
                        break;
                    case 1:
                        addTestButton.hide();
                        if(StaticValues.getShouldRefresh()) {
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

        addTestButton.setOnClickListener(v -> {
            startActivity(new Intent(TeacherTestListActivity.this, AddTestActivity.class));
        });
    }

}

package app.zerobugz.fcms.ims.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import app.zerobugz.fcms.ims.eventgallery.AllEventFragment;
import app.zerobugz.fcms.ims.eventgallery.SpecificEventFragment;
import app.zerobugz.fcms.ims.R;

public class EventGallery extends AppCompatActivity {
    private View parent_view;
    private ViewPager event_view_pager;
    private TabLayout tab_event_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_gallery);
        parent_view = findViewById(android.R.id.content);

        initToolbar();
        initComponent();
    }

    private void initComponent() {
        event_view_pager = (ViewPager) findViewById(R.id.event_view_pager);
        setupViewPager(event_view_pager);

        tab_event_layout = (TabLayout) findViewById(R.id.tab_event_layout);
        tab_event_layout.setupWithViewPager(event_view_pager);
    }

    private void setupViewPager(ViewPager viewPager) {
        EventGallery.SectionsPagerAdapter adapter = new EventGallery.SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllEventFragment(), "ALL");
        adapter.addFragment(new SpecificEventFragment(), "SPECIFIC");
        viewPager.setAdapter(adapter);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



    // initializing Toolbar
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Event Gallery");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Back Button Action Here
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(this, Dashboard.class);
                startActivity(intent);
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

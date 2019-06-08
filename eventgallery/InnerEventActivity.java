package app.zerobugz.fcms.ims.eventgallery;

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

import app.zerobugz.fcms.ims.activity.EventGallery;
import app.zerobugz.fcms.ims.R;

public class InnerEventActivity extends AppCompatActivity {
    private View parent_view;
    private ViewPager event_cat_view_pager;
    private TabLayout tab_event_cat_layout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_event);
        parent_view = findViewById(android.R.id.content);

        initToolbar();
        initComponent();

    }

    // initializing Toolbar
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        setSupportActionBar(toolbar);
        //Get the Event Name & limit the word to bind
        String gallery_name = getIntent().getExtras().getString("galleryname");
        if(gallery_name.length() > 20){
            gallery_name = gallery_name.substring(0, 20) + "...";
        }
        getSupportActionBar().setTitle(gallery_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {
        event_cat_view_pager = (ViewPager) findViewById(R.id.event_cat_view_pager);
        setupViewPager(event_cat_view_pager);

        tab_event_cat_layout = (TabLayout) findViewById(R.id.tab_event_cat_layout);
        tab_event_cat_layout.setupWithViewPager(event_cat_view_pager);
    }

    private void setupViewPager(ViewPager viewPager) {
        InnerEventActivity.SectionsPagerAdapter adapter = new InnerEventActivity.SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ImageEventFragment(), "IMAGES");
        adapter.addFragment(new VideoEventFragment(), "VIDEOS");
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

    // Back Button Action Here
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(this, EventGallery.class);
                startActivity(intent);
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}

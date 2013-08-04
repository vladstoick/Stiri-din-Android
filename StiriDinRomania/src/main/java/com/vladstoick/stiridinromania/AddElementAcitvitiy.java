package com.vladstoick.stiridinromania;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.vladstoick.Fragments.AddElementManuallyFragment;
import com.vladstoick.Fragments.AddElementRecommendedFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddElementAcitvitiy extends SherlockFragmentActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AddElementPagerAdapter mAddElementPageAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addelements);
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Show the Up button in the action bar.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        List<SherlockFragment> fragments = new ArrayList<SherlockFragment>();
        fragments.add(new AddElementManuallyFragment());
        fragments.add(new AddElementRecommendedFragment());
        mAddElementPageAdapter = new AddElementPagerAdapter(getSupportFragmentManager(),
                fragments);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mViewPager.setAdapter(mAddElementPageAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);

            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAddElementPageAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAddElementPageAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.add_elements_acitvitiy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home : {
                NavUtils.navigateUpTo(this, new Intent(this, NewsGroupListActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }
    public class AddElementPagerAdapter extends FragmentPagerAdapter {
        List<SherlockFragment> fragments;
        public AddElementPagerAdapter(FragmentManager fm, List<SherlockFragment> fragments)
        {
            super(fm);
            this.fragments = fragments;

        }
        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale locale = Locale.getDefault();
            switch(position)
            {
                case 0: return getString(R.string.manual).toUpperCase(locale);
                case 1: return getString(R.string.recommended).toUpperCase(locale);
            }
            return "";
        }
    }


}

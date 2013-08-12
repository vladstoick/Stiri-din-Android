package com.vladstoick.stiridinromania;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.vladstoick.DataModel.NewsItem;
import com.vladstoick.Fragments.SearchResultsFragment;
import com.vladstoick.Utils.NewsItemAdapter;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.InjectView;
import butterknife.Views;

public class SearchResultsActivity extends SherlockFragmentActivity implements ActionBar.TabListener {
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    String query;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresults);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ArrayList<SearchResultsFragment> fragments = new ArrayList<SearchResultsFragment>();
        fragments.add(new SearchResultsFragment());
        fragments.add(new SearchResultsFragment());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.fragments = fragments;
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            getData();

        }
    }
    public void getData(){
        int selectedPosition = getSupportActionBar().getSelectedTab().getPosition();
        SearchResultsFragment fragment = mSectionsPagerAdapter.fragments.get(selectedPosition);
        if(selectedPosition==0){
            if(fragment!=null){
                fragment.setData(getLocalResults(),this);
            }
        }
    }
    private ArrayList<NewsItem> getLocalResults(){
        ArrayList<NewsItem> results = ((StiriApp)getApplication()).newsDataSource
                .searchNewsItems(query);
        return results;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.search_results_activity, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        menu.findItem(R.id.search).expandActionView();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(searchableInfo);
        try {
            searchView.setQuery(query, false);
            searchView.setIconified(false);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            searchView.clearFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, NewsItemListActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public ArrayList<SearchResultsFragment> fragments;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public SherlockFragment getItem(int position) {
            return fragments.get(position);

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.local).toUpperCase(l);
                case 1:
                    return getString(R.string.online).toUpperCase(l);
            }
            return null;
        }
    }

}

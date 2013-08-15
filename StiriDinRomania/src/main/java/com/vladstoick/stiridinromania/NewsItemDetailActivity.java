package com.vladstoick.stiridinromania;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.vladstoick.DataModel.NewsItem;
import com.vladstoick.DataModel.NewsSource;
import com.vladstoick.Fragments.NewsItemDetailFragment;
import com.vladstoick.Utils.NewsItemPagerAdapter;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;


public class NewsItemDetailActivity extends SherlockFragmentActivity {
    public static String FROM_SEARCH="FROMSEARCH";
    private ArrayList<NewsItem> news;
    public NewsItemPagerAdapter mAdapter;
    @InjectView(R.id.newsitem_detail_container) public ViewPager mPager;
    boolean fromSearch = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsitem_detail);
        Views.inject(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            fromSearch = getIntent().getBooleanExtra(FROM_SEARCH,false);
            Bundle extras = getIntent().getExtras();

            if(extras.containsKey(NewsItemDetailFragment.ARG_ITEM)){
                int newsSourceId = getIntent().getIntExtra(NewsItemDetailFragment.ARG_NEWSOURCE, 0);
                NewsSource ns = ((StiriApp)getApplication()).newsDataSource
                        .getNewsSource(newsSourceId);
                setTitle(getString(R.string.app_name)+ " " + ns.getTitle());
                news = ns.news;

                mAdapter = new NewsItemPagerAdapter(getSupportFragmentManager(),news);
            } else {
                ArrayList<NewsItem> news = new ArrayList<NewsItem>();
                news.add((NewsItem)
                        getIntent().getParcelableExtra(NewsItemDetailFragment.ARG_ITEM_JO));
                mAdapter =  new NewsItemPagerAdapter(getSupportFragmentManager(),news);
                mAdapter.fromOnlineSearch = true;
            }
            mPager.setAdapter(mAdapter);
            mPager.setCurrentItem(getIntent().getIntExtra(NewsItemDetailFragment.ARG_ITEMPOSITION,0));
        }
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(!fromSearch){
                    NavUtils.navigateUpTo(this, new Intent(this, NewsItemListActivity.class));
                } else {
                    NavUtils.navigateUpTo(this, new Intent(this, SearchResultsActivity.class));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

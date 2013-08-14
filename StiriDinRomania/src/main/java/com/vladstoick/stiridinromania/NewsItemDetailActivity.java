package com.vladstoick.stiridinromania;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.vladstoick.DataModel.NewsSource;
import com.vladstoick.Fragments.NewsItemDetailFragment;
import com.vladstoick.Utils.NewsItemPagerAdapter;

import butterknife.InjectView;
import butterknife.Views;


public class NewsItemDetailActivity extends SherlockFragmentActivity {
    public static String FROM_SEARCH="FROMSEARCH";
    private NewsSource newsSource;
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
                newsSource = ((StiriApp)getApplication()).newsDataSource.getNewsSource(newsSourceId);
                mAdapter = new NewsItemPagerAdapter(getSupportFragmentManager(),newsSource.news);
                mPager.setAdapter(mAdapter);
                mPager.setCurrentItem(getIntent().getIntExtra(NewsItemDetailFragment.ARG_ITEMPOSITION,0));

            } else {
//                arguments.putParcelable(NewsItemDetailFragment.ARG_ITEM_JO,
//                        getIntent().getParcelableExtra(NewsItemDetailFragment.ARG_ITEM_JO));
            }


//            NewsItemDetailFragment fragment = new NewsItemDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.newsitem_detail_container, fragment)
//                    .commit();
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

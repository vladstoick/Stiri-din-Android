package com.vladstoick.stiridinromania;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.vladstoick.Fragments.NewsItemDetailFragment;


public class NewsItemDetailActivity extends SherlockFragmentActivity {
    public static String FROM_SEARCH="FROMSEARCH";
    boolean fromSearch = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsitem_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            fromSearch = getIntent().getBooleanExtra(FROM_SEARCH,false);
            Bundle arguments = new Bundle();
            Bundle extras = getIntent().getExtras();
            if(extras.containsKey(NewsItemDetailFragment.ARG_ITEM)){
                arguments.putString(NewsItemDetailFragment.ARG_ITEM,
                        getIntent().getStringExtra(NewsItemDetailFragment.ARG_ITEM));
            } else {
                arguments.putParcelable(NewsItemDetailFragment.ARG_ITEM_JO,
                        getIntent().getParcelableExtra(NewsItemDetailFragment.ARG_ITEM_JO));
            }

            NewsItemDetailFragment fragment = new NewsItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.newsitem_detail_container, fragment)
                    .commit();
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

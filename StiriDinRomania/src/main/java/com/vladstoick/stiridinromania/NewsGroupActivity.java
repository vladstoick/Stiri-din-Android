package com.vladstoick.stiridinromania;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.DataModel.NewsSource;
import com.vladstoick.Fragments.NewsGroupFragment;
import com.vladstoick.Utils.Tags;

/**
 * Created by Vlad on 7/28/13.
 */
public class NewsGroupActivity extends SherlockFragmentActivity
        implements NewsGroupFragment.NewsGroupFragmentCommunicationInterface{
    private NewsGroup ng;
    private static String TAG = "NewsGroupActivity";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsgroup);
        if(getIntent().getExtras()!=null)
            ng=getIntent().getExtras().getParcelable(Tags.NEWSGROUP_TAG);
        SherlockFragment newsGroupFragment = NewsGroupFragment.newInstance(ng);
        getSupportFragmentManager().beginTransaction().replace(R.id.newsGroupFragment,
                        newsGroupFragment).commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void selectedNewsSource(NewsSource ns) {
        Intent intent = new Intent(this, NewsItemListActivity.class);
        //TODO SCAPA DE ASTA
        NewsSource realNs = ((StiriApp)getApplication()).newsDataSource.getNewsSource(ns.getId());
        intent.putExtra(NewsSource.TAG,realNs);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.vladstoick.stiridinromania;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.vladstoick.DataModel.NewsSource;
import com.vladstoick.Utils.Adapters.AddElementFeedAdapter;
import com.vladstoick.stiridinromania.R;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;

public class AddElementSelectFeedActivity extends SherlockFragmentActivity {

    public final static String TAG_FEEDS = "FEEDSDATA";
    public final static String TAG_TITLE = "title";
    @InjectView(R.id.feedList) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addelementselectfeed);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Views.inject(this);
        ArrayList<NewsSource> feeds = new ArrayList<NewsSource>();
        if(getIntent().getExtras()!=null){
            feeds = getIntent().getParcelableArrayListExtra(TAG_FEEDS);
            setTitle(getIntent().getStringExtra(TAG_TITLE));
        }
        listView.setAdapter(new AddElementFeedAdapter(this,feeds));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                NavUtils.navigateUpTo(this, new Intent(this, AddElementAcitvitiy.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

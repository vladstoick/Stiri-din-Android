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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;

public class AddElementSelectFeedActivity extends SherlockFragmentActivity implements ListView.OnItemClickListener{

    public final static String TAG_FEEDS = "FEEDSDATA";
    public final static String TAG_TITLE = "title";
    private ArrayList<NewsSource> feeds;
    @InjectView(R.id.feedList) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addelementselectfeed);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Views.inject(this);
        feeds = new ArrayList<NewsSource>();
        if(getIntent().getExtras()!=null){
            feeds = getIntent().getParcelableArrayListExtra(TAG_FEEDS);
            setTitle(getIntent().getStringExtra(TAG_TITLE));
        }
        listView.setAdapter(new AddElementFeedAdapter(this,feeds));
        listView.setOnItemClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this,AddElementSelectGroupActivity.class);
        intent.putExtra(AddElementSelectGroupActivity.TAG_FEED,feeds.get(position));
        startActivity(intent);
    }
}

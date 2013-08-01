package com.vladstoick.stiridinromania;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.otto.Subscribe;
import com.vladstoick.DataModel.NewsDataSource;
import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.Fragments.AllGroupsFragment;
import com.vladstoick.Fragments.NewsGroupFragment;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.DataLoadedEvent;
import com.vladstoick.OttoBus.DataModifiedEvent;
import com.vladstoick.Utils.Tags;

import java.util.ArrayList;

/**
 * Created by vlad on 7/17/13.
 */
public class AllGroupsActivity extends SherlockFragmentActivity implements AllGroupsFragment.AllGroupsFragmentCommunicationInterface {
    private String USER_ID_TAG = "userId";
    private String TAG = "MAINACTIVITY";
    private NewsDataSource newsDataSource;
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Tags.NEWSDATASOURCE_TAG, newsDataSource);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allgroups);
        BusProvider.getInstance().register(this);
        Bundle extras = getIntent().getExtras();
        AllGroupsFragment fragment = new AllGroupsFragment();
        if(savedInstanceState!=null){
            newsDataSource = savedInstanceState.getParcelable(Tags.NEWSDATASOURCE_TAG);
            fragment = AllGroupsFragment.newInstance(newsDataSource.getAllNewsGroups());
        }
        else if (extras != null) {
            int userId = extras.getInt(USER_ID_TAG);
            newsDataSource = new NewsDataSource(userId,this);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.allGroupsFragment,
                fragment).commit();
    }

    @Subscribe
    public void dataModified(DataModifiedEvent event) {
        if (event.dataModifiedType == DataModifiedEvent.TAG_DELETEGROUP) {
            newsDataSource.deleteNewsGroup(event.id);
        }
        if (event.dataModifiedType == DataModifiedEvent.TAG_GROUPADD) {
            newsDataSource.addNewsGroup(event.title);
        }
    }

    @Override
    public void newsGroupSelected(int index) {
        Intent intent = new Intent(this, NewsGroupActivity.class);
        NewsGroup ng = newsDataSource.getNewsGroup(index);
        intent.putExtra(Tags.NEWSGROUP_TAG, (Parcelable) newsDataSource.getNewsGroup(index));
        startActivity(intent);
    }
}

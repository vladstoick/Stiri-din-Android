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
import com.vladstoick.DialogFragment.AddNewsGroupDialogFragment;
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
public class AllGroupsActivity extends SherlockFragmentActivity implements
        AllGroupsFragment.AllGroupsFragmentCommunicationInterface {
    private String USER_ID_TAG = "userId";
    private String TAG = "MAINACTIVITY";
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Tags.NEWSDATASOURCE_TAG,((StiriApp)getApplication()).newsDataSource);
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
            ((StiriApp)getApplication()).newsDataSource =
                    savedInstanceState.getParcelable(Tags.NEWSDATASOURCE_TAG);
            fragment = new AllGroupsFragment();
        }
        else if (extras != null) {
            int userId = extras.getInt(USER_ID_TAG);
            ((StiriApp)getApplication()).newsDataSource = new NewsDataSource(userId,this);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.allGroupsFragment,
                fragment).commit();
    }
    @Override
    public void newsGroupSelected(int id) {
        Intent intent = new Intent(this, NewsGroupActivity.class);
        NewsGroup ng = ((StiriApp)getApplication()).newsDataSource.getNewsGroup(id);
        intent.putExtra(Tags.NEWSGROUP_TAG,ng);
        startActivity(intent);
    }

    @Override
    public void showAddGroupDialog() {
        AddNewsGroupDialogFragment addNewsGroupDialogFragment = new AddNewsGroupDialogFragment();
        addNewsGroupDialogFragment.show(getSupportFragmentManager(),AddNewsGroupDialogFragment.TAG);
    }
}

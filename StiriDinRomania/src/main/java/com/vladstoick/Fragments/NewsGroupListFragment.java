package com.vladstoick.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.otto.Subscribe;
import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.DialogFragment.NoConnectionDialogFragment;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.DataLoadedEvent;
import com.vladstoick.Utils.NewsGroupAdapter;
import com.vladstoick.Utils.Tags;
import com.vladstoick.Utils.Utils;
import com.vladstoick.stiridinromania.AddElementAcitvitiy;
import com.vladstoick.stiridinromania.LoginActivity;
import com.vladstoick.stiridinromania.R;
import com.vladstoick.stiridinromania.StiriApp;

import java.util.ArrayList;


public class NewsGroupListFragment extends SherlockListFragment {
    private NewsGroupAdapter adapter;
    private ArrayList<NewsGroup> newsDataSource;
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private Callbacks mCallbacks;
    MenuItem refreshItem;
    private int mActivatedPosition = ListView.INVALID_POSITION;

    public interface Callbacks {
        public void onItemSelected(int id);
        public void onAddNewGroupSelected();
    }

    public NewsGroupListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
        setHasOptionsMenu(true);
    }

    private void setAdapter() {
        newsDataSource = ((StiriApp) (getSherlockActivity().getApplication())).newsDataSource
                .getAllNewsGroups();
        Context context = getSherlockActivity();
        StiriApp stiriApp = (StiriApp)(getSherlockActivity().getApplication());
        if (newsDataSource != null) {
            adapter = new NewsGroupAdapter(newsDataSource, context, stiriApp);
            setListAdapter(adapter);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
        setAdapter();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        mCallbacks.onItemSelected(newsDataSource.get(position).getId());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }

    @Subscribe
    public void onDataLoaded(DataLoadedEvent event) {
        refreshItem.setActionView(null);
        setAdapter();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news_group_list_fragment,menu);
        refreshItem  = menu.findItem(R.id.action_refresh);
        if(!((StiriApp) (getSherlockActivity().getApplication())).newsDataSource.isDataLoaded)
            refreshItem.setActionView(R.layout.actionbar_refresh);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add: {
                if(Utils.isOnline(getSherlockActivity())==true){
                    mCallbacks.onAddNewGroupSelected();
                }else {
                    NoConnectionDialogFragment ndf = new NoConnectionDialogFragment();
                    ndf.show(getSherlockActivity().getSupportFragmentManager(),
                            NoConnectionDialogFragment.TAG);
                }
                break;
            }
            case R.id.action_refresh:{
                refreshItem = item;
                ((StiriApp)(getActivity().getApplication())).newsDataSource.loadDataFromInternet();
                item.setActionView(R.layout.actionbar_refresh);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

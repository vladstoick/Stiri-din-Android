package com.vladstoick.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.otto.Subscribe;
import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.DialogFragment.NoConnectionDialogFragment;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.DataLoadedEvent;
import com.vladstoick.Utils.Adapters.NewsGroupAdapter;
import com.vladstoick.Utils.Utils;
import com.vladstoick.stiridinromania.R;
import com.vladstoick.stiridinromania.StiriApp;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;


public class NewsGroupListFragment extends SherlockFragment
        implements ListView.OnItemClickListener{
    private NewsGroupAdapter adapter;
    private ArrayList<NewsGroup> newsDataSource;
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    @InjectView(R.id.newsgroup_listview) ListView mListView;
    @InjectView(R.id.warning) TextView mWarning;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsgroup_list,container,true);
        Views.inject(this,view);
        mListView.setOnItemClickListener(this);
        mWarning.setVisibility(View.GONE);
        return view;
    }

    private void setAdapter() {
        newsDataSource = ((StiriApp) (getSherlockActivity().getApplication())).newsDataSource
                .getAllNewsGroups();
        Context context = getSherlockActivity();
        StiriApp stiriApp = (StiriApp)(getSherlockActivity().getApplication());
        if (newsDataSource != null) {
            mWarning.setVisibility(newsDataSource.size()==0 ? View.VISIBLE : View.GONE);
            adapter = new NewsGroupAdapter(newsDataSource, context, stiriApp, this);
            mListView.setAdapter(adapter);
        } else {
            mWarning.setVisibility(View.VISIBLE);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mCallbacks.onItemSelected(newsDataSource.get(i).getId());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        mListView.setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            mListView.setItemChecked(mActivatedPosition, false);
        } else {
            mListView.setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }

    @Subscribe
    public void onDataLoaded(DataLoadedEvent event) {
        try{
        refreshItem.setActionView(null);
        } catch (Exception e){
            e.printStackTrace();
        }
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
                if(Utils.isOnline(getSherlockActivity()) == true){
                    ((StiriApp)(getActivity().getApplication())).newsDataSource.loadData();
                    item.setActionView(R.layout.actionbar_refresh);
                } else {
                    NoConnectionDialogFragment ndf = new NoConnectionDialogFragment();
                    ndf.show(getSherlockActivity().getSupportFragmentManager(),
                            NoConnectionDialogFragment.TAG);
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.vladstoick.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.Utils.AllGroupsFragmentAdapter;
import com.vladstoick.Utils.Tags;
import com.vladstoick.stiridinromania.StiriApp;

import java.util.ArrayList;


/**
 * A list fragment representing a list of NewsSources. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link NewsGroupDetailFragmentaa}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class NewsGroupListFragment extends SherlockListFragment {
    private AllGroupsFragmentAdapter adapter;
    private ArrayList<NewsGroup> newsDataSource;
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private Callbacks mCallbacks ;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    public interface Callbacks {
        public void onItemSelected(int id);
    }
    public NewsGroupListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            newsDataSource = savedInstanceState.getParcelableArrayList(Tags.NEWSDATASOURCE_TAG);
        }
        else{
            newsDataSource = ((StiriApp)(getSherlockActivity().getApplication())).newsDataSource
                    .getAllNewsGroups();
        }
    }
    private void setAdapter() {
        Context context = getSherlockActivity();
        if (newsDataSource != null) {
            adapter = new AllGroupsFragmentAdapter(newsDataSource, context);
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
        mCallbacks = null ;
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
}

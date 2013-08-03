package com.vladstoick.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.otto.Subscribe;
import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.DialogFragment.NoConnectionDialogFragment;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.DataLoadedEvent;
import com.vladstoick.Utils.AllGroupsFragmentAdapter;
import com.vladstoick.DialogFragment.DeleteDialogFragment;
import com.vladstoick.Utils.Utils;
import com.vladstoick.stiridinromania.R;
import com.vladstoick.stiridinromania.StiriApp;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by vlad on 7/19/13.
 */
public class NewsGroupListFragmentOld extends SherlockFragment {
    public interface AllGroupsFragmentCommunicationInterface {
        public void onItemSelected(int index);
        public void showAddGroupDialog();
    }
    public static String TAG = "ALLGROUPSFRAGMENT";
    ArrayList<NewsGroup> newsDataSource;
    private static String TAG_NDS = "DATA" + TAG;
    private View mView;
    @InjectView(R.id.allGroupsListView) ListView mList;
    private AllGroupsFragmentAdapter adapter;
    private AllGroupsFragmentCommunicationInterface mListener;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TAG_NDS, newsDataSource);
    }

    public NewsGroupListFragmentOld() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AllGroupsFragmentCommunicationInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FragmentCommunicationInterface");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            newsDataSource = savedInstanceState.getParcelableArrayList(TAG_NDS);
        }
        else{
            newsDataSource = ((StiriApp)(getSherlockActivity().getApplication())).newsDataSource
                    .getAllNewsGroups();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_allgroups, container, false);
        BusProvider.getInstance().register(this);
        Views.inject(this, mView);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onItemSelected(newsDataSource.get(position).getId());
            }
        });
        setAdapter();
        return mView;
    }

    private void setAdapter() {
        Context context = getSherlockActivity();
        if (newsDataSource != null) {
            adapter = new AllGroupsFragmentAdapter(newsDataSource, context);
            mList.setAdapter(adapter);
        }
    }
    @Subscribe
    public void newDataLoaded(DataLoadedEvent event) {
        newsDataSource = event.nds;
        if (event.dataLoadedType == DataLoadedEvent.TAG_NEWSDATASOURCE) {
            setAdapter();
            return;
        }
        if (event.dataLoadedType == DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED) {
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_allgroups, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add: {
                if(Utils.isOnline(getSherlockActivity()) == true)
                    mListener.showAddGroupDialog();
                else
                {
                    new NoConnectionDialogFragment().show(getSherlockActivity().
                            getSupportFragmentManager(),NoConnectionDialogFragment.TAG);
                }
                break;
            }
            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

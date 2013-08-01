package com.vladstoick.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.otto.Subscribe;
import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.DataLoadedEvent;
import com.vladstoick.Utils.AllGroupsFragmentAdapter;
import com.vladstoick.DialogFragment.DeleteDialogFragment;
import com.vladstoick.stiridinromania.R;

import java.util.ArrayList;

/**
 * Created by vlad on 7/19/13.
 */
public class AllGroupsFragment extends SherlockFragment {
    public interface AllGroupsFragmentCommunicationInterface {
        public void newsGroupSelected(int index);
    }
    public static String TAG = "ALLGROUPSFRAGMENT";
    ArrayList<NewsGroup> newsDataSource;
    private static String TAG_NDS = "DATA" + TAG;
    private View mView;
    private ListView mList;
    private AllGroupsFragmentAdapter adapter;
    private AllGroupsFragmentCommunicationInterface mListener;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TAG_NDS, newsDataSource);
    }

    public static AllGroupsFragment newInstance(ArrayList<NewsGroup> newsDataSource)
    {
        AllGroupsFragment fragment = new AllGroupsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(TAG_NDS,newsDataSource);
        fragment.setArguments(bundle);
        return fragment;
    }
    public AllGroupsFragment() {}

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
            setAdapter();
        }
        else if(getArguments()!=null){
            newsDataSource = getArguments().getParcelableArrayList(TAG_NDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_allgroups, container, false);
        mList = (ListView) mView.findViewById(R.id.allGroupsListView);
        BusProvider.getInstance().register(this);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.newsGroupSelected(position);
            }
        });
//        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d(TAG, "LongClicked");
//                new DeleteDialogFragment(getSherlockActivity(), position, "GROUP").show(
//                        getSherlockActivity().getSupportFragmentManager(), DeleteDialogFragment.TAG);
//
//                return false;
//            }
//        });
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
//                mListener.showDialog(new AddNewsGroupDialogFragment(), AddNewsGroupDialogFragment.TAG);
                break;
            }
            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

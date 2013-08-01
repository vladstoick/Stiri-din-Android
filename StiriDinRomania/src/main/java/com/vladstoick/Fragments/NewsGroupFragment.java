package com.vladstoick.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.DialogFragment.AddNewsGroupDialogFragment;
import com.vladstoick.DialogFragment.AddNewsSourceDialogFragment;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.Utils.NewsGroupFragmentAdapter;
import com.vladstoick.stiridinromania.R;

/**
 * Created by vlad on 7/19/13.
 */
public class NewsGroupFragment extends SherlockFragment {
    public static String TAG = "NEWSGROUPFRAGMENT";
    private static String NG_TAG = "NG_TAG";
    private NewsGroup newsGroup;
    private View mView;
    private ListView mList;
//    private FragmentCommunicationInterface mListener;
    NewsGroupFragmentAdapter adapter;
    public NewsGroupFragment() {

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
//            mListener = (FragmentCommunicationInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FragmentCommunicationInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(NG_TAG, newsGroup);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        BusProvider.getInstance().register(this);
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            newsGroup = savedInstanceState.getParcelable(NG_TAG);
        else if (getArguments() != null) {
            newsGroup = getArguments().getParcelable(NG_TAG);
        } else {
            Log.wtf(TAG, "I have no idea why this is happening");
        }
    }

    public static NewsGroupFragment newInstance(NewsGroup ng) {
        NewsGroupFragment mNGF = new NewsGroupFragment();
        Bundle arg = new Bundle();
        arg.putParcelable(NG_TAG, ng);
        mNGF.setArguments(arg);
        return mNGF;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_newsgroup, container, false);
        mList = (ListView) mView.findViewById(R.id.newsGroupListView);
        adapter = new NewsGroupFragmentAdapter(newsGroup, getSherlockActivity());
        mList.setAdapter(adapter);
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_newsgroup,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_add:{
//                mListener.showDialog(new AddNewsSourceDialogFragment(),
//                        AddNewsSourceDialogFragment.TAG);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

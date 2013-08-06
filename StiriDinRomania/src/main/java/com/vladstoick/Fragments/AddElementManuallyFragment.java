package com.vladstoick.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.DataModel.NewsSource;
import com.vladstoick.Utils.AddElementManuallySpinnerAdapter;
import com.vladstoick.stiridinromania.NewsGroupListActivity;
import com.vladstoick.stiridinromania.R;
import com.vladstoick.stiridinromania.StiriApp;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Vlad on 8/4/13.
 */
public class AddElementManuallyFragment extends SherlockFragment{
    private View mView;
    @InjectView(R.id.add_element_group_spinner) Spinner mGroupSpinner;
    @InjectView(R.id.add_element_group_title) EditText mGroupTitle;
    @InjectView(R.id.add_element_feed_title) EditText mSourceTitle;
    @InjectView(R.id.add_element_feed_description) EditText mSourceDescription;
    @InjectView(R.id.add_element_feed_rss) EditText mSourceRss;
    public AddElementManuallyFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_add_elements_manually,container,false);
        Views.inject(this,mView);
        ArrayList<NewsGroup> newsDataSource =
                ((StiriApp)((getSherlockActivity()).getApplication())).newsDataSource
                        .getAllNewsGroups();
        mGroupSpinner.setAdapter(new AddElementManuallySpinnerAdapter(newsDataSource,
                getSherlockActivity()));
        mGroupTitle.setVisibility(newsDataSource.size() == 0
                ? View.VISIBLE : View.GONE);
        mGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGroupTitle.setVisibility(position==mGroupSpinner.getCount()-1
                        ? View.VISIBLE : View.GONE);
                mGroupTitle.requestFocus() ;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_element_manual_fragment,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:{
                NewsSource ns = new NewsSource();
                ns.setTitle(mSourceTitle.getText().toString());
                ns.setDescription(mSourceDescription.getText().toString());
                ns.setRssLink(mSourceRss.getText().toString());
                if(mGroupTitle.getVisibility() == View.VISIBLE &&
                        mGroupTitle.getText().toString() != ""){
                    String groupTitle = mGroupTitle.getText().toString();
                    ((StiriApp)(getSherlockActivity()).getApplication()).newsDataSource
                            .addNewsGroupAndNewsSource(groupTitle, ns);
                }
                else
                {
                    int groupId = (int) mGroupSpinner.getSelectedItemId();
                    ((StiriApp)(getSherlockActivity().getApplication())).newsDataSource.
                        addNewsSource(ns,groupId);
                }

            }
            case R.id.action_cancel:{
                NavUtils.navigateUpTo(getSherlockActivity(), new Intent(getSherlockActivity(),
                        NewsGroupListActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

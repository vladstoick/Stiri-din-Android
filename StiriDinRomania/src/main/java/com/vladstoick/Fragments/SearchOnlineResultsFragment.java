package com.vladstoick.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;


import com.actionbarsherlock.app.SherlockFragment;
import com.vladstoick.DataModel.NewsItem;
import com.vladstoick.Utils.NewsItemAdapter;
import com.vladstoick.stiridinromania.NewsItemDetailActivity;
import com.vladstoick.stiridinromania.R;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Vlad on 8/12/13.
 */
public class SearchOnlineResultsFragment extends SherlockFragment {
    NewsItemAdapter adapter;
    ArrayList<NewsItem> newsItems;
    @InjectView(R.id.newsitem_listview) ListView mListView;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;
    public void setData(ArrayList<NewsItem> results,Activity activity){
        mListView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        newsItems = results;
        adapter = new NewsItemAdapter(activity,results);
        adapter.notifyDataSetChanged();
        try{
            mListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void showProgressIndicator(){
        mListView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_online_results ,container, false);
        Views.inject(this,rootView);
        if(adapter!=null){
            try{
                mListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getBaseContext(),
                        NewsItemDetailActivity.class);
                intent.putExtra(NewsItemDetailFragment.ARG_ITEM_JO,newsItems.get(position));
                intent.putExtra(NewsItemDetailActivity.FROM_SEARCH,true);
                startActivity(intent);
            }
        });
        return rootView;
    }
}

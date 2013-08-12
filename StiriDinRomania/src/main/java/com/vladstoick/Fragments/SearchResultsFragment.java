package com.vladstoick.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.vladstoick.DataModel.NewsItem;
import com.vladstoick.Utils.NewsItemAdapter;
import com.vladstoick.stiridinromania.R;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Vlad on 8/12/13.
 */
public class SearchResultsFragment extends SherlockFragment{
    NewsItemAdapter adapter;
    @InjectView(R.id.newsitem_listview) public ListView mListView;
    public SearchResultsFragment() {
    }

    public void setData(ArrayList<NewsItem> results,Activity activity){
        adapter = new NewsItemAdapter(activity,results);
        adapter.notifyDataSetChanged();
        try{
            mListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);
        Views.inject(this,rootView);
        if(adapter!=null){
            try{
                mListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return rootView;
    }
}

package com.vladstoick.Fragments.AddElement;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.vladstoick.DataModel.NewsDataSource;
import com.vladstoick.DataModel.NewsSource;
import com.vladstoick.Utils.Adapters.AddElementCategoryAdapter;
import com.vladstoick.stiridinromania.AddElementSelectFeedActivity;
import com.vladstoick.stiridinromania.R;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;


/**
 * Created by Vlad on 9/27/13.
 */
public class AddElementCategoryFragment extends SherlockFragment {
    ArrayList<String> categories;
    ArrayList<NewsSource> feeds;
    @InjectView(R.id.categoriesListView) ListView mListView;
    public AddElementCategoryFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_add_elemens_category,container,false);
        Views.inject(this,mView);
        feeds = NewsDataSource.getInstance().feeds;
        categories = new ArrayList<String>();
        for(NewsSource newsSource : feeds){
            if(!categories.contains(newsSource.category)){
                categories.add(newsSource.category);
            }
        }
        mListView.setAdapter(new AddElementCategoryAdapter(getSherlockActivity(),categories));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getSherlockActivity(), AddElementSelectFeedActivity.class);
                String clickedCategory = categories.get(position);
                ArrayList<NewsSource> feedsForCategory = new ArrayList<NewsSource>();

                for(NewsSource ns : feeds ){
                    if(clickedCategory.equals(ns.category)){
                        feedsForCategory.add(ns);
                    }
                }
                clickedCategory = clickedCategory.substring(0,1).toUpperCase()
                        + clickedCategory.substring(1);
                intent.putExtra(AddElementSelectFeedActivity.TAG_FEEDS,feedsForCategory);
                intent.putExtra(AddElementSelectFeedActivity.TAG_TITLE,clickedCategory);
                getSherlockActivity().startActivity(intent);
            }
        });
        return mView;
    }
}

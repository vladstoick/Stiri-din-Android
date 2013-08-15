package com.vladstoick.Utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.vladstoick.DataModel.NewsItem;
import com.vladstoick.DataModel.NewsSource;
import com.vladstoick.Fragments.NewsItemDetailFragment;

import java.util.ArrayList;

/**
 * Created by vladstoick on 8/14/13.
 */
public class NewsItemPagerAdapter extends FragmentStatePagerAdapter {
    public ArrayList<NewsItem> newsItems;
    public boolean fromOnlineSearch;
    public NewsItemPagerAdapter(FragmentManager fm, ArrayList<NewsItem> newsItems){
        super(fm);
        this.newsItems = newsItems;
    }

    @Override
    public SherlockFragment getItem(int i) {
        SherlockFragment fragment = new NewsItemDetailFragment();
        Bundle arguments = new Bundle();
        if(fromOnlineSearch){
            arguments.putParcelable(NewsItemDetailFragment.ARG_ITEM_JO,newsItems.get(i));
        } else {
            arguments.putString(NewsItemDetailFragment.ARG_ITEM, newsItems.get(i).getUrlLink());
        }
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public int getCount() {
        return newsItems.size();
    }
}

package com.vladstoick.OttoBus;

import com.vladstoick.DataModel.NewsItem;

import java.util.ArrayList;

/**
 * Created by Vlad on 8/12/13.
 */
public class SearchResultsEvent {
    public ArrayList<NewsItem> results;
    public SearchResultsEvent(ArrayList<NewsItem> results){
        this.results = results;
    }
}

package com.vladstoick.OttoBus;

import com.vladstoick.DataModel.NewsItem;

import java.util.ArrayList;

/**
 * Created by Vlad on 8/12/13.
 */
public class SearchResultsEvent {
    public ArrayList<NewsItem> results;
    public String query;
    public SearchResultsEvent(ArrayList<NewsItem> results,String query){
        this.results = results;
        this.query = query;
    }
}

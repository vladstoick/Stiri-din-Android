package com.vladstoick.OttoBus;

import com.vladstoick.DataModel.NewsItem;

/**
 * Created by Vlad on 8/7/13.
 */
public class NewsItemLoadedEvent{
    public NewsItem ni;
    public NewsItemLoadedEvent(NewsItem ni){
        this.ni = ni ;
    }
}

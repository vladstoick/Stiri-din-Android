package com.vladstoick.OttoBus;

import com.vladstoick.DataModel.NewsGroup;

import java.util.ArrayList;

/**
 * Created by Vlad on 7/23/13.
 */
public class DataLoadedEvent {
    public static String TAG_NEWSDATASOURCE = "NewsDataSource";
    public static String TAG_NEWSDATASOURCE_MODIFIED = "NewsDataSourceModified";
    public final ArrayList<NewsGroup> nds;
    public final String dataLoadedType;

    public DataLoadedEvent(String type, ArrayList<NewsGroup> nds) {
        this.dataLoadedType = type;
        this.nds = nds;
    }

    @Override
    public String toString() {
        return this.dataLoadedType;
    }

}
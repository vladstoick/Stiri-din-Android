package com.vladstoick.stiridinromania;

import android.app.Application;

import com.squareup.otto.Subscribe;
import com.vladstoick.DataModel.NewsDataSource;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.DataModifiedEvent;

/**
 * Created by Vlad on 8/1/13.
 */
public class StiriApp extends Application {
    public NewsDataSource newsDataSource = null ;
    @Override
    public void onCreate() {
        super.onCreate();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onTerminate() {
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void dataModified(DataModifiedEvent event) {
        if (event.dataModifiedType == DataModifiedEvent.TAG_DELETEGROUP) {
            newsDataSource.deleteNewsGroup(event.id);
        }
        if (event.dataModifiedType == DataModifiedEvent.TAG_GROUPADD) {
            newsDataSource.addNewsGroup(event.title);
        }
    }
}

package com.vladstoick.stiridinromania;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.newrelic.agent.android.NewRelic;

import com.squareup.otto.Subscribe;
import com.vladstoick.DataModel.NewsDataSource;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.DataModifiedEvent;

import java.util.Date;

/**
 * Created by Vlad on 8/1/13.
 */
public class StiriApp extends Application {
    public NewsDataSource newsDataSource = null ;
    public static RequestQueue queue;
    @Override
    public void onCreate() {
        super.onCreate();
        NewRelic.withApplicationToken(
                "AAda1a6278e5ef8e4349079aa07d6b5039aaa395a0"
        ).start(this);
        BusProvider.getInstance().register(this);
        queue = Volley.newRequestQueue(this);

    }

    @Override
    public void onTerminate() {
        BusProvider.getInstance().unregister(this);
    }
}

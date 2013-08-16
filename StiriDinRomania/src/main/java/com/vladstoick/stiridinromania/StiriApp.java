package com.vladstoick.stiridinromania;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.newrelic.agent.android.NewRelic;
import com.vladstoick.DataModel.NewsDataSource;
import com.vladstoick.OttoBus.BusProvider;




/**
 * Created by Vlad on 8/1/13.
 */
public class StiriApp extends Application {
    public NewsDataSource newsDataSource = null;
    public static RequestQueue queue;

    @Override
    public void onCreate() {
        super.onCreate();

        BusProvider.getInstance().register(this);
        queue = Volley.newRequestQueue(this);

    }

    @Override
    public void onTerminate() {
        BusProvider.getInstance().unregister(this);
    }
}

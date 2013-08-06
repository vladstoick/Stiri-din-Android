package com.vladstoick.DataModel;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by vlad on 7/19/13.
 */
public class NewsGroup implements Parcelable {

    public static String TAG_TITLE = "group_title";
    private String title;
    public static String TAG_ID = "group_id";
    private int id;
    public static String TAG_NEWSOURCES = "group_feeds";
    private int noFeeds;
    public static String TAG_NOFEEDS = "group_ids";
    public ArrayList<NewsSource> newsSources;

    public int getNoFeeds() {
        return noFeeds;
    }

    public void setNoFeeds(int noFeeds) {
        this.noFeeds = noFeeds;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public NewsGroup(Cursor cursor) {
        this.id = cursor.getInt(0);
        this.title = cursor.getString(1);
        this.noFeeds = cursor.getInt(2);
    }

    public NewsGroup() {
        newsSources = new ArrayList<NewsSource>();
    }

    public NewsGroup(String title, int id) {
        this.title = title;
        this.id = id;
        newsSources = new ArrayList<NewsSource>();
    }

    //PARCEL
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeTypedList(newsSources);
    }

    private NewsGroup(Parcel in) {
        newsSources = new ArrayList<NewsSource>();
        this.title = in.readString();
        in.readTypedList(newsSources, NewsSource.CREATOR);
    }

    public static final Parcelable.Creator<NewsGroup> CREATOR
            = new Parcelable.Creator<NewsGroup>() {
        public NewsGroup createFromParcel(Parcel in) {
            return new NewsGroup(in);
        }

        public NewsGroup[] newArray(int size) {
            return new NewsGroup[size];
        }
    };


}

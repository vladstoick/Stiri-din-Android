package com.vladstoick.DataModel;

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
    public ArrayList<NewsSource> newsSources;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }
    public NewsGroup()
    {
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

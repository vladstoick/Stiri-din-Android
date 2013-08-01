package com.vladstoick.DataModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by vlad on 7/19/13.
 */
public class NewsSource implements Parcelable {
    public static String TAG_RSSLINK = "url";
    private String rssLink;
    public static String TAG_TITLE = "title";
    private String title;
    public static String TAG_DESCRIPTION = "description";
    private String description;
    public ArrayList<NewsItem> news;
    public static String TAG_ID = "id";
    private int id;
    public NewsSource(String rssLink, String title, String description, int id) {
        this.rssLink = rssLink;
        this.title = title;
        this.description = description;
        this.id = id;
        news = new ArrayList<NewsItem>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRssLink() {

        return rssLink;
    }

    public void setRssLink(String rssLink) {
        this.rssLink = rssLink;
    }

    //PARCELABLE
    public static final Parcelable.Creator<NewsSource> CREATOR
            = new Parcelable.Creator<NewsSource>() {
        public NewsSource createFromParcel(Parcel in) {
            return new NewsSource(in);
        }

        public NewsSource[] newArray(int size) {
            return new NewsSource[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.rssLink);
        dest.writeString(this.title);
        dest.writeString(this.description);
//        dest.writeTypedList(news);
    }

    private NewsSource(Parcel in) {
        news = new ArrayList<NewsItem>();
        this.rssLink = in.readString();
        this.title = in.readString();
        this.description = in.readString();
//        in.readTypedList(news, NewsItem.CREATOR);
    }

}

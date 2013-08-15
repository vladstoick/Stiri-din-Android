package com.vladstoick.DataModel;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vlad on 7/19/13.
 */
public class NewsItem implements Parcelable {
    public static String TAG_DATE = "date";
    public static String TAG_TITLE = "title";
    public static String TAG_DESCRIPTION = "description";
    public static String TAG_URLLINK = "url";
    private String title;
    private String description;
    private String urlLink;
    private long pubDate;
    private int sourceId;

    public NewsItem(String title, String description, String urlLink, long pubDate) {
        this.title = title;
        this.description = description;
        this.urlLink = urlLink;
        this.pubDate = pubDate;
    }

    public NewsItem(Cursor cursor) {
        this.urlLink = cursor.getString(0);
        this.title = cursor.getString(1);
        this.description = cursor.getString(2);
        this.sourceId = cursor.getInt(3);
        this.pubDate = cursor.getLong(4);
    }

    public long getPubDate() {
        return pubDate;
    }

    public void setPubDate(long pubDate) {
        this.pubDate = pubDate;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlLink() {
        return urlLink;
    }

    public void setUrlLink(String urlLink) {
        this.urlLink = urlLink;
    }

    public String getMobilizedUrlLink() {
        return "http://parserizer.eu01.aws.af.cm/?url=" + this.urlLink;
    }

    //PARCELABLE STUFF
    public static final Parcelable.Creator<NewsItem> CREATOR
            = new Parcelable.Creator<NewsItem>() {
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(urlLink);
        dest.writeInt(sourceId);
        dest.writeLong(pubDate);
    }

    private NewsItem(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.urlLink = in.readString();
        this.sourceId = in.readInt();
        this.pubDate = in.readLong();
    }
}

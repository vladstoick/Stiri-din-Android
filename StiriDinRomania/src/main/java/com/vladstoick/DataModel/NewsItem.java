package com.vladstoick.DataModel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vlad on 7/19/13.
 */
public class NewsItem implements Parcelable {
    private String title;
    private String description;
    private String urlLink;

    public NewsItem(String title, String description, String urlLink) {
        this.title = title;
        this.description = description;
        this.urlLink = urlLink;
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
    }

    private NewsItem(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.urlLink = in.readString();
    }
}

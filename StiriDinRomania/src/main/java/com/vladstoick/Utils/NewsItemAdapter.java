package com.vladstoick.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vladstoick.DataModel.NewsItem;
import com.vladstoick.stiridinromania.R;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Vlad on 8/2/13.
 */
public class NewsItemAdapter extends BaseAdapter {
    private static final int SEPARATOR = 0;
    private static final int ITEM = 1;
    static class Holder {
        @InjectView(R.id.title) TextView mTitle;
        @InjectView(R.id.date) TextView mDate;

        public Holder(View mView) {
            Views.inject(this, mView);
        }
    }

    public ArrayList<NewsItem> news;
    Context context;

    public NewsItemAdapter(Context context, ArrayList<NewsItem> news) {
        this.context = context;
        this.news = news;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return SEPARATOR;
        }
        return ITEM;
    }

    @Override
    public int getCount() {
        return news.size() + 2;
    }

    @Override
    public Object getItem(int position) {
        if(position == 0){
            return "Hello World";
        }
        if(position>0){
            return news.get(position-1);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;
        int type = getItemViewType(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            switch(type){
                case ITEM:{
                    row = inflater.inflate(R.layout.list_row_newsitem_list, parent, false);
                    break;
                }
                case SEPARATOR:{
                    row = inflater.inflate(R.layout.list_row_newsitem_list_separator, parent, false);
                    break;
                }
            }
            holder = new Holder(row);
            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }
        switch (type){
            case ITEM:{

                final NewsItem ni = (NewsItem) getItem(position);
                holder.mTitle.setText(ni.getTitle());
                holder.mDate.setText(ni.getPubDateAsString(context));
                break;
            }
            case SEPARATOR:{
                String separatorTitle = (String) getItem(position);
                holder.mTitle.setText(separatorTitle);
                break;
            }
        }

        return row;
    }

}

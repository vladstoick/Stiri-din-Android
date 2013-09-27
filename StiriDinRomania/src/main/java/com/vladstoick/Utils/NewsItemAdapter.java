package com.vladstoick.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
        return news.size();
    }

    @Override
    public Object getItem(int position) {
        return news.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();

             row = inflater.inflate(R.layout.list_row_newsitem_list, parent, false);
            holder = new Holder(row);
            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }
        final NewsItem ni = (NewsItem) getItem(position);
        Log.e("ADAPTER",ni.getTitle());
        holder.mTitle.setText(ni.getTitle());
        holder.mDate.setText(ni.getPubDateAsString(context));

        return row;
    }

}

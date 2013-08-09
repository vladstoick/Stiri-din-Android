package com.vladstoick.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.DataModel.NewsSource;
import com.vladstoick.stiridinromania.R;

import butterknife.InjectView;
import butterknife.Views;


/**
 * Created by vlad on 7/19/13.
 */
public class NewsGroupFragmentAdapter extends BaseAdapter {
    static class RowHolder {
        @InjectView(R.id.newsSourceTitle)
        TextView mTitle;
        @InjectView(R.id.description)
        TextView mDescription;
        @InjectView(R.id.numberOfNews)
        TextView mNumberOfNews;

        public RowHolder(View view) {
            Views.inject(this, view);
        }
    }

    private final Context context;
    private NewsGroup data;

    public void setData(NewsGroup data) {
        this.data = data;
    }

    public NewsGroupFragmentAdapter(NewsGroup data, Context context) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.newsSources.size();
    }

    @Override
    public NewsSource getItem(int position) {
        return data.newsSources.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        RowHolder holder;
        final NewsSource ns = getItem(position);
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.list_row_newssource, parent, false);
            holder = new RowHolder(row);
            row.setTag(holder);
        } else
            holder = (RowHolder) row.getTag();
        holder.mTitle.setText(ns.getTitle());
        holder.mDescription.setText(ns.getDescription());
        holder.mNumberOfNews.setText(ns.getNumberOfUnreadNews() + "");
        return row;
    }
}
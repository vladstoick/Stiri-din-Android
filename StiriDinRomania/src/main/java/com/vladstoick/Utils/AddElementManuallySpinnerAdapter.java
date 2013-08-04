package com.vladstoick.Utils;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.stiridinromania.R;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Vlad on 8/4/13.
 */
public     class AddElementManuallySpinnerAdapter implements SpinnerAdapter {
    class RowHolder {
        @InjectView(R.id.spinner_title)
        TextView mTitle;
        @InjectView(R.id.spinner_image)
        ImageView mAddImage;
        public RowHolder(View view)
        {
            Views.inject(this, view);
        }
    }
    ArrayList<NewsGroup> newsGroups;
    Context context;
    public AddElementManuallySpinnerAdapter(ArrayList<NewsGroup> newsGroups, Context context)
    {
        this.newsGroups = newsGroups;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        RowHolder holder;
        final NewsGroup newsGroup;
        newsGroup = getItem(position);
        if(row==null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.spinner_row_newsgroup_dropdown, parent, false);
            holder = new RowHolder(row);
            row.setTag(holder);
        }
        else
            holder = (RowHolder) row.getTag();
        holder.mTitle.setText(newsGroup.getTitle());
        if(position == getCount() - 1 )
        {
            holder.mAddImage.setImageResource(R.drawable.content_new_dark);
            holder.mAddImage.setVisibility(View.VISIBLE);
        }
        else
            holder.mAddImage.setVisibility(View.INVISIBLE);
        return row;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return newsGroups.size() +1 ;
    }

    @Override
    public NewsGroup getItem(int position) {
        if(position!= getCount() - 1)
            return newsGroups.get(position);
        else
        {
            NewsGroup newsGroup = new NewsGroup();
            newsGroup.setTitle(context.getString(R.string.new_group));
            return newsGroup;
        }
    }

    @Override
    public long getItemId(int position) {
        if(position!= getCount() - 1)
            return newsGroups.get(position).getId();
        return -1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RowHolder holder;
        final NewsGroup newsGroup;
        newsGroup = getItem(position);
        if(row==null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.spinner_row_newsgroup, parent, false);
            holder = new RowHolder(row);
            row.setTag(holder);
        }
        else
            holder = (RowHolder) row.getTag();
        holder.mTitle.setText(newsGroup.getTitle());
        return row;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return newsGroups.size() != 0;
    }
}
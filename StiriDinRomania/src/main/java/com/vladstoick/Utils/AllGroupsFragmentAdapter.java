package com.vladstoick.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.stiridinromania.R;

import java.util.ArrayList;

/**
 * Created by vlad on 7/19/13.
 */
public class AllGroupsFragmentAdapter extends BaseAdapter {
    static class RowHolder {
        TextView mTitle;
        TextView mNumberOfGroups;
    }

    private final Context context;
    private ArrayList<NewsGroup> data;

    public AllGroupsFragmentAdapter(ArrayList<NewsGroup> data, Context context) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public NewsGroup getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RowHolder holder;
        final NewsGroup ng = getItem(position);
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.list_row_allgroups, parent, false);
            holder = new RowHolder();
            holder.mTitle = (TextView) row.findViewById(R.id.groupTitle);
            holder.mNumberOfGroups = (TextView) row.findViewById(R.id.numberOfGroups);
            row.setTag(holder);
        } else {
            holder = (RowHolder) row.getTag();
        }
        holder.mTitle.setText(ng.getTitle());
        String noGroups = ng.newsSources.size()+" ";
        if(ng.newsSources.size()==1)
            noGroups = noGroups + context.getString(R.string.feed).toLowerCase();
        else
            noGroups = noGroups + context.getString(R.string.feeds).toLowerCase();
        holder.mNumberOfGroups.setText(noGroups);
        return row;
    }
}
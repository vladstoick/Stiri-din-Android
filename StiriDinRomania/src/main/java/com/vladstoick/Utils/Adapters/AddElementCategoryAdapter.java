package com.vladstoick.Utils.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.internal.ca;
import com.vladstoick.stiridinromania.R;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Vlad on 9/30/13.
 */
public class AddElementCategoryAdapter extends BaseAdapter {
    private ArrayList<String> categories;
    private Context context;
    static class Holder {
        @InjectView(android.R.id.text1) TextView mTitle;
        public Holder(View mView) {
            Views.inject(this, mView);
        }
    }

    public AddElementCategoryAdapter(Context context, ArrayList<String> categories){
        this.context = context;
        this.categories = categories;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public String getItem(int position) {
        return categories.get(position).toUpperCase();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;
        if(row == null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            holder = new Holder(row);
            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }
        holder.mTitle.setText(getItem(position));
        return row;
    }
}

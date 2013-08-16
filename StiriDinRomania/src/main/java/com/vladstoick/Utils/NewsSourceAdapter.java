package com.vladstoick.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.DataModel.NewsSource;
import com.vladstoick.DialogFragment.RenameDialogFragment;
import com.vladstoick.Fragments.NewsGroupDetailFragment;
import com.vladstoick.stiridinromania.R;
import com.vladstoick.stiridinromania.StiriApp;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;


/**
 * Created by vlad on 7/19/13.
 */
public class NewsSourceAdapter extends BaseAdapter {
    static class RowHolder {
        @InjectView(R.id.newsSourceTitle)
        TextView mTitle;
        @InjectView(R.id.numberOfNews)
        TextView mNumberOfNews;
        @InjectView(R.id.overflow_icon)
        ImageButton mButton;

        public RowHolder(View view) {
            Views.inject(this, view);
        }
    }

    private final Context context;
    private NewsGroup data;
    public StiriApp app;
    public NewsGroupDetailFragment fragment;

    public void setData(NewsGroup data) {
        this.data = data;
    }

    public NewsSourceAdapter(NewsGroup data, Context context, StiriApp app,
                             NewsGroupDetailFragment fragment) {
        this.context = context;
        this.data = data;
        this.app = app;
        this.fragment = fragment;
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
            row = inflater.inflate(R.layout.list_row_newsgroup_detail, parent, false);
            holder = new RowHolder(row);
            row.setTag(holder);
        } else
            holder = (RowHolder) row.getTag();
        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT <= 11) {
                    buildPopUpWindow(v, ns);
                } else {
                    buildPopUpMenu(v, ns);
                }

            }
        });
        holder.mTitle.setText(ns.getTitle());
        holder.mNumberOfNews.setText(ns.getNumberOfUnreadNews() + "");
        return row;
    }

    @TargetApi(11)
    public void buildPopUpMenu(View v, final NewsSource newsSource) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popupmenu_newssource, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete: {
                        deleteSource(newsSource);
                        return true;
                    }
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public void buildPopUpWindow(View v, final NewsSource newsSource) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.popupmenu_newsource, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        deleteSource(newsSource);
                    }
                }
            }
        });
        builder.setInverseBackgroundForced(true);
        builder.create();
        builder.show();
    }

    public void deleteSource(final NewsSource newsSource) {
        app.newsDataSource.deleteNewsSource(newsSource);
    }
}
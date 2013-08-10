package com.vladstoick.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.internal.widget.PopupWindowCompat;
import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.stiridinromania.R;

import java.lang.annotation.Target;
import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by vlad on 7/19/13.
 */
public class AllGroupsFragmentAdapter extends BaseAdapter implements View.OnClickListener{
    static class RowHolder {
        @InjectView(R.id.overflow_icon) ImageButton mButton;
        @InjectView(R.id.groupTitle) TextView mTitle;
        @InjectView(R.id.numberOfGroups) TextView mNumberOfGroups;

        public RowHolder(View view) {
            Views.inject(this, view);
        }
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
            holder = new RowHolder(row);
            holder.mButton.setOnClickListener(this);
            row.setTag(holder);
        } else {
            holder = (RowHolder) row.getTag();
        }
        holder.mTitle.setText(ng.getTitle());

        int noGroups = ng.getNoFeeds();
        String noGroupsString;
        if (noGroups == 1)
            noGroupsString = noGroups + " " + context.getString(R.string.feed).toLowerCase();
        else
            noGroupsString = noGroups + " " + context.getString(R.string.feeds).toLowerCase();
        holder.mNumberOfGroups.setText(noGroupsString);
        return row;
    }
    @Override
    public void onClick(View v) {
        if(Build.VERSION.SDK_INT>11){
            buildPopUpMenu(v);
        } else {
            buildPopUpWindow(v);
        }
    }
    @TargetApi(11)
    public void buildPopUpMenu(View v){
        PopupMenu popupMenu = new PopupMenu(context,v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popupmenu_newsgroup, popupMenu.getMenu());
        popupMenu.show();
    }
    public void buildPopUpWindow(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ArrayList<String> list = new ArrayList<String>();
        list.add("adda");list.add("ggdgd");
        builder.setItems(R.array.popupmenu_newsgroup,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setInverseBackgroundForced(true);
        builder.create();
        builder.show();
    }
}
package com.vladstoick.stiridinromania;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.vladstoick.DataModel.NewsDataSource;
import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.DataModel.NewsSource;
import com.vladstoick.Utils.AddElementManuallySpinnerAdapter;
import com.vladstoick.stiridinromania.R;


import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;

public class AddElementSelectGroupActivity extends SherlockFragmentActivity
        implements Button.OnClickListener {
    public static final String TAG_FEED = "FEEDDATA";
    @InjectView(R.id.add_element_group_spinner) Spinner mGroupSpinner;
    @InjectView(R.id.add_element_group_title) EditText mGroupTitle;
    @InjectView(R.id.donebutton) Button mAddButton;
    public NewsSource newsSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elementselectgroupactivity);
        Views.inject(this);
        newsSource = getIntent().getParcelableExtra(TAG_FEED);
        setTitle(newsSource.getTitle());
        ArrayList<NewsGroup> newsDataSource = NewsDataSource.getInstance().getAllNewsGroups();
        mGroupSpinner.setAdapter(new AddElementManuallySpinnerAdapter(newsDataSource,this));
        mGroupTitle.setVisibility(newsDataSource.size() == 0 ? View.VISIBLE : View.GONE);
        mAddButton.setOnClickListener(this);
        mGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGroupTitle.setVisibility(position == mGroupSpinner.getCount() - 1
                        ? View.VISIBLE : View.GONE);
                mGroupTitle.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        NewsSource ns = new NewsSource();
        ns.setRssLink(newsSource.getRssLink().toString());
        if (mGroupTitle.getVisibility() == View.VISIBLE &&
                mGroupTitle.getText().toString() != "") {
            String groupTitle = mGroupTitle.getText().toString();
            NewsDataSource.getInstance().addNewsGroupAndNewsSource(groupTitle, ns);
        } else {
            int groupId = (int) mGroupSpinner.getSelectedItemId();
            NewsDataSource.getInstance().addNewsSource(ns, groupId);
        }
        Intent intent = new Intent(this,NewsGroupListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}

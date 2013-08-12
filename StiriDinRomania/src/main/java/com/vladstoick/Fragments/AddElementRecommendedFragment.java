package com.vladstoick.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.Utils.AddElementManuallySpinnerAdapter;
import com.vladstoick.stiridinromania.R;
import com.vladstoick.stiridinromania.StiriApp;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Vlad on 8/4/13.
 */
public class AddElementRecommendedFragment extends SherlockFragment {
    @InjectView(R.id.add_element_group_title)EditText mGroupTitle;
    @InjectView(R.id.add_element_group_spinner) Spinner mGroupSpinner;
    @InjectView(R.id.add_element_category_spinner) Spinner mCategorySpinner;
    @InjectView(R.id.add_element_feed_spinner) Spinner mFeedSpinner;
    public AddElementRecommendedFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_add_elements_recommended,container, false);
        Views.inject(this, view);
        ArrayList<NewsGroup> newsDataSource =
                ((StiriApp) ((getSherlockActivity()).getApplication())).newsDataSource
                        .getAllNewsGroups();
        mGroupSpinner.setAdapter(new AddElementManuallySpinnerAdapter(newsDataSource,
                getSherlockActivity()));
        mCategorySpinner.setAdapter(new AddElementManuallySpinnerAdapter(newsDataSource,
                getSherlockActivity()));
        mFeedSpinner.setAdapter(new AddElementManuallySpinnerAdapter(newsDataSource,
                getSherlockActivity()));
        mGroupTitle.setVisibility(newsDataSource.size() == 0 ? View.VISIBLE : View.GONE);
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
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_element_manual_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}

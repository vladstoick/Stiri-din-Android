package com.vladstoick.Fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.otto.Subscribe;
import com.vladstoick.DataModel.NewsItem;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.NewsItemLoadedEvent;
import com.vladstoick.stiridinromania.R;
import com.vladstoick.stiridinromania.StiriApp;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.InjectView;
import butterknife.Views;

//import com.vladstoick.stiridinromania.myapp.dummy.DummyContent;

/**
 * A fragment representing a single NewsItem detail screen.
 * This fragment is either contained in a {@link com.vladstoick.stiridinromania.NewsItemListActivity}
 * in two-pane mode (on tablets) or a {@link com.vladstoick.stiridinromania.NewsItemDetailActivity}
 * on handsets.
 */
public class NewsItemDetailFragment extends SherlockFragment {
    public static final String ARG_NEWSOURCE ="newsource_id";
    public static final String ARG_ITEM = "item_id";
    public static final String ARG_ITEMPOSITION = "item_position";
    public static final String ARG_ITEM_JO = "item_jo";
    private int isInMode = 0;
    public static final String ARG_MODE = "mode";
    private NewsItem mItem;
    @InjectView(R.id.news_item_detail_webView) WebView mWebView;
    @InjectView(R.id.news_item_detail_title) TextView mTitle;
    @InjectView(R.id.news_item_detail_paperized) TextView mPaperized;
    @InjectView(R.id.news_item_detail_date) TextView mDate;
//    @InjectView(R.id.news_item_scrollview) ScrollView mScrollView;

    public NewsItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null && savedInstanceState.containsKey(ARG_MODE))
            isInMode = savedInstanceState.getInt(ARG_MODE);
        if (getArguments().containsKey(ARG_ITEM)) {
            String url =getArguments().getString(ARG_ITEM);
            mItem = ((StiriApp)getSherlockActivity().getApplication())
                    .newsDataSource.getNewsItem(url);
        } else if (getArguments().containsKey(ARG_ITEM_JO)){
            mItem = getArguments().getParcelable(ARG_ITEM_JO);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_MODE, isInMode);
    }

    @Subscribe public void onItemViewLoaded(NewsItemLoadedEvent event){
        if(mItem.getUrlLink().equals(event.ni.getUrlLink()) ){
            mItem = event.ni;
            mPaperized.setText(Html.fromHtml(mItem.getDescription()));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_newsitem_detail, container, false);
        Views.inject(this, rootView);
        if (mItem != null) {
            mTitle.setText(mItem.getTitle());
            mDate.setText(mItem.getPubDateAsString(getSherlockActivity().getApplicationContext()));

            if(!mItem.getDescription().equals("null")){
                mPaperized.setText(Html.fromHtml(mItem.getDescription()));
            } else {
                BusProvider.getInstance().register(this);
                mPaperized.setText(getString(R.string.loading));
                ((StiriApp)(getSherlockActivity().getApplication())).newsDataSource
                        .paperizeNewsItem(mItem);
            }
            mWebView.setWebViewClient(new WebViewClient());
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news_item_detail_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_full_article: {
                if (isInMode == 0) {
                    mWebView.setVisibility(View.VISIBLE);
                    mTitle.setVisibility(View.GONE);
                    mPaperized.setVisibility(View.GONE);
                    mWebView.loadUrl(mItem.getUrlLink());
                    isInMode = 1;
                } else {
                    mWebView.setVisibility(View.GONE);
                    mTitle.setVisibility(View.VISIBLE);
                    mPaperized.setVisibility(View.VISIBLE);
                    isInMode = 0;
                }

            }
        }
        return super.onOptionsItemSelected(item);
    }
}

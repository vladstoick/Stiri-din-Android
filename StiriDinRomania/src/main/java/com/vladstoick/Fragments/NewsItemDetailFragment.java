package com.vladstoick.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.vladstoick.DataModel.NewsItem;
import com.vladstoick.stiridinromania.R;

import butterknife.InjectView;
import butterknife.Views;

//import com.mycompany.myapp.dummy.DummyContent;

/**
 * A fragment representing a single NewsItem detail screen.
 * This fragment is either contained in a {@link com.vladstoick.stiridinromania.NewsItemListActivity}
 * in two-pane mode (on tablets) or a {@link com.vladstoick.stiridinromania.NewsItemDetailActivity}
 * on handsets.
 */
public class NewsItemDetailFragment extends SherlockFragment {
    public static final String ARG_ITEM = "item_id";
    private int isInMode = 0;
    public static final String ARG_MODE = "mode";
    private NewsItem mItem;
    @InjectView(R.id.news_item_detail_webView) WebView mWebView;
    @InjectView(R.id.news_item_detail_title) TextView mTitle;
    public NewsItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(savedInstanceState!=null && savedInstanceState.containsKey(ARG_MODE))
            isInMode = savedInstanceState.getInt(ARG_MODE);
        if (getArguments().containsKey(ARG_ITEM)) {
            mItem = getArguments().getParcelable(ARG_ITEM);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_MODE,isInMode);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_newsitem_detail, container, false);
        Views.inject(this,rootView);

        if (mItem != null) {
            mTitle.setText(mItem.getTitle());
            mWebView.loadData(mItem.getDescription(),"text/html; charset=utf-8", null);
            mWebView.setWebViewClient(new WebViewClient());
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news_item_detail_fragment,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_full_article:{
                if(isInMode == 0 ){
                    mTitle.setVisibility(View.GONE);
                    mWebView.loadUrl(mItem.getUrlLink());
                    isInMode = 1;
                } else{
                    mTitle.setVisibility(View.VISIBLE);
                    isInMode = 0;
                    mWebView.loadData(mItem.getDescription(),"text/html; charset=utf-8", null);
                }

            }
        }
        return super.onOptionsItemSelected(item);
    }
}

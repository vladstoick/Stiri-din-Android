package com.vladstoick.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

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
public class NewsItemDetailFragment extends Fragment {
    public static final String ARG_ITEM = "item_id";
    private NewsItem mItem;
    @InjectView(R.id.news_item_detail_webView) WebView mWebView;
    public NewsItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM)) {
            mItem = getArguments().getParcelable(ARG_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_newsitem_detail, container, false);
        Views.inject(this,rootView);
        if (mItem != null) {
            mWebView.loadData(mItem.getDescription(),"text/html","utf-8");
        }

        return rootView;
    }
}

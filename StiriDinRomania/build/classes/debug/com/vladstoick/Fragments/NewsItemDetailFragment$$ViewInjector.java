// Generated code from Butter Knife. Do not modify!
package com.vladstoick.Fragments;

import android.view.View;
import butterknife.Views.Finder;

public class NewsItemDetailFragment$$ViewInjector {
  public static void inject(Finder finder, final com.vladstoick.Fragments.NewsItemDetailFragment target, Object source) {
    View view;
    view = finder.findById(source, 2130968668);
    target.mWebView = (android.webkit.WebView) view;
    view = finder.findById(source, 2130968667);
    target.mTitle = (android.widget.TextView) view;
  }

  public static void reset(com.vladstoick.Fragments.NewsItemDetailFragment target) {
    target.mWebView = null;
    target.mTitle = null;
  }
}

// Generated code from Butter Knife. Do not modify!
package com.vladstoick.Utils;

import android.view.View;
import butterknife.Views.Finder;

public class NewsGroupAdapter$RowHolder$$ViewInjector {
  public static void inject(Finder finder, final com.vladstoick.Utils.NewsGroupAdapter.RowHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131165277);
    target.mButton = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131165278);
    target.mTitle = (android.widget.TextView) view;
    view = finder.findById(source, 2131165279);
    target.mNumberOfGroups = (android.widget.TextView) view;
  }

  public static void reset(com.vladstoick.Utils.NewsGroupAdapter.RowHolder target) {
    target.mButton = null;
    target.mTitle = null;
    target.mNumberOfGroups = null;
  }
}

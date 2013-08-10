// Generated code from Butter Knife. Do not modify!
package com.vladstoick.Utils;

import android.view.View;
import butterknife.Views.Finder;

public class AllGroupsFragmentAdapter$RowHolder$$ViewInjector {
  public static void inject(Finder finder, final com.vladstoick.Utils.AllGroupsFragmentAdapter.RowHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131034204);
    target.mButton = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131034205);
    target.mTitle = (android.widget.TextView) view;
    view = finder.findById(source, 2131034206);
    target.mNumberOfGroups = (android.widget.TextView) view;
  }

  public static void reset(com.vladstoick.Utils.AllGroupsFragmentAdapter.RowHolder target) {
    target.mButton = null;
    target.mTitle = null;
    target.mNumberOfGroups = null;
  }
}

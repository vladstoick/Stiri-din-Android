// Generated code from Butter Knife. Do not modify!
package com.vladstoick.Utils;

import android.view.View;
import butterknife.Views.Finder;

public class AllGroupsFragmentAdapter$RowHolder$$ViewInjector {
  public static void inject(Finder finder, final com.vladstoick.Utils.AllGroupsFragmentAdapter.RowHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131099736);
    target.mTitle = (android.widget.TextView) view;
    view = finder.findById(source, 2131099737);
    target.mNumberOfGroups = (android.widget.TextView) view;
  }

  public static void reset(com.vladstoick.Utils.AllGroupsFragmentAdapter.RowHolder target) {
    target.mTitle = null;
    target.mNumberOfGroups = null;
  }
}

// Generated code from Butter Knife. Do not modify!
package com.vladstoick.Fragments;

import android.view.View;
import butterknife.Views.Finder;

public class AddElementManuallyFragment$$ViewInjector {
  public static void inject(Finder finder, final com.vladstoick.Fragments.AddElementManuallyFragment target, Object source) {
    View view;
    view = finder.findById(source, 2131099726);
    target.mGroupSpinner = (android.widget.Spinner) view;
    view = finder.findById(source, 2131099727);
    target.mGroupTitle = (android.widget.EditText) view;
    view = finder.findById(source, 2131099728);
    target.mSourceTitle = (android.widget.EditText) view;
    view = finder.findById(source, 2131099729);
    target.mSourceDescription = (android.widget.EditText) view;
    view = finder.findById(source, 2131099730);
    target.mSourceRss = (android.widget.EditText) view;
  }

  public static void reset(com.vladstoick.Fragments.AddElementManuallyFragment target) {
    target.mGroupSpinner = null;
    target.mGroupTitle = null;
    target.mSourceTitle = null;
    target.mSourceDescription = null;
    target.mSourceRss = null;
  }
}

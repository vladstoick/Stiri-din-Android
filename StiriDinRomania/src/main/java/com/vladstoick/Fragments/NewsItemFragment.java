package com.vladstoick.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.vladstoick.stiridinromania.R;

/**
 * Created by vlad on 7/19/13.
 */
public class NewsItemFragment extends SherlockFragment {
    public NewsItemFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_newsitem, container, false);
    }
}

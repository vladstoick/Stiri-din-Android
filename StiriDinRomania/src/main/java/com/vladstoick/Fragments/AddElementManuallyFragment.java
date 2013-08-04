package com.vladstoick.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.vladstoick.stiridinromania.R;

/**
 * Created by Vlad on 8/4/13.
 */
public class AddElementManuallyFragment extends SherlockFragment{
    private View mView;
    public AddElementManuallyFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_add_elements_manually,container,false);
        return mView;
    }
}

package com.pranav.raut.apps.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.pranav.raut.apps.PostActivity;
import com.pranav.raut.apps.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created on 12-04-19 at 4:46 by Pranav Raut.
 * For KrishiMitra
 */
public class ExploreFragment extends GFragment {

    private View mParent;
    private AppCompatActivity activity;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mParent = inflater.inflate(R.layout.home_explore_fragment, container, false);
        init();
        return mParent;
    }

    private void init() {
        activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(mParent.findViewById(R.id.toolbar));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
                startActivity(new Intent(activity, PostActivity.class));
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.explore_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}

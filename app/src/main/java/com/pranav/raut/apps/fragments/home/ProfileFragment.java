package com.pranav.raut.apps.fragments.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.pranav.raut.apps.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import pranav.utilities.Logger;

/**
 * Created on 12-04-19 at 4:46 by Pranav Raut.
 * For KrishiMitra
 */
public class ProfileFragment extends GFragment {

    private static String TAG = "ProfileFragment";
    private Logger mLogger = new Logger(true, TAG);

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mParent = inflater.inflate(R.layout.home_profile_fragment, container, false);
        AppBarLayout appBarLayout = mParent.findViewById(R.id.appbar);
        Log.d(TAG, "onCreateView: a" + appBarLayout);
        CollapsingToolbarLayout toolbarLayout = mParent.findViewById(R.id.collapsingToolbar);
        return mParent;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
                break;
        }
    }
}

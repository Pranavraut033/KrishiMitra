package com.pranav.raut.apps.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.pranav.raut.apps.R;
import com.pranav.raut.apps.SplashScreen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import pranav.utilities.Animations;
import pranav.utilities.Utilities;

/**
 * Created on 12-04-19 at 4:46 by Pranav Raut.
 * For KrishiMitra
 */
public class ProfileFragment extends GFragment {

    private Toolbar mToolbar;

    @Nullable

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mParent = inflater.inflate(R.layout.home_profile_fragment, container, false);
        mToolbar = mParent.findViewById(R.id.toolbar);
        mParent.findViewById(R.id.logout_btn).setOnClickListener(this);
        Animations.toggleVisibility(mParent.findViewById(R.id.progressBar));
        init();
        return mParent;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void gotUser() {

        mToolbar.setTitle(Utilities.titleCase(mUser.getName()));
        activity.setSupportActionBar(mToolbar);
        activity.setTitle(Utilities.titleCase(mUser.getName()));

        ((TextView) mParent.findViewById(R.id.email)).setText(mUser.getEmail());
        ((TextView) mParent.findViewById(R.id.num_bookmarks)).setText(String.valueOf(mUser.getBookmarks().size()));
        Animations.toggleVisibility(mParent.findViewById(R.id.progressBar));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout_btn:
                FirebaseAuth.getInstance().signOut();
                activity.startActivity(new Intent(activity, SplashScreen.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
        }
    }
}

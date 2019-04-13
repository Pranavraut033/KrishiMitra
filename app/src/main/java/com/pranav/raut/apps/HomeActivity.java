package com.pranav.raut.apps;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pranav.raut.apps.fragments.home.ExploreFragment;
import com.pranav.raut.apps.fragments.home.GFragment;
import com.pranav.raut.apps.fragments.home.ProfileFragment;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private GFragment currentFragment;
    private int currentTabId;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        if (currentTabId == item.getItemId()) return false;
        GFragment fragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_explore:
                fragment = new ExploreFragment();
                break;
            case R.id.navigation_bookmark:
                fragment = null;

                break;
            case R.id.navigation_profile:
                fragment = new ProfileFragment();
                break;
        }
        currentTabId = item.getItemId();
        if (fragment != null) {
            loadFragment(fragment);
            return true;
        } else return false;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    void loadFragment(GFragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment).commit();
        this.currentFragment = fragment;
    }

    private void init() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(new ExploreFragment());
    }

    public void clickListener(View view) {
        if (currentFragment != null) currentFragment.onClick(view);
    }
}

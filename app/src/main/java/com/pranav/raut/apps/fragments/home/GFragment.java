package com.pranav.raut.apps.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.pranav.raut.apps.R;
import com.pranav.raut.apps.adapters.PostListAdapter;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import model.PostSchema;
import model.UserSchema;
import pranav.lib.PaytmHelper;
import pranav.utils.AuthUtils;
import pranav.utils.StorageUtils;
import pranav.utils.StoreUtils;

/**
 * Created on 12-04-19 at 5:14 by Pranav Raut.
 * For KrishiMitra
 */
public abstract class GFragment extends Fragment implements View.OnClickListener {
    protected View mParent;
    protected UserSchema mUser;
    protected StoreUtils mStoreUtils;
    protected AppCompatActivity activity;
    protected StorageUtils mStorageUtils;

    protected void init() {
        activity = (AppCompatActivity) getActivity();
        if (activity == null) return;
        activity.setSupportActionBar(mParent.findViewById(R.id.toolbar));

        AuthUtils authUtils = new AuthUtils(activity, FirebaseAuth.getInstance(), false);
        mStorageUtils = new StorageUtils(activity, FirebaseStorage.getInstance());
        mStoreUtils = new StoreUtils(activity, FirebaseFirestore.getInstance());

        UserSchema.Companion.get(mStoreUtils,
                Objects.requireNonNull(authUtils.getCurrentUser()).getUid(),
                result -> {
                    mUser = result;
                    gotUser();
                }
        );
    }

    protected abstract void gotUser();
}

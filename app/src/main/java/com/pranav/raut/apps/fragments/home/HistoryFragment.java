package com.pranav.raut.apps.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.pranav.raut.apps.PostActivity;
import com.pranav.raut.apps.R;
import com.pranav.raut.apps.adapters.PostListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import model.PostSchema;
import pranav.lib.PaytmHelper;
import pranav.utilities.Logger;
import pranav.utilities.Utilities;
import pranav.utils.StorageUtils;
import pranav.utils.StoreUtils.Condition;


/**
 * Created on 12-04-19 at 4:46 by Pranav Raut.
 * For KrishiMitra
 */
public class HistoryFragment extends GFragment {

    private static String TAG = "BookmarkFragment";
    private Logger mLogger = new Logger(true, TAG);

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView mRecyclerView;
    private ArrayList<PostSchema> posts = new ArrayList<>();
    private PostListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mParent = inflater.inflate(R.layout.home_history_fragment, container, false);

        mRecyclerView = mParent.findViewById(R.id.main_list);
        refreshLayout = mParent.findViewById(R.id.refresh_layout);
        refreshLayout.setRefreshing(true);

        init();
        return mParent;
    }

    @Override
    protected void init() {
        super.init();
        activity.setSupportActionBar(mParent.findViewById(R.id.toolbar));
    }

    @Override
    protected void gotUser() {
        StorageUtils mStorageUtils = new StorageUtils(activity, FirebaseStorage.getInstance());
        PaytmHelper paytmHelpher = new PaytmHelper(activity);

        mAdapter = new PostListAdapter(posts, paytmHelpher, mStorageUtils, mStoreUtils);
        mAdapter.setUser(mUser);
        Utilities.initRec(null, mRecyclerView);

        mRecyclerView.setAdapter(mAdapter);

        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            mStoreUtils.collection("/posts", new Condition[0],
                    this::updateList, mLogger::w, Source.DEFAULT);
        });


        mStoreUtils.collection("/posts", new Condition[0],
                this::updateList, mLogger::w, Source.DEFAULT)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e == null) {
                        if (queryDocumentSnapshots != null) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            updateList(list);
                        }
                    }
                });
    }

    private void updateList(List<DocumentSnapshot> list) {
        refreshLayout.setRefreshing(true);
        posts.clear();
        for (DocumentSnapshot snapshot : list) {
            PostSchema schema = new PostSchema(Objects.requireNonNull(snapshot.getData()), snapshot.getId());
            if (mUser.getUid().equals(schema.getBoughtBy()))
                posts.add(schema);
        }
        mAdapter.notifyDataSetChanged();

        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.explore_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}

package com.pranav.raut.apps.adapters;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.pranav.raut.apps.R;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import model.PostSchema;
import model.UserSchema;
import pranav.lib.PaytmHelper;
import pranav.utilities.Animations;
import pranav.utilities.Animations.AnimatingDimensions;
import pranav.utilities.Utilities;
import pranav.utils.StorageUtils;
import pranav.utils.StoreUtils;


/**
 * Created on 13-04-19 at 17:55 by Pranav Raut.
 * For KrishiMitra
 */
public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostView> {

    private final ArrayList<PostSchema> mSchemas;
    private final PaytmHelper mPaytmHelper;
    private final StorageUtils mStorageUtils;
    private final StoreUtils mStoreUtils;

    private UserSchema mUser;

    public PostListAdapter(ArrayList<PostSchema> schemas,
                           PaytmHelper paytmHelper,
                           StorageUtils storageUtils, StoreUtils storeUtils) {
        this.mSchemas = schemas;
        this.mPaytmHelper = paytmHelper;
        this.mStorageUtils = storageUtils;
        this.mStoreUtils = storeUtils;
    }

    @NonNull
    @Override
    public PostView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.postitem_layout, parent, false);
        return new PostView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostView holder, int position) {
        holder.init(mSchemas.get(position));
    }

    @Override
    public int getItemCount() {
        return mSchemas.size();
    }

    public void setUser(UserSchema user) {
        this.mUser = user;
    }

    class PostView extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AnimatingDimensions animate;
        private TextView title, description, date, price, time;
        private ImageView photo;
        private PostSchema post;
        private ProgressBar bar;
        private Utilities.Resources res;
        private Boolean expanded = false;

        PostView(@NonNull View itemView) {
            super(itemView);

            res = new Utilities.Resources(itemView.getContext());

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);

            price = itemView.findViewById(R.id.price);
            photo = itemView.findViewById(R.id.photo);
            bar = itemView.findViewById(R.id.progressbar);
            animate = new AnimatingDimensions(
                    photo,
                    res.getPx(78),
                    res.getPx(224))
                    .setDuration(350);
        }

        private void init(PostSchema schema) {
            this.post = schema;

            MaterialButton buyBtn = itemView.findViewById(R.id.buy_btn);
            MaterialButton bookmarkBtn = itemView.findViewById(R.id.bookmark_btn);

            itemView.findViewById(R.id.cardView).setOnClickListener(this);
            buyBtn.setOnClickListener(this);
            bookmarkBtn.setOnClickListener(this);


            title.setText(schema.getTitle());
            date.setText(schema.getDate());
            time.setText(schema.getTime());
            description.setText(schema.getDescription());
            price.setText(String.valueOf(schema.getPrice()));

            bookmarkBtn.setIconResource(mUser.getBookmarks().indexOf(post.getId()) != -1 ?
                    R.drawable.ic_check :
                    R.drawable.ic_bookmark);

            if (post.isBought()) {
                Animations.animateAlpha(buyBtn, 0);
                Animations.animateAlpha(bookmarkBtn, 0);
                price.setText(R.string.sold_already);
            } else if (Objects.equals(post.getPostedBy(), mUser.getUid())) {
                buyBtn.setText(R.string.cant_buy);
                buyBtn.setEnabled(false);
            } else {
                Animations.animateAlpha(buyBtn, 1);
                Animations.animateAlpha(bookmarkBtn, 1);
            }

            File cacheDir = itemView.getContext().getExternalCacheDir();
            String filename = (schema.getCategory() + "_" + schema.getTitle())
                    .replaceAll("[\\s]+", "_").toLowerCase() + ".jpg";
            File file = new File(cacheDir, filename);

            if (!file.exists()) {
                bar.setIndeterminate(false);
                Animations.animateAlpha(bar, 1);
                mStorageUtils.downloadFile("posts/images/" + filename, file, taskSnapshot -> {
                    if (file.exists()) {
                        photo.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                    }
                    Animations.animateAlpha(bar, 0);
                }, e -> Animations.animateAlpha(bar, 0), bar::setProgress);
            } else {
                photo.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buy_btn:
                    bar.setIndeterminate(true);
                    Animations.animateAlpha(bar, 1);
                    mPaytmHelper.initPayment(post, mUser, result -> {
                        Animations.animateAlpha(bar, 0);

                        if (result != null) {
                            if (Objects.equals(result.getString("STATUS"), "TXN_FAILURE")) {
                                Toast.makeText(v.getContext(), "Transaction Failed",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }
                            post.buy(mUser);
                            post.update(mStoreUtils, null, null);
                        }
                    });

                    break;
                case R.id.bookmark_btn:
                    MaterialButton v1 = (MaterialButton) v;
                    boolean b = mUser.addBookmark(post);
                    v1.setIconResource(b ?
                            R.drawable.ic_check :
                            R.drawable.ic_bookmark);
                    mUser.update(mStoreUtils, null, null);

                    if (!b) {
                        notifyDataSetChanged();
                    }
                    break;
                case R.id.cardView:
                    animate.animate(expanded = !expanded);
                    break;
            }
        }
    }
}

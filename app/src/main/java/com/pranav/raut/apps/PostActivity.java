package com.pranav.raut.apps;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import model.PostSchema;
import pranav.utilities.Animations;
import pranav.utilities.Logger;
import pranav.utilities.Utilities;
import pranav.utils.StorageUtils;
import pranav.utils.StoreUtils;
import pranav.views.TextField.TextField;
import pranav.views.photoPicker.PhotoPicker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "PostActivity";
    private static final int PERMISSION_REQUEST = 123;
    private static final int PICKER_REQUEST_CODE = 456;

    private TextField mTitleField, mPriceField, mCategoryField, mDescriptionField;
    private ProgressBar mProgressbar;

    private PostTask mPostTask;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private StoreUtils mStore;
    private StorageUtils mStorageUtils;

    private Logger mLogger = new Logger(true, true, TAG);
    private PhotoPicker mPhotoPicker;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mPhotoPicker = new PhotoPicker(this, PICKER_REQUEST_CODE);
        mStore = new StoreUtils(this, FirebaseFirestore.getInstance());
        mStorageUtils = new StorageUtils(this, FirebaseStorage.getInstance());
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        mTitleField = findViewById(R.id.title_field);
        mCategoryField = findViewById(R.id.category_field);
        mDescriptionField = findViewById(R.id.description_field);
        mPriceField = findViewById(R.id.price_field);

        mProgressbar = findViewById(R.id.main_progressbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (resultCode == RESULT_OK) mPhotoPicker.picker();
                else Toast.makeText(this, "Permission Required", Toast.LENGTH_LONG).show();
                break;
            case PICKER_REQUEST_CODE:
                ImageView imageView = findViewById(R.id.image_gallery);
                if (resultCode == RESULT_OK) {
                    image = mPhotoPicker.processResult(data);
                    imageView.setImageBitmap(image);
                    Animations.toggleVisibility(imageView);
                    ((Button) findViewById(R.id.image_picker_btn)).setText(R.string.replace_image);
                } else {
                    image = null;
                    ((Button) findViewById(R.id.image_picker_btn)).setText(R.string.attach_image_btn_text);
                    Animations.toggleVisibility(imageView);
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void attemptPost() {
        if (mPostTask != null) return;

        final String ERROR_TEXT = getString(R.string.error_field_required);

        mTitleField.setError(null);
        mDescriptionField.setError(null);
        mPriceField.setError(null);
        mCategoryField.setError(null);

        String title = mTitleField.getText();
        String description = String.valueOf(mDescriptionField.getText());
        String price = mPriceField.getText();
        String category = mCategoryField.getText();
        if ((mTitleField.setErrorIfEmpty(ERROR_TEXT) ||
                mPriceField.setErrorIfEmpty(ERROR_TEXT)) ||
                mCategoryField.setErrorIfEmpty(ERROR_TEXT) ||
                mDescriptionField.setErrorIfEmpty(ERROR_TEXT)) {
            return;
        }

        mPostTask = new PostTask(title, description, Integer.parseInt(price), category,
                image);
        mPostTask.execute();
        showProgress(true);
    }

    private void showProgress(final boolean show) {
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(!show);
            supportActionBar.setDisplayShowHomeEnabled(!show);
        }
        mProgressbar.animate().alpha(show ? 1f : 0f).start();
        Animations.animateAlpha(findViewById(R.id.bg), show ? 1f : 0f);
    }

    public void clickListener(View view) {
        switch (view.getId()) {
            case R.id.post_btn:
                attemptPost();
                break;
            case R.id.image_picker_btn:
                boolean b = Utilities.checkAndAsk(this, PERMISSION_REQUEST,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE);

                if (b) {
                    mPhotoPicker.picker();
                }

                break;
            case R.id.bg:
                break;
        }
    }


    @SuppressLint("StaticFieldLeak")
    public class PostTask extends AsyncTask<Void, Void, Boolean> {

        private final String mTitle;
        private final String mDescription;
        private final Integer mPrice;
        private final String mCategory;
        @Nullable
        private final Bitmap mPhoto;

        PostTask(String title, String description, Integer price, String category,
                 @Nullable Bitmap photo) {
            this.mTitle = title;
            this.mDescription = description;
            this.mPrice = price;
            this.mCategory = category;
            this.mPhoto = photo;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            OnFailureListener failureListener = e -> {
                onPostExecute(false);
                mLogger.w(e);
            };
            final String uid = firebaseAuth.getUid();

            if (uid != null) {
                if (mPhoto != null) {
                    try {
                        File file = saveFile(mPhoto);
                        final String path = file.toString();
                        Uri a = Uri.fromFile(file);
                        mProgressbar.setIndeterminate(false);
                        mStorageUtils.uploadFileFromUri(a, "posts/images",
                                taskSnapshot -> {
                                    PostSchema post = new PostSchema(mTitle, mDescription, mPrice, mCategory, path, uid);
                                    post.insert(mStore, t -> {
                                        mLogger.d("Posted with id:", post.getId());
                                        onPostExecute(true);
                                    }, failureListener);
                                }, failureListener, result -> {
                                    if (result != null) {
                                        mProgressbar.setProgress(result);
                                    } else {
                                        mProgressbar.setIndeterminate(true);
                                    }
                                });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    PostSchema post = new PostSchema(mTitle, mDescription, mPrice, mCategory,
                            null, uid);
                    post.insert(mStore, t -> {
                        mLogger.d("Posted with id:", post.getId());
                        onPostExecute(true);
                    }, failureListener);
                }

            } else return false;

            return null;
        }

        private File saveFile(Bitmap photo) throws IOException {
            File cacheDir = Objects.requireNonNull(getExternalCacheDir());
            File file = new File(cacheDir,
                    (mCategory + "_" + mTitle).replaceAll("[\\s]+", "_").toLowerCase() + ".jpg");

            OutputStream fOut = new FileOutputStream(file);

            photo.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

//            MediaStore.Images.Media.insertImage(getContentResolver(),
//                    file.getAbsolutePath(), file.getName(), file.getName());
            return file;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success != null) {
                close();
                Toast.makeText(
                        PostActivity.this,
                        success ? R.string.post_success_message :
                                R.string.post_error_message,
                        Toast.LENGTH_LONG
                ).show();
                if (success) {
                    finish();
                }
            }
        }

        private void close() {
            mPostTask = null;
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            close();
        }
    }

}

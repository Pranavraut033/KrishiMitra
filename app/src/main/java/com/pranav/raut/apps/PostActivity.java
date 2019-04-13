package com.pranav.raut.apps;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import model.PostSchema;
import pranav.utilities.Animations;
import pranav.utilities.Logger;
import pranav.utils.StoreUtils;
import pranav.views.TextField.TextField;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "PostActivity";

    private TextField mTitleField, mPriceField, mCategoryField, mDescriptionField;
    private View mProgressbar;

    private PostTask mPostTask;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private StoreUtils mStore;

    private Logger mLogger = new Logger(true, true, TAG);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mStore = new StoreUtils(this, FirebaseFirestore.getInstance());
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

    public void clickListener(View view) {
        switch (view.getId()) {
            case R.id.post_btn:
                attemptPost();
                break;
            case R.id.bg:
                break;
        }
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

        mPostTask = new PostTask(title, description, Integer.parseInt(price), category);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @SuppressLint("StaticFieldLeak")
    public class PostTask extends AsyncTask<Void, Void, Boolean> {

        private final String mTitle;
        private final String mDescription;
        private final Integer mPrice;
        private final String mCategory;

        PostTask(String title, String description, Integer price, String category) {
            this.mTitle = title;
            this.mDescription = description;
            this.mPrice = price;
            this.mCategory = category;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            OnFailureListener failureListener = e -> {
                onPostExecute(false);
                mLogger.w(e);
            };
            String uid = firebaseAuth.getUid();

            if (uid != null) {
                PostSchema post = new PostSchema(mTitle, mDescription, mPrice, mCategory, uid);
                post.insert(mStore, t -> {
                    mLogger.d("Posted with id:", post.getId());
                    onPostExecute(true);
                }, failureListener);
            } else return false;

            return null;
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

package com.pranav.raut.apps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import model.UserSchema;
import pranav.utilities.Animations;
import pranav.utilities.Logger;
import pranav.utils.AuthUtils;
import pranav.utils.RegexPatterns;
import pranav.utils.StoreUtils;
import pranav.views.TextField.TextField;


public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private UserRegisterTask mAuthTask = null;

    private TextField mFirstNameFiled, mLastNameFiled, mEmailFiled;
    private TextInputEditText mPasswordFiled;
    private View mProgressBar;

    private AuthUtils mAuth;
    private StoreUtils mStore;
    private RegexPatterns mPatterns = RegexPatterns.INSTANCE;
    private Logger mLogger = new Logger(true, TAG);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = new AuthUtils(this, FirebaseAuth.getInstance(), true);
        mStore = new StoreUtils(this, FirebaseFirestore.getInstance());
        initViews();
    }

    private void initViews() {
        mFirstNameFiled = findViewById(R.id.firstname_field);
        mLastNameFiled = findViewById(R.id.lastname_field);
        mEmailFiled = findViewById(R.id.email_field);
        mPasswordFiled = findViewById(R.id.password_field);
        mProgressBar = findViewById(R.id.reg_progressbar);
    }

    public void clickListener(View view) {
        switch (view.getId()) {
            case R.id.register_btn:
                attemptRegister();
                break;
        }
    }

    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        mEmailFiled.setError(null);
        mPasswordFiled.setError(null);
        mFirstNameFiled.setError(null);

        String email = mEmailFiled.getText();
        String password = String.valueOf(mPasswordFiled.getText());
        String firstName = mFirstNameFiled.getText();
        String lastName = mLastNameFiled.getText();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(firstName)) {
            mFirstNameFiled.setError(getString(R.string.error_field_required));
            focusView = mFirstNameFiled;
            cancel = true;
        } else if (!TextUtils.isEmpty(password) && !mPatterns.isPasswordValid(password)) {
            mPasswordFiled.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordFiled;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            mEmailFiled.setError(getString(R.string.error_field_required));
            focusView = mEmailFiled;
            cancel = true;
        } else if (!mPatterns.isEmailValid(email)) {
            mEmailFiled.setError(getString(R.string.error_invalid_email));
            focusView = mEmailFiled;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserRegisterTask(email, password, firstName, lastName);
            mAuthTask.execute((Void) null);
        }
    }

    private void showProgress(final boolean show) {
        mProgressBar.animate().alpha(show ? 1f : 0f).start();
        Animations.animateAlpha(findViewById(R.id.bg), show ? 1f : 0f);
    }

    enum RegistrationErrors {
        EMAIL_EXISTS,
        NONE
    }

    @SuppressLint("StaticFieldLeak")
    public class UserRegisterTask extends AsyncTask<Void, Void, RegistrationErrors> {

        private final String mEmail;
        private final String mPassword;
        private final String mName;

        UserRegisterTask(String email, String password, String firstName, String lastName) {
            this.mEmail = email;
            this.mPassword = password;
            this.mName = (firstName + " " + lastName).trim();
        }

        @Override
        protected RegistrationErrors doInBackground(Void... params) {
            OnFailureListener a = mLogger::w;

            mAuth.createUserUsingEmail(mEmail, mPassword, authResult ->
                    mAuth.signInUsingEmail(mEmail, mPassword, authResult1 ->
                            mAuth.updateProfile(mName, null, aVoid -> {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    mStore.insertData(new UserSchema(user.getUid(), mName,
                                            user.getPhoneNumber(), mEmail), aVoid1 ->
                                            onPostExecute(RegistrationErrors.NONE), e ->
                                            mLogger.w(new Object[0], e));
                                }
                            }, a), a), e -> {
                mLogger.w(new Object[0], e);
                if (e instanceof FirebaseAuthUserCollisionException) {
                    onPostExecute(RegistrationErrors.EMAIL_EXISTS);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(final RegistrationErrors success) {
            if (success != null) {
                mAuthTask = null;
                showProgress(false);
                switch (success) {
                    case NONE:
                        Toast.makeText(
                                RegisterActivity.this,
                                "Welcome, " + mName,
                                Toast.LENGTH_SHORT
                        ).show();
                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                    case EMAIL_EXISTS:
                        mEmailFiled.setError(getString(R.string.error_email_exists));
                        mEmailFiled.requestFocus();
                        break;
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

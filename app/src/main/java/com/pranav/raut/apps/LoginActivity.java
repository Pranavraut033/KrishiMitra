package com.pranav.raut.apps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import androidx.appcompat.app.AppCompatActivity;
import pranav.utilities.Animations;
import pranav.utilities.Logger;
import pranav.utils.AuthUtils;
import pranav.utils.RegexPatterns;
import pranav.views.TextField.TextField;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private UserLoginTask mAuthTask = null;

    private TextField mEmailField;
    private TextInputEditText mPasswordField;
    private View mProgressBar;

    private AuthUtils mAuth;
    private Logger mLogger = new Logger(true, TAG);
    private RegexPatterns mPatterns = RegexPatterns.INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailField = findViewById(R.id.login_email_field);
        mAuth = new AuthUtils(this, FirebaseAuth.getInstance(), true);

        mPasswordField = findViewById(R.id.login_password_field);
        mPasswordField.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        mProgressBar = findViewById(R.id.login_progressbar);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mEmailField.setError(null);
        mPasswordField.setError(null);

        String email = mEmailField.getText();
        String password = String.valueOf(mPasswordField.getText());

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password)) {
            mPasswordField.setError(getString(R.string.error_field_required));
            focusView = mPasswordField;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            mEmailField.setError(getString(R.string.error_field_required));
            focusView = mEmailField;
            cancel = true;
        } else if (!mPatterns.isEmailValid(email)) {
            mEmailField.setError(getString(R.string.error_invalid_email));
            focusView = mEmailField;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private void showProgress(final boolean show) {
        mProgressBar.animate().alpha(show ? 1f : 0f).start();
        Animations.animateAlpha(findViewById(R.id.bg), show ? 1f : 0f);
    }

    public void clickListener(View view) {
        switch (view.getId()) {
            case R.id.signin_btn:
                attemptLogin();
                break;
            case R.id.signup_btn:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.google_signin:
                break;
            case R.id.bg:
                break;
        }
    }

    enum LoginErrors {
        WRONG_PASSWORD,
        EMAIL_NOT_FOUND,
        NONE
    }

    @SuppressLint("StaticFieldLeak")
    public class UserLoginTask extends AsyncTask<Void, Void, LoginErrors> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected LoginErrors doInBackground(Void... params) {
            mAuth.signInUsingEmail(
                    mEmail,
                    mPassword,
                    authResult -> onPostExecute(LoginErrors.NONE),
                    e -> {
                        mLogger.w(e);
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            onPostExecute(LoginErrors.EMAIL_NOT_FOUND);
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            onPostExecute(LoginErrors.WRONG_PASSWORD);
                        }
                    });
            return null;
        }

        @Override
        protected void onPostExecute(final LoginErrors success) {
            if (success != null) {
                mAuthTask = null;
                showProgress(false);
                switch (success) {
                    case NONE:
                        Toast.makeText(
                                LoginActivity.this,
                                "Welcome, " + mAuth.getCurrentUser().getDisplayName(),
                                Toast.LENGTH_SHORT
                        ).show();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                    case EMAIL_NOT_FOUND:
                        mEmailField.setError(getString(R.string.error_email_not_found));
                        mEmailField.requestFocus();
                        break;
                    case WRONG_PASSWORD:
                        mPasswordField.setError(getString(R.string.error_incorrect_password));
                        mPasswordField.requestFocus();
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


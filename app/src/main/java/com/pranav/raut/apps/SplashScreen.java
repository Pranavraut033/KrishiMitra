package com.pranav.raut.apps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import model.UserSchema;
import pranav.utilities.Animations;
import pranav.utilities.Logger;

public class SplashScreen extends AppCompatActivity {

    static {
        Logger.Companion.setDEBUG(true);
        Logger.Companion.setVERBOSE(true);
    }

    private boolean taskAdded = false;
    private FirebaseAuth mAuth;

    private Handler mTaskHandler = new Handler();
    private Runnable startActivityTask = () -> {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Class cls = currentUser != null ? HomeActivity.class : LoginActivity.class;
        if (currentUser != null) {
            Toast.makeText(this, currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
        }
        startActivity(new Intent(this, cls)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK));
        taskAdded = false;
    }, showProgressTask = () -> {
        Animations.toggleVisibility(findViewById(R.id.progressBar));
        Animations.toggleVisibility(findViewById(R.id.appNameTextView));
        taskAdded = true;
        mTaskHandler.postDelayed(startActivityTask, 500);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseApp.initializeApp(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mTaskHandler.postDelayed(showProgressTask, 500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (taskAdded) mTaskHandler.removeCallbacks(startActivityTask);
    }
}

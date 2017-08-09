package com.colege.project.krishimitra;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.preons.pranav.util.DBHelper;
import com.preons.pranav.util.FirebaseTasks;

import java.io.File;
import java.io.IOException;

import static com.preons.pranav.util.Constants.DATABASE_NAME;
import static com.preons.pranav.util.Constants.FIELD_ERR;
import static com.preons.pranav.util.Constants.FULL_NAME;
import static com.preons.pranav.util.Constants.PASS;
import static com.preons.pranav.util.Constants.PASS_ERR;
import static com.preons.pranav.util.Constants.PASS_LENGTH;
import static com.preons.pranav.util.Constants.REGISTER;
import static com.preons.pranav.util.Constants.REMEMBER;
import static com.preons.pranav.util.Constants.USER;
import static com.preons.pranav.util.Constants.USER_COLUMN_NAME;
import static com.preons.pranav.util.Constants.USER_COLUMN_PASS;
import static com.preons.pranav.util.Constants.USER_COLUMN_USERNAME;
import static com.preons.pranav.util.Constants.USER_ERR;
import static com.preons.pranav.util.Constants.copyFiles;
import static com.preons.pranav.util.Constants.dDime;
import static com.preons.pranav.util.Constants.editor;
import static com.preons.pranav.util.Constants.preferences;

/**
 * Created on 21-03-17 at 19:38 by Pranav Raut.
 * For MumbaiLocal
 */

public class LoginActivity extends AppCompatActivity {

    TextInputEditText editText;
    TextInputEditText editText1;

    CheckBox checkBox;
    private DBHelper dbHelper;
    private FirebaseTasks tasks = new FirebaseTasks();
    private Context c = this;
    private ProgressDialog dialog;

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
            for (String permission : permissions)
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
        return true;
    }

    private void fTask() {
        tasks.setDownloadListener(new FirebaseTasks.DownloadListener() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task, File file) {
                DBHelper helper = new DBHelper(getApplicationContext());
                File dir = new File(Environment.getExternalStorageDirectory(), "krishiMitra_database");
                boolean b = true;
                if (!dir.exists())
                    b = dir.mkdir();
                File finalFile = new File(dir.getPath(), "users.db");
                if (b) {
                    try {
                        copyFiles(file, finalFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (finalFile.exists()) {
                    try {
                        helper.importDatabase(finalFile.getPath(), getDatabasePath(DATABASE_NAME).getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String u = editText.getText().toString(), p = editText1.getText().toString();
                if (check(u, p)) {
                    dialog.dismiss();
                    return;
                }
                Cursor cursor = dbHelper.getData(u);
                if (cursor.moveToFirst()) {
                    String u1 = cursor.getString(cursor.getColumnIndex(USER_COLUMN_USERNAME));
                    String p1 = cursor.getString(cursor.getColumnIndex(USER_COLUMN_PASS));
                    boolean b1 = u1.equals(u) && p1.equals(p);
                    if (b1) {
                        startActivity(new Intent(c, DashboardActivity.class));
                        editor.putString(FULL_NAME, cursor.getString(cursor.getColumnIndex(USER_COLUMN_NAME)));
                        editor.putBoolean(REMEMBER, checkBox.isChecked());
                        editor.apply();
                        finish();
                    } else Toast.makeText(c, "Incorrect password", Toast.LENGTH_LONG).show();
                } else Toast.makeText(c, "Username not found", Toast.LENGTH_LONG).show();
                cursor.close();
                dialog.dismiss();
            }
        });
    }

    private boolean check(String s, String s1) {
        boolean b;
        if (b = s.isEmpty()) setErr1(FIELD_ERR);
        else if (b = s.length() < 5) setErr1(USER_ERR);
        else if (b = s1.isEmpty()) setErr2(USER_ERR);
        else if (b = s1.length() < PASS_LENGTH) setErr2(PASS_ERR);
        return b;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REGISTER:
                if (resultCode == RESULT_OK) {
                    editText.setText(data.getStringExtra(USER));
                    editText1.setText(data.getStringExtra(PASS));
                }
                break;
        }
    }

    private void setErr2(String s) {
        editText1.setError(s);
    }

    private void setErr1(String s) {
        editText.setError(s);
    }

    public void clicks(View view) {
        switch (view.getId()) {
            case R.id.reg_button:
                startActivityForResult(new Intent(this, RegisterActivity.class), REGISTER);
                break;
            case R.id.fab:
                dialog.show();
                tasks.downloadFile(getDatabasePath(DATABASE_NAME).getParent() + DATABASE_NAME);
                break;
            case R.id.for_button:
                break;
        }
    }

    private void checkPermission() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            PERMISSIONS = new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA};
        }
        if (!hasPermissions(this, PERMISSIONS))
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean b = true;
        for (int i : grantResults)
            b = b && grantResults[i] == RESULT_OK;
        if (!b) {
            Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
            checkPermission();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_activity_login));
        setSupportActionBar(toolbar);
        editText = (TextInputEditText) findViewById(R.id.username);
        editText1 = (TextInputEditText) findViewById(R.id.password);
        checkBox = (CheckBox) findViewById(R.id.rem_me);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        dbHelper = new DBHelper(this);
        dDime = getResources().getDimension(R.dimen.dPadding);
        if (preferences.getBoolean(REMEMBER, false)) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }
        checkPermission();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Logging in");
        dialog.setCancelable(false);
        c = this;
        fTask();
    }
}

package com.colege.project.krishimitra;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;
import com.preons.pranav.util.DBHelper;
import com.preons.pranav.util.FirebaseTasks;

import java.io.File;
import java.io.IOException;

import static com.preons.pranav.util.Constants.DATABASE_NAME;
import static com.preons.pranav.util.Constants.FIELD_ERR;
import static com.preons.pranav.util.Constants.FILEPATH;
import static com.preons.pranav.util.Constants.PASS;
import static com.preons.pranav.util.Constants.PASS_ERR;
import static com.preons.pranav.util.Constants.PASS_LENGTH;
import static com.preons.pranav.util.Constants.USER;
import static com.preons.pranav.util.Constants.USER_ERR;
import static com.preons.pranav.util.Constants.copyFiles;

/**
 * Created on 21-03-17 at 19:38 by Pranav Raut.
 * For MumbaiLocal
 */

public class RegisterActivity extends AppCompatActivity {

    int[] editTextIDs = new int[]{
            R.id.full_name, R.id.username, R.id.password, R.id.address, R.id.email, R.id.number};
    TextInputEditText[] editTexts = new TextInputEditText[editTextIDs.length];
    String[] allInfo = new String[editTexts.length];
    FirebaseTasks tasks;
    private boolean[] b = new boolean[2];
    private DBHelper dbHelper = new DBHelper(this);
    private ProgressDialog dialog;

    private void intiET() {
        editTexts[1].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String s1 = editTexts[1].getText().toString();
                if (b[0] = s1.isEmpty())
                    setErr(FIELD_ERR, editTexts[1]);
                else if (b[0] = s1.length() < 5)
                    setErr(USER_ERR, editTexts[1]);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });
        editTexts[2].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String s1 = editTexts[2].getText().toString();
                if (b[1] = s1.isEmpty())
                    setErr(FIELD_ERR, editTexts[2]);
                else if (b[1] = s1.length() < PASS_LENGTH)
                    setErr(PASS_ERR, editTexts[2]);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setErr(String s, TextInputEditText editText) {
        editText.setError(s);
    }

    public void done(View view) {
        switch (view.getId()) {
            case R.id.reg_done:
                int i = 0;
                for (TextInputEditText t : editTexts)
                    allInfo[i++] = t.getText().toString();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(USER, allInfo[1]);
                resultIntent.putExtra(PASS, allInfo[2]);
                setResult(Activity.RESULT_OK, resultIntent);
                if (!b[0] && !b[1]) {
                    tasks.downloadFile(FILEPATH);
                    dialog.show();
                } else Toast.makeText(this, "Incomplete Form!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void fTasks() {
        tasks.setUploadListener(new FirebaseTasks.UploadListener() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Successfully created", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(@NonNull Exception e) {

            }

            @Override
            public void onSuccess(@Nullable UploadTask.TaskSnapshot taskSnapshot) {

            }

            @Override
            public void onProgress(Double percentage) {

            }
        });
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
                dbHelper.insertUser(allInfo[0], allInfo[1], allInfo[3], allInfo[5], allInfo[4], allInfo[2]);
                tasks.deleteFile(FILEPATH);
            }
        });
        tasks.setDeleteListener(new FirebaseTasks.DeleteListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }

            @Override
            public void onSuccess(Void aVoid) {
                tasks.uploadFile(getDatabasePath(DATABASE_NAME));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        for (int i = 0; i < editTextIDs.length; i++)
            editTexts[i] = (TextInputEditText) findViewById(editTextIDs[i]);
        tasks = new FirebaseTasks();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Creating User");
        dialog.setCancelable(false);
        fTasks();
        intiET();
    }
}
package com.colege.project.krishimitra;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.preons.pranav.util.DBHelper2;
import com.preons.pranav.util.FirebaseTasks;

import java.io.File;
import java.io.IOException;

import static com.preons.pranav.util.Constants.DATABASE_NAME1;
import static com.preons.pranav.util.Constants.FILEPATH1;
import static com.preons.pranav.util.Constants.copyFiles;

public class BrowseActivity extends AppCompatActivity {
    private DBHelper2 helper;
    private FirebaseTasks tasks = new FirebaseTasks();
    private Context c = this;
    private ProgressDialog dialog;
    private Snackbar snackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        helper = new DBHelper2(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Fecthing data");
        dialog.setCancelable(false);
        tasks.downloadFile(FILEPATH1);
        dialog.show();
        snackBar = Snackbar.make(findViewById(R.id.list), "Loading Images", Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });
        fTasks();
    }

    private void fTasks() {
        tasks.setDownloadListener(new FirebaseTasks.DownloadListener() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                snackBar.dismiss();
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task, File file) {
                File dir = new File(Environment.getExternalStorageDirectory(), "krishiMitra_database");
                boolean b = true;
                if (!dir.exists())
                    b = dir.mkdir();
                File finalFile = new File(dir.getPath(), "ads.db");
                if (b) {
                    try {
                        copyFiles(file, finalFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (finalFile.exists()) {
                    try {
                        helper.importDatabase(finalFile.getPath(), getDatabasePath(DATABASE_NAME1).getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                complete();
                dialog.dismiss();
            }
        });
    }

    private void complete() {
        snackBar.show();
        DBHelper2 helper2 = new DBHelper2(this);
        ListView listView = (ListView) findViewById(R.id.list);
        CustomAdapter adapter = new CustomAdapter(this,helper2.getEverything());
        adapter.setListener(new CustomAdapter.onCompleteListener() {
            @Override
            public void completed() {
                snackBar.dismiss();
            }
        });
        listView.setAdapter(adapter);
    }
}

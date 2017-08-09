package com.colege.project.krishimitra;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;
import com.preons.pranav.util.CFileHelper;
import com.preons.pranav.util.DBHelper2;
import com.preons.pranav.util.DBHelper3;
import com.preons.pranav.util.FirebaseTasks;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static com.preons.pranav.util.Constants.DATABASE_NAME1;
import static com.preons.pranav.util.Constants.FILEPATH1;
import static com.preons.pranav.util.Constants.copyFiles;
import static com.preons.pranav.util.Constants.dateFormat;

public class CreateActivity extends AppCompatActivity {
    private static final int LINK1 = 1231;
    private static final int LINK2 = 1232;
    private static final int LINK3 = 1233;
    private static final int LINK4 = 1234;
    private static final int LINK5 = 1235;
    int[] ints = new int[]{R.id.link1, R.id.link2, R.id.link3, R.id.link4, R.id.link5,};
    int[] ints2 = new int[]{R.id.name, R.id.disp, R.id.price, R.id.contact, R.id.email};
    ImageView[] imageViews = new ImageView[ints.length];
    TextInputEditText[] texts = new TextInputEditText[ints2.length];
    ProgressDialog dialog;
    private FirebaseTasks tasks;
    private DBHelper2 helper;
    private String[] strings = new String[ints2.length + ints.length];
    private String[] strings2 = new String[ints.length];
    View.OnClickListener[] listeners = new View.OnClickListener[]{
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    capture(LINK1, 1);

                }
            },
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    capture(LINK2, 2);
                }
            },
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    capture(LINK3, 3);
                }
            },
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    capture(LINK4, 4);
                }
            },
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    capture(LINK5, 5);
                }
            }
    };
    Button button;
    private String[] strings3 = new String[ints.length];
    private DBHelper3 dbHelper3;
    private Snackbar snackBar;

    private void capture(int requestCode, int i) {
        boolean b = true;
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "KrishiMitra");
        if (!dir.exists())
            b = dir.mkdir();
        File tempFile = null;
        if (b) {
            try {
                tempFile = new File(dir.getPath(), "image" + i + dateFormat.format(new Date()) + ".jpg");
                b = tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (tempFile.exists() || b) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider", tempFile);
                strings2[i - 1] = tempFile.getPath();
                strings3[i - 1] = tempFile.getParent()+tempFile.getName();
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(cameraIntent, requestCode);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        for (int i = 0; i < ints.length; i++) {
            imageViews[i] = (ImageView) findViewById(ints[i]);
            imageViews[i].setContentDescription("");
            imageViews[i].setOnClickListener(listeners[i]);
        }
        for (int i = 0; i < ints2.length; i++)
            texts[i] = (TextInputEditText) findViewById(ints2[i]);
        texts[1].getText();
        helper = new DBHelper2(this);
        tasks = new FirebaseTasks();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Creating Ad");
        dialog.setCancelable(false);
        button = (Button) findViewById(R.id.done);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                for (TextInputEditText text : texts) strings[i++] = text.getText().toString();
                for (ImageView imageView : imageViews)
                    strings[i++] = imageView.getContentDescription().toString();
                tasks.downloadFile(FILEPATH1);
                dialog.show();
            }
        });
        snackBar = Snackbar.make(findViewById(R.id.content), "Uploading Images", Snackbar.LENGTH_INDEFINITE);
        dbHelper3 = new DBHelper3(this);
        fTasks();
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
                helper.insertProduct(strings[0], strings[1], strings[2], strings[3], strings[4], strings[5],
                        strings[6], strings[7], strings[8], strings[9]);
                tasks.deleteFile(FILEPATH1);
                dbHelper3.insertProduct(strings[0], strings[1], strings[2]);
            }
        });
        tasks.setDeleteListener(new FirebaseTasks.DeleteListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }

            @Override
            public void onSuccess(Void aVoid) {
                tasks.uploadFile(getDatabasePath(DATABASE_NAME1));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap result = (Bitmap) data.getExtras().get("data");
            switch (requestCode) {
                case LINK1:
                    capture(0, result);
                    break;
                case LINK2:
                    capture(1, result);
                    break;
                case LINK3:
                    capture(2, result);
                    break;
                case LINK4:
                    capture(3, result);
                    break;
                case LINK5:
                    capture(4, result);
                    break;
            }

        }
    }

    private void capture(final int i, Bitmap result) {
        button.setEnabled(false);
        Toast.makeText(this, "Uploading file: " + strings2[i], Toast.LENGTH_SHORT).show();
        imageViews[i].setImageBitmap(result);
        //TODO: click listner
        if (i < ints.length)
            imageViews[i + 1].setVisibility(View.VISIBLE);
        FirebaseTasks tasks = new FirebaseTasks();
        final File file = new File(strings2[i]);
        if(!snackBar.isShown())
            snackBar.show();
        tasks.uploadFile(file);
        tasks.setUploadListener(new FirebaseTasks.UploadListener() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                button.setEnabled(true);
                imageViews[i].setContentDescription(strings3[i]);
                snackBar.dismiss();
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

        new CFileHelper(this).scanMedia(file);
    }
}

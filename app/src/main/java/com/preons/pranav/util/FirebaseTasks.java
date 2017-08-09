package com.preons.pranav.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static com.preons.pranav.util.Constants.dateFormat;

/**
 * Created by Pranav Raut
 * for Qr code protection project
 */
public class FirebaseTasks {

    private final StorageReference appReference;
    @Nullable
    private UploadListener uploadListener;
    @Nullable
    private DownloadListener downloadListener;
    private String filePath;
    private UploadTask uploadTask;
    @Nullable
    private DeleteListener deleteListener;


    public FirebaseTasks() {
        appReference = FirebaseStorage.getInstance().getReference();
    }

    public void uploadFile(File file) {
        Uri tFile = Uri.fromFile(file);
        filePath = file.getParent() + tFile.getLastPathSegment();
        StorageReference fileReference = appReference.child(filePath);
        uploadTask = fileReference.putFile(tFile);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (uploadListener != null) uploadListener.onFailure(e);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (uploadListener != null) uploadListener.onSuccess(taskSnapshot);
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (uploadListener != null) uploadListener.onComplete(task);
            }
        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                if (uploadListener != null) uploadListener.onProgress(progress);
            }
        });
    }

    public UploadTask getUploadTask() {
        return uploadTask;
    }

    public String getFilePath() {
        return filePath;
    }

    public void downloadFile(@NonNull String pathName) {
        File check = new File(pathName);
        if (check.exists()){
            if (downloadListener != null) downloadListener.onComplete(null, check);
            return;
        }
        StorageReference fileReference = appReference.child(pathName);
        File file = null;
        try {
            file = File.createTempFile("database" + dateFormat.format(new Date()), ".db");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file != null) {
            //Todo:complete this
            final File finalFile = file;
            fileReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    if (downloadListener != null) downloadListener.onSuccess(taskSnapshot);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if (downloadListener != null) downloadListener.onFailure(exception);
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    if (downloadListener != null) downloadListener.onProgress(progress);
                }
            }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    if (downloadListener != null) downloadListener.onComplete(task, finalFile);
                }
            });
        }
    }

    public void deleteFile(String path) {

        StorageReference fileReference = appReference.child(path);
        // Delete the file
        fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (deleteListener != null) deleteListener.onSuccess(aVoid);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (deleteListener != null) deleteListener.onFailure(exception);
            }
        });
    }

    public void setUploadListener(@Nullable UploadListener uploadListner) {
        this.uploadListener = uploadListner;
    }

    public void setDownloadListener(@Nullable DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public void setDeleteListener(@Nullable DeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public interface UploadListener {
        void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task);

        void onFailure(@NonNull Exception e);

        void onSuccess(@Nullable UploadTask.TaskSnapshot taskSnapshot);

        void onProgress(Double percentage);
    }

    public interface DeleteListener {
        void onFailure(@NonNull Exception e);

        void onSuccess(Void aVoid);
    }

    public interface DownloadListener {
        void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot);

        void onFailure(@NonNull Exception exception);

        void onProgress(int progress);

        void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task, File file);
    }
}
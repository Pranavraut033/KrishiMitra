package com.preons.pranav.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created on 12-03-17 at 11:23 by Pranav Raut.
 * For QRCodeProtection
 */

public class CFileHelper {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyhhmm", Locale.ENGLISH);
    private Context c;

    public CFileHelper(Context c) {
        this.c = c;
    }

    @Nullable
    public static File tempImageFile(InputStream inputStream, int i) {
        byte[] buffer;
        String date = dateFormat.format(new Date());
        File temp = null;
        File tempFol = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "KrishiMitra");
        try {
            buffer = new byte[inputStream.available()];
            int res = inputStream.read(buffer);
            boolean b = true;
            if (!tempFol.exists())
                b = tempFol.mkdir();
            if (b) {
                temp = new File(tempFol, "Image" + i + date + ".jpg");
                temp.deleteOnExit();
                OutputStream outStream = new FileOutputStream(temp);
                outStream.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public void scanMedia(File file) {
        Intent intent =
                new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        c.sendBroadcast(intent);
    }
}
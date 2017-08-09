package com.preons.pranav.util;


import android.content.SharedPreferences;
import android.os.Build;

import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created on 21-03-17 at 20:16 by Pranav Raut.
 * For MumbaiLocal
 */

@SuppressWarnings("WeakerAccess")
public class Constants {
    public static final int PASS_LENGTH = 6;
    public static final String USER = "username";
    public static final String PASS = "password";
    public static final String FULL_NAME = "full_name";
    public static final String FIELD_ERR = "Field required";
    public static final String USER_ERR = "Username too short";
    public static final String PASS_ERR = "Password too short";
    public static final String REMEMBER = "rem";
    public static final int REGISTER = 0x4d2;
    public static final String FILEPATH = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP?
            "data/user/0/com.colege.project.krishimitra/databaseskrishiMitra.db":
            "data/data/com.colege.project.krishimitra/databaseskrishiMitra.db";
    public static final String FILEPATH1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP?
            "data/user/0/com.colege.project.krishimitra/databasesads.db":
            "data/data/com.colege.project.krishimitra/databasesads.db";
    //dbHelper
    public static final String DATABASE_NAME = "krishiMitra.db";
    public static final String USER_TABLE_NAME = "users";
    public static final String USER_COLUMN_ID = "id";
    public static final String USER_COLUMN_NAME = "name";
    public static final String USER_COLUMN_USERNAME = "username";
    public static final String USER_COLUMN_ADDRESS = "address";
    public static final String USER_COLUMN_PHONE = "phone";
    public static final String USER_COLUMN_EMAIL = "email";
    public static final String USER_COLUMN_PASS = "pass";
    //dbHelper2
    //dbHelper EN
    public static final String DATABASE_NAME1 = "ads.db";
    public static final String DATABASE_NAME2 = "yourAds.db";
    public static final String PRODUCT_COLUMN_ID = "id";
    public static final String PRODUCT_TABLE_NAME = "products";
    public static final String PRODUCT_COLUMN_NAME = "name";
    public static final String PRODUCT_COLUMN_DISP = "disp";
    public static final String PRODUCT_COLUMN_PRICE = "price";
    public static final String PRODUCT_COLUMN_CONTACT = "contact";
    public static final String PRODUCT_COLUMN_EMAIL = "email";
    public static final String PRODUCT_COLUMN_LINK1 = "link1";
    public static final String PRODUCT_COLUMN_LINK2 = "link2";
    public static final String PRODUCT_COLUMN_LINK3 = "link3";
    public static final String PRODUCT_COLUMN_LINK4 = "link4";
    public static final String PRODUCT_COLUMN_LINK5 = "link5";
    //functions
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    //dbHelper2 END
    public static float dDime;
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyhhmm", Locale.ENGLISH);
    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {
                String files[] = src.list();
                for (String file : files) {
                    String src1 = (new File(src, file).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);
                }
            } else {
                copyFiles(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFiles(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

}

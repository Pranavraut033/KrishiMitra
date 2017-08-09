package com.preons.pranav.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import static com.preons.pranav.util.Constants.DATABASE_NAME;
import static com.preons.pranav.util.Constants.USER_COLUMN_ADDRESS;
import static com.preons.pranav.util.Constants.USER_COLUMN_EMAIL;
import static com.preons.pranav.util.Constants.USER_COLUMN_NAME;
import static com.preons.pranav.util.Constants.USER_COLUMN_PASS;
import static com.preons.pranav.util.Constants.USER_COLUMN_PHONE;
import static com.preons.pranav.util.Constants.USER_COLUMN_USERNAME;
import static com.preons.pranav.util.Constants.USER_TABLE_NAME;
import static com.preons.pranav.util.Constants.copyFiles;

public class DBHelper extends SQLiteOpenHelper {

    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + USER_TABLE_NAME +
                        " (id integer primary key autoincrement, name text, username text, address text, phone text, email text, pass text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_COLUMN_NAME);
        onCreate(db);
    }

    public boolean insertUser(String name, String username, String address, String phone, String email, String pass) {
        getWritableDatabase().insert(USER_TABLE_NAME, null, contentVal(name, username, address, phone, email, pass));
        return true;
    }

    private ContentValues contentVal(String name, String username, String address, String phone, String email, String pass) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_NAME, name);
        contentValues.put(USER_COLUMN_USERNAME, username);
        contentValues.put(USER_COLUMN_ADDRESS, address);
        contentValues.put(USER_COLUMN_PHONE, phone);
        contentValues.put(USER_COLUMN_EMAIL, email);
        contentValues.put(USER_COLUMN_PASS, pass);
        return contentValues;
    }

    public Cursor getData(String s) {
        return getReadableDatabase().rawQuery("select * from " + USER_TABLE_NAME + " where " +
                USER_COLUMN_USERNAME + "= '" + s + "'", null);
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(getReadableDatabase(), USER_TABLE_NAME);
    }

    public boolean updateUser(Integer id, String name, String username, String address, String phone, String email, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(USER_TABLE_NAME, contentVal(name, username, address, phone, email, pass), "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteUser(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(USER_TABLE_NAME,
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public ArrayList<String> getAllUsers() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + USER_TABLE_NAME, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex(USER_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public boolean importDatabase(String newDataBase, String oldDataBase) throws IOException {

        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.
        close();
        File newDb = new File(newDataBase);
        File oldDb = new File(oldDataBase);
        if (newDb.exists()) {
            copyFiles(newDb, oldDb);

            // Access the copied database so SQLiteHelper will cache it and mark
            // it as created.
            getWritableDatabase().close();
            return true;
        }
        return false;
    }

}
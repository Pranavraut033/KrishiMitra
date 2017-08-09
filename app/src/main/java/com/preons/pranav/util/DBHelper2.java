package com.preons.pranav.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.preons.pranav.util.Constants.DATABASE_NAME1;
import static com.preons.pranav.util.Constants.PRODUCT_COLUMN_CONTACT;
import static com.preons.pranav.util.Constants.PRODUCT_COLUMN_DISP;
import static com.preons.pranav.util.Constants.PRODUCT_COLUMN_EMAIL;
import static com.preons.pranav.util.Constants.PRODUCT_COLUMN_LINK1;
import static com.preons.pranav.util.Constants.PRODUCT_COLUMN_LINK2;
import static com.preons.pranav.util.Constants.PRODUCT_COLUMN_LINK3;
import static com.preons.pranav.util.Constants.PRODUCT_COLUMN_LINK4;
import static com.preons.pranav.util.Constants.PRODUCT_COLUMN_LINK5;
import static com.preons.pranav.util.Constants.PRODUCT_COLUMN_NAME;
import static com.preons.pranav.util.Constants.PRODUCT_COLUMN_PRICE;
import static com.preons.pranav.util.Constants.PRODUCT_TABLE_NAME;
import static com.preons.pranav.util.Constants.copyFiles;


public class DBHelper2 extends SQLiteOpenHelper {

    public DBHelper2(Context context) {
        super(context, DATABASE_NAME1, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + PRODUCT_TABLE_NAME +
                        " (id integer primary key autoincrement, name text, disp text, price text, contact text," +
                        " email text, link1 text, link2 text, link3 text, link4 text, link5 text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_COLUMN_NAME);
        onCreate(db);
    }

    public boolean insertProduct(String name, String disp, String price, String contact,
                                 String email, String link1, String link2, String link3,
                                 String link4, String link5) {
        getWritableDatabase().insert(PRODUCT_TABLE_NAME, null, contentVal(name, disp, price, contact,
                email, link1, link2, link3, link4, link5));
        return true;
    }

    private ContentValues contentVal(String name, String disp, String price, String contact,
                                     String email, String link1, String link2, String link3,
                                     String link4, String link5) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCT_COLUMN_NAME, name);
        contentValues.put(PRODUCT_COLUMN_DISP, disp);
        contentValues.put(PRODUCT_COLUMN_PRICE, price);
        contentValues.put(PRODUCT_COLUMN_CONTACT, contact);
        contentValues.put(PRODUCT_COLUMN_EMAIL, email);
        contentValues.put(PRODUCT_COLUMN_LINK1, link1);
        contentValues.put(PRODUCT_COLUMN_LINK2, link2);
        contentValues.put(PRODUCT_COLUMN_LINK3, link3);
        contentValues.put(PRODUCT_COLUMN_LINK4, link4);
        contentValues.put(PRODUCT_COLUMN_LINK5, link5);
        return contentValues;
    }

    public Cursor getData(String s) {
        return getReadableDatabase().rawQuery("select * from " + PRODUCT_TABLE_NAME + " where " +
                PRODUCT_COLUMN_NAME + "= '" + s + "'", null);
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(getReadableDatabase(), PRODUCT_TABLE_NAME);
    }

    public boolean updateUser(Integer id, String name, String disp, String price, String contact,
                              String email, String link1, String link2, String link3,
                              String link4, String link5) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(PRODUCT_TABLE_NAME, contentVal(name, disp, price, contact,
                email, link1, link2, link3, link4, link5), "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteUser(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PRODUCT_TABLE_NAME,
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public ArrayList<String> getAllUsers() {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + PRODUCT_TABLE_NAME, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex(PRODUCT_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public boolean importDatabase(String newDataBase, String oldDataBase) throws IOException {
        close();
        File newDb = new File(newDataBase);
        File oldDb = new File(oldDataBase);
        if (newDb.exists()) {
            copyFiles(newDb, oldDb);
            getWritableDatabase().close();
            return true;
        }
        return false;
    }

    public String[][] getEverything() {
        String[] strings = new String[]{
                PRODUCT_COLUMN_NAME, PRODUCT_COLUMN_DISP,
                PRODUCT_COLUMN_CONTACT, PRODUCT_COLUMN_EMAIL,PRODUCT_COLUMN_PRICE, PRODUCT_COLUMN_LINK1,
                PRODUCT_COLUMN_LINK2, PRODUCT_COLUMN_LINK3, PRODUCT_COLUMN_LINK4,
                PRODUCT_COLUMN_LINK5
        };
        String[][] temp = new String[strings.length][numberOfRows()];
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + PRODUCT_TABLE_NAME, null);
        for (int i = 0; i < strings.length; i++) {
            res.moveToFirst();
            int j = 0;
            while (!res.isAfterLast() && j < numberOfRows()) {
                temp[i][j++] = res.getString(res.getColumnIndex(strings[i]));
                res.moveToNext();
            }
        }
        res.close();
        close();
        return temp;
    }
}
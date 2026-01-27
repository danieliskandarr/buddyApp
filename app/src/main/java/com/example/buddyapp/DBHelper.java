package com.example.buddyapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "BuddyAppDB";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "friends";

    // Columns
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_GENDER = "gender";
    private static final String COL_HP = "hpno";
    private static final String COL_EMAIL = "email";
    private static final String COL_ADDR1 = "address1";
    private static final String COL_ADDR2 = "address2";
    private static final String COL_ADDR3 = "address3";
    private static final String COL_ADDR4 = "address4"; // State

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_GENDER + " TEXT, " +
                COL_HP + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_ADDR1 + " TEXT, " +
                COL_ADDR2 + " TEXT, " +
                COL_ADDR3 + " TEXT, " +
                COL_ADDR4 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // --- CRUDS OPERATIONS ---

    // CREATE
    public boolean addFriend(String name, String gender, String hp, String email,
                             String a1, String a2, String a3, String a4) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, name);
        cv.put(COL_GENDER, gender);
        cv.put(COL_HP, hp);
        cv.put(COL_EMAIL, email);
        cv.put(COL_ADDR1, a1);
        cv.put(COL_ADDR2, a2);
        cv.put(COL_ADDR3, a3);
        cv.put(COL_ADDR4, a4);
        long result = db.insert(TABLE_NAME, null, cv);
        return result != -1;
    }

    // READ & SEARCH (Returns list of friends)
    public ArrayList<Friend> getAllFriends(String searchQuery) {
        ArrayList<Friend> friendList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;

        // If there is a search query, filter by name
        if (searchQuery != null && !searchQuery.isEmpty()) {
            query += " WHERE " + COL_NAME + " LIKE '%" + searchQuery + "%'";
        }

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                friendList.add(new Friend(
                        cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5),
                        cursor.getString(6), cursor.getString(7), cursor.getString(8)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return friendList;
    }

    // UPDATE
    public boolean updateFriend(int id, String name, String gender, String hp, String email,
                                String a1, String a2, String a3, String a4) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, name);
        cv.put(COL_GENDER, gender);
        cv.put(COL_HP, hp);
        cv.put(COL_EMAIL, email);
        cv.put(COL_ADDR1, a1);
        cv.put(COL_ADDR2, a2);
        cv.put(COL_ADDR3, a3);
        cv.put(COL_ADDR4, a4);
        int result = db.update(TABLE_NAME, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // DELETE
    public boolean deleteFriend(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // --- REPORT DATA GENERATION ---

    // Count Male vs Female [cite: 30]
    public int getGenderCount(String gender) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COL_GENDER + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{gender});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    // Count friends per State [cite: 31]
    public Map<String, Integer> getStateCounts() {
        Map<String, Integer> stateMap = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_ADDR4 + ", COUNT(*) FROM " + TABLE_NAME + " GROUP BY " + COL_ADDR4;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                stateMap.put(cursor.getString(0), cursor.getInt(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return stateMap;
    }
}

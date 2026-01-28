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
    private static final int DB_VERSION = 2; // Incremented version for onUpgrade
    private static final String TABLE_FRIENDS = "friends";
    private static final String TABLE_USERS = "users";

    // Friends Table Columns
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_GENDER = "gender";
    private static final String COL_HP = "hpno";
    private static final String COL_EMAIL = "email";
    private static final String COL_ADDR1 = "address1";
    private static final String COL_ADDR2 = "address2";
    private static final String COL_ADDR3 = "address3";
    private static final String COL_ADDR4 = "address4"; // State

    // Users Table Columns
    private static final String COL_USER_ID = "user_id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createFriendsTable = "CREATE TABLE " + TABLE_FRIENDS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_GENDER + " TEXT, " +
                COL_HP + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_ADDR1 + " TEXT, " +
                COL_ADDR2 + " TEXT, " +
                COL_ADDR3 + " TEXT, " +
                COL_ADDR4 + " TEXT)";
        db.execSQL(createFriendsTable);

        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // --- USER OPERATIONS ---

    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USERNAME, username);
        cv.put(COL_PASSWORD, password); // In a real app, HASH the password!
        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COL_USER_ID };
        String selection = COL_USERNAME + " = ?" + " AND " + COL_PASSWORD + " = ?";
        String[] selectionArgs = { username, password }; // In a real app, compare HASHED passwords!
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // --- FRIENDS CRUDS OPERATIONS ---

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
        long result = db.insert(TABLE_FRIENDS, null, cv);
        return result != -1;
    }

    // READ & SEARCH (Returns list of friends)
    public ArrayList<Friend> getAllFriends(String searchQuery) {
        ArrayList<Friend> friendList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_FRIENDS;

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
        int result = db.update(TABLE_FRIENDS, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // DELETE
    public boolean deleteFriend(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_FRIENDS, COL_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // --- REPORT DATA GENERATION ---

    // Count Male vs Female [cite: 30]
    public int getGenderCount(String gender) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_FRIENDS + " WHERE " + COL_GENDER + "=?";
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
        String query = "SELECT " + COL_ADDR4 + ", COUNT(*) FROM " + TABLE_FRIENDS + " GROUP BY " + COL_ADDR4;
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
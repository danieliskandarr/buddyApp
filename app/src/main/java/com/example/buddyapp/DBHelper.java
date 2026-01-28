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
    private static final int DB_VERSION = 3; // Incremented version for schema change
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
    private static final String COL_USER_ID_FK = "user_id"; // Foreign Key

    // Users Table Columns
    private static final String COL_USER_ID = "user_id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);

        String createFriendsTable = "CREATE TABLE " + TABLE_FRIENDS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_GENDER + " TEXT, " +
                COL_HP + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_ADDR1 + " TEXT, " +
                COL_ADDR2 + " TEXT, " +
                COL_ADDR3 + " TEXT, " +
                COL_ADDR4 + " TEXT, " +
                COL_USER_ID_FK + " INTEGER, " +
                "FOREIGN KEY(" + COL_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))";
        db.execSQL(createFriendsTable);
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
    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COL_USER_ID };
        String selection = COL_USERNAME + " = ?";
        String[] selectionArgs = { username };
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public int checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_USER_ID};
        String selection = COL_USERNAME + " = ?" + " AND " + COL_PASSWORD + " = ?";
        String[] selectionArgs = {username, password}; // In a real app, compare HASHED passwords!
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID));
        }
        cursor.close();
        return userId;
    }

    // --- FRIENDS CRUDS OPERATIONS ---
    public Friend getFriendById(int userId, int friendId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FRIENDS, null, COL_ID + "=? AND " + COL_USER_ID_FK + "=?",
                new String[]{String.valueOf(friendId), String.valueOf(userId)}, null, null, null);

        Friend friend = null;
        if (cursor.moveToFirst()) {
            friend = new Friend(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_GENDER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_HP)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDR1)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDR2)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDR3)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDR4))
            );
        }
        cursor.close();
        return friend;
    }

    // CREATE
    public boolean addFriend(int userId, String name, String gender, String hp, String email,
                             String a1, String a2, String a3, String a4) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_ID_FK, userId);
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

    // READ & SEARCH (Returns list of friends for a specific user)
    public ArrayList<Friend> getAllFriends(int userId, String searchQuery) {
        ArrayList<Friend> friendList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_USER_ID_FK + " = ?";
        ArrayList<String> selectionArgsList = new ArrayList<>();
        selectionArgsList.add(String.valueOf(userId));

        if (searchQuery != null && !searchQuery.isEmpty()) {
            selection += " AND " + COL_NAME + " LIKE ?";
            selectionArgsList.add("%" + searchQuery + "%");
        }
        String[] selectionArgs = selectionArgsList.toArray(new String[0]);
        Cursor cursor = db.query(TABLE_FRIENDS, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                friendList.add(new Friend(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_GENDER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_HP)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDR1)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDR2)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDR3)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDR4))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return friendList;
    }

    // UPDATE
    public boolean updateFriend(int userId, int id, String name, String gender, String hp, String email,
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
        int result = db.update(TABLE_FRIENDS, cv, COL_ID + "=? AND " + COL_USER_ID_FK + "=?", new String[]{String.valueOf(id), String.valueOf(userId)});
        return result > 0;
    }

    // DELETE
    public boolean deleteFriend(int userId, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_FRIENDS, COL_ID + "=? AND " + COL_USER_ID_FK + "=?", new String[]{String.valueOf(id), String.valueOf(userId)});
        return result > 0;
    }

    // --- REPORT DATA GENERATION ---

    // Count Male vs Female for a specific user
    public int getGenderCount(int userId, String gender) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_FRIENDS + " WHERE " + COL_GENDER + "=? AND " + COL_USER_ID_FK + "=?", new String[]{gender, String.valueOf(userId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Count friends per State for a specific user
    public Map<String, Integer> getStateCounts(int userId) {
        Map<String, Integer> stateMap = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_ADDR4 + ", COUNT(*) FROM " + TABLE_FRIENDS + " WHERE " + COL_USER_ID_FK + "=? GROUP BY " + COL_ADDR4, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                String state = cursor.getString(0);
                int count = cursor.getInt(1);
                if(state != null && !state.isEmpty()){
                    stateMap.put(state, count);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return stateMap;
    }
}
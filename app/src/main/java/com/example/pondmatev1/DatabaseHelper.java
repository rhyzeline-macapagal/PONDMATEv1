package com.example.pondmatev1;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UserDB";
    private static final int DATABASE_VERSION = 2; //for onUpgrade to work

    private static final String TABLE_USERS = "users";

    // NEW COLUMN
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_FULLNAME = "fullname";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_USERTYPE = "usertype";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT UNIQUE, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_FULLNAME + " TEXT, "
                + COLUMN_ADDRESS + " TEXT, "
                + COLUMN_USERTYPE + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean addUser(String username, String password, String fullname, String address, String userType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_FULLNAME, fullname);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_USERTYPE, userType);

        try {
            long result = db.insertOrThrow(TABLE_USERS, null, values);
            return result != -1;
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            // Handle UNIQUE constraint (duplicate username)
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }


    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);

        boolean matchFound = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return matchFound;
    }

    public Cursor getUserInfo(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_USERNAME + "=?",
                new String[]{username}, null, null, null);
    }
}

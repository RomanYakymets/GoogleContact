package com.example.roma.testtask.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "user_contacts.db";

    public static final String TABLE_CONTACTS = "contact";
    public static final String CONTACTS_COLUMN_ID = "_id";
    public static final String CONTACTS_COLUMN_FIRST_NAME = "first_name";
    public static final String CONTACTS_COLUMN_LAST_NAME = "last_name";
    public static final String CONTACTS_COLUMN_CONTACT_ID = "contact_id";
    public static final String CONTACTS_COLUMN_USER_ID = "user_id";

    public static final String TABLE_EMAILS = "emails";
    public static final String EMAILS_COLUMN_ID = "_id";
    public static final String EMAILS_COLUMN_EMAIL = "email";
    public static final String EMAILS_CONTACT_ID = "email_contact_id";

    public static final String TABLE_PHONE_NUMBERS = "phone_numbers";
    public static final String PHONE_NUMBERS_COLUMN_ID = "_id";
    public static final String PHONE_NUMBERS_COLUMN_PHONE_NUMBER = "phone_number";
    public static final String PHONE_NUMBERS_CONTACT_ID = "phone_contact_id";

    private static final String CONTACTS_TABLE_CREATE_SCRIPT = "create table if not exists "
            + TABLE_CONTACTS + " (" + CONTACTS_COLUMN_ID + " integer primary key autoincrement, "
            + CONTACTS_COLUMN_CONTACT_ID + " text not null, "
            + CONTACTS_COLUMN_USER_ID + " text not null, "
            + CONTACTS_COLUMN_FIRST_NAME + " text, "
            + CONTACTS_COLUMN_LAST_NAME + " text, "
            + "UNIQUE(" + CONTACTS_COLUMN_CONTACT_ID + ")"
            + ");";

    private static final String PHONE_NUMBERS_TABLE_CREATE_SCRIPT = "create table if not exists "
            + TABLE_PHONE_NUMBERS + " (" + PHONE_NUMBERS_COLUMN_ID + " integer primary key autoincrement, "
            + PHONE_NUMBERS_COLUMN_PHONE_NUMBER + " text, "
            + PHONE_NUMBERS_CONTACT_ID + " text not null, "
            + "FOREIGN KEY (" + PHONE_NUMBERS_CONTACT_ID
            + ") REFERENCES " + TABLE_CONTACTS + "(" + CONTACTS_COLUMN_ID + ") "
            + "ON DELETE CASCADE ON UPDATE NO ACTION"
            + ");";

    private static final String EMAILS_TABLE_CREATE_SCRIPT = "create table if not exists "
            + TABLE_EMAILS + " (" + EMAILS_COLUMN_ID + " integer primary key autoincrement, "
            + EMAILS_COLUMN_EMAIL + " text, "
            + EMAILS_CONTACT_ID + " text not null, "
            + "FOREIGN KEY (" + EMAILS_CONTACT_ID
            + ") REFERENCES " + TABLE_CONTACTS + "(" + CONTACTS_COLUMN_ID + ")"
            + "ON DELETE CASCADE ON UPDATE NO ACTION"
            + ");";


    public MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(PHONE_NUMBERS_TABLE_CREATE_SCRIPT);
        db.execSQL(EMAILS_TABLE_CREATE_SCRIPT);
        db.execSQL(CONTACTS_TABLE_CREATE_SCRIPT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONE_NUMBERS);

        onCreate(db);
    }
}
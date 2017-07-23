package com.example.roma.testtask.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.roma.testtask.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactData {

    private static final String TAG = "ContactData";

    private SQLiteDatabase db;
    private MyDbHelper dbHelper;

    public ContactData(Context context) {
        dbHelper = new MyDbHelper(context);
    }

    private void openDb() {
        db = dbHelper.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    private void closeDb() {
        db.close();
    }

    public void insertContacts(List<Contact>  contactList, String userId) {
        openDb();
        for (int i = 0; i < contactList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(MyDbHelper.CONTACTS_COLUMN_CONTACT_ID, contactList.get(i).getContactId());
            values.put(MyDbHelper.CONTACTS_COLUMN_USER_ID, userId);
            values.put(MyDbHelper.CONTACTS_COLUMN_FIRST_NAME, contactList.get(i).getFirstName());
            values.put(MyDbHelper.CONTACTS_COLUMN_LAST_NAME, contactList.get(i).getLastName());

            long id = db.insertWithOnConflict(MyDbHelper.TABLE_CONTACTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);

            for (int j = 0; j < contactList.get(i).getEmailAdress().size(); j++){
                ContentValues emails = new ContentValues();
                emails.put(MyDbHelper.EMAILS_COLUMN_EMAIL, contactList.get(i).getEmailAdress().get(j));
                emails.put(MyDbHelper.EMAILS_CONTACT_ID, id);
                db.insertWithOnConflict(MyDbHelper.TABLE_EMAILS, null, emails, SQLiteDatabase.CONFLICT_REPLACE);
            }

            for (int j = 0; j < contactList.get(i).getPhoneNumber().size(); j++){
                ContentValues phone = new ContentValues();
                phone.put(MyDbHelper.PHONE_NUMBERS_COLUMN_PHONE_NUMBER, contactList.get(i).getPhoneNumber().get(j));
                phone.put(MyDbHelper.PHONE_NUMBERS_CONTACT_ID, id);
                db.insertWithOnConflict(MyDbHelper.TABLE_PHONE_NUMBERS, null, phone, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
        closeDb();
    }

    public List<Contact> getAllContacts(String userId){


        List<String> phoneNumbers = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        String selectQuery1 = "SELECT  " + MyDbHelper.PHONE_NUMBERS_COLUMN_PHONE_NUMBER
                + " FROM " + MyDbHelper.TABLE_PHONE_NUMBERS;

        Cursor cursor1 = db.rawQuery(selectQuery1, null);

        if (cursor1.moveToFirst()) {
            do {
                phoneNumbers.add(cursor1.getString(0));
            } while (cursor1.moveToNext());
        }
        cursor1.close();
        for (int i = 0; i < phoneNumbers.size(); i++){
            Log.d(TAG, "phone : "+ i + " " + phoneNumbers.get(i));
        }


        List<Contact>  contactList = new ArrayList<>();


        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + MyDbHelper.TABLE_CONTACTS
                + " WHERE " + MyDbHelper.CONTACTS_COLUMN_USER_ID + " = " + "\"" + userId + "\"";
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d(TAG, "getAllContacts : " + MyDbHelper.CONTACTS_COLUMN_USER_ID + " = " + userId);
        if (cursor.moveToFirst()) {
            do {

                Contact contact = new Contact(
                        cursor.getString(1),
                        cursor.getString(3),
                        cursor.getString(4),
                        getEmailAdress(cursor.getInt(0)),
                        getPhoneNumbers(cursor.getInt(0))
                );
                Log.d(TAG, "contact name : " + contact.getFirstName());
                Log.d(TAG, "contact id : " + contact.getContactId());
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contactList;
    }

//    public int updateContact(Contact contact) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(MyDbHelper.CONTACTS_COLUMN_CONTACT_ID, contact.getContactId());
//        values.put(MyDbHelper.CONTACTS_COLUMN_FIRST_NAME, contact.getFirstName());
//        values.put(MyDbHelper.CONTACTS_COLUMN_LAST_NAME, contact.getLastName());
//
//        db.update(MyDbHelper.TABLE_CONTACTS, values, MyDbHelper.CONTACTS_COLUMN_ID + " = ?",
//                new String[] { String.valueOf(contact.getContactId()) });
//
//        for (int j = 0; j < contact.getEmailAdress().size(); j++){
//            ContentValues emails = new ContentValues();
//            emails.put(MyDbHelper.EMAILS_COLUMN_EMAIL, contact.getEmailAdress().get(j));
//            db.update(MyDbHelper.TABLE_EMAILS, emails, MyDbHelper.EMAILS_CONTACT_ID + " = ?",
//                    new String[] { String.valueOf(id) });
//        }
//
//        for (int j = 0; j < contact.getPhoneNumber().size(); j++){
//            ContentValues phone = new ContentValues();
//            phone.put(MyDbHelper.PHONE_NUMBERS_COLUMN_PHONE_NUMBER, contact.getPhoneNumber().get(j));
//            db.update(MyDbHelper.TABLE_PHONE_NUMBERS, phone, MyDbHelper.PHONE_NUMBERS_CONTACT_ID + " = ?",
//                    new String[] { String.valueOf(id) });
//        }
//        return id;
//    }

    public void deleteContact(String contactId) {
        openDb();
        int a = db.delete(MyDbHelper.TABLE_CONTACTS, MyDbHelper.CONTACTS_COLUMN_CONTACT_ID + " = ?",
                new String[] { contactId });
        Log.d(TAG, "deleted row count : "+ a);
        closeDb();
    }

    private List<String> getPhoneNumbers(int contactID) {
        List<String> phoneNumbers = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " + MyDbHelper.PHONE_NUMBERS_COLUMN_PHONE_NUMBER
                + " FROM " + MyDbHelper.TABLE_PHONE_NUMBERS
                + " WHERE " + MyDbHelper.PHONE_NUMBERS_CONTACT_ID + " = " + "\"" + contactID + "\"";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                phoneNumbers.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return phoneNumbers;
    }

    private List<String> getEmailAdress(int contactID) {
        List<String> emailAdress = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " + MyDbHelper.EMAILS_COLUMN_EMAIL
                + " FROM " + MyDbHelper.TABLE_EMAILS
                + " WHERE " + MyDbHelper.EMAILS_CONTACT_ID + " = " + "\"" + contactID + "\"";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                emailAdress.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return emailAdress;
    }


}
package com.example.android.bookinventoryappstage2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookinventoryappstage2.data.BookContract.BookEntry;


/**
 * Created by abdulelah on 20/01/2018.
 */

public class BookDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Inventory.db";
    private static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_PRODUCT_IMAGE + " BLOB, "
                + BookEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL, "
                + BookEntry.COLUMN_SUPPLIER_NUMBER + " TEXT NOT NULL);";// I changed the phone number to text
                                                                        // because integer have max positive number which is 2,147,483,647
        sqLiteDatabase.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //nothing for now
    }
}

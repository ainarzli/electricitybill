package com.example.electricitybill; // Make sure this matches your package name

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ElectricityBill.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "bill_history";
    public static final String COL_ID = "ID";
    public static final String COL_MONTH = "MONTH";
    public static final String COL_UNITS = "UNITS";
    public static final String COL_TOTAL_CHARGES = "TOTAL_CHARGES";
    public static final String COL_REBATE = "REBATE";
    public static final String COL_FINAL_COST = "FINAL_COST";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MONTH + " TEXT, " +
                COL_UNITS + " REAL, " +
                COL_TOTAL_CHARGES + " REAL, " +
                COL_REBATE + " REAL, " +
                COL_FINAL_COST + " REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // CREATE: Insert data into the database
    public boolean insertData(String month, double units, double totalCharges, double rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_MONTH, month);
        contentValues.put(COL_UNITS, units);
        contentValues.put(COL_TOTAL_CHARGES, totalCharges);
        contentValues.put(COL_REBATE, rebate);
        contentValues.put(COL_FINAL_COST, finalCost);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // returns true if inserted successfully
    }

    // READ: Get all rows from the database
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // UPDATE: Modify an existing record
    public boolean updateData(String id, String month, double units, double totalCharges, double rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_MONTH, month);
        contentValues.put(COL_UNITS, units);
        contentValues.put(COL_TOTAL_CHARGES, totalCharges);
        contentValues.put(COL_REBATE, rebate);
        contentValues.put(COL_FINAL_COST, finalCost);

        int affectedRows = db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
        return affectedRows > 0;
    }

    // DELETE: Remove a record from the database
    public Integer deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }
}
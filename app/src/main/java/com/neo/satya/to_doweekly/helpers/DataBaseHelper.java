package com.neo.satya.to_doweekly.helpers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by satya on 2016-09-11.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "myweeklistneonew2.db";
    public static final String TABLE_NAME = "myweeklist_dataneonew2";
    public static final String COL1 = "ID";
    public static final String COL2 = "TITLE";
    public static final String COL3 = "DESC";
    public static final String COL4 = "TIME";
    public static final String COL5 = "DATE";
    public static final String COL6 = "DAY";
    public static final String COL7 = "STATUS";
    public static final String COL8 = "NOTIFY";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // db.execSQL(SQL_CREATE_ENTRIES);
    String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + "TITLE TEXT, " + "DESC TEXT, " + "TIME TEXT, "+ "DATE TEXT, "+ "DAY TEXT, " +"STATUS INTEGER, "+"NOTIFY INTEGER)";
        db.execSQL(createTable);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public boolean addData(String title, String desc, String time, String date, String day, int stat){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,title);
        contentValues.put(COL3,desc);
        contentValues.put(COL4,time);
        contentValues.put(COL5,date);
        contentValues.put(COL6,day);
        contentValues.put(COL7,stat);
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    public boolean deleteUser(String userName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, "TITLE = ?", new String[] { userName });
        db.close();
        return result != 0;
    }


    public Cursor getListContents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
        return data;
    }

    public Cursor getListContentsDayWise(String date){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM "+ TABLE_NAME + " WHERE DATE = \""+date+"\""  ,null);
        return data;
    }

    public boolean updateData(String item1, String newHeading, String newDescription, String time, String date, String day) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,newHeading);
        contentValues.put(COL3,newDescription);
        contentValues.put(COL4,time);
        contentValues.put(COL5,date);
        contentValues.put(COL6,day);
        long result = db.update(TABLE_NAME,contentValues,"TITLE = ?", new String[] { item1 });
        db.close();
        return result != -1;
    }


    public boolean deleteAllData(String itemToDelete) {
        int result;
        SQLiteDatabase db = this.getWritableDatabase();
      // if you want the function to return the count of deleted rows,
        if(itemToDelete == "all"){
            result = db.delete(TABLE_NAME, "1", null);
        }
        else {
            result = db.delete(TABLE_NAME, "DATE = ?", new String[] { itemToDelete });
        }
      // or simply db.execSQL("delete from "+ TABLE_NAME);
        db.close();
        return result != 0;
    }


    public boolean deleteItemDayAndDate(String selectedItemDate, String selectedItemName, String selectedItemTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, "DATE =? AND TITLE=? AND TIME=?", new String[]{selectedItemDate, selectedItemName, selectedItemTime});
        db.close();
        return result != 0;
    }

    public boolean updateStatus(String taskCreationDate, String taskName, String taskCreationTime, int checked) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,taskName);
        contentValues.put(COL4,taskCreationTime);
        contentValues.put(COL5,taskCreationDate);
        contentValues.put(COL7,checked);
        long result = db.update(TABLE_NAME,contentValues,"DATE =? AND TITLE=? AND TIME=?", new String[] { taskCreationDate,taskName, taskCreationTime });
        db.close();
        return result != -1;
    }

    public boolean updateNotifier(String taskCreationDate, int checked) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL5,taskCreationDate);
        contentValues.put(COL8,checked);
        long result = db.update(TABLE_NAME,contentValues,"DATE =? ", new String[] { taskCreationDate});
        db.close();
        return result != -1;
    }
}

package com.paint.sqLite.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.paint.sqLite.data.NodeContract.*;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "hw4.db";
    public static final int DB_VER = 1;

    // CREATE TABLE IF NOT EXISTS node (_id INTEGER PRIMARY KEY AUTOINCREMENT, value INTEGER NOT NULL);
    private static final String CREATE_TABLE1 = new StringBuilder()
            .append("CREATE TABLE IF NOT EXISTS " + NodeEntry.TABLE_NAME + " (" + NodeEntry._ID)
            .append(" INTEGER PRIMARY KEY AUTOINCREMENT, " + NodeEntry.COLUMN_VALUE + " INTEGER NOT NULL);")
            .toString();

    // CREATE TABLE IF NOT EXISTS parent_child (parent_id INTEGER, child_id INTEGER,
    // FOREIGN KEY (parent_id) REFERENCES node(_id), FOREIGN KEY (child_id) REFERENCES node(_id));
    private static final String CREATE_TABLE2 = "CREATE TABLE IF NOT EXISTS " + RelationEntry.TABLE_NAME
            + " (" + RelationEntry.COLUMN_PARENT_ID + " INTEGER, " + RelationEntry.COLUMN_CHILD_ID
            + " INTEGER, FOREIGN KEY (" + RelationEntry.COLUMN_PARENT_ID + ") REFERENCES "
            + NodeEntry.TABLE_NAME + "(" + NodeEntry._ID + "), FOREIGN KEY (" + RelationEntry.COLUMN_CHILD_ID
            + ") REFERENCES " + NodeEntry.TABLE_NAME + "(" + NodeEntry._ID + "));";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onOpen(SQLiteDatabase db){
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE1);
        db.execSQL(CREATE_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int j) {

    }

}

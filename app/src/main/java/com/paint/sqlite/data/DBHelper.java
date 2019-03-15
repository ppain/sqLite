package com.paint.sqLite.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.paint.sqLite.data.NodeContract.*;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "hw4.db";
    public static final int DB_VER = 1;

    // CREATE TABLE IF NOT EXISTS node (_id INTEGER PRIMARY KEY AUTOINCREMENT, value INTEGER NOT NULL);
    private static final String CREATE_TABLE1 = "CREATE TABLE IF NOT EXISTS " + NodeEntry.TABLE_NAME
            + " (" + NodeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NodeEntry.COLUMN_VALUE + " INTEGER NOT NULL);";

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
    public void onOpen(SQLiteDatabase db) {
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

    public Cursor getNodeItemsWithRelation(SQLiteDatabase mDatabase) {
        // SELECT _id, value, parent_id, child_id
        // FROM (SELECT distinct _id, value, parent_id FROM node LEFT JOIN parent_child ON _id = parent_id) as first
        // JOIN (SELECT distinct _id as _id_s, value as value_s, child_id FROM node LEFT JOIN parent_child ON _id = child_id) as second
        // ON first._id = second._id_s
        // ORDER BY _id
        return mDatabase.rawQuery("SELECT " + NodeEntry._ID + ", " + NodeEntry.COLUMN_VALUE + ", "
                + RelationEntry.COLUMN_PARENT_ID + ", " + RelationEntry.COLUMN_CHILD_ID + " FROM "
                + "(SELECT distinct " + NodeEntry._ID + ", " + NodeEntry.COLUMN_VALUE + ", "
                + RelationEntry.COLUMN_PARENT_ID + " FROM " + NodeEntry.TABLE_NAME + " "
                + "LEFT JOIN " + RelationEntry.TABLE_NAME + " ON " + NodeEntry._ID + " = "
                + RelationEntry.COLUMN_PARENT_ID + ") as parentTable JOIN (SELECT distinct "
                + NodeEntry._ID + " as " + NodeEntry._ID + "_s, " + NodeEntry.COLUMN_VALUE + " as "
                + NodeEntry.COLUMN_VALUE + "_s, " + RelationEntry.COLUMN_CHILD_ID + " FROM "
                + NodeEntry.TABLE_NAME + " LEFT JOIN " + RelationEntry.TABLE_NAME + " ON "
                + NodeEntry._ID + " = " + RelationEntry.COLUMN_CHILD_ID + ") as childTable ON parentTable."
                + NodeEntry._ID + " = childTable." + NodeEntry._ID + "_s ORDER BY " + NodeEntry._ID, null);
    }

    public Cursor getSortedChildItems(SQLiteDatabase mDatabase, String id) {
        // SELECT DISTINCT _id, value FROM node LEFT JOIN parent_child
        // ON node._id = parent_child.child_id WHERE node._id != { id }
        // ORDER BY CASE WHEN parent_id = { id } THEN 0 ELSE 1 END, parent_id DESC
        return mDatabase.query(true,
                NodeEntry.TABLE_NAME + " LEFT JOIN " + RelationEntry.TABLE_NAME + " ON "
                        + NodeEntry.TABLE_NAME + "." + NodeEntry._ID + " = " + RelationEntry.TABLE_NAME
                        + "." + RelationEntry.COLUMN_CHILD_ID,
                new String[]{"_id", "value"},
                NodeEntry._ID + " != ?",
                new String[]{id},
                null,
                null,
                "CASE WHEN " + RelationEntry.COLUMN_PARENT_ID + " = " + id
                        + " THEN 0 ELSE 1 END, " + RelationEntry.COLUMN_PARENT_ID + " DESC",
                null
        );
    }

    public Cursor getSortedParentItems(SQLiteDatabase mDatabase, String id) {
        // SELECT DISTINCT _id, value FROM node LEFT JOIN parent_child
        // ON node._id = parent_child.parent_id WHERE node._id != { id }
        // ORDER BY CASE WHEN child_id = { id } THEN 0 ELSE 1 END, child_id DESC
        return mDatabase.query(true,
                NodeEntry.TABLE_NAME + " LEFT JOIN " + RelationEntry.TABLE_NAME + " ON "
                        + NodeEntry.TABLE_NAME + "." + NodeEntry._ID + " = " + RelationEntry.TABLE_NAME
                        + "." + RelationEntry.COLUMN_PARENT_ID,
                new String[]{"_id", "value"},
                NodeEntry._ID + " != ?",
                new String[]{id},
                null,
                null,
                "CASE WHEN " + RelationEntry.COLUMN_CHILD_ID + " = " + id
                        + " THEN 0 ELSE 1 END, " + RelationEntry.COLUMN_CHILD_ID + " DESC",
                null
        );
    }

    public void insertNodeValue(SQLiteDatabase mDatabase, int value) {
        if (mDatabase != null) {
            ContentValues v = new ContentValues();
            v.put(NodeEntry.COLUMN_VALUE, value);
            mDatabase.insert(NodeEntry.TABLE_NAME, null, v);
        }
    }

    public int getCountRelation(SQLiteDatabase mDatabase, String id, String Column) {
        return (int) DatabaseUtils.queryNumEntries(mDatabase, RelationEntry.TABLE_NAME,
                Column + " = ?", new String[]{id});
    }

    public void insertRelationValue(SQLiteDatabase mDatabase, String first_id, String second_id) {
        if (mDatabase != null) {
            ContentValues v = new ContentValues();
            v.put(RelationEntry.COLUMN_PARENT_ID, first_id);
            v.put(RelationEntry.COLUMN_CHILD_ID, second_id);
            mDatabase.insert(RelationEntry.TABLE_NAME, null, v);
        }
    }

    public void deleteRelationValue(SQLiteDatabase mDatabase, String first_id, String second_id) {
        if (mDatabase != null) {
            mDatabase.delete(RelationEntry.TABLE_NAME, RelationEntry.COLUMN_PARENT_ID + " = "
                    + first_id + " AND " + RelationEntry.COLUMN_CHILD_ID + " = " + second_id, null);
        }
    }

    public boolean checkExistRelation(SQLiteDatabase mDatabase, String first_id, String second_id) {
        if ((DatabaseUtils.queryNumEntries(mDatabase, RelationEntry.TABLE_NAME,
                RelationEntry.COLUMN_PARENT_ID + " = ? AND " + RelationEntry.COLUMN_CHILD_ID + " = ?",
                new String[]{first_id, second_id})) > 0) {
            return true;
        } else {
            return false;
        }
    }

}

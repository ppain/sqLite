package com.paint.sqlite.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "hw4.db";
    public static final int DB_VER = 1;

    private static final String CREATE_DB = new StringBuilder()
            .append("CREATE TABLE node (id INTEGER PRIMARY KEY AUTOINCREMENT, value INTEGER NOT NULL);")
            .append("CREATE TABLE parent_child (parent_id INTEGER NOT NULL, child_id INTEGER NOT NULL, ")
            .append("FOREIGN KEY (parent_id) REFERENCES node(id), FOREIGN KEY (child_id) REFERENCES node(id));")
            .toString();



    private static final class Factory implements SQLiteDatabase.CursorFactory {
        @Override
        public Cursor newCursor(SQLiteDatabase sqLiteDatabase, SQLiteCursorDriver sqLiteCursorDriver, String s, SQLiteQuery sqLiteQuery) {
            return new SQLiteCursor(sqLiteCursorDriver, s, sqLiteQuery);
        }
    }

    public DBHelper(Context context) {
        this(context, DB_NAME, new Factory(), DB_VER);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onOpen(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int j) {

    }

    public int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        final String regionQuery = "select Count(*) as count from node";
        Cursor cur = null;
        int result = 0;
        if (db != null) {
            try {
                cur = db.rawQuery(regionQuery, null);
                cur.moveToFirst();
                result = cur.getInt(cur.getColumnIndexOrThrow("count"));
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (cur != null) {
                    cur.close();
                }
                db.close();
            }
        }
        return result;
    }

    public void addValue(String table, int value) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            ContentValues v = new ContentValues();
            v.put("value", value);
            db.insert(table, null, v);
            db.close();
        }
    }

    public void editValue(String table, int pos, String value) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            ContentValues v = new ContentValues();
            v.put("value", value);
            db.update(table, v, "id = ?", new String[]{String.valueOf(pos)});
            db.close();
        }
    }

    public int getValue(String table, int pos) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = null;
        int id = 0;
        int value = 0;
        if (db != null) {
            try {
                cur = db.query(table, new String[]{"id", "value"}, "id=?", new String[]{String.valueOf(pos)}, null, null, null);
                cur.moveToFirst();
                id = cur.getInt(cur.getColumnIndexOrThrow("id"));
                value = cur.getInt(cur.getColumnIndexOrThrow("value"));
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (cur != null) {
                    cur.close();
                }
                db.close();
            }
        }
        return value;
    }
}

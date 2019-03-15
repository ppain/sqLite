package com.paint.sqLite;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


import com.paint.sqLite.adapter.NodeAdapter;
import com.paint.sqLite.data.DBHelper;
import com.paint.sqLite.data.NodeContract.*;


public class MainActivity extends AppCompatActivity{

    private DBHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    private NodeAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mDBHelper = new DBHelper(this);
        mDatabase = mDBHelper.getWritableDatabase();
        RecyclerView recyclerView = findViewById(R.id.nodeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NodeAdapter(this, getNodeItemsWithRelation());
        recyclerView.setAdapter(mAdapter);

        findViewById(R.id.btnAddNew).setOnClickListener(v -> addNewValue());
    }


    private void addNewValue() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        final LayoutInflater l = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = l.inflate(R.layout.add_new, null);
        final EditText text = (EditText) v.findViewById(R.id.add);
        alertDialog.setView(v);

        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final int value = Integer.parseInt(text.getText().toString());
                insertValue(value);
                mAdapter.swapCursor(getNodeItemsWithRelation());
            }
        });
        alertDialog.show();
    }

    private void insertValue(int value) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        if (db != null) {
            ContentValues v = new ContentValues();
            v.put("value", value);
            db.insert(NodeEntry.TABLE_NAME, null, v);
        }
    }

    private Cursor getNodeItemsWithRelation(){
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
                + NodeEntry.COLUMN_VALUE + "_s, " + RelationEntry.COLUMN_CHILD_ID +  " FROM "
                + NodeEntry.TABLE_NAME + " LEFT JOIN " + RelationEntry.TABLE_NAME + " ON "
                + NodeEntry._ID + " = " + RelationEntry.COLUMN_CHILD_ID + ") as childTable ON parentTable."
                + NodeEntry._ID + " = childTable." + NodeEntry._ID + "_s ORDER BY " + NodeEntry._ID, null);
    }

}
package com.paint.sqLite;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.paint.sqLite.adapter.OnAdapterClickListener;
import com.paint.sqLite.adapter.RelationAdapter;
import com.paint.sqLite.data.DBHelper;
import com.paint.sqLite.data.NodeContract.*;

public class RelationActivity extends AppCompatActivity {

    private String nodeId; //target node
    private DBHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    private RelationAdapter ParentAdapter;
    private RelationAdapter ChildAdapter;
    private TextView relationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation);
        init();
    }

    private void init() {
        mDBHelper = new DBHelper(this);
        mDatabase = mDBHelper.getWritableDatabase();
        relationId = findViewById(R.id.relation);

        String value;
        if (getIntent().hasExtra("nodeId")) {
            nodeId = getIntent().getStringExtra("nodeId");
            value = getIntent().getStringExtra("value");
        } else {
            nodeId = "1";
            value = "1";
        }
        relationId.setText(value);

        RecyclerView parentRView = findViewById(R.id.parentList);
        parentRView.setLayoutManager(new LinearLayoutManager(this));
        ParentAdapter = new RelationAdapter(mDatabase, getParentItems(nodeId), nodeId,false,
                new OnAdapterClickListener() {
            @Override
            public void onItemClicked(String clickedId, boolean isChildList) {
                editRelation(clickedId, isChildList);
            }
        });
        parentRView.setAdapter(ParentAdapter);


        RecyclerView childRView = findViewById(R.id.childList);
        childRView.setLayoutManager(new LinearLayoutManager(this));
        ChildAdapter = new RelationAdapter(mDatabase, getChildItems(nodeId), nodeId,true,
                new OnAdapterClickListener() {
            @Override
            public void onItemClicked(String clickedId, boolean isChildList) {
                editRelation(clickedId, isChildList);
            }
        });
        childRView.setAdapter(ChildAdapter);
    }

    public void editRelation(String clickedId, boolean isChildList) {

        String actionTxt;
        String first_id;
        String second_id;

        if(isChildList) {
            first_id = nodeId;
            second_id = clickedId;
        } else {
            first_id = clickedId;
            second_id = nodeId;
        }

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        if(existRelation(first_id, second_id)){
                            deleteValue(first_id, second_id);
                        } else {
                            insertValue(first_id, second_id);
                        }
                        if(isChildList){
                            ChildAdapter.swapCursor(getChildItems(nodeId));
                        } else {
                            ParentAdapter.swapCursor(getParentItems(nodeId));
                        }

                        break;
                }
            }
        };

        if(existRelation(first_id, second_id)) {
            actionTxt = "Delete relation?";
        } else {
            actionTxt = "Add relation?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(actionTxt).setNegativeButton("No", dialogClickListener)
                .setPositiveButton("Yes", dialogClickListener).show();
    }


    public void insertValue(String first_id, String second_id) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        if (db != null) {
            ContentValues v = new ContentValues();
            v.put("parent_id", first_id);
            v.put("child_id", second_id);
            db.insert(RelationEntry.TABLE_NAME, null, v);
        }
    }

    public void deleteValue(String first_id, String second_id) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        if (db != null) {
            ContentValues v = new ContentValues();
            v.put("value", second_id);
            db.delete(RelationEntry.TABLE_NAME, RelationEntry.COLUMN_PARENT_ID + " = "
                    + first_id + " AND " + RelationEntry.COLUMN_CHILD_ID + " = " + second_id, null);
        }
    }

    //get sorted parent items(with relation on top)
    private boolean existRelation(String first_id, String second_id) {
        if((DatabaseUtils.queryNumEntries(mDatabase, RelationEntry.TABLE_NAME,
                RelationEntry.COLUMN_PARENT_ID + " = ? AND " + RelationEntry.COLUMN_CHILD_ID + " = ?", new String[] {first_id, second_id})) > 0){
            return true;
        } else {
            return false;
        }
    }

    //get sorted child items(with relation on top)
    private Cursor getChildItems(String id) {
        // SELECT DISTINCT _id, value FROM node LEFT JOIN parent_child
        // ON node._id = parent_child.child_id WHERE node._id != { id }
        // ORDER BY CASE WHEN parent_id = { id } THEN 0 ELSE 1 END, parent_id DESC
        return mDatabase.query(true,
                NodeEntry.TABLE_NAME + " LEFT JOIN " + RelationEntry.TABLE_NAME + " ON "
                        + NodeEntry.TABLE_NAME + "." + NodeEntry._ID + " = " + RelationEntry.TABLE_NAME
                        + "." + RelationEntry.COLUMN_CHILD_ID,
                new String[] {"_id", "value"},
                NodeEntry._ID + " != ?",
                new String[] { id },
                null,
                null,
                "CASE WHEN " + RelationEntry.COLUMN_PARENT_ID + " = " + id
                        + " THEN 0 ELSE 1 END, " + RelationEntry.COLUMN_PARENT_ID + " DESC",
                null
        );
    }

    //get sorted parent items
    private Cursor getParentItems(String id) {
        // SELECT DISTINCT _id, value FROM node LEFT JOIN parent_child
        // ON node._id = parent_child.parent_id WHERE node._id != { id }
        // ORDER BY CASE WHEN child_id = { id } THEN 0 ELSE 1 END, child_id DESC
        return mDatabase.query(true,
                NodeEntry.TABLE_NAME + " LEFT JOIN " + RelationEntry.TABLE_NAME + " ON "
                        + NodeEntry.TABLE_NAME + "." + NodeEntry._ID + " = " + RelationEntry.TABLE_NAME
                        + "." + RelationEntry.COLUMN_PARENT_ID,
                new String[] {"_id", "value"},
                NodeEntry._ID + " != ?",
                new String[] { id },
                null,
                null,
                "CASE WHEN " + RelationEntry.COLUMN_CHILD_ID + " = " + id
                        + " THEN 0 ELSE 1 END, " + RelationEntry.COLUMN_CHILD_ID + " DESC",
                null
        );
    }

}

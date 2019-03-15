package com.paint.sqLite;

import android.content.DialogInterface;
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
import com.paint.sqLite.data.NodeContract;

public class RelationActivity extends AppCompatActivity {

    private String nodeId; //target node
    private DBHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    private RelationAdapter parentAdapter;
    private RelationAdapter childAdapter;
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
        RecyclerView parentRView = findViewById(R.id.parentList);
        RecyclerView childRView = findViewById(R.id.childList);

        final String DEFAULT_VALUE = "1";
        String value;
        if (getIntent().hasExtra(NodeContract.NodeEntry._ID)) {
            nodeId = getIntent().getStringExtra(NodeContract.NodeEntry._ID);
            value = getIntent().getStringExtra(NodeContract.NodeEntry.COLUMN_VALUE);
        } else {
            nodeId = DEFAULT_VALUE;
            value = DEFAULT_VALUE;
        }
        relationId.setText(value);


        parentRView.setLayoutManager(new LinearLayoutManager(this));
        parentAdapter = new RelationAdapter(mDBHelper, mDatabase, mDBHelper.getSortedParentItems(mDatabase, nodeId), nodeId, false,
                new OnAdapterClickListener() {
                    @Override
                    public void onItemClicked(String clickedId, boolean isChildList) {
                        editRelation(clickedId, isChildList);
                    }
                });
        parentRView.setAdapter(parentAdapter);

        childRView.setLayoutManager(new LinearLayoutManager(this));
        childAdapter = new RelationAdapter(mDBHelper, mDatabase, mDBHelper.getSortedChildItems(mDatabase, nodeId), nodeId, true,
                new OnAdapterClickListener() {
                    @Override
                    public void onItemClicked(String clickedId, boolean isChildList) {
                        editRelation(clickedId, isChildList);
                    }
                });
        childRView.setAdapter(childAdapter);
    }

    public void editRelation(String clickedId, boolean isChildList) {

        String actionTxt;
        String first_id;
        String second_id;

        if (isChildList) {
            first_id = nodeId;
            second_id = clickedId;
        } else {
            first_id = clickedId;
            second_id = nodeId;
        }

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        if (mDBHelper.checkExistRelation(mDatabase, first_id, second_id)) {
                            mDBHelper.deleteRelationValue(mDatabase, first_id, second_id);
                        } else {
                            mDBHelper.insertRelationValue(mDatabase, first_id, second_id);
                        }
                        if (isChildList) {
                            childAdapter.swapCursor(mDBHelper.getSortedChildItems(mDatabase, nodeId));
                        } else {
                            parentAdapter.swapCursor(mDBHelper.getSortedParentItems(mDatabase, nodeId));
                        }

                        break;
                }
            }
        };

        if (mDBHelper.checkExistRelation(mDatabase, first_id, second_id)) {
            actionTxt = "Delete relation?";
        } else {
            actionTxt = "Add relation?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(actionTxt).setNegativeButton("No", dialogClickListener)
                .setPositiveButton("Yes", dialogClickListener).show();
    }

    @Override
    protected void onDestroy() {
        mDBHelper.close();
        super.onDestroy();
    }

}

package com.paint.sqLite;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.paint.sqLite.adapter.NodeAdapter;
import com.paint.sqLite.data.DBHelper;


public class MainActivity extends AppCompatActivity {

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
        mAdapter = new NodeAdapter(this, mDBHelper.getNodeItemsWithRelation(mDatabase));
        recyclerView.setAdapter(mAdapter);

        Button buttonAdd = findViewById(R.id.btnAddNew);
        buttonAdd.setOnClickListener(v -> addNewValue());
    }


    private void addNewValue() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        final LayoutInflater l = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = l.inflate(R.layout.add_new, null);
        final EditText text = (EditText) v.findViewById(R.id.add);
        alertDialog.setView(v);

        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final int value = Integer.parseInt(text.getText().toString());
                mDBHelper.insertNodeValue(mDatabase, value);
                mAdapter.swapCursor(mDBHelper.getNodeItemsWithRelation(mDatabase));
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        mDBHelper.close();
        super.onDestroy();
    }

}
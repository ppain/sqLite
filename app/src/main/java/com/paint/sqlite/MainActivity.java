package com.paint.sqlite;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.paint.sqlite.data.DBHelper;


public class MainActivity extends AppCompatActivity{

    final String LOG_TAG = "myLogs";
    final String nodeDB = "node";
    final String parentChildDB = "parent_child";
    private DBHelper mDBHelper;
    private ListAdapter mListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mDBHelper = new DBHelper(this);
        ListView listView = (ListView)findViewById(R.id.list);
        mListAdapter = new ListAdapter(this, mDBHelper);
        listView.setAdapter(mListAdapter);
        //listView.setOnItemClickListener((adapterView, view, position, id) -> Toast.makeText(getApplicationContext(), "tratata", Toast.LENGTH_SHORT).show());
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, RelationshipActivity.class);
            intent.putExtra("position", position);
            startActivity(intent);
        });

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
                mDBHelper.addValue(nodeDB, value);
                mListAdapter.notifyDataSetChanged();
            }
        });
        alertDialog.show();
    }
}
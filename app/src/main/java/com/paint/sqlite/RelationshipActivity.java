package com.paint.sqlite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.paint.sqlite.data.DBHelper;


public class RelationshipActivity extends AppCompatActivity {
    final String LOG_TAG = "myLogs";
    final String nodeDB = "node";
    final String parentChildDB = "parent_child";
    private DBHelper mDBHelper;
    private ListAdapter mListParentAdapter;
    private ListAdapter mListChildAdapter;


    private ListAdapter mListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init();
        ListView listView = (ListView)findViewById(R.id.list);
        mListAdapter = new ListAdapter(this, mDBHelper);
    }

    private void init() {
        mDBHelper = new DBHelper(this);
        ListView listParentView = (ListView)findViewById(R.id.listParent);
        ListView listChildView = (ListView)findViewById(R.id.listChild);
        mListParentAdapter = new ListAdapter(this, mDBHelper);
        mListChildAdapter = new ListAdapter(this, mDBHelper);

        listParentView.setAdapter(mListParentAdapter);
        listParentView.setOnItemClickListener((adapterView, view, position, id) -> Toast.makeText(getApplicationContext(), "tratata " + position, Toast.LENGTH_SHORT).show());
        listChildView.setAdapter(mListChildAdapter);
        listChildView.setOnItemClickListener((adapterView, view, position, id) -> Toast.makeText(getApplicationContext(), "tratata " + position, Toast.LENGTH_SHORT).show());

    }
}

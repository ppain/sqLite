package com.paint.sqlite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paint.sqlite.data.DBHelper;

import java.lang.ref.WeakReference;

public class ListAdapter extends BaseAdapter {
    private WeakReference<Context> mContextWeakReference;
    private DBHelper mDBHelper;

    public ListAdapter(Context context, DBHelper dbHelper) {
        mContextWeakReference = new WeakReference<Context>(context);
        mDBHelper = dbHelper;
    }

    @Override
    public int getCount() {
        return mDBHelper.getCount();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Context context = mContextWeakReference.get();
        if (context != null) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.node_list, null);

            }
            int value = mDBHelper.getValue("node",i+1);
            TextView textView = (TextView)view.findViewById(R.id.text);
            textView.setText(String.valueOf(value));
            return view;
        }
        return null;
    }
}

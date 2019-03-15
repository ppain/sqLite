package com.paint.sqLite.adapter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paint.sqLite.R;
import com.paint.sqLite.data.DBHelper;
import com.paint.sqLite.data.NodeContract.*;


public class RelationAdapter extends RecyclerView.Adapter<RelationAdapter.rViewHolder> {
    private SQLiteDatabase mDatabase;
    private DBHelper mDBHelper;
    private Cursor mCursor;
    private boolean isChildList;
    private String nodeId;
    private int count; //count items related with target node
    private boolean flagCount; //update flag

    public OnAdapterClickListener onAdapterClickListener;

    public RelationAdapter(DBHelper dbHelper, SQLiteDatabase database, Cursor cursor, String nodeId, boolean isChildList,
                           OnAdapterClickListener onAdapterClickListener) {
        flagCount = true;
        mDBHelper = dbHelper;
        mDatabase = database;
        mCursor = cursor;
        this.nodeId = nodeId;
        this.isChildList = isChildList;
        this.onAdapterClickListener = onAdapterClickListener;
    }

    public class rViewHolder extends RecyclerView.ViewHolder {
        public TextView valueText;
        LinearLayout parentLayout;

        public rViewHolder(View itemView) {
            super(itemView);
            valueText = itemView.findViewById(R.id.tv_value_item);
            parentLayout = itemView.findViewById(R.id.node_layout);
        }
    }

    @Override
    public rViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.node_item, parent, false);
        return new rViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RelationAdapter.rViewHolder holder, final int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String clickedId = mCursor.getString(mCursor.getColumnIndex(NodeEntry._ID));
        String value = mCursor.getString(mCursor.getColumnIndex(NodeEntry.COLUMN_VALUE));

        if (flagCount) {
            flagCount = false;
            setCount();
        }

        //test on setBackground green
        if (count-- > 0) {
            holder.valueText.setBackgroundResource(R.color.colorGreen);
        } else {
            holder.valueText.setBackgroundResource(0);
        }

        holder.valueText.setText(value);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAdapterClickListener.onItemClicked(clickedId, isChildList);
            }
        });
    }

    private void setCount() {
        if (isChildList) {
            count = mDBHelper.getCountRelation(mDatabase, nodeId, RelationEntry.COLUMN_PARENT_ID);
        } else {
            count = mDBHelper.getCountRelation(mDatabase, nodeId, RelationEntry.COLUMN_CHILD_ID);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        flagCount = true;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}
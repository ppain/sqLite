package com.paint.sqLite.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paint.sqLite.R;
import com.paint.sqLite.RelationActivity;
import com.paint.sqLite.data.NodeContract.*;

public class NodeAdapter extends RecyclerView.Adapter<NodeAdapter.NodeViewHolder> {
    private Context mContext;
    private Cursor mCursor;

    public NodeAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public class NodeViewHolder extends RecyclerView.ViewHolder {
        public TextView valueText;
        LinearLayout parentLayout;

        public NodeViewHolder(View itemView) {
            super(itemView);
            valueText = itemView.findViewById(R.id.tv_value_item);
            parentLayout = itemView.findViewById(R.id.node_layout);
        }
    }

    @Override
    public NodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.node_item, parent, false);
        return new NodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NodeViewHolder holder, final int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String nodeId = mCursor.getString(mCursor.getColumnIndex(NodeEntry._ID));
        String value = mCursor.getString(mCursor.getColumnIndex(NodeEntry.COLUMN_VALUE));
        String parent_id = mCursor.getString(mCursor.getColumnIndex(RelationEntry.COLUMN_PARENT_ID));
        String child_id = mCursor.getString(mCursor.getColumnIndex(RelationEntry.COLUMN_CHILD_ID));

        //no relation
        if ((parent_id == null) && (child_id == null)) {
            holder.valueText.setBackgroundResource(0);
        }
        //parent relation
        else if ((parent_id != null) && (child_id == null)) {
            holder.valueText.setBackgroundResource(R.color.colorBlue);
        }
        //child relation
        else if ((parent_id == null) && (child_id != null)) {
            holder.valueText.setBackgroundResource(R.color.colorYellow);
        }
        //parent and child relation
        else {
            holder.valueText.setBackgroundResource(R.color.colorRed);
        }

        holder.valueText.setText(value);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RelationActivity.class);
                intent.putExtra(NodeEntry._ID, nodeId);
                intent.putExtra(NodeEntry.COLUMN_VALUE, value);
                mContext.startActivity(intent);
            }
        });
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

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}

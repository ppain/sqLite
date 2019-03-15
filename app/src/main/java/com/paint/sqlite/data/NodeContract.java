package com.paint.sqLite.data;

import android.provider.BaseColumns;

public final class NodeContract {
    private NodeContract(){}

    public static final class NodeEntry implements BaseColumns {
        public final static String TABLE_NAME = "node";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_VALUE = "value";
    }

    public static final class RelationEntry implements BaseColumns {
        public final static String TABLE_NAME = "parent_child";
        public final static String COLUMN_PARENT_ID = "parent_id";
        public final static String COLUMN_CHILD_ID = "child_id";
    }
}

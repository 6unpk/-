package parkjunu.finder.com.viewer;

import android.provider.BaseColumns;

public final class DataBases{
    public  static final class CreateTagDB implements BaseColumns {
        public static final String FILENAME = "tagList.db";
        public static final String TAG = "tag";
        public static final String _TABLENAME = "tagList";
        public static final String _CREATAE = "create table " +_TABLENAME +
                "("+_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + TAG + ");";

    }

}

package parkjunu.finder.com.viewer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper {

    public static String DATABASE_NAME = "";

    public static int DATABASE_VERSION = 1;

    public Context context;

    private static SQLiteDatabase database;

    private DataBaseOpenHelper dataBaseOpenHelper;

    public DbHelper(Context context, String name){
        this.context = context;
        DATABASE_NAME = name;
    }

    public boolean open(){
        try{
            dataBaseOpenHelper = new DataBaseOpenHelper();
            database = dataBaseOpenHelper.getWritableDatabase();
        }catch (Exception e){
            Log.d("tag", ""+ e);
            return false;
        }
        return true;
    }

    public static Cursor rawQuery(String SQL){
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(SQL, null);
        }catch (Exception e){
            Log.d("tag", ""+e);
        }
        return cursor;
    }

    public boolean exeQuery(String SQL){
        try {
            database.execSQL(SQL);
        }catch (Exception e){
            Log.d("tag", ""+e);
            return false;
        }
        return true;
    }

    private class DataBaseOpenHelper extends SQLiteOpenHelper{
        public DataBaseOpenHelper(){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db){
            String DROP_SQL = "drop table if exists " + DataBases.CreateTagDB._TABLENAME;
            try {
                db.execSQL(DROP_SQL);
            }catch (Exception e){
                Log.d("tag", ""+e);
            }

            try {
                db.execSQL(DataBases.CreateTagDB._CREATAE);
            }catch (Exception e){
                Log.d("tag", ""+e);
            }
        }

        public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer){

        }
    }

    public void close(){
        database.close();
        database = null;
    }
}
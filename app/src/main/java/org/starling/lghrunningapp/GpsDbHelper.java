package org.starling.lghrunningapp;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GpsDbHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "contacts.db";
    private static final int DATABASE_VERSION = 1;

    //Constants for table and columns
    public static final String TABLE_POINTS = "points";
    public static final String ID = "_id";
    public static final String LAT = "lat";
    public static final String LONG = "long";
    public static final String TIME = "time";

    public static final String[] ALL_COLUMNS = {ID, LAT, LONG, TIME};

    //Create Table
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_POINTS + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LAT + " LONG, " +
                    LONG + " LONG, " +
                    TIME + "  default CURRENT_TIMESTAMP" +
                    ")";

    public GpsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_POINTS);
        onCreate(sqLiteDatabase);
    }
}
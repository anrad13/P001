package ru.anrad.p001.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "core.MySQLiteHelper";

    private static final String DATABASE_NAME = "anrad.duty.db";
    private static final int DATABASE_VERSION = 2;

    public static final String DUTY_TABLENAME = "duty";
    public static final String ARHIVE_TABLENAME = "arhive";
    public static final String TRASH_TABLENAME = "trash";
    public static final String ITEMS_TABLENAME = "items";


    public static final String DUTY_ID = "id";
    public static final String DUTY_UUID = "uuid";
    public static final String DUTY_WHO = "duty_who";
    public static final String DUTY_WHAT = "duty_what";
    public static final String DUTY_WHEN = "duty_when";
    public static final String DUTY_NOTE = "duty_note";
    public static final String DUTY_TIMESTAMP = "state_timestamp";
    public static final String DUTY_STATE = "state";


    public static final String DATE_NULL_VALUE = "0";
    public static final String STRING_NULL_VALUE = "";

    // Database creation sql statement
    private static final String DUTY_TABLE_CREATE =
            "create table "
                    + DUTY_TABLENAME + "("
                    + DUTY_ID + " integer primary key autoincrement, "
                    + DUTY_UUID + " text, "
                    + DUTY_STATE + " text, "
                    + DUTY_TIMESTAMP + " long, "
                    + DUTY_WHAT + " text, "
                    + DUTY_WHO + " text, "
                    + DUTY_WHEN + " long, "
                    + DUTY_NOTE + " text "
                    + ");";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DUTY_TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(MySQLiteHelper.class.getName()," Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + DUTY_TABLENAME);
        //db.execSQL("DROP TABLE IF EXISTS " + ARHIVE_TABLENAME);
        //db.execSQL("DROP TABLE IF EXISTS " + TRASH_TABLENAME);
        //db.execSQL("DROP TABLE IF EXISTS " + ITEMS_TABLENAME);
        onCreate(db);
    }

}

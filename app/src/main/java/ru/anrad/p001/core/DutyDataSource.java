package ru.anrad.p001.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;
import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class DutyDataSource {


    //Singletone functionality
    private static DutyDataSource instance;

    public static DutyDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new DutyDataSource(context.getApplicationContext());
        }
        return instance;
    }

    private SQLiteDatabase db;
    private MySQLiteHelper dbHelper;

    private HashMap<String, HashMap<String,Duty>> items;

    private final String[] ALL_COLUMNS = {
            MySQLiteHelper.DUTY_UUID,
            MySQLiteHelper.DUTY_WHAT,
            MySQLiteHelper.DUTY_WHO,
            MySQLiteHelper.DUTY_WHEN,
            MySQLiteHelper.DUTY_NOTE,
            MySQLiteHelper.DUTY_TIMESTAMP,
            MySQLiteHelper.DUTY_STATE,
    };

    public static final int ACTIVE_AGENDA = 1;
    public static final int ACTIVE_INBOX = 2;
    public static final int ACTIVE_SHEDULE = 3;
    public static final int ACTIVE_ASSIGNED = 4;
    public static final int TRASH = 10;
    public static final int ARHIVE = 20;

    private DutyDataSource(Context context) {
        this.dbHelper = new MySQLiteHelper(context);
        this.open();

        items = initItems();
    }

    private void open() throws SQLException {
        this.db = this.dbHelper.getWritableDatabase();
    }
    public void close() {
        this.dbHelper.close();
    }

    private HashMap<String, HashMap<String,Duty>> initItems() {
        HashMap<String, HashMap<String,Duty>> i = new HashMap<>();
        i.put(Duty.ACTIVE, new HashMap<String, Duty>());
        i.put(Duty.ARHIVE, new HashMap<String, Duty>());
        i.put(Duty.TRASH, new HashMap<String, Duty>());
        return i;
    }



    public Duty getItem(String uuid){
        Duty d = items.get(Duty.ACTIVE).get(uuid);
        if (d == null) { d = items.get(Duty.ARHIVE).get(uuid); }
        if (d == null) { d = items.get(Duty.TRASH).get(uuid); }
        if (d == null) { d = getItemFromDatabase(uuid); }

        //Log.v("DutyDataSource", "getItem: uuid=" + uuid + ": item= " + d.toString());
        return d;
    }

    public ArrayList<Duty> getItemsList (int list) {
        switch (list) {
            case DutyDataSource.ACTIVE_AGENDA:
                return getActiveItemsList(list);
            case DutyDataSource.ACTIVE_INBOX:
                return getActiveItemsList(list);
            case DutyDataSource.ACTIVE_ASSIGNED:
                return getActiveItemsList(list);
            case DutyDataSource.ACTIVE_SHEDULE:
                return getActiveItemsList(list);
            case DutyDataSource.TRASH:
                return getTrashItemsList();
            case DutyDataSource.ARHIVE:
                return getArhiveItemsList();
            default:
                return new ArrayList<Duty>();
        }
    }

    private ArrayList<Duty> getActiveItemsList(int list) {
        HashMap<String,Duty> in = items.get(Duty.ACTIVE);
        if (in.isEmpty()) { loadItemsFromDatabase(Duty.ACTIVE);}
        //
        ArrayList<Duty> out = new ArrayList<>();
        //Log.v("DutyDataSource", "getActiveItemsList: list: " + list);

        for (Duty d : in.values()) {
            switch (list) {
                case DutyDataSource.ACTIVE_AGENDA:
                    out.add(d);
                    break;
                case DutyDataSource.ACTIVE_SHEDULE:
                    if (d.getWhen() != null) out.add(d);
                    break;
                case DutyDataSource.ACTIVE_ASSIGNED:
                    if (d.getWho() != null) out.add(d);
                    break;
                case DutyDataSource.ACTIVE_INBOX:
                    if (d.getWho() == null && d.getWhen() == null) out.add(d);
                    break;

            }

        }
        Collections.sort(out, new DutyComparator());
        return out;
    }

    private ArrayList<Duty> getTrashItemsList() {
        HashMap<String,Duty> in = items.get(Duty.TRASH);
        if (in.isEmpty()) { loadItemsFromDatabase(Duty.TRASH);}
        //
        ArrayList<Duty> out = new ArrayList<>();
        for (Duty d : in.values()) {
            out.add(d);
        }

        Collections.sort(out,  new Comparator<Duty>() {
            @Override
            public int compare(Duty d1, Duty d2) {
                return d1.getTimestamp().compareTo(d2.getTimestamp());
            }
        });
        return out;
    }

    private ArrayList<Duty> getArhiveItemsList() {
        HashMap<String,Duty> in = items.get(Duty.ARHIVE);
        if (in.isEmpty()) { loadItemsFromDatabase(Duty.ARHIVE);}
        //
        ArrayList<Duty> out = new ArrayList<>();
        for (Duty d : in.values()) {
            out.add(d);
        }
        Collections.sort(out,  new Comparator<Duty>() {
            @Override
            public int compare(Duty d1, Duty d2) {
                return d1.getTimestamp().compareTo(d2.getTimestamp());
            }
        });

        return out;
    }


    public void putItem(Duty duty) {
        items.get(Duty.ACTIVE).put(duty.getUUID(), duty);
        putItemInDatabase(duty);
    }

    public void updateItem(Duty duty) {
        Duty dutySaved = getItemFromDatabase(duty.getUUID());
        if (dutySaved == null) return;

        if (!dutySaved.getState().equals(duty.getState())) {
            if (items.get(dutySaved.getState()).containsKey(dutySaved.getUUID())) {
                items.get(dutySaved.getState()).remove(dutySaved.getUUID());}
            if (items.get(duty.getState()).containsKey(duty.getUUID())) {
                items.get(duty.getState()).put(duty.getUUID(), duty);}
        }

        updateItemInDatabase(duty);
     }

    public void deleteItem(Duty duty) {
        if (items.get(duty.getState()).containsKey(duty.getUUID())) {
            items.get(duty.getState()).remove(duty.getUUID());
        }
        deleteItemFromDatabase(duty);
    }

    static private int TRASH_DELAY_DAY = 10;
    public void clearTrash() {
        Calendar timepoint = Calendar.getInstance();
        timepoint.set(Calendar.HOUR_OF_DAY, 0);
        timepoint.set(Calendar.MINUTE, 0);
        timepoint.set(Calendar.SECOND, 0);
        timepoint.set(Calendar.MILLISECOND, 0);
        timepoint.add(Calendar.DATE, -TRASH_DELAY_DAY);

        ArrayList<Duty> in = getTrashItemsList();

        for (Duty d : in ) {
            Calendar c = Calendar.getInstance();
            c.setTime(d.getTimestamp());
            if (c.before(timepoint)) deleteItem(d);
        }
    }

    private ContentValues buildDutyContentValuesForDatabase(Duty duty) {
        //Log.v("DutyDataSource", "buildDutyContentValuesForDatabase: duty = "+ duty.toString());
        ContentValues values = new ContentValues();
        //insert uuid
        values.put(MySQLiteHelper.DUTY_UUID, duty.getUUID());
        //insert what
        values.put(MySQLiteHelper.DUTY_WHAT, duty.getWhat());
        //insert who
        if (!duty.isWho()) {
            values.put(MySQLiteHelper.DUTY_WHO, MySQLiteHelper.STRING_NULL_VALUE);
        }
        else {
            values.put(MySQLiteHelper.DUTY_WHO, duty.getWho());
        }
        //insert when
        if (duty.isWhen()) {
            values.put(MySQLiteHelper.DUTY_WHEN, duty.getWhen().getTime());
            //Log.v("DutyDataSource", "buildDutyContentValuesForDatabase: Put When");
        }
        else {
            values.put(MySQLiteHelper.DUTY_WHEN, MySQLiteHelper.DATE_NULL_VALUE);
        }
        //insert note
        values.put(MySQLiteHelper.DUTY_NOTE, duty.getNote());
        //insert timestamp
        values.put(MySQLiteHelper.DUTY_TIMESTAMP, duty.getTimestamp().getTime());
        //insert state
        values.put(MySQLiteHelper.DUTY_STATE, duty.getState());

        return values;
    }

    private void putItemInDatabase(Duty duty) {
        ContentValues values = buildDutyContentValuesForDatabase(duty);
        db.insert(MySQLiteHelper.DUTY_TABLENAME, null, values);
        //Log.v(DutyDataSource.class.getName(), "Put duty in db:" + duty.toString());
    }
    private void deleteItemFromDatabase(Duty duty) {
        db.delete(MySQLiteHelper.DUTY_TABLENAME, MySQLiteHelper.DUTY_UUID + " = ?", new String[]{duty.getUUID()});
        //Log.v(DutyDataSource.class.getName(), "Delete duty in db");
    }
    private void updateItemInDatabase(Duty duty) {
        ContentValues values = buildDutyContentValuesForDatabase(duty);
        db.update(MySQLiteHelper.DUTY_TABLENAME, values, MySQLiteHelper.DUTY_UUID + " = ?", new String[]{duty.getUUID()});
        //Log.v(DutyDataSource.class.getName(), "Update duty in db");
    }
    private Duty getItemFromDatabase(String uuid) {
        Duty d = null;
        Cursor cursor = db.query(MySQLiteHelper.DUTY_TABLENAME, ALL_COLUMNS, MySQLiteHelper.DUTY_UUID + " = ?", new String[]{uuid}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            d = cursorToDuty(cursor);
            Log.v("MyApp:DutyDataSource", "getItemFromDatabase, uuid="+ uuid+ ", item= " + d.toString());
            cursor.moveToNext();
        }
        cursor.close();
        return d;
    }

    private void loadItemsFromDatabase(String state) {
        HashMap<String,Duty> l = items.get(state);
        l.clear();
        //Log.v("DutyDataSource", "loadItemsFromDatabase: state: " + state);
        Cursor cursor = db.query(MySQLiteHelper.DUTY_TABLENAME, ALL_COLUMNS, MySQLiteHelper.DUTY_STATE + " = ?", new String[]{state}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Duty d = cursorToDuty(cursor);
            l.put(d.getUUID(), d);
            //Log.v("DutyDataSource", "loadItemsFromDatabase: item: " + d.toString());
            cursor.moveToNext();
        }
        cursor.close();
    }

    private Duty cursorToDuty (Cursor cursor){
        String uuid;
        String what;
        String who;
        Date when;
        String state;
        Date timestamp;
        String note;

        uuid = cursor.getString(0);
        //what
        what = cursor.getString(1);
        //who
        if (cursor.getString(2).length() != 0) {
            who = cursor.getString(2);
        }
        else {
            who = null;
        }
        //when
        if (cursor.getLong(3) != 0) {
            when = new Date(cursor.getLong(3));
        }
        else {
            when = null;
        }
        //note
        note = cursor.getString(4);
        timestamp = new Date(cursor.getLong(5));
        state = cursor.getString(6);

        return new Duty(uuid, what, who, when, note, state, timestamp);
    }

}

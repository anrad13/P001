package ru.anrad.p001.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;


public class Duty {

    public static final String ACTIVE = "1";
    public static final String ARHIVE = "2";
    public static final String TRASH = "3";

    private String uuid;
    private String what;
    private String who;
    private Date when;
    private String note;
    private Date timestamp;
    private String state;


    public Duty(@NonNull String _what) {
        this.uuid = UUID.randomUUID().toString();
        this.what = _what;
        this.state = Duty.ACTIVE;
        this.timestamp = new Date();
        this.note = "";
    }

    public Duty(
            @NonNull String _uuid,
            @NonNull String _what,
            @Nullable String _who,
            @Nullable Date _when,
            @NonNull String _note,
            @NonNull String _state,
            @NonNull Date _timestamp
            )
    {
        uuid = _uuid;
        what = _what;
        who = _who;
        when = _when;
        note = _note;
        state = _state;
        timestamp = _timestamp;
    }


    public Duty(@NonNull String _what, String _who, Date _when) {
        this(_what);
        this.who = _who;
        this.when = _when;
    }

    public void setWhat(String s) {
        if (s != null || !s.trim().isEmpty()) {
            this.what = s;
        }
    }
    public void setWho(String s) {
        if (s == null || s.trim().isEmpty()) {
            who = null;
        }
        else {
            who = s;
        }
    }
    public void setWhen(Date date) {
        if (date == null) {
            when = null;
        }
        else {
            when = date;
        }
    }
    public void setNote(String s) {
        if (s == null ) {
            note = "";
        }
        else {
            note = s.trim();
        }
    }
    public void setActive() {
        state = Duty.ACTIVE;
        timestamp = new Date();
    }
    public void setArhive() {
        state = Duty.ARHIVE;
        timestamp = new Date();
    }
    public void setTrash() {
        state = Duty.TRASH;
        timestamp = new Date();
    }



    public String getUUID() {
        return uuid;
    }
    public String getWhat() {
        return what;
    }
    public Date getWhen() {
        return when;
    }
    public String getWho() {
        return who;
    }
    public String getNote() {
        return note;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public String getState() {
        return state;
    }

    public boolean isActive() { return state.equals(Duty.ACTIVE); }
    public boolean isArhive() { return state.equals(Duty.ARHIVE); }
    public boolean isTrash() { return state.equals(Duty.TRASH); }

    public boolean isWho() {return (who != null);}
    public boolean isWhen() {return when != null;}

    public String toString() {
        String s = "UUID=" + this.uuid;
        if (this.who != null) {
            s = s + ", who=" + this.who;
        }
        if (this.when != null) {
            s = s + ", when=" + this.when.toString();
        }
        if (this.what.length() > 50) {
            s = s + ", what=" + this.what.substring(0,48) + "...";
        }
        else {
            s = s + ", what=" + this.what;
        }
        return s;
    }
}

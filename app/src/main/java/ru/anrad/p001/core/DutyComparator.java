package ru.anrad.p001.core;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class DutyComparator implements Comparator<Duty> {

    private Calendar today;
    private String dutyState;

    public DutyComparator() {
        today = Calendar.getInstance();
    }

     public int compare(Duty d1, Duty d2) {
        Date date1 = d1.getWhen();
        Date date2 = d2.getWhen();
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        int res;

        if (date1 == null) {
            if (date2 == null) {
                res = 0;
            }
            else {
                c1 = today;
                c2.setTime(date2);
                int r = c1.compareTo(c2);
                if (r < 0) {
                    res = -1;
                }
                else {
                    res =  1;
                }
            }
        }
        else {
            if (date2 == null) {
                c1.setTime(date1);
                c2 = today;
                int r = c1.compareTo(c2);
                if (r > 0) {
                    res =  1;
                } else {
                    res =  -1;
                }
            }
            else {
                c1.setTime(date1);
                c2.setTime(date2);
                res = c1.compareTo(c2);
            }
        }
        if (res == 0) {
            res = d1.getWhat().compareTo(d2.getWhat());
        }
        return res;
    }

}

package com.rishi.frendzapp.ui.event;

import com.rishi.frendzapp_core.models.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by AD on 19-Feb-16.
 */
public class DateCompare implements Comparable<User> {
    public DateCompare(){

    }

    @Override
    public int compareTo(User user) {
        DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        Date date1;
        try {
            date1 = f.parse(user.getDob());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
        Calendar cal1 = Calendar.getInstance();
        System.out.println(cal1.get(Calendar.YEAR));
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date1);

        int month1 = cal1.get(Calendar.MONTH);
        int month2 = cal2.get(Calendar.MONTH);

        if(month1 < month2)
            return -1;
        else if(month1 == month2)
            return cal1.get(Calendar.DAY_OF_MONTH) - cal2.get(Calendar.DAY_OF_MONTH);

        else return 1;
    }
}

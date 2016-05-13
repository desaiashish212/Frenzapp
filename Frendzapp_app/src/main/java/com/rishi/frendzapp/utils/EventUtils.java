package com.rishi.frendzapp.utils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by AD on 17-Feb-16.
 */
public class EventUtils {
    private static String postpixDate;

    public static String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    public static int diffDay(int day, int month){
        postpixDate = " Days left";
        Calendar myBirthday=Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        myBirthday.set(now.get(Calendar.YEAR),month,day);
        System.out.println(now.get(Calendar.YEAR));
        long diffMillis= Math.abs(myBirthday.getTimeInMillis() - now.getTimeInMillis());
        long differenceInDays = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);

        if (now.get(Calendar.DAY_OF_MONTH)>day){
            differenceInDays = -1;
        }else if ((int)differenceInDays == 1){
            postpixDate = " Tomorrow";
        }
        if ((int)differenceInDays == 0){
            postpixDate = "Today";
        }

        return (int)differenceInDays;
    }

    public static String diffMonth(int day, int month){

        Calendar myBirthday=Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        postpixDate = " Months left";
        String returnDate=null;
        myBirthday.set(today.get(Calendar.YEAR), month, day);

        int difInMonths =  myBirthday.get(Calendar.MONTH) - today.get(Calendar.MONTH);
        returnDate = String.valueOf(difInMonths)+postpixDate;
        if (difInMonths==0){

            difInMonths = diffWeek(day, month);
            if (difInMonths==0)
                returnDate = postpixDate;
            else
                returnDate = String.valueOf(difInMonths)+postpixDate;
        }
        if (difInMonths<0){
            postpixDate = " Months left";
            returnDate = String.valueOf(12 + difInMonths)+postpixDate;
        }

        if(difInMonths==1){
            returnDate = "Next Week";
        }
        
        return returnDate;
    }

    public static int diffWeek(int day, int month){
        postpixDate = " Weeks left";
        Calendar myBirthday=Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        myBirthday.set(today.get(Calendar.YEAR), month, day);

        int weeks = myBirthday.get(Calendar.WEEK_OF_YEAR)-today.get(Calendar.WEEK_OF_YEAR);
        if(weeks==0){
            
            weeks = diffDay(day, month);
        }
        return weeks;
    }
}

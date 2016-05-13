package com.rishi.frendzapp.ui.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseLogeableActivity;
import com.rishi.frendzapp_core.db.managers.UsersDatabaseManager;
import com.rishi.frendzapp_core.models.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by AD on 08-Feb-16.
 */
public class EventActivity extends BaseLogeableActivity {
    private ListView list;
    private EventAdapter adapter;
    private String databaseDateFormat = getDatabaseDateFormat();
    public static void start(Context context) {
        Intent intent = new Intent(context, EventActivity.class);
        context.startActivity(intent);
    }
    private String getDatabaseDateFormat() {
        // Not yet needed till Issue #9676 & #2947 are fixed - currently the database format is yyyy-mm-dd
        return "dd-mm-yyyy";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        list = (ListView) findViewById(R.id.events_listview);
        initActionBar();
        List<User> friendList = UsersDatabaseManager.getAllUsers(this);
        System.out.println("friendList:"+friendList.toString());
        Collections.sort(friendList, new Comparator<User>() {
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
            Calendar now = Calendar.getInstance();
            Date date1;
            Date date2;

            @Override
            public int compare(User d1, User d2) {
                try {
                    date1 = f.parse(d1.getDob());
                    date2 = f.parse(d2.getDob());
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(date1);
                    c1.set(Calendar.YEAR, now.get(Calendar.YEAR));
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(date2);
                    c2.set(Calendar.YEAR, now.get(Calendar.YEAR));
                    return c1.getTime().compareTo(c2.getTime());
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });


        Iterator<User> ite = friendList.iterator();
        List<User> tmp = new ArrayList();
        Calendar cal1 = Calendar.getInstance();
        while (ite.hasNext()){
            User user = ite.next();
            int month = getDatabaseDateMonth(user.getDob());
            int day = getDatabaseDateDay(user.getDob());
            System.out.println("User:"+user.getFullName()+" MONTH:"+month);
            if (month==0 || (day<cal1.get(Calendar.DAY_OF_MONTH)&& month==cal1.get(Calendar.MONTH))){
                ite.remove();
                tmp.add(user);
                System.out.println("tmp:" + tmp.toString()+ " DAY" + day + " MONTH:" + month);
            }
        }
        friendList.addAll(tmp);


        adapter = new EventAdapter(this,friendList);
        list.setAdapter(adapter);
    }
    private int getDatabaseDateMonth(String string) {
        int index = this.databaseDateFormat.indexOf("mm");
        String str = string.substring(index, index + 2);
        String str1 = str.replace("/","");
        return Integer.parseInt(str1)-1;
    }
    private int getDatabaseDateDay(String string) {
        int index = this.databaseDateFormat.indexOf("dd");
        String str = string.substring(index, index + 2);
        String str1 = str.replace("/", "");
        return Integer.parseInt(str1);

    }

    private void initActionBar(){
        actionBar.setTitle("Birthday");
        actionBar.setLogo(R.drawable.bob);
    }

}

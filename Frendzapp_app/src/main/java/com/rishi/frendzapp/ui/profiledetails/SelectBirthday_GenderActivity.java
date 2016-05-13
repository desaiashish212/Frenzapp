// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.rishi.frendzapp.ui.profiledetails;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.users.model.QBUser;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.InitInterface;
import com.rishi.frendzapp.ui.authorization.base.BaseAuthActivity;
import com.rishi.frendzapp_core.core.command.Command;
import com.rishi.frendzapp_core.models.AppSession;
import com.rishi.frendzapp_core.models.UserCustomData;
import com.rishi.frendzapp_core.qb.commands.QBUpdateUserCommand;
import com.rishi.frendzapp_core.service.QBServiceConsts;
import com.rishi.frendzapp_core.utils.ConstsCore;
import com.rishi.frendzapp_core.utils.DialogUtils;
import com.rishi.frendzapp_core.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class SelectBirthday_GenderActivity extends BaseAuthActivity
    implements InitInterface, View.OnClickListener,AdapterView.OnItemSelectedListener
{
    private QBUser user;
    private UserCustomData userCustomData;
    private String dob=null;
    private String name=null;
    private String email=null;
    private EditText fullname_edittext;
    private EditText email_edittext;
    private TextView txt_submit;

    //
    private TextView display_date;
    ImageView img_calender;
    private int year;
    private int month;
    private int day;
    Date date_current;
    static final int DATE_PICKER_ID = 1111;
    public static void start(Context context) {

        Intent intent = new Intent(context, SelectBirthday_GenderActivity.class);
        context.startActivity(intent);
    }

    public SelectBirthday_GenderActivity()
    {
    }

    public void init()
    {
        txt_submit = (TextView)findViewById(R.id.txt_submit);
        fullname_edittext = (EditText) findViewById(R.id.fullname_edittext);
        email_edittext = (EditText) findViewById(R.id.email_edittext);
        display_date=(TextView)findViewById(R.id.textView3);
        img_calender=(ImageView)findViewById(R.id.img_calender);


}

    public void onClick(View view)
    {
        switch (view.getId()) {
            default:
                break;
            case R.id.txt_submit:

                dob=display_date.getText().toString();
                name = fullname_edittext.getText().toString().trim();
                email= email_edittext.getText().toString().trim();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) &&!TextUtils.isEmpty(dob )) {
                    if (name.length()>2)
                    updateUserData();
                    else
                        Toast.makeText(this,"Name must be more than two character",Toast.LENGTH_LONG).show();
                }
                else
                Toast.makeText(this,"Please fill all field",Toast.LENGTH_LONG).show();
                break;
            case R.id.img_calender:
                showDialog(DATE_PICKER_ID);
                break;


        }
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);/*
        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            bundle = getWindow();
            bundle.addFlags(0x80000000);
            bundle.setStatusBarColor(getResources().getColor(0x7f04000e));
        }*/
        setContentView(R.layout.activity_profile_fill);
        user = AppSession.getSession().getUser();
        init();
        initCustomData();
        initBroadcastActionList();
        setListener();
    }
    private void initCustomData() {
        userCustomData = Utils.customDataToObject(user.getCustomData());

        if (userCustomData == null) {
            userCustomData = new UserCustomData();
        }
    }
    private void initBroadcastActionList() {
        addAction(QBServiceConsts.UPDATE_USER_SUCCESS_ACTION, new UpdateUserSuccessAction());
        addAction(QBServiceConsts.UPDATE_USER_FAIL_ACTION, new UpdateUserFailAction());
    }
    public void setListener()
    {
        txt_submit.setOnClickListener(this);
        img_calender.setOnClickListener(this);
    }
    public void onItemSelected(AdapterView adapterview, View view, int i, long l)
    {

    }

    public void onNothingSelected(AdapterView adapterview)
    {
    }
    private void updateUserData() {
       // updateCurrentUserData();
        if (isUserDataChanged()) {
            saveChanges();
            System.out.println("Next Activity");
        }else{
            Toast.makeText(SelectBirthday_GenderActivity.this, "Please fill all information", Toast.LENGTH_LONG).show();
        }
    }

    public void updateCurrentUserData(){

        dob = display_date.getText().toString();

        name = fullname_edittext.getText().toString();

        email= email_edittext.getText().toString();
    }

    private boolean isUserDataChanged() {
        return !TextUtils.isEmpty(name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(dob);
    }

    private void saveChanges(){
        if (!isUserDataCorrect()) {
            DialogUtils.showLong(this, getString(R.string.dlg_not_all_fields_entered));
            return;
        }
        showProgress();

        QBUser newUser = createUserForUpdating();
        QBUpdateUserCommand.start(this, newUser, null);
    }
    private boolean isUserDataCorrect() {
        return name.length() > ConstsCore.ZERO_INT_VALUE;
    }

    private QBUser createUserForUpdating() {
        QBUser newUser = new QBUser();
        newUser.setId(user.getId());

        user.setFullName(name);
        newUser.setFullName(name);

        user.setEmail(email);
        newUser.setEmail(email);

        userCustomData.setDob(dob);
        user.setCustomData(Utils.customDataToString(userCustomData));

        newUser.setCustomData(Utils.customDataToString(userCustomData));

        return newUser;
    }

    public class UpdateUserFailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            Exception exception = (Exception) bundle.getSerializable(QBServiceConsts.EXTRA_ERROR);
            DialogUtils.showLong(SelectBirthday_GenderActivity.this, exception.getMessage());
            hideProgress();
        }
    }

    private class UpdateUserSuccessAction implements Command {
        @Override
        public void execute(Bundle bundle) {
            QBUser user = (QBUser) bundle.getSerializable(QBServiceConsts.EXTRA_USER);
            AppSession.getSession().updateUser(user);
            hideProgress();
            startMainActivity(SelectBirthday_GenderActivity.this,user,true);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                return new DatePickerDialog(this, pickerListener, year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;

            // Show selected date

            String Selected_Date=day+"/"+(month+1)+"/"+year;
            DateFormat f_Selected=new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date_Selected=f_Selected.parse(Selected_Date);
                display_date.setText(Selected_Date);
                Calendar birth_date=Calendar.getInstance();
                birth_date.setTime(date_Selected);

                Calendar cal_current=Calendar.getInstance();

                int compare_date=cal_current.getTime().compareTo(birth_date.getTime());
                if(compare_date==1)
                {
                    display_date.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year).append(" "));
                }

                else
                {
                    Toast.makeText(SelectBirthday_GenderActivity.this,"Please Select another date",Toast.LENGTH_SHORT).show();
                    display_date.setText("");
                }

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }



        }

    };

}

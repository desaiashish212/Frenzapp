package com.rishi.frendzapp.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.authorization.intializing.IntializingActivity;

/**
 * Created by Dharendra on 20-May-16.
 */
public class AccountDeleteOneActivity extends Activity  {
    Spinner spinner;
    String DB_mobileNumber;
    Button btn_delet_account;
    String[] items = { "Select reason", "Changing my device", "Changing my mobile number",
            "Deleting my account temporarily", "Missing the features","App is not working" ,"Other"};
    IntializingActivity intializingActivity=new IntializingActivity();
    EditText edit_we_can_improve;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle bundle = getIntent().getExtras();

        //Extract the data…
        String DB_mobileNumber = bundle.getString("DB_mobileNumber");

        setContentView(R.layout.activity_delete_account_one);
       spinner = (Spinner) findViewById(R.id.planets_spinner);
        edit_we_can_improve=(EditText)findViewById(R.id.edit_we_can_improve);
        btn_delet_account=(Button)findViewById(R.id.btn_delet_account) ;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(AccountDeleteOneActivity.this, spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_delet_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Edit_we_can=edit_we_can_improve.getText().toString();
                String Selected_Item= spinner.getSelectedItem().toString();

                startActivity(new Intent(AccountDeleteOneActivity.this,AccountDeleteTwoActivity.class));
                finish();
            }
        });

    }



}
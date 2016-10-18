package com.neo.satya.to_doweekly;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.neo.satya.to_doweekly.helpers.DataBaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by satya on 2016-09-10.
 */
public class EditDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private DataBaseHelper myDB;
    private TextView btnSave, btnTime;
    private EditText editHeading, editDescription;
    private TextView timeView, dateView, homeViewEditActivity;
    private String heading, desc;
    private boolean pickedTime, calendarPicked = false;
    private Calendar myCalendar = Calendar.getInstance();
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private SimpleDateFormat times = new SimpleDateFormat("hh:mm a");
    private String newHeading = null, newDescription = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);

        editHeading = (EditText) findViewById(R.id.edit_Heading);
        editDescription = (EditText) findViewById(R.id.edit_Description);
        timeView = (TextView) findViewById(R.id.timeView);
        dateView = (TextView) findViewById(R.id.dateView);

        homeViewEditActivity = (TextView) findViewById(R.id.homeViewEditActivity);
        btnSave = (TextView) findViewById(R.id.btn_Save);
        btnTime = (TextView) findViewById(R.id.btn_time);

        homeViewEditActivity.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        myDB = new DataBaseHelper(this);
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendarPicked = true;
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yy");
            dateView.setText(sdf.format(myCalendar.getTime()));
            //Toast.makeText(EditDetailActivity.this, dateFormat.format(myCalendar.getTime()) , Toast.LENGTH_SHORT).show();
            timePick();
        }

    };


    private void timePick() {
        // TODO Auto-generated method stub
        int hour = myCalendar.get(Calendar.HOUR);
        int minute = myCalendar.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(EditDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                //String am_pm = (selectedHour < 12) ? "AM" : "PM";
                //timeView.setText("" + selectedHour + ":" + selectedMinute + " " + am_pm);
                myCalendar.set(Calendar.HOUR,selectedHour);
                myCalendar.set(Calendar.MINUTE,selectedMinute);
                timeView.setText(times.format(myCalendar.getTime()));
                pickedTime = true;
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }


    @Override
    public void onClick(View view) {
         newHeading = editHeading.getText().toString();
         newDescription = editDescription.getText().toString();
        if(view == btnTime){
            // pick date
            new DatePickerDialog(EditDetailActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }

        else if(view == btnSave){
            if (newHeading.length() != 0 && newDescription.length() != 0) {
               if(pickedTime == true) {
                   pickedTime = false;
                   editDescription.setText("");
                   editDescription.setText("");
                   AddData(newHeading, newDescription);
                   Intent intent = new Intent(EditDetailActivity.this, MainActivity.class);
                   startActivity(intent);
                   finish(); // call this to finish the current activity
               }
             else{
                   Toast.makeText(EditDetailActivity.this, "Select time!", Toast.LENGTH_SHORT).show();
               }
               }
            else {
                Toast.makeText(EditDetailActivity.this, "You must put something in the fields!", Toast.LENGTH_SHORT).show();
            }
        }
        else if(view == homeViewEditActivity){
                if (newHeading.length() != 0 || newDescription.length() != 0){
                    dialogBox();
                }
                else onBackPressed();
        }
    }

    public void AddData(String newHeading, String newDescription) {
        //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        boolean insertData = myDB.addData(newHeading, newDescription, times.format(myCalendar.getTime()),dateFormat.format(myCalendar.getTime()), dayFormat.format(myCalendar.getTime()),0 );
        if(insertData==true){
            Toast.makeText(this, "Successfully added to " + dateFormat.format(myCalendar.getTime()), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        // Save settings here
        newHeading = editHeading.getText().toString();
        newDescription = editDescription.getText().toString();
            if(newHeading == null || newDescription == null){
                finish();
                super.onBackPressed();            }
        else if (newHeading.length() != 0 || newDescription.length() != 0) {
            dialogBox();
        }
        else {
                finish();
                super.onBackPressed();
            }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.delete) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void dialogBox() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Discard data entered?");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(EditDetailActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

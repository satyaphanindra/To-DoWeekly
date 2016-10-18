package com.neo.satya.to_doweekly;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.neo.satya.to_doweekly.helpers.DataBaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by satya on 2016-09-11.
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener{
    private DataBaseHelper myDB;
    private TextView btnEdit, btnCal, btnSave2;
    private TextView heading, desc;
    private TextView timeView, dateView;

    public String item1;
    private boolean pickedTime, edited, calendarPicked = false;
    private Calendar myCalendar = Calendar.getInstance();
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private SimpleDateFormat times = new SimpleDateFormat("hh:mm a");
    private Cursor data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar3 = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar3);
        heading = (TextView) findViewById(R.id.heading);
        desc = (TextView) findViewById(R.id.desc);
        btnCal = (TextView) findViewById(R.id.btn_Calendar);
        btnEdit = (TextView) findViewById(R.id.btn_Edit);
        btnSave2 = (TextView) findViewById(R.id.btn_Save2);
        timeView = (TextView) findViewById(R.id.timeView);
        dateView = (TextView) findViewById(R.id.dateView);

        myDB = new DataBaseHelper(this);
        data = myDB.getListContents();

        Intent mIntent = getIntent();
        item1 = mIntent.getStringExtra("title");
        final int position = mIntent.getIntExtra("position",0);
        data.moveToNext();
        data.moveToPosition(position);
        heading.setText(data.getString(1));
        desc.setText(data.getString(2));
        timeView.setText(data.getString(3));
        dateView.setText(data.getString(5)+" "+data.getString(4));
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy hh:mm a");
        try {
            myCalendar.setTime(formatter.parse(data.getString(4)+" "+data.getString(3)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        btnSave2.setOnClickListener(this);
        btnCal.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnSave2.setClickable(false);
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
        mTimePicker = new TimePickerDialog(DetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                //String am_pm = (selectedHour < 12) ? "AM" : "PM";
                //timeView.setText("" + selectedHour + ":" + selectedMinute + " " + am_pm);
                myCalendar.set(Calendar.HOUR,selectedHour);
                myCalendar.set(Calendar.MINUTE,selectedMinute);
                timeView.setText(times.format(myCalendar.getTime()));
                pickedTime = true;
                btnSave2.setClickable(true);

            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    @Override
    public void onClick(View view) {
        if(view == btnCal){
            edited = true;
            new DatePickerDialog(DetailActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            //updateCalendar(item1);
        }
        else if(view == btnEdit){
            //heading.setBackground(this.getResources().getDrawable(R.drawable.rounded_corner));
            //You can store it in some variable and use it over here while making non editable.
            edited = true;
            heading.setBackgroundResource(R.drawable.rounded_edittext);
            heading.setPadding(19,19,19,19);
            heading.layout(10,10,10,10);
            heading.setTextSize(14);
            heading.setCursorVisible(true);
            heading.setFocusableInTouchMode(true);

            //You can store it in some variable and use it over here while making non editable.
            desc.setBackgroundResource(R.drawable.rounded_edittext);
            desc.setPadding(24,24,24,50);
            desc.layout(10,10,10,50);
            desc.setTextSize(14);
            desc.setCursorVisible(true);
            desc.setFocusableInTouchMode(true);

            btnEdit.setClickable(false);
            btnSave2.setClickable(true);
    }
    else if(view == btnSave2)
        {
            //make textview non-editable and update data in database
            //You can store it in some variable and use it over here while making non editable.
            heading.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
            heading.setCursorVisible(false);
            heading.setFocusableInTouchMode(false);
            heading.setPadding(19,19,19,19);
            heading.layout(10,10,10,10);
            heading.setTextSize(14);

            desc.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
            desc.setCursorVisible(false);
            desc.setFocusableInTouchMode(false);
            desc.setPadding(24,24,24,50);
            desc.layout(10,10,10,50);
            desc.setTextSize(14);

            btnSave2.setClickable(false);
            btnEdit.setClickable(true);

            // Update DB with new data entered
            String newHeading = heading.getText().toString();
            String newDescription = desc.getText().toString();
            if(newHeading.length()!= 0 && newDescription.length()!=0)
            {
                updateData(newHeading,newDescription);
            }else {
                Toast.makeText(DetailActivity.this, "You must put something in the fields!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateData(String newHeading, String newDescription) {
        boolean insertData = false;
        if(pickedTime == true) {
             insertData = myDB.updateData(item1, newHeading, newDescription, times.format(myCalendar.getTime()), dateFormat.format(myCalendar.getTime()), dayFormat.format(myCalendar.getTime()));
            //boolean insertData = myDB.updateData(newHeading, newDescription, times.format(myCalendar.getTime()),dateFormat.format(myCalendar.getTime()), dayFormat.format(myCalendar.getTime()) );
        }
        else{
            insertData = myDB.updateData(item1, newHeading, newDescription, data.getString(3),data.getString(4),data.getString(5));
            //Toast.makeText(this, "Something went wrong :( ", Toast.LENGTH_SHORT).show();
        }

        if(insertData==true){
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // call this to finish the current activity
            Toast.makeText(this, "Data successfully updated!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Something went wrong :( ", Toast.LENGTH_SHORT).show();
        }
    }

    private void delData(String item1) {
        boolean deleteData = myDB.deleteUser(item1);
        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // call this to finish the current activity

        if(deleteData==true){
            Toast.makeText(this, "Data Successfully Deleted!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Something went wrong :( ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        // Save settings here
        if(edited == true){
            dialogBoxForDiscard();
        }
        else {
            finish();
            super.onBackPressed();
        }}
    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.delete) {
            dialogBox(item1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void dialogBox(final String item1) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Delete this item permanently?");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        delData(item1);

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

    public void dialogBoxForDiscard() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Discard changes made?");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
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



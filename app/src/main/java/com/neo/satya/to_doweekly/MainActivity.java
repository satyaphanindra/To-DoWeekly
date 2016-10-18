package com.neo.satya.to_doweekly;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.widget.*;
import com.neo.satya.to_doweekly.helpers.DataBaseHelper;
import com.neo.satya.to_doweekly.helpers.HelpActivity;
import com.neo.satya.to_doweekly.receivers.StartReceiver;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private List<Tasks> taskList;
    private ListView taskListView;
    private TaskListAdapter adapter;
    private DataBaseHelper myDB;
    private Calendar mainCal = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private SimpleDateFormat weekFormat = new SimpleDateFormat("MMM dd");
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
    private SimpleDateFormat times = new SimpleDateFormat("hh:mm a");
    private int onScreenData = R.id.homeAll;
    private LayoutInflater inflater;
    private View header;
    private TextView dayHeaderView, dateHeaderView, todayBtn;
    private NavigationView navigationView;
    private NotificationManager notificationManager;
    private Notification myNotication;
    private String todaysDate;

    /** Duration of wait **/
    // On Creation of Main Activity do these
    // 1. Open Database and query for any Today's list  and if found display using adapter
    // 2. Onclicklistener for details of each item in list
    // 3. Create Floating Action button to add a new item to current day's list
    // 4. Display Drawer Layout and write method to listen on Item selected

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskListView = (ListView) findViewById(R.id.listView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        header = inflater.inflate(R.layout.header, null);
        dayHeaderView = (TextView) header.findViewById(R.id.dayHeader);
        dateHeaderView = (TextView) header.findViewById(R.id.dateHeader);
        taskListView.addHeaderView(header, "Header", false);

        View hView =  navigationView.getHeaderView(0);
        TextView currentWeekDates = (TextView) hView.findViewById(R.id.currentWeekDates);

        // Open Database and query for any To-Do lists  and if found display using adapter
        todaysDate = dateFormat.format(mainCal.getTime());
        displayAllDaysList();

        // setting current week dates in NavigationView header
        currentWeekDates.setText(getCurrentWeek());

        //  Set Onclicklistener for each item in list for details
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "Clicked product id =" + view.getTag(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, DetailActivity.class);
                //Header is taking first position in list, so position - 1 gives first item
                i.putExtra("position", position - 1);
                i.putExtra("title", taskList.get(position - 1).getTaskName());
                startActivity(i);
            }
        });

        //Handle On long press of listview item
        taskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg3) {
                deleteItemDialog(taskList.get(position - 1).getTaskName(), position);
                return true;
            }
        });

        // Create Floating Action button to add a new Task
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, EditDetailActivity.class);
                startActivity(i);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
            }
        });

        // Display Drawer Layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

    }

    // method to get current week dates
    private String getCurrentWeek() {
        Calendar cw = Calendar.getInstance();
        cw.set(Calendar.DAY_OF_WEEK, cw.getFirstDayOfWeek());
        String weekDay1 = weekFormat.format(cw.getTime());
        cw.set(Calendar.DAY_OF_WEEK, 7);
        String weekDay2 = weekFormat.format(cw.getTime());
        return weekDay1+" - "+weekDay2;
    }

    // Handle go to Today List button method here
    public void todayList(View v){
        onScreenData = 1;
        Calendar c = Calendar.getInstance();
        String todaysDate2 = dateFormat.format(c.getTime());
        displayTheDayList(todaysDate2);
    }

    // Handle go to All days List button method here
    public void allDayList(View v){
        onScreenData = R.id.homeAll;
        displayAllDaysList();
    }

    // Handle Checked or Unchecked button onclick method here (this method is called for each item in list from adapter)
    // also update DB if it is checked or unchecked
    @Override
    public void onClick(View v) {
            CheckedTextView ct = (CheckedTextView) v.findViewById(R.id.taskStatus);
            ct.toggle();
        View parentRow = (View) v.getParent();
            final int position = taskListView.getPositionForView(parentRow);
            int flag = (ct.isChecked()) ? 1 : 0;
            boolean updateStatus = myDB.updateStatus(taskList.get(position-1).getTaskCreationDate(), taskList.get(position-1).getTaskName(), taskList.get(position-1).getTaskCreationTime(), flag);
            if (updateStatus == true) {
                if(ct.isChecked()) {
                    Toast.makeText(this, "Marked Done", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(this, "Marked Undone", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
            }
    }

    // Handle displaying today's list
    private void displayTheDayList(String thisDate) {
        header.setBackgroundColor(Color.parseColor("#ffffbb33"));
        RelativeLayout li=(RelativeLayout) findViewById(R.id.li);
        li.setBackground(null);

        Calendar c = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        try {
            Date date = formatter.parse(thisDate);
            c.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        myDB = new DataBaseHelper(this);
        taskList = new ArrayList<>();
        Cursor data = myDB.getListContentsDayWise(thisDate);
        if(data.getCount() == 0)
        { li.setBackground(getResources().getDrawable(R.drawable.mygrayimg));
            adapter =  new TaskListAdapter(MainActivity.this,null);
            dayHeaderView.setText("No list found for "+dayFormat.format(c.getTime()));
            dateHeaderView.setText(dateFormat.format(c.getTime()));
            try {
                taskListView.setAdapter(null);
            }
            catch (Exception e){
            }
        }
        else{
            int i = 1;
            while(data.moveToNext()){
                taskList.add(new Tasks(i, data.getString(1), data.getString(3), data.getString(5),data.getString(4),data.getInt(6)));
                i++;
            }
            adapter =  new TaskListAdapter(MainActivity.this, taskList);
            dayHeaderView.setText(dayFormat.format(c.getTime()));
            dateHeaderView.setText(dateFormat.format(c.getTime()));
            try {
                taskListView.setAdapter(adapter);
            }
            catch (Exception e){
            }
        }
    }

    // Handle displaying All Days list
    private void displayAllDaysList() {
        header.setBackgroundColor(getResources().getColor(R.color.rounded_plain_color));
        RelativeLayout li=(RelativeLayout) findViewById(R.id.li);
        li.setBackground(null);

        myDB = new DataBaseHelper(this);
        taskList = new ArrayList<>();
        Cursor data = myDB.getListContents();
        if(data.getCount() == 0){
            li.setBackground(getResources().getDrawable(R.drawable.mygrayimg));
            Toast.makeText(this, " No List found! please create one " ,Toast.LENGTH_SHORT).show();
            adapter =  new TaskListAdapter(MainActivity.this,null);
            dayHeaderView.setText("No List Found!");
            dateHeaderView.setText(null);
            try {
                taskListView.setAdapter(null);
            }
            catch (Exception e){
            }
        }
        else{
            int i = 1;
            while(data.moveToNext()){
                taskList.add(new Tasks(i, data.getString(1), data.getString(3),data.getString(5), data.getString(4),data.getInt(6)));
                i++;
            }
            adapter =  new TaskListAdapter(MainActivity.this,taskList);
            dayHeaderView.setText("All Days Lists");
            dateHeaderView.setText(null);
            try {
                taskListView.setAdapter(adapter);
            }
            catch (Exception e) {
            }
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(MainActivity.this, StartReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
        if(data.getCount()!= 0)
        {
            Calendar calendar2 = Calendar.getInstance();
                calendar2.set(Calendar.HOUR_OF_DAY, 06);
                calendar2.set(Calendar.MINUTE, 00);
                calendar2.set(Calendar.SECOND, 0);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, pendingIntent);  //set repeating every 24 hours
        }
        else
        {
            alarmManager.cancel (pendingIntent);
        }
    }

    // Handle method to delete all tables
    private void deleteData(String itemToDelete) {
        boolean deletedData = false;
        if(itemToDelete == "ALL"){
             deletedData = myDB.deleteAllData("all");
        }
        else {
            deletedData = myDB.deleteAllData(itemToDelete);
        }

        if(deletedData==true){
            Toast.makeText(this, " Deleted the content ", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
        }

        taskList.removeAll(taskList);
        adapter.notifyDataSetChanged();
    }

    // delete all items related a particular date
    private boolean deleteDayAndDate(String selectedItemDate, String selectedItemName, String selectedItemTime){
        boolean result = myDB.deleteItemDayAndDate(selectedItemDate, selectedItemName, selectedItemTime );
        if(result==true){
            Toast.makeText(this, " Deleted content ", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    // delete one selected item in the list
    private void deleteItemDayAndDate(String item, int position){
        boolean result = deleteDayAndDate(taskList.get(position-1).getTaskCreationDate(), taskList.get(position-1).getTaskName(), taskList.get(position-1).getTaskCreationTime());
        if(result==true){
            //taskList.remove(position-1);
            //adapter.notifyDataSetChanged();
            recreate();}
        else{
        }
    }

    // Handle onBackPressed
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            backButtonHandler();
            return;
        }
    }

    // Handle menu on top right corner
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.delete) {
            if(taskListView == null) {
                Toast.makeText(this, " List is empty!" , Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (onScreenData == R.id.homeAll) {
                    dialogBox("ALL");
                } else {
                    dialogBox(dateFormat.format(mainCal.getTime()));
                }
                return true;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
            onScreenData = id;
        //Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
            if (id == R.id.homeAll) {
                displayAllDaysList();

            } else if (id == R.id.mon) {
                //mainCal.add(Calendar.DAY_OF_WEEK, 1);
                mainCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                displayTheDayList(dateFormat.format(mainCal.getTime()));

            } else if (id == R.id.tue) {
                mainCal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                displayTheDayList(dateFormat.format(mainCal.getTime()));


            } else if (id == R.id.wed) {
                mainCal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                displayTheDayList(dateFormat.format(mainCal.getTime()));


            } else if (id == R.id.thu) {
                mainCal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                displayTheDayList(dateFormat.format(mainCal.getTime()));


            } else if (id == R.id.fri) {
                mainCal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                displayTheDayList(dateFormat.format(mainCal.getTime()));

            } else if (id == R.id.sat) {
                mainCal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                displayTheDayList(dateFormat.format(mainCal.getTime()));


            } else if (id == R.id.sun) {
                mainCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                displayTheDayList(dateFormat.format(mainCal.getTime()));

            }else if (id == R.id.email){
                sendEmail();
            } else if (id == R.id.help){
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Handle sending feedback email method
    private void sendEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"todoweeklyapp@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Fedback for To-Do Weekly App");
        i.putExtra(Intent.EXTRA_TEXT   , "Thank you! Your feedback is valuable :) - Please provide your feedback here ");
        try {
            startActivity(Intent.createChooser(i, "Send email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle alert message
    public void dialogBox(final String item) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Delete \""+ item + "\" lists ?");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteData(item);
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

    // Handle alert message when deleting individual item
    public void deleteItemDialog(final String item, final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Delete \""+ item + "\" ?");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteItemDayAndDate(item, position);
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

 // Handle back button method to exit application
    public void backButtonHandler() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Exit application?");
        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to leave the application?");
        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.exit44);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        finishAffinity();
                        System.exit(0);
                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        dialog.cancel();
                    }
                });
        // Showing Alert Message
        alertDialog.show();
    }
}
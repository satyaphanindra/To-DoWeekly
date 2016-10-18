package com.neo.satya.to_doweekly.receivers;

/**
 * Created by satya on 2016-09-26.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;

import com.neo.satya.to_doweekly.MainActivity;
import com.neo.satya.to_doweekly.R;
import com.neo.satya.to_doweekly.helpers.DataBaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class NotifierService extends Service
{

    private NotificationManager mManager;
    Notification myNotication;
    DataBaseHelper myDB;
    Calendar servCal = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
    SimpleDateFormat times = new SimpleDateFormat("hh:mm a");
    String todaysDate;


    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        stopSelf();
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        //super.onStart(intent, startId);

        myDB = new DataBaseHelper(this);
        Cursor data = myDB.getListContentsDayWise(dateFormat.format(servCal.getTime()));
        todaysDate = dateFormat.format(servCal.getTime());
        //if(data.getInt(7)!=1) {
            if(data.getCount()!=0) {
                data.moveToFirst();
                int n = data.getInt(7);
                int done = 0;
                int unDone = 0;
                do{
                    if(data.getInt(6)!=0) {
                        done++;
                    }
                    else unDone++;
                }while (data.moveToNext());

                //int stat = data.getInt(6);
            if(n != 1 && done < data.getCount()) {
                mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
                Intent intent1 = new Intent(this.getApplicationContext(), MainActivity.class);
                Notification.Builder builder = new Notification.Builder(NotifierService.this);

                //Notification notification = new Notification(R.mipmap.ic_launcher,"This is a test message!", System.currentTimeMillis());
                intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(NotifierService.this, 1, intent1, 0);
                builder.setAutoCancel(true);
                //builder.setTicker("this is ticker text");
                //builder.setColor(Color.parseColor("#F7040B"));
                builder.setColor(getResources().getColor(R.color.colorAccent));
                //builder.setContentTitle(dayFormat.format(servCal.getTime()) + " " + todaysDate);
                if(data.getCount()==1 || unDone == 1) {
                    builder.setContentTitle("You have 1 task pending today");
                }
                else builder.setContentTitle("You have " + unDone + " tasks pending today");

                builder.setContentText("Tap to see the list");
                builder.setSmallIcon(R.drawable.favicon);
                String s = dayFormat.format(servCal.getTime());
                switch(s) {
                    case "Monday":
                        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.mon));
                        break;
                    case "Tuesday":
                        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.tue));
                        break;
                    case "Wednesday":
                        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.wed));
                        break;
                    case "Thursday":
                        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.thu));
                        break;
                    case "Friday":
                        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.fri));
                        break;
                    case "Saturday":
                        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.sat));
                        break;
                    case "Sunday":
                        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.sun));
                        break;
                    default:
                        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.calendar44));
                        break;
                }

                builder.setContentIntent(pendingIntent);
                //builder.setOngoing(true);
                //builder.setSubText("This is subtext...");   //API level 16
                builder.setNumber(100);
                builder.setDefaults(Notification.DEFAULT_SOUND);
                builder.build();
                //Notification myNotication = new Notification(R.mipmap.ic_launcher,"This is a test message!", System.currentTimeMillis());
                myNotication = builder.getNotification();
                mManager.notify(11, myNotication);
                myDB.updateNotifier(todaysDate, 1);
            }
        }
        else{

            }
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
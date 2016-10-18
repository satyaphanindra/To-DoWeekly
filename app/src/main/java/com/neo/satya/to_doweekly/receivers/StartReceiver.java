package com.neo.satya.to_doweekly.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by satya on 2016-09-26.
 */

public class StartReceiver extends BroadcastReceiver{

    PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Calendar calendar = Calendar.getInstance();

        Intent service1 = new Intent(context, StartServiceReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, service1,0);

        calendar.set(Calendar.HOUR_OF_DAY, 06);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 0);
        //calendar.set(Calendar.AM_PM,Calendar.PM);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

    }

}

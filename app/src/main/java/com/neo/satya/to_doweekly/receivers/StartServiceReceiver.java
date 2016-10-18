package com.neo.satya.to_doweekly.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by satya on 2016-09-26.
 */

public class StartServiceReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {


        Intent service1 = new Intent(context, NotifierService.class);
        context.startService(service1);

    }
}
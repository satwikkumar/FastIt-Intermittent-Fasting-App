package edu.neu.madcourse.fastit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id" ;
    public static String NOTIFICATION = "notification" ;
    public static String NOTIFICATION_CHANNEL_ID = "notification-channel-id" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferenceManager preferenceManager = new SharedPreferenceManager(context);
        String res = preferenceManager.getStringPref(Constants.SP_SHOW_NOTIFICATION);
        if (res.length() == 0 || res.equals("Yes")){
            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context. NOTIFICATION_SERVICE ) ;
            Notification notification = intent.getParcelableExtra( NOTIFICATION ) ;
            int id = intent.getIntExtra( NOTIFICATION_ID , 0 ) ;
            assert notificationManager != null;
            notificationManager.notify(id , notification) ;
        }
    }
}

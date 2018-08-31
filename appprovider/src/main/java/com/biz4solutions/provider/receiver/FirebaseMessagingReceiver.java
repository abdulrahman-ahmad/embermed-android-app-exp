package com.biz4solutions.provider.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.biz4solutions.utilities.Constants;

public class FirebaseMessagingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            System.out.println("aa ------------ intent=" + intent.getAction());
            int notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID_KEY, 0);
            System.out.println("aa ------------ notificationId=" + notificationId);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(notificationId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

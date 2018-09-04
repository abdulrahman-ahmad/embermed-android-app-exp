package com.biz4solutions.provider.receiver;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.utilities.Constants;

import java.util.List;

public class FirebaseMessagingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            int notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID_KEY, 0);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(notificationId);
            }
            if (isMainActivityRunning(context)) {
                if (MainActivity.isActivityOpen) {
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(Constants.LOCAL_NOTIFICATION_ACTION_VIEW);
                    broadcastIntent.putExtra(Constants.NOTIFICATION_REQUEST_ID_KEY, intent.getStringExtra(Constants.NOTIFICATION_REQUEST_ID_KEY));
                    context.sendBroadcast(broadcastIntent);
                } else {
                    Intent notificationIntent = new Intent(context, MainActivity.class);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    notificationIntent.putExtra(Constants.NOTIFICATION_REQUEST_ID_KEY, intent.getStringExtra(Constants.NOTIFICATION_REQUEST_ID_KEY));
                    context.startActivity(notificationIntent);
                }
            } else {
                Intent notificationIntent = new Intent(context, MainActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                notificationIntent.putExtra(Constants.NOTIFICATION_REQUEST_ID_KEY, intent.getStringExtra(Constants.NOTIFICATION_REQUEST_ID_KEY));
                context.startActivity(notificationIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isMainActivityRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(Integer.MAX_VALUE);
            for (int i = 0; i < tasksInfo.size(); i++) {
                if (tasksInfo.get(i).baseActivity.getClassName().contains("com.biz4solutions.provider.main.views.activities.MainActivity"))
                    return true;
            }
        }
        return false;
    }
}

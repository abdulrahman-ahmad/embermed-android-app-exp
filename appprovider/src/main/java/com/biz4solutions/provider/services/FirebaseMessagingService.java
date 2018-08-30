package com.biz4solutions.provider.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

/**
 * firebase messaging service
 * Created by ketan
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static int DEFAULT_NOTIFICATION_ID = 202;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            String message = null;
            String notificationId = null;
            String priority = null;
            if (remoteMessage.getData() != null && remoteMessage.getData().containsKey("message")) {
                message = remoteMessage.getData().get("message");
                priority = remoteMessage.getData().get("priority");
                notificationId = remoteMessage.getData().get("notificationId");
            }

            if (Constants.STATUS_IMMEDIATE.equals("" + priority)) {
                sendCustomNotification(this, message, notificationId);
            } else {
                sendNotification(this, message, notificationId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotification(Service service, String message, String nid) {
        Intent intent = new Intent(service, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, PendingIntent.FLAG_NO_CREATE);

        NotificationManager notificationManager = (NotificationManager) service.getSystemService(NOTIFICATION_SERVICE);
        int notificationId = getNotificationId(nid, notificationManager);
        Notification notification = new NotificationCompat.Builder(service, "" + notificationId)
                .setSmallIcon(R.drawable.icon_notification_tranperant)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_notification))
                .setColor(ContextCompat.getColor(service, R.color.notification_bg_color))
                .setContentTitle(service.getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .build();
        if (notificationManager != null) {
            notificationManager.notify(notificationId, notification);
            DEFAULT_NOTIFICATION_ID++;
        }
    }

    private int getNotificationId(String nid, NotificationManager notificationManager) {
        int notificationId;
        if (nid == null || nid.isEmpty()) {
            notificationId = DEFAULT_NOTIFICATION_ID;
        } else {
            try {
                notificationId = Integer.parseInt(nid);
            } catch (Exception e) {
                notificationId = DEFAULT_NOTIFICATION_ID;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                NotificationChannel channel = new NotificationChannel("" + notificationId,
                        notificationId + " Notification Channel",
                        NotificationManager.IMPORTANCE_LOW);
                notificationManager.createNotificationChannel(channel);
            }
        }
        return notificationId;
    }

    public void sendCustomNotification(Service service, String message, String nid) {
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small_cardiac);
        /*notificationLayout.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        notificationLayout.setTextViewText(R.id.title, "Custom notification");
        notificationLayout.setTextViewText(R.id.text, "This is a custom layout");*/
        RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_large_cardiac);

        NotificationManager notificationManager = (NotificationManager) service.getSystemService(NOTIFICATION_SERVICE);
        int notificationId = getNotificationId(nid, notificationManager);
        // Apply the layouts to the notification
        Notification notification = new NotificationCompat.Builder(service, nid)
                .setSmallIcon(R.drawable.icon_notification_tranperant)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_notification))
                .setColor(ContextCompat.getColor(service, R.color.notification_bg_color))
                .setCustomContentView(notificationLayout)
                //.setContentTitle(service.getString(R.string.app_name))
                //.setContentText(message)
                .setCustomBigContentView(notificationLayoutExpanded)
                .build();
        if (notificationManager != null) {
            notificationManager.notify(notificationId, notification);
            DEFAULT_NOTIFICATION_ID++;
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sendRegistrationToServer(s, FirebaseMessagingService.this);
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public String getSystemServiceName(Class<?> serviceClass) {
        return super.getSystemServiceName(serviceClass);
    }

    private static void sendRegistrationToServer(String token, Context context) {
        if (CommonFunctions.getInstance().isOffline(context)) {
            return;
        }
        String userAuthKey = SharedPrefsManager.getInstance().retrieveStringPreference(context, Constants.USER_PREFERENCE, Constants.USER_AUTH_KEY);
        if (token != null && userAuthKey != null && !userAuthKey.isEmpty()) {
            HashMap<String, Object> body = new HashMap<>();
            body.put("fcmToken", token);
            new ApiServices().setFcmToken(context, body);
        }
    }

    public static void setFcmToken(final Context context) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            sendRegistrationToServer(token, context);
                        }
                    }
                });
    }
}

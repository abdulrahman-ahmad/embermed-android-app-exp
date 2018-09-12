package com.biz4solutions.services;

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

import com.biz4solutions.R;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.receiver.FirebaseMessagingReceiver;
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

    private static int DEFAULT_NOTIFICATION_ID = 201;
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    private int notificationId;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            String message = "";
            String requestId = "";
            String notificationId = "";
            if (remoteMessage.getData() != null) {
                message = remoteMessage.getData().get("message");
                notificationId = remoteMessage.getData().get("notificationId");
                requestId = remoteMessage.getData().get("requestId");
            }
            createPendingIntent(this, notificationId, requestId);
            sendNotification(this, message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPendingIntent(Service service, String nid, String requestId) {
        notificationManager = (NotificationManager) service.getSystemService(NOTIFICATION_SERVICE);
        notificationId = getNotificationId(nid, notificationManager);
        Intent intent = new Intent(this, FirebaseMessagingReceiver.class);
        intent.setAction(Constants.PATIENT_NOTIFICATION_ACTION_VIEW);
        intent.putExtra(Constants.NOTIFICATION_REQUEST_ID_KEY, requestId);
        intent.putExtra(Constants.NOTIFICATION_ID_KEY, notificationId);
        pendingIntent = PendingIntent.getBroadcast(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void sendNotification(Service service, String message) {
        try {
            Notification notification = new NotificationCompat.Builder(service, Constants.EMBER_CHANNEL_ID)
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
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        DEFAULT_NOTIFICATION_ID++;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                String channelId = Constants.EMBER_CHANNEL_ID;
                NotificationChannel channel = new NotificationChannel(channelId, getString(R.string.app_name),
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
        }
        return notificationId;
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
        System.out.println("aa ------- fcmToken=" + token);
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
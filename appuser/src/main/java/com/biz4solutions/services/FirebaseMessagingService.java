package com.biz4solutions.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.biz4solutions.R;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.preferences.SharedPrefsManager;
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

    private static final int NOTIFICATION_ID = 201;
    private static final String CHANNEL_ID = "201";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            //System.out.println("aa ----------Came here");
            //System.out.println("aa ----------message:" + remoteMessage);
            String message = null;
            if (remoteMessage.getData() != null && remoteMessage.getData().containsKey("message")) {
                message = remoteMessage.getData().get("message");
                //System.out.println("aa ----------message:" + message);
            }

            sendNotification(this, message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotification(Service service, String message) {
        Intent intent = new Intent(service, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, PendingIntent.FLAG_NO_CREATE);

        NotificationManager notificationManager = (NotificationManager) service.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_ID + " Notification Channel",
                        NotificationManager.IMPORTANCE_LOW);
                notificationManager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(service, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(service.getString(R.string.app_name))
                .setOngoing(true)
                .setAutoCancel(false)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setContentIntent(pendingIntent);
        //addActions(service, mNotifyBuilder);
        Notification notification = mNotifyBuilder.build();
        service.startForeground(NOTIFICATION_ID, notification);
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

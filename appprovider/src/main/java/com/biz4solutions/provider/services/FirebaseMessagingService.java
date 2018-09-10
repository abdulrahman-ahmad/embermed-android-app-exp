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
import com.biz4solutions.provider.receiver.FirebaseMessagingReceiver;
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
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    private int notificationId;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            String message = "";
            String notificationId = "";
            String priority = "";
            String patientName = "";
            String age = "";
            String gender = "";
            String requestId = "";
            if (remoteMessage.getData() != null) {
                message = remoteMessage.getData().get("message");
                priority = remoteMessage.getData().get("priority");//IMMEDIATE
                patientName = remoteMessage.getData().get("patientName");//Ellen Thomson
                age = remoteMessage.getData().get("age");//26
                gender = remoteMessage.getData().get("gender");//Female
                notificationId = remoteMessage.getData().get("notificationId");
                requestId = remoteMessage.getData().get("requestId");
            }
            createPendingIntent(this, notificationId, requestId, Constants.STATUS_IMMEDIATE.equals("" + priority));
            if (Constants.STATUS_IMMEDIATE.equals("" + priority)) {
                sendCustomNotification(this, patientName, age, gender);
            } else {
                sendNotification(this, message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPendingIntent(Service service, String nid, String requestId, boolean isCardiacChannelId) {
        notificationManager = (NotificationManager) service.getSystemService(NOTIFICATION_SERVICE);
        notificationId = getNotificationId(nid, notificationManager, isCardiacChannelId);
        Intent intent = new Intent(this, FirebaseMessagingReceiver.class);
        intent.setAction(Constants.NOTIFICATION_ACTION_VIEW);
        intent.putExtra(Constants.NOTIFICATION_REQUEST_ID_KEY, requestId);
        intent.putExtra(Constants.NOTIFICATION_ID_KEY, notificationId);
        pendingIntent = PendingIntent.getBroadcast(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void sendNotification(Service service, String message) {
        try {
            Notification notification = new NotificationCompat.Builder(service, Constants.EMBER_MEDICS_CHANNEL_ID)
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

    private int getNotificationId(String nid, NotificationManager notificationManager, boolean isCardiacChannelId) {
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
                String channelId = Constants.EMBER_MEDICS_CHANNEL_ID;
                if (isCardiacChannelId) {
                    channelId = Constants.EMBER_MEDICS_CARDIAC_CHANNEL_ID;
                }
                NotificationChannel channel = new NotificationChannel(channelId, getString(R.string.app_name),
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
        }
        return notificationId;
    }

    private void sendCustomNotification(Service service, String patientName, String age, String gender) {
        try {
            String genderAge = gender + ", " + age + "yrs";

            RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small_cardiac);
            notificationLayout.setTextViewText(R.id.patientName, patientName);
            notificationLayout.setTextViewText(R.id.genderAge, genderAge);
            notificationLayout.setOnClickPendingIntent(R.id.btn_view, pendingIntent);

            RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_large_cardiac);
            notificationLayoutExpanded.setTextViewText(R.id.patientName, patientName);
            notificationLayoutExpanded.setTextViewText(R.id.genderAge, genderAge);
            notificationLayoutExpanded.setOnClickPendingIntent(R.id.btn_view, pendingIntent);

            // Apply the layouts to the notification
            Notification notification = new NotificationCompat.Builder(service, Constants.EMBER_MEDICS_CARDIAC_CHANNEL_ID)
                    .setSmallIcon(R.drawable.icon_notification_tranperant)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_notification))
                    .setColor(ContextCompat.getColor(service, R.color.notification_bg_color))
                    .setCustomContentView(notificationLayout)
                    .setCustomBigContentView(notificationLayoutExpanded)
                    .build();
            if (notificationManager != null) {
                notificationManager.notify(notificationId, notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

package com.biz4solutions.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

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

import java.util.Date;
import java.util.HashMap;

/**
 * firebase messaging service
 * Created by ketan
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

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

    public static void sendNotification(Context context, String message) {
        int notificationId = (int) new Date().getTime();
        Intent intent = new Intent(context, MainActivity.class);
        //intent.setComponent(new ComponentName("com.biz4solutions.activities", "com.biz4solutions.main.views.activities.MainActivity"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_ONE_SHOT);
        //int appIcon = getApplicationContext().getApplicationInfo().icon;

        Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo);

        android.support.v4.app.NotificationCompat.Builder builder =
                new android.support.v4.app.NotificationCompat.Builder(context);
        // Big Text Style
        android.support.v4.app.NotificationCompat.BigTextStyle style
                = new android.support.v4.app.NotificationCompat.BigTextStyle(builder);

        style.bigText(message).setBigContentTitle(context.getString(context.getApplicationContext().getApplicationInfo().labelRes));

        Notification notification = builder
                .setStyle(style)
                .setColor(ContextCompat.getColor(context, R.color.white))
                .setLargeIcon(image)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(context.getString(context.getApplicationContext().getApplicationInfo().labelRes))
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, notification);
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

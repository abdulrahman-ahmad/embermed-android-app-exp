package com.biz4solutions.provider.utilities;

import android.content.Context;
import android.support.annotation.NonNull;

import com.biz4solutions.interfaces.FirebaseCallbackListener;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.models.OpenTok;
import com.biz4solutions.models.User;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.FirebaseAuthUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class FirebaseEventUtil {
    private static FirebaseEventUtil instance = null;
    private ValueEventListener userEventListener;
    private ValueEventListener alertEventListener;
    private ValueEventListener requestEventListener;
    private String userId;
    private String rUserId;
    private String requestId;
    private String otRequestId;

    private FirebaseEventUtil() {
    }

    public static FirebaseEventUtil getInstance() {
        if (instance == null) {
            instance = new FirebaseEventUtil();
        }
        return instance;
    }

    public void addFirebaseUserEvent(Context context, final FirebaseCallbackListener<User> callbackListener) {
        try {
            User user = SharedPrefsManager.getInstance().retrieveUserPreference(context, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);
            if (user != null) {
                removeFirebaseUserEvent();
                userEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user1 = dataSnapshot.getValue(User.class);
                        System.out.println("aa ---------- Firebase user details= " + user1);
                        callbackListener.onSuccess(user1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                userId = user.getUserId();
                FirebaseAuthUtil.getInstance().addValueEventListener(Constants.FIREBASE_USER_TABLE, userId, userEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFirebaseUserEvent() {
        try {
            if (userEventListener != null && userId != null && !userId.isEmpty()) {
                FirebaseAuthUtil.getInstance().removeEventListener(Constants.FIREBASE_USER_TABLE, userId, userEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFirebaseAlertEvent() {
        try {
            if (alertEventListener != null && rUserId != null && !rUserId.isEmpty()) {
                FirebaseAuthUtil.getInstance().removeEventListener(Constants.FIREBASE_ALERT_TABLE, rUserId, alertEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFirebaseAlertEvent(Context context, final FirebaseCallbackListener<Boolean> callbackListener) {
        removeFirebaseAlertEvent();
        try {
            User user = SharedPrefsManager.getInstance().retrieveUserPreference(context, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);
            if (user != null) {
                alertEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        System.out.println("aa ---------- Alert dataSnapshot.getValue() = " + dataSnapshot.getValue());
                        callbackListener.onSuccess(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                rUserId = user.getUserId();
                FirebaseAuthUtil.getInstance().addValueEventListener(Constants.FIREBASE_ALERT_TABLE, rUserId, alertEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFirebaseRequestEvent() {
        try {
            if (requestEventListener != null && requestId != null && !requestId.isEmpty()) {
                FirebaseAuthUtil.getInstance().removeEventListener(Constants.FIREBASE_REQUEST_TABLE, requestId, requestEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFirebaseRequestEvent(String requestId, final FirebaseCallbackListener<EmsRequest> callbackListener) {
        removeFirebaseRequestEvent();
        try {
            requestEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    EmsRequest emsRequest = dataSnapshot.getValue(EmsRequest.class);
                    System.out.println("aa ---------- EmsRequest = " + emsRequest);
                    callbackListener.onSuccess(emsRequest);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            this.requestId = requestId;
            FirebaseAuthUtil.getInstance().addValueEventListener(Constants.FIREBASE_REQUEST_TABLE, requestId, requestEventListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFirebaseOpenTokEvent() {
        try {
            if (requestEventListener != null && otRequestId != null && !otRequestId.isEmpty()) {
                FirebaseAuthUtil.getInstance().removeEventListener(Constants.FIREBASE_OPEN_TOK_TABLE, otRequestId, requestEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFirebaseOpenTokEvent(String requestId, final FirebaseCallbackListener<OpenTok> callbackListener) {
        removeFirebaseOpenTokEvent();
        try {
            requestEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    OpenTok openTok = dataSnapshot.getValue(OpenTok.class);
                    System.out.println("aa ---------- OpenTok = " + openTok);
                    callbackListener.onSuccess(openTok);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            this.otRequestId = requestId;
            FirebaseAuthUtil.getInstance().addValueEventListener(Constants.FIREBASE_OPEN_TOK_TABLE, otRequestId, requestEventListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFirebaseOpenTok(String requestId, final FirebaseCallbackListener<OpenTok> callbackListener) {
        try {
            FirebaseAuthUtil.getInstance().addListenerForSingleValueEvent(Constants.FIREBASE_OPEN_TOK_TABLE, requestId, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    OpenTok openTok = dataSnapshot.getValue(OpenTok.class);
                    //System.out.println("aa ---------- openTok = " + openTok);
                    callbackListener.onSuccess(openTok);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFirebaseRequest(String requestId, final FirebaseCallbackListener<EmsRequest> callbackListener) {
        try {
            FirebaseAuthUtil.getInstance().addListenerForSingleValueEvent(Constants.FIREBASE_REQUEST_TABLE, requestId, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    EmsRequest emsRequest = dataSnapshot.getValue(EmsRequest.class);
                    //System.out.println("aa ---------- EmsRequest = " + emsRequest);
                    callbackListener.onSuccess(emsRequest);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
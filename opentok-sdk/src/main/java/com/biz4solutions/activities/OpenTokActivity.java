package com.biz4solutions.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.interfaces.DialogDismissCallBackListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.opentok.sdk.BuildConfig;
import com.biz4solutions.opentok.sdk.R;
import com.biz4solutions.opentok.sdk.databinding.ActivityOpentokBinding;
import com.biz4solutions.utilities.CommonFunctions;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;

import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class OpenTokActivity extends AppCompatActivity implements
        Session.SessionListener,
        PublisherKit.PublisherListener,
        SubscriberKit.SubscriberListener, View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 124;
    public static final int RC_OPENTOK_ACTIVITY = 125;
    public static final String OPENTOK_SESSION_ID = "OPENTOK_SESSION_ID";
    public static final String OPENTOK_SUBSCRIBER_TOKEN = "OPENTOK_SUBSCRIBER_TOKEN";
    public static final String OPENTOK_PUBLISHER_TOKEN = "OPENTOK_PUBLISHER_TOKEN";
    public static final String OPENTOK_END_CALL_RECEIVER = "OPENTOK_END_CALL_RECEIVER";
    public static final String OPENTOK_SESSION_EXPIRED_RECEIVER = "OPENTOK_SESSION_EXPIRED_RECEIVER";
    public static final String OPENTOK_REQUEST_ID = "OPENTOK_REQUEST_ID";
    public static final String OPENTOK_CALLER_NAME = "OPENTOK_CALLER_NAME";
    public static final String OPENTOK_CALLER_SUB_TEXT = "OPENTOK_CALLER_SUB_TEXT";
    public static final String OPENTOK_CALL_START_TIME = "OPENTOK_CALL_START_TIME";

    private String mSessionId;
    private String mSubscriberToken;
    private String mPublisherToken;
    private String requestId;
    private long startTime = 0;

    private Session mSession;
    private Subscriber mSubscriber;
    private Publisher mPublisher;

    private ActivityOpentokBinding binding;
    private BroadcastReceiver broadcastReceiver;

    private Timer timer = new Timer();
    private TimerTask timerTask;
    private ObservableField<String> callTime = new ObservableField<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_opentok);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(OPENTOK_SESSION_ID)) {
                mSessionId = bundle.getString(OPENTOK_SESSION_ID);
            }
            if (bundle.containsKey(OPENTOK_SUBSCRIBER_TOKEN)) {
                mSubscriberToken = bundle.getString(OPENTOK_SUBSCRIBER_TOKEN);
            }
            if (bundle.containsKey(OPENTOK_PUBLISHER_TOKEN)) {
                mPublisherToken = bundle.getString(OPENTOK_PUBLISHER_TOKEN);
            }
            if (bundle.containsKey(OPENTOK_REQUEST_ID)) {
                requestId = bundle.getString(OPENTOK_REQUEST_ID);
            }
            if (bundle.containsKey(OPENTOK_CALL_START_TIME)) {
                startTime = bundle.getLong(OPENTOK_CALL_START_TIME, 0);
            }
            String name = "";
            String subText = "";
            if (bundle.containsKey(OPENTOK_CALLER_NAME)) {
                name = bundle.getString(OPENTOK_CALLER_NAME);
            }
            if (bundle.containsKey(OPENTOK_CALLER_SUB_TEXT)) {
                subText = bundle.getString(OPENTOK_CALLER_SUB_TEXT);
            }
            binding.tvCallerName.setText(name);
            binding.tvSubText.setText(subText);
        }
        initClickListeners();
        requestPermissions();
        registerBroadcastReceiver();
        reSetTimer();
        calculateCallTime();
        callTime.set(getString(R.string._00_00_00));
        binding.setCallTime(callTime);
    }

    private void registerBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //method call here
                String action = intent.getAction();
                if (action != null) {
                    switch (action) {
                        case OPENTOK_END_CALL_RECEIVER:
                            finishOpenTokActivity(RESULT_OK);
                            break;
                        case OPENTOK_SESSION_EXPIRED_RECEIVER:
                            finishOpenTokActivity(RESULT_CANCELED);
                            break;
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OPENTOK_END_CALL_RECEIVER);
        intentFilter.addAction(OPENTOK_SESSION_EXPIRED_RECEIVER);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void initClickListeners() {
        binding.ivAudio.setOnClickListener(this);
        binding.ivMuteAudio.setOnClickListener(this);
        binding.ivVideo.setOnClickListener(this);
        binding.ivMuteVideo.setOnClickListener(this);
        binding.btnEndCall.setOnClickListener(this);
    }

    /* Activity lifecycle methods */
    @Override
    protected void onPause() {
        super.onPause();
        if (mSession != null) {
            mSession.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSession != null) {
            mSession.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        stopTimer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            boolean userAllowedAllRequestPermissions = true;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    userAllowedAllRequestPermissions = false;
                }
            }

            if (userAllowedAllRequestPermissions) {
                switch (requestCode) {
                    case PERMISSION_REQUEST_CODE:
                        startSession();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
                requestPermissions(perms, PERMISSION_REQUEST_CODE);
            } else {
                startSession();
            }
        } else {
            startSession();
        }
    }

    private void startSession() {
        if (mSessionId != null && !mSessionId.isEmpty()) {
            if (mSubscriberToken != null && !mSubscriberToken.isEmpty()) {
                initializeSession(BuildConfig.OPENTOK_API_KEY, mSessionId, mSubscriberToken);
            } else if (mPublisherToken != null && !mPublisherToken.isEmpty()) {
                initializeSession(BuildConfig.OPENTOK_API_KEY, mSessionId, mPublisherToken);
            }
        }
    }

    private void initializeSession(String apiKey, String sessionId, String token) {
        mSession = new Session.Builder(this, apiKey, sessionId).build();
        mSession.setSessionListener(this);
        mSession.connect(token);
    }

    /* Session Listener methods */
    @Override
    public void onConnected(Session session) {
        // initialize Publisher and set this object to listen to Publisher events
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        // set publisher video style to fill view
        mPublisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL);
        binding.publisherContainer.addView(mPublisher.getView());
        if (mPublisher.getView() instanceof GLSurfaceView) {
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSubscriber.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
            mSubscriber.setSubscriberListener(this);
            mSession.subscribe(mSubscriber);
            binding.subscriberContainer.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        if (mSubscriber != null) {
            mSubscriber = null;
            binding.subscriberContainer.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        showOpenTokError(opentokError);
    }

    /* Publisher Listener methods */
    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        showOpenTokError(opentokError);
    }

    @Override
    public void onConnected(SubscriberKit subscriberKit) {
    }

    @Override
    public void onDisconnected(SubscriberKit subscriberKit) {
    }

    @Override
    public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {
        showOpenTokError(opentokError);
    }

    private void showOpenTokError(OpentokError opentokError) {
        Toast.makeText(this, opentokError.getErrorDomain().name() + ": " + opentokError.getMessage() + " Please, see the logcat.", Toast.LENGTH_LONG).show();
        endCall();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_audio) {
            binding.ivAudio.setVisibility(View.GONE);
            binding.ivMuteAudio.setVisibility(View.VISIBLE);
            mPublisher.setPublishAudio(false);
        } else if (view.getId() == R.id.iv_mute_audio) {
            binding.ivAudio.setVisibility(View.VISIBLE);
            binding.ivMuteAudio.setVisibility(View.GONE);
            mPublisher.setPublishAudio(true);
        } else if (view.getId() == R.id.iv_video) {
            binding.ivVideo.setVisibility(View.GONE);
            binding.ivMuteVideo.setVisibility(View.VISIBLE);
            mPublisher.setPublishVideo(false);
        } else if (view.getId() == R.id.iv_mute_video) {
            binding.ivVideo.setVisibility(View.VISIBLE);
            binding.ivMuteVideo.setVisibility(View.GONE);
            mPublisher.setPublishVideo(true);
        } else if (view.getId() == R.id.btn_end_call) {
            CommonFunctions.getInstance().showAlertDialog(OpenTokActivity.this, R.string.end_message, R.string.yes, R.string.no, new DialogDismissCallBackListener<Boolean>() {
                @Override
                public void onClose(Boolean result) {
                    if (result) {
                        endCall();
                    }
                }
            });
        }
    }

    private void endCall() {
        if (CommonFunctions.getInstance().isOffline(this)) {
            Toast.makeText(this, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }
        CommonFunctions.getInstance().loadProgressDialog(this);
        new ApiServices().endCall(this, requestId, new RestClientResponse() {
            @Override
            public void onSuccess(Object response, int statusCode) {
                EmptyResponse emptyResponse = (EmptyResponse) response;
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(OpenTokActivity.this, emptyResponse.getMessage(), Toast.LENGTH_SHORT).show();
                finishOpenTokActivity(RESULT_OK);
            }

            @Override
            public void onFailure(String errorMessage, int statusCode) {
                CommonFunctions.getInstance().dismissProgressDialog();
                Toast.makeText(OpenTokActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void finishOpenTokActivity(int resultCode) {
        if (mSession != null) {
            mSession.disconnect();
        }
        Intent intent = new Intent();
        intent.putExtra(OPENTOK_REQUEST_ID, requestId);
        setResult(resultCode, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // not do anything
    }

    private void reSetTimer() {
        stopTimer();
        timerTask = new TimerTask();
        timer.schedule(timerTask, 1000); //1 sec
    }

    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }
    }

    private class TimerTask extends java.util.TimerTask {
        @Override
        public void run() {
            calculateCallTime();
            reSetTimer();
        }
    }

    @SuppressLint("DefaultLocale")
    private void calculateCallTime() {
        try {
            Calendar calendar = Calendar.getInstance();
            if (startTime > 0) {
                long millis = calendar.getTimeInMillis() - startTime;
                String callTimeStr = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                if (callTimeStr != null) {
                    callTime.set(callTimeStr);
                }
            } else {
                stopTimer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

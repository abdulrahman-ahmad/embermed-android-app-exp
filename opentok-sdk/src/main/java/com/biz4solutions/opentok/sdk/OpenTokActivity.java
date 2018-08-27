package com.biz4solutions.opentok.sdk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;

public class OpenTokActivity extends AppCompatActivity implements WebServiceCoordinator.Listener,
        Session.SessionListener,
        PublisherKit.PublisherListener,
        SubscriberKit.SubscriberListener {

    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    public static final int RC_OPENTOK_ACTIVITY = 125;
    public static final String OPENTOK_SESSION_ID = "OPENTOK_SESSION_ID";
    public static final String OPENTOK_SUBSCRIBER_TOKEN = "OPENTOK_SUBSCRIBER_TOKEN";
    public static final String OPENTOK_PUBLISHER_TOKEN = "OPENTOK_PUBLISHER_TOKEN";

    private String mSessionId;
    private String mSubscriberToken;
    private String mPublisherToken;

    private WebServiceCoordinator mWebServiceCoordinator;

    private Session mSession;
    private Subscriber mSubscriber;

    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opentok);

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
        }

        // initialize view objects from your layout
        mPublisherViewContainer = findViewById(R.id.publisher_container);
        mSubscriberViewContainer = findViewById(R.id.subscriber_container);

        requestPermissions();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestPermissions() {
        //String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        //if (EasyPermissions.hasPermissions(this, perms)) {
        // use hard coded session values
        if (OpenTokConfig.CHAT_SERVER_URL == null) {
            // use hard coded session values
            if (mSessionId!=null && !mSessionId.isEmpty()){
                if(mSubscriberToken!=null && !mSubscriberToken.isEmpty()) {
                    initializeSession(OpenTokConfig.API_KEY, mSessionId, mSubscriberToken);
                } else if(mPublisherToken!=null && !mPublisherToken.isEmpty()) {
                    initializeSession(OpenTokConfig.API_KEY, mSessionId, mPublisherToken);
                } else {
                    showConfigError("Token Error", OpenTokConfig.hardCodedConfigErrorMessage);
                }
            } else {
                showConfigError("Configuration Error", OpenTokConfig.hardCodedConfigErrorMessage);
            }
        } else {
            // otherwise initialize WebServiceCoordinator and kick off request for session data
            // session initialization occurs once data is returned, in onSessionConnectionDataReady
            if (OpenTokConfig.isWebServerConfigUrlValid()) {
                mWebServiceCoordinator = new WebServiceCoordinator(this, this);
                mWebServiceCoordinator.fetchSessionConnectionData(OpenTokConfig.SESSION_INFO_ENDPOINT);
            } else {
                showConfigError("Configuration Error", OpenTokConfig.webServerConfigErrorMessage);
            }
        }
        /*} else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_video_app), RC_VIDEO_APP_PERM, perms);
        }*/
    }

    private void initializeSession(String apiKey, String sessionId, String token) {
        mSession = new Session.Builder(this, apiKey, sessionId).build();
        mSession.setSessionListener(this);
        mSession.connect(token);
    }

    /* Web Service Coordinator delegate methods */

    @Override
    public void onSessionConnectionDataReady(String apiKey, String sessionId, String token) {
        initializeSession(apiKey, sessionId, token);
    }

    @Override
    public void onWebServiceCoordinatorError(Exception error) {
        Toast.makeText(this, "Web Service error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        finish();
    }

    /* Session Listener methods */

    @Override
    public void onConnected(Session session) {
        // initialize Publisher and set this object to listen to Publisher events
        Publisher mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        // set publisher video style to fill view
        mPublisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL);
        mPublisherViewContainer.addView(mPublisher.getView());
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
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
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
        finish();
    }

    private void showConfigError(String alertTitle, final String errorMessage) {
        new AlertDialog.Builder(this)
                .setTitle(alertTitle)
                .setMessage(errorMessage)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        OpenTokActivity.this.finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}

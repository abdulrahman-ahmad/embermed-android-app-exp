package com.biz4solutions.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.biz4solutions.interfaces.DialogDismissCallBackListener;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

public class CommonFunctions implements Serializable {

    private static CommonFunctions instance = null;
    private ProgressDialog mProgressBar;
    private AppCompatDialog cProgressBar;
    private AlertDialog.Builder alertDialog;
    private AlertDialog mDialog;

    private CommonFunctions() {
    }

    public static CommonFunctions getInstance() {
        if (instance == null) {
            instance = new CommonFunctions();
        }
        return instance;
    }

    /*
     * method used to check network.
     */
    public boolean isOffline(Context context) {
        try {
            if (context != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = null;
                if (connectivityManager != null) {
                    networkInfo = connectivityManager.getActiveNetworkInfo();
                }
                return (networkInfo == null || !networkInfo.isConnected());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String getSupportDirectoryPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Constants.FILE_DIRECTORY + File.separator + Constants.SUPPORT_DIRECTORY + File.separator;
    }

    /*
     * method used to hide SoftKeyBoard.
     */
    public void hideSoftKeyBoard(Activity activity) {
        try {
            if (activity == null) {
                return;
            }
            hideSoftKeyBoard(activity, activity.getCurrentFocus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * method used to hide SoftKeyBoard.
     */
    public void hideSoftKeyBoard(Activity activity, View view) {
        try {
            if (activity == null) {
                return;
            }
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * method used to show loader.
     */
    public void loadProgressDialog(Context context) {
        try {
            if (mProgressBar != null && mProgressBar.isShowing()) {
                dismissProgressDialog();
            }
            mProgressBar = new ProgressDialog(context, R.style.CustomProgressBarStyle);
            mProgressBar.setIndeterminateDrawable(new CircularProgressDrawable
                    .Builder(context)
                    .colors(context.getResources().getIntArray(R.array.progress_bar_color))
                    .sweepSpeed(1f)
                    .style(CircularProgressDrawable.STYLE_ROUNDED)
                    .build());
            mProgressBar.show();
            mProgressBar.setCancelable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * method used to show loader.
     */
    public void loadCustomProgressDialog(Context context) {
        if ((cProgressBar != null && cProgressBar.isShowing()) || (mProgressBar != null && mProgressBar.isShowing())) {
            return;
        }
        cProgressBar = new AppCompatDialog(context);
        cProgressBar.setCancelable(false);
        if (cProgressBar.getWindow() != null) {
            cProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        cProgressBar.setContentView(R.layout.progress_loading);
        cProgressBar.show();

        final ImageView img_loading_frame = cProgressBar.findViewById(R.id.iv_frame_loading);
        if (img_loading_frame != null) {
            final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
            img_loading_frame.post(new Runnable() {
                @Override
                public void run() {
                    frameAnimation.start();
                }
            });
        }
    }

    /*
     * method used to dismiss loader.
     */
    public void dismissProgressDialog() {
        if (mProgressBar != null) {
            mProgressBar.dismiss();
        }
    }

    public void showAlertDialog(Context context, int messageId) {
        showAlertDialog(context, messageId, R.string.ok, 0, false, false, null);
    }

    public void showAlertDialog(Context context, int messageId, final DialogDismissCallBackListener<Boolean> callBackListener) {
        showAlertDialog(context, messageId, R.string.ok, R.string.cancel, false, false, callBackListener);
    }

    public void showAlertDialog(Context context, int messageId, boolean isHighPriority, final DialogDismissCallBackListener<Boolean> callBackListener) {
        showAlertDialog(context, messageId, R.string.ok, R.string.cancel, false, isHighPriority, callBackListener);
    }

    public void showAlertDialog(Context context, int messageId, int ptBtnTextId, final DialogDismissCallBackListener<Boolean> callBackListener) {
        showAlertDialog(context, messageId, ptBtnTextId, R.string.cancel, true, false, callBackListener);
    }

    public void showAlertDialog(Context context, int messageId, int ptBtnTextId, int ntBtnTextId, final DialogDismissCallBackListener<Boolean> callBackListener) {
        showAlertDialog(context, messageId, ptBtnTextId, ntBtnTextId, true, false, callBackListener);
    }

    private void showAlertDialog(Context context, int messageId, int ptBtnTextId, int ntBtnTextId, boolean isNtBtn, boolean isHighPriority, final DialogDismissCallBackListener<Boolean> callBackListener) {
        if (!isHighPriority && alertDialog != null) {
            return;
        }
        dismissAlertDialog();
        alertDialog = new AlertDialog.Builder(context);
        // Setting Dialog Message
        alertDialog.setMessage(context.getString(messageId));

        // On pressing the Settings button.
        alertDialog.setPositiveButton(ptBtnTextId, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // add callback here
                if (callBackListener != null) {
                    callBackListener.onClose(true);
                }
                alertDialog = null;
                mDialog = null;
            }
        });
        if (isNtBtn) {
            alertDialog.setNegativeButton(ntBtnTextId, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // add callback here
                    if (callBackListener != null) {
                        callBackListener.onClose(false);
                    }
                    alertDialog = null;
                    mDialog = null;
                }
            });
        }
        alertDialog.setCancelable(false);
        // Showing Alert Message
        mDialog = alertDialog.show();
    }

    public void dismissAlertDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    /**
     * Getting GPS status
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     */
    public boolean isGPSEnabled(final Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        LocationManager locationManager;
        boolean isGPSEnabled = false;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!isGPSEnabled) {
                showAlertDialog(context, R.string.error_gps_off, new DialogDismissCallBackListener<Boolean>() {
                    @Override
                    public void onClose(Boolean result) {
                        if (result) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(intent);
                        }
                    }
                });
            }
        }
        return isGPSEnabled;
    }

    public void doLogOut(final Context context, String alertMessage) {
        dismissProgressDialog();
        Intent intent1 = new Intent();
        intent1.setAction(Constants.LOGOUT_RECEIVER);
        intent1.putExtra(Constants.LOGOUT_MESSAGE, alertMessage);
        context.sendBroadcast(intent1);
    }

    public String getTimeAgo(long durationInMilliseconds) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMilliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMilliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(durationInMilliseconds);
        long days = TimeUnit.MILLISECONDS.toDays(durationInMilliseconds);
        String s;
        if (seconds < 60) {
            if (seconds == 1) {
                s = "Just now";
            } else {
                s = seconds + " sec ago";
            }
        } else if (minutes < 60) {
            if (minutes == 1) {
                s = minutes + " min ago";
            } else {
                s = minutes + " mins ago";
            }
        } else if (hours < 24) {
            if (hours == 1) {
                s = hours + " hr ago";
            } else {
                s = hours + " hrs ago";
            }
        } else {
            if (days == 1) {
                s = days + " day ago";
            } else {
                if (days >= 365) {
                    int yr = (int) (days / 365);
                    if (yr == 1) {
                        s = yr + " yr ago";
                    } else {
                        s = yr + " yrs ago";
                    }
                } else if (days >= 30) {
                    int month = (int) (days / 30);
                    if (month == 1) {
                        s = month + " month ago";
                    } else {
                        s = month + " months ago";
                    }
                } else if (days >= 7) {
                    int week = (int) (days / 7);
                    if (week == 1) {
                        s = week + " week ago";
                    } else {
                        s = week + " weeks ago";
                    }
                } else {
                    s = days + " days ago";
                }
            }
        }
        return s;
    }
}
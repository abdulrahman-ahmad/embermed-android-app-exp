package com.biz4solutions.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.biz4solutions.interfaces.DialogDismissCallBackListener;

import java.io.File;
import java.io.Serializable;

import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

public class CommonFunctions implements Serializable {

    private static CommonFunctions instance = null;
    private AppCompatDialog mProgressBar;

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
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            return (networkInfo == null || !networkInfo.isConnected());
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
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );

            View view = activity.getCurrentFocus();
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
                return;
            }
            mProgressBar = new AppCompatDialog(context);
            mProgressBar.setCancelable(false);
            if(mProgressBar.getWindow() != null) {
                mProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
            mProgressBar.setContentView(R.layout.progress_loading);
            mProgressBar.show();

            final ImageView img_loading_frame = mProgressBar.findViewById(R.id.iv_frame_loading);
            if(img_loading_frame != null) {
                final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
                img_loading_frame.post(new Runnable() {
                    @Override
                    public void run() {
                        frameAnimation.start();
                    }
                });
            }
            /*if (mProgressBar != null && mProgressBar.isShowing()) {
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
            mProgressBar.setCancelable(false);*/
        } catch (Exception e) {
            e.printStackTrace();
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
        showAlertDialog(context, messageId, R.string.ok, 0,false, null);
    }

    public void showAlertDialog(Context context, int messageId, final DialogDismissCallBackListener<Boolean> callBackListener) {
        showAlertDialog(context, messageId, R.string.ok, R.string.cancel,true, callBackListener);
    }

    public void showAlertDialog(Context context, int messageId, int ptBtnTextId, final DialogDismissCallBackListener<Boolean> callBackListener) {
        showAlertDialog(context, messageId, ptBtnTextId, R.string.cancel,true, callBackListener);
    }

    public void showAlertDialog(Context context, int messageId, int ptBtnTextId,int ntBtnTextId, final DialogDismissCallBackListener<Boolean> callBackListener) {
        showAlertDialog(context, messageId, ptBtnTextId,ntBtnTextId, true, callBackListener);
    }

    private void showAlertDialog(Context context, int messageId, int ptBtnTextId,int ntBtnTextId, boolean isNtBtn, final DialogDismissCallBackListener<Boolean> callBackListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        // Setting Dialog Message
        alertDialog.setMessage(context.getString(messageId));
        // On pressing the Settings button.
        alertDialog.setPositiveButton(ptBtnTextId, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // add callback here
                if (callBackListener != null) {
                    callBackListener.onClose(true);
                }
            }
        });
        if (isNtBtn) {
            alertDialog.setNegativeButton(ntBtnTextId, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // add callback here
                    if (callBackListener != null) {
                        callBackListener.onClose(false);
                    }
                }
            });
        }
        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }
}
package com.biz4solutions.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.biz4solutions.R;
import com.biz4solutions.activities.MainActivity;
import com.biz4solutions.interfaces.DialogDismissCallBackListener;

import java.io.Serializable;

import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

public class CommonFunctions implements Serializable {

    private static CommonFunctions instance = null;
    private ProgressDialog mProgressBar;

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
     * method used to dismiss loader.
     */
    public void dismissProgressDialog() {
        if (mProgressBar != null) {
            mProgressBar.dismiss();
        }
    }

    public void showAlertDialog(Context context, int messageId) {
        showAlertDialog(context, messageId, R.string.ok, false, null);
    }

    public void showAlertDialog(Context context, int messageId, final DialogDismissCallBackListener<Boolean> callBackListener) {
        showAlertDialog(context, messageId, R.string.ok, true, callBackListener);
    }

    public void showAlertDialog(Context context, int messageId, int ptBtnTextId, final DialogDismissCallBackListener<Boolean> callBackListener) {
        showAlertDialog(context, messageId, ptBtnTextId, true, callBackListener);
    }

    private void showAlertDialog(Context context, int messageId, int ptBtnTextId, boolean isNtBtn, final DialogDismissCallBackListener<Boolean> callBackListener) {
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
            alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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

    public void showBackArrow(final MainActivity mainActivity) {
        try {
            if (mainActivity != null) {
                ActionBarDrawerToggle actionBarDrawerToggle = mainActivity.toggle;
                actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideSoftKeyBoard(mainActivity);
                        mainActivity.getSupportFragmentManager().popBackStack();
                    }
                });
                android.support.v7.app.ActionBar actionBar = mainActivity.getSupportActionBar();

                if (actionBar != null) {
                    final Drawable drawable = mainActivity.getResources().getDrawable(R.drawable.ic_arrow_back);
                    actionBar.setHomeAsUpIndicator(drawable);
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setHomeButtonEnabled(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideBackArrow(MainActivity mainActivity) {
        try {
            if (mainActivity != null) {
                hideSoftKeyBoard(mainActivity);
                android.support.v7.app.ActionBar actionBar = mainActivity.getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setDisplayHomeAsUpEnabled(false);
                    actionBar.setHomeButtonEnabled(false);
                }
                ActionBarDrawerToggle actionBarDrawerToggle = mainActivity.toggle;
                if (actionBarDrawerToggle != null) {
                    actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
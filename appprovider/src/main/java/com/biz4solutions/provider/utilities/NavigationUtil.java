package com.biz4solutions.provider.utilities;

import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;

import com.biz4solutions.provider.activities.MainActivity;
import com.biz4solutions.interfaces.OnBackClickListener;
import com.biz4solutions.utilities.CommonFunctions;

public class NavigationUtil {
    private static NavigationUtil instance = null;

    private NavigationUtil() {
    }

    public static NavigationUtil getInstance() {
        if (instance == null) {
            instance = new NavigationUtil();
        }
        return instance;
    }

    public void showBackArrow(final MainActivity mainActivity) {
        showBackArrow(mainActivity, null);
    }

    public void showBackArrow(final MainActivity mainActivity, final OnBackClickListener onBackClickListener) {
        try {
            if (mainActivity != null) {
                ActionBarDrawerToggle actionBarDrawerToggle = mainActivity.toggle;
                actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonFunctions.getInstance().hideSoftKeyBoard(mainActivity);
                        if (onBackClickListener != null) {
                            onBackClickListener.onBackPress();
                        } else {
                            mainActivity.getSupportFragmentManager().popBackStack();
                        }
                    }
                });
                android.support.v7.app.ActionBar actionBar = mainActivity.getSupportActionBar();

                if (actionBar != null) {
                    final Drawable drawable = mainActivity.getResources().getDrawable(com.biz4solutions.utilities.R.drawable.ic_arrow_back_btn);
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
                CommonFunctions.getInstance().hideSoftKeyBoard(mainActivity);
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

    public void hideMenu(MainActivity activity) {
        try {
            if (activity != null) {
                ActionBarDrawerToggle actionBarDrawerToggle = activity.toggle;
                actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                //activity.toolbarTitleSpace.setVisibility(View.VISIBLE);
                android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setDisplayHomeAsUpEnabled(false);
                    actionBar.setHomeButtonEnabled(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMenu(MainActivity activity) {
        //activity.toolbarTitleSpace.setVisibility(View.GONE);
        hideBackArrow(activity);
    }
}

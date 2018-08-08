package com.biz4solutions.utilities;

import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;

import com.biz4solutions.activities.MainActivity;

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
        try {
            if (mainActivity != null) {
                ActionBarDrawerToggle actionBarDrawerToggle = mainActivity.toggle;
                actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonFunctions.getInstance().hideSoftKeyBoard(mainActivity);
                        mainActivity.getSupportFragmentManager().popBackStack();
                    }
                });
                android.support.v7.app.ActionBar actionBar = mainActivity.getSupportActionBar();

                if (actionBar != null) {
                    final Drawable drawable = mainActivity.getResources().getDrawable(R.drawable.ic_arrow_back_btn);
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
}

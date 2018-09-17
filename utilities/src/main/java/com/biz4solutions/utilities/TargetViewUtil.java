package com.biz4solutions.utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import com.biz4solutions.utilities.models.TargetModel;
import com.biz4solutions.customs.taptargetview.TapTarget;
import com.biz4solutions.customs.taptargetview.TapTargetSequence;
import com.biz4solutions.customs.taptargetview.TapTargetView;

import java.util.ArrayList;

/*
 * Created by saurabh.asati on 9/17/2018.
 */
public class TargetViewUtil {

    public void showTargetView(View v, Activity activity) {
        TapTargetView.showFor(activity,                 // `this` is an Activity
                TapTarget.forView(v, "This is a target", "We have the best targets, believe me")
                        // All options below are optional
                        .outerCircleColor(R.color.btn_bg_color)      // Specify a color for the outer circle
//                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
//                        .targetCircleColor(R.color.btn_bg_color)   // Specify a color for the target circle
//                        .titleTextSize(20)                  // Specify the size (in sp) of the title text
//                        .titleTextColor(R.color.btn_bg_color)      // Specify the color of the title text
//                        .descriptionTextSize(10)            // Specify the size (in sp) of the description text
//                        .descriptionTextColor(R.color.rippelColor)  // Specify the color of the description text
//                        .textColor(R.color.btn_bg_color)            // Specify a color for both the title and description text
//                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
//                        .dimColor(R.color.common_google_signin_btn_text_light_disabled)            // If set, will dim behind the view with 30% opacity of the given color
//                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
//                        .tintTarget(true)                   // Whether to tint the target view's color
//                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        .icon(v.getContext().getResources().getDrawable(R.drawable.common_google_signin_btn_icon_dark))              // Specify a custom drawable to draw as the target
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        doSomething(view.getContext());
                    }
                });
    }

    private void doSomething(Context context) {
        Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
    }


//    public void showTargetViewForToolbar(Activity activity, View v) {
//        TapTargetView.showFor(activity,
//
//
//                TapTarget.forToolbarMenuItem((Toolbar) v,activity.getString(R.string.already_have_an_account),
//
//                        activity.getString(R.string.coming_soon))
//                        // 3
//                        .cancelable(false)
//                        // 4
//                        .tintTarget(true), null);
//
//    }

    public void showTargetForButton(Activity activity, View v, String title, String description) {
        TapTargetView.showFor(activity,
                TapTarget.forView(v, title, description)
                        .targetRadius(20)
                        .transparentTarget(true)
                        .targetCircleColor(R.color.btn_red_color),

                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                    }
                });
    }

    /**
     * Method for showing views in sequence
     *
     * @param activity Activity
     * @param vList List of views
     */
    public void showTargetSequence(Activity activity, ArrayList<TargetModel> vList) {
        ArrayList<TapTarget> targetModelArrayList = prepareTarget(vList);
        new TapTargetSequence(activity)
                .targets(targetModelArrayList)
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {

                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                }).start();
    }

    private ArrayList<TapTarget> prepareTarget(ArrayList<TargetModel> targetModels) {
        ArrayList<TapTarget> targetModelArrayList = new ArrayList<>();
        for (int i = 0; i < targetModels.size(); i++) {
            TargetModel targetModel = targetModels.get(i);
            targetModelArrayList.add(
                    TapTarget.forView(targetModel.getView(), targetModel.getTitle(), targetModel.getDesc())
                            .targetRadius(20)
                            .targetCircleColor(R.color.btn_red_color)
                            //.targetCircleColorInt(Color.parseColor("#FFFF00"))
                            .transparentTarget(true)
            );
        }
        return targetModelArrayList;
    }
}

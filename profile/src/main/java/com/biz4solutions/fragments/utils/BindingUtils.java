package com.biz4solutions.fragments.utils;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.biz4solutions.profile.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class BindingUtils {

    /**
     * @param imageView:no need to pass
     * @param url:url      to load
     *                     <p>
     *                     usage:->
     *                     <p>
     *                     <ImageView
     *                     imageUrl="viewModel.imageUrl"
     *                     android:layout_width="@dimen/_100dp"/>
     */

    @BindingAdapter("circularImageUrl")
    public static void loadImage(final ImageView imageView, String url) {
//        Glide.with(imageView.getContext()).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_default_profile_pic)).load(url).into(imageView);
        Glide.with(imageView.getContext()).load(url).apply(new RequestOptions().placeholder(R.drawable.ic_default_profile_pic).circleCrop()).into(imageView);
    }
}

package com.biz4solutions.provider.utilities;

import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
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
        if (url != null && !url.isEmpty()) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_default_profile_pic)
                            .circleCrop())
                    .into(imageView);
        } else {
            imageView.setBackground(ContextCompat.getDrawable(imageView.getContext(), R.drawable.ic_default_profile_pic));
        }
    }
}

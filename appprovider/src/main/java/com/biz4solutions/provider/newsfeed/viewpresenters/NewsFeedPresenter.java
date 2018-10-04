package com.biz4solutions.provider.newsfeed.viewpresenters;

import com.biz4solutions.models.response.NewsFeedDataResponse;

public interface NewsFeedPresenter {
    void toastMsg(String msg);

    //needed for internationalization purpose.
    void startAnimation(NewsFeedDataResponse newsFeedData);
}

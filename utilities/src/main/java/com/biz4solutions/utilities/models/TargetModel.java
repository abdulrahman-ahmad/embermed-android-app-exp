package com.biz4solutions.utilities.models;

import android.view.View;

/**
 * Created by saurabh.asati on 9/17/2018.
 */

public class TargetModel {
    private String title;
    private View view;
    private String desc;

    public TargetModel( View view,String title,String desc) {
        this.title = title;
        this.view = view;
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

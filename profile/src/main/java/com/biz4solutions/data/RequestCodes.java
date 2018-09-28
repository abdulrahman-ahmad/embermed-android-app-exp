package com.biz4solutions.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        RequestCodes.RESULT_GALLERY,
        RequestCodes.RESULT_CAMERA,
        RequestCodes.RESULT_RECORD,
        RequestCodes.PERMISSION_CAMERA,
        RequestCodes.PERMISSION_GALLERY,
        RequestCodes.PERMISSION_READ_EXTERNAL_STORAGE,
        RequestCodes.PERMISSION_WRITE_EXTERNAL_STORAGE
})
@Retention(RetentionPolicy.SOURCE)
public @interface RequestCodes {
    int RESULT_GALLERY = 101;
    int RESULT_CAMERA = 201;
    int RESULT_RECORD = 301;
    int PERMISSION_CAMERA = 401;
    int PERMISSION_GALLERY = 501;
    int PERMISSION_READ_EXTERNAL_STORAGE = 601;
    int PERMISSION_WRITE_EXTERNAL_STORAGE = 701;
}

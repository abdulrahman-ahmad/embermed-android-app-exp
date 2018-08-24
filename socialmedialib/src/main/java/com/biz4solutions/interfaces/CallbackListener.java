package com.biz4solutions.interfaces;

/**
 * Created by ketan on 12/20/2017.
 */
public interface CallbackListener<T> {
    void onSuccess(T data);
    void onFailure(String errorMessage);
}

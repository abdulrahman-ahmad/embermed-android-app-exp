package com.biz4solutions.interfaces;

public interface RestClientResponse {
    void onSuccess(Object response, int statusCode);

    void onFailure(String errorMessage, int statusCode);
}
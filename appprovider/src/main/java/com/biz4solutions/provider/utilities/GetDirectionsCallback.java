package com.biz4solutions.provider.utilities;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ketan on 11-05-2017.
 */

public interface GetDirectionsCallback {
    void showProgress();
    void hideProgress();
    void onFailure(String message);
    void onSuccess(List<List<HashMap<String, String>>> results, String distance, String duration);
}

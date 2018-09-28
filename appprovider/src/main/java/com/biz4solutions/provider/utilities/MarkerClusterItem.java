package com.biz4solutions.provider.utilities;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;


public interface MarkerClusterItem extends ClusterItem {
    void setIcon(BitmapDescriptor icon);
    BitmapDescriptor getIcon();
    Marker getMarker();
    void setMarker(Marker marker);
}

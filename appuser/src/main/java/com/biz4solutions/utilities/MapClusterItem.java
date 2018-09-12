package com.biz4solutions.utilities;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapClusterItem implements MarkerClusterItem {

    private String id;
    private BitmapDescriptor icon;
    private Marker marker;
    private LatLng position;
    private String name;

    public MapClusterItem(LatLng position,String userId,String name) {
        this.id = userId;
        this.position = position;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return id;
    }

    public void setUserId(String userId) {
        this.id = userId;
        this.position = position;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
        if (marker!=null) {
            marker.setIcon(icon);
        }
    }

    @Override
    public BitmapDescriptor getIcon() {
        return icon;
    }

    @Override
    public Marker getMarker() {
        return marker;
    }

    @Override
    public void setMarker(Marker marker) {
        this.marker = marker;
    }

}
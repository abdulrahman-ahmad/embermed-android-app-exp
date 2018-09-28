package com.biz4solutions.provider.utilities;

import android.content.Context;

import com.biz4solutions.provider.R;
import com.biz4solutions.utilities.Constants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * custom map cluster renderer to set cluster size
 */

public class CustomMapClusterRenderer<MapClusterItem extends MarkerClusterItem> extends DefaultClusterRenderer<MapClusterItem> {
    public CustomMapClusterRenderer(Context context, GoogleMap map, ClusterManager<MapClusterItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<MapClusterItem> cluster) {
        //start clustering if at least 2 items overlap
        return cluster.getSize() > Constants.MINIMUM_CLUSTER_SIZE;
    }

    @Override
    protected void onClusterItemRendered(MapClusterItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        clusterItem.setMarker(marker);
    }

    @Override
    protected void onBeforeClusterItemRendered(MapClusterItem item,
                                               MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.aed_pin));
    }

    @Override
    public void onRemove() {
        super.onRemove();
    }
}

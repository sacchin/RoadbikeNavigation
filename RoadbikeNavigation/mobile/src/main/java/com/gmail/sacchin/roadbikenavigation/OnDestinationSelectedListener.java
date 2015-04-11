package com.gmail.sacchin.roadbikenavigation;

import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sacchin on 2015/04/11.
 */
public class OnDestinationSelectedListener implements OnMapLongClickListener {
    protected MapsActivity activity = null;

    public OnDestinationSelectedListener(MapsActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onMapLongClick(LatLng point) {
        activity.destinationSelected(point);
    }
}


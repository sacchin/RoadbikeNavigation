package com.gmail.sacchin.roadbikenavigation;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by sacchin on 2015/04/11.
 */
public class OnDestinationSelectedListener implements GoogleMap.OnMarkerClickListener {
    protected MapsActivity activity = null;

    public OnDestinationSelectedListener(MapsActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        activity.onStartNavigation();
        return false;
    }
}


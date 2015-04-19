package com.gmail.sacchin.roadbikenavigation;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by sacchin on 2015/04/05.
 */
public class Bounds {

    protected GeoPoint southwest;
    protected GeoPoint northeast;

    public GeoPoint getSouthwest() {
        return southwest;
    }

    public void setSouthwest(GeoPoint southwest) {
        this.southwest = southwest;
    }

    public GeoPoint getNortheast() {
        return northeast;
    }

    public void setNortheast(GeoPoint northeast) {
        this.northeast = northeast;
    }

    public LatLngBounds getLatLngBounds(){
        return LatLngBounds.builder()
                .include(new LatLng(southwest.lat, southwest.lng))
                .include(new LatLng(northeast.lat, northeast.lng))
                .build();

    }

}

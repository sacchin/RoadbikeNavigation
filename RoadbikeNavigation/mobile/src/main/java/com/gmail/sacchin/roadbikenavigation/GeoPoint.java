package com.gmail.sacchin.roadbikenavigation;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sacchin on 2015/04/05.
 */
public class GeoPoint {

    protected double lat;
    protected double lng;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public LatLng getLatlng(){
        return new LatLng(lat, lng);
    }

}

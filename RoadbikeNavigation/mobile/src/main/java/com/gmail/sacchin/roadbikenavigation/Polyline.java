package com.gmail.sacchin.roadbikenavigation;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sacchin on 2015/04/05.
 */
public class Polyline {
    protected String points;

    public List<LatLng> getPoints() {
        return decodePoly(points);
    }

    public void setPoints(String points) {
        this.points = points;
    }

    private List<LatLng> decodePoly(String encoded) {

        Log.i("Location", "String received: " + encoded);
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng(((double) lat /1E5), ((double) lng/1E5));
            poly.add(p);
        }

        for(int i=0;i<poly.size();i++){
            Log.i("Location", "Point sent: Latitude: "+poly.get(i).latitude+" Longitude: "+poly.get(i).longitude);
        }
        return poly;
    }
}

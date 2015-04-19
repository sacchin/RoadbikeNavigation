package com.gmail.sacchin.roadbikenavigation;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sacchin on 2015/04/19.
 */
public class MapCameraUtil {

    public static void moveCamera(GoogleMap mMap, LatLng moveTo) {
        if(mMap == null || moveTo == null){
            return;
        }
        float zoomLevel = mMap.getCameraPosition().zoom;
        float tilt = mMap.getCameraPosition().tilt;
        float bearing = mMap.getCameraPosition().bearing;

        CameraPosition pos = new CameraPosition(moveTo, zoomLevel, tilt, bearing);
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(pos);
        mMap.moveCamera(camera);
    }

    public static void zoomCamera(GoogleMap mMap, float zoomLevel) {
        if(mMap == null || zoomLevel < 0 || 15 < zoomLevel){
            return;
        }
        float tilt = 0.0f;
        float bearing = 0.0f;

        CameraPosition pos = new CameraPosition(mMap.getCameraPosition().target, zoomLevel, tilt, bearing);
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(pos);
        mMap.moveCamera(camera);

//        mMap.setOnMapLongClickListener(new OnCandidateSelectedListener(this));
    }
}

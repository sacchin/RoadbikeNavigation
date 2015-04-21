package com.gmail.sacchin.roadbikenavigation;

import android.graphics.Color;
import android.location.Location;

import com.activeandroid.query.Select;
import com.gmail.sacchin.roadbikenavigation.directions.DirectionsResponse;
import com.gmail.sacchin.roadbikenavigation.directions.Leg;
import com.gmail.sacchin.roadbikenavigation.directions.Route;
import com.gmail.sacchin.roadbikenavigation.directions.Step;
import com.gmail.sacchin.roadbikenavigation.model.GeoPointModel;
import com.gmail.sacchin.roadbikenavigation.model.RouteModel;
import com.gmail.sacchin.roadbikenavigation.model.StepModel;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sacchin on 2015/04/21.
 */
public class NavigationLogic {

    protected GoogleApiClient mGoogleApiClient;
    protected DirectionsResponse directionsResponse = null;
    protected Location location;
    protected int nowRouteId = 0;
    protected int nowStepId = 0;

    public boolean startNavigation() {
        if (directionsResponse == null) {
            return false;
        }

        RouteModel routeModel = new RouteModel();
        routeModel.id = DatabaseUtil.getMaxId(RouteModel.class);
        routeModel.startTime = System.currentTimeMillis();
        routeModel.endTime = 0;
        routeModel.save();

        nowRouteId = routeModel.id;

        StepModel stepModel = new StepModel();
        stepModel.id = DatabaseUtil.getMaxId(StepModel.class);
        stepModel.routeId = nowRouteId;
        stepModel.startTime = System.currentTimeMillis();
        stepModel.endTime = 0;
        stepModel.save();

        nowStepId = stepModel.id;

        return true;
    }

    public void forwardStep() {
        StepModel beforeStep = new Select().from(StepModel.class).orderBy("id desc").executeSingle();
        beforeStep.endTime = System.currentTimeMillis();
        beforeStep.save();

        StepModel nextStep = new StepModel();
        nextStep.id = DatabaseUtil.getMaxId(StepModel.class);
        nextStep.routeId = nowRouteId;
        nextStep.startTime = System.currentTimeMillis();
        nextStep.endTime = 0;
        nextStep.save();

        nowStepId = nextStep.id;
    }

    public void onLocationChanged(Location location) {
        GeoPointModel geoPointModel = new GeoPointModel();
        geoPointModel.stepId = nowStepId;
        geoPointModel.time = System.currentTimeMillis();
        geoPointModel.lat = location.getLatitude();
        geoPointModel.lng = location.getLongitude();
        geoPointModel.save();
    }

    public void setDirectionsResult(String json) {
        Gson gson = new Gson();
        directionsResponse = gson.fromJson(json, DirectionsResponse.class);
    }

    public List<PolylineOptions> createPolylineOptions() {
        List<PolylineOptions> result = new ArrayList<PolylineOptions>();

        for (Route route : directionsResponse.getRoutes()) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.width(10).color(Color.BLUE);

            for (Leg leg : route.getLegs()) {
                for (Step step : leg.getSteps()) {
                    List<LatLng> polyline = step.getPolyline().getPoints();
                    for (int i = 0; i < polyline.size() - 1; i++) {
                        polylineOptions.add(polyline.get(i), polyline.get(i + 1));
                    }
                }
            }
            result.add(polylineOptions);
        }

        return result;
    }

    public LatLngBounds getLatLngBounds() {
        if(directionsResponse.getRoutes().isEmpty()) {
            return null;
        }else{
            return directionsResponse.getRoutes().get(0).getBounds().getLatLngBounds();
        }
    }
}
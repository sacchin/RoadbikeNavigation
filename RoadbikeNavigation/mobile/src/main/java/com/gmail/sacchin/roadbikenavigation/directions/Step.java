package com.gmail.sacchin.roadbikenavigation.directions;

import com.gmail.sacchin.roadbikenavigation.directions.Distance;
import com.gmail.sacchin.roadbikenavigation.directions.Duration;
import com.gmail.sacchin.roadbikenavigation.directions.GeoPoint;
import com.gmail.sacchin.roadbikenavigation.directions.Polyline;

/**
 * Created by sacchin on 2015/04/05.
 */
public class Step {
protected String travelMode;
    protected GeoPoint start_location;
    protected GeoPoint end_location;
    protected Duration duration;
    protected Distance distance;
    protected Polyline polyline;

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public GeoPoint getStartLocation() {
        return start_location;
    }

    public void setStartLocation(GeoPoint start_location) {
        this.start_location = start_location;
    }

    public GeoPoint getEndLocation() {
        return end_location;
    }

    public void setEndLocation(GeoPoint end_location) {
        this.end_location = end_location;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }
}

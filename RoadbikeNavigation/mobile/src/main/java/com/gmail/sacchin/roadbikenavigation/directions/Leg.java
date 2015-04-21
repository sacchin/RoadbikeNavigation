package com.gmail.sacchin.roadbikenavigation.directions;

import java.util.List;

/**
 * Created by sacchin on 2015/04/05.
 */
public class Leg {
    protected List<Step> steps;
    protected GeoPoint start_location;
    protected GeoPoint end_location;
    protected String start_address;
    protected String end_address;
    protected Duration duration;
    protected Distance distance;

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

    public GeoPoint getStart_location() {
        return start_location;
    }

    public void setStart_location(GeoPoint start_location) {
        this.start_location = start_location;
    }

    public GeoPoint getEnd_location() {
        return end_location;
    }

    public void setEnd_location(GeoPoint end_location) {
        this.end_location = end_location;
    }

    public String getStart_address() {
        return start_address;
    }

    public void setStart_address(String start_address) {
        this.start_address = start_address;
    }

    public String getEnd_address() {
        return end_address;
    }

    public void setEnd_address(String end_address) {
        this.end_address = end_address;
    }


    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}

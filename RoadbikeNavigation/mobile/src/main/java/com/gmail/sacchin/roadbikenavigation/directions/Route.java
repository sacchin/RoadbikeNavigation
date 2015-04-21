package com.gmail.sacchin.roadbikenavigation.directions;

import com.gmail.sacchin.roadbikenavigation.directions.Bounds;
import com.gmail.sacchin.roadbikenavigation.directions.Leg;
import com.gmail.sacchin.roadbikenavigation.directions.Polyline;

import java.util.List;

/**
 * Created by sacchin on 2015/04/05.
 */
public class Route {
    protected String summary;
    protected List<Leg> legs;
    protected Bounds bounds;
    protected Polyline overview_polyline;

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public Polyline getOverview_polyline() {
        return overview_polyline;
    }

    public void setOverview_polyline(Polyline overview_polyline) {
        this.overview_polyline = overview_polyline;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }
}

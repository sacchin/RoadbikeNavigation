package com.gmail.sacchin.roadbikenavigation.directions;

import java.util.List;

/**
 * Created by sacchin on 2015/04/05.
 */
public class DirectionsResponse {
protected String status;
    protected List<Route> routes;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

}

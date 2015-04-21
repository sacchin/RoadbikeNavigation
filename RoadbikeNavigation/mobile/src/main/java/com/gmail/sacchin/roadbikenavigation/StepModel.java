package com.gmail.sacchin.roadbikenavigation;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by sacchin on 2015/04/19.
 */
@Table(name = "onestep")
public class StepModel extends Model {
    @Column(name = BaseColumns._ID, notNull = true)
    public int id;

    @Column(name = "route_id", notNull = true)
    public int routeId;

    @Column(name = "start_time")
    public long startTime;

    @Column(name = "end_time")
    public long endTime;

    @Column(name = "RouteModel")
    public RouteModel routeModel;

    public StepModel(){
        super();
    }

    public StepModel(int id, int routeId, long startTime, long endTime){
        super();
        this.id = id;
        this.routeId = routeId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String toString(){
        return "{id:" + id + ", routId:" + routeId + ", startTime:" + startTime + ", endTime:" + endTime + ", steps" + geoPointModels().size() + "}";
    }

    public List<GeoPointModel> geoPointModels() {
        return getMany(GeoPointModel.class, "StepModel");
    }
}

package com.gmail.sacchin.roadbikenavigation;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by sacchin on 2015/04/19.
 */
@Table(name = "oneroute")
public class RouteModel extends RoadbikeModel {
    @Column(name = BaseColumns._ID, notNull = true)
    public int id;

    @Column(name = "start_time")
    public long startTime;

    @Column(name = "end_time")
    public long endTime;

    public RouteModel(){
        super();
    }

    public RouteModel(int id, long startTime, long endTime){
        super();
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String toString(){
        return "{id:" + id + ", startTime:" + startTime + ", endTime:" + endTime + ", steps" + stepModels().size() + "}";
    }

    public List<StepModel> stepModels() {
        return getMany(StepModel.class, "RouteModel");
    }
}

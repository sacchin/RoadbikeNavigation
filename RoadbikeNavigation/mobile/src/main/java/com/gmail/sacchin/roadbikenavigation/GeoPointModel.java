package com.gmail.sacchin.roadbikenavigation;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by sacchin on 2015/04/21.
 */
@Table(name = "geopoint")
public class GeoPointModel extends Model {
    @Column(name = BaseColumns._ID, notNull = true)
    public int id;

    @Column(name = "step_id", notNull = true)
    public int stepId;

    @Column(name = "time")
    public long time;

    @Column(name = "lat")
    public double lat;

    @Column(name = "lng")
    public double lng;

    @Column(name = "StepModel")
    public StepModel stepModel;

    public GeoPointModel(){
        super();
    }

    public GeoPointModel(int id, int stepId, long time, double lat, double lng){
        super();
        this.id = id;
        this.stepId = stepId;
        this.time = time;
        this.lat = lat;
        this.lng = lng;
    }

    public String toString(){
        return "{id:" + id + ", stepId:" + stepId + ", time:" + time + ", lat:" + lat + ", lng:" + lng + "}";
    }

}

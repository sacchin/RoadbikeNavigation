package com.gmail.sacchin.roadbikenavigation.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

/**
 * Created by sacchin on 2015/04/21.
 */
public abstract class RoadbikeModel extends Model{
    @Column(name = BaseColumns._ID, notNull = true)
    public int id;

}

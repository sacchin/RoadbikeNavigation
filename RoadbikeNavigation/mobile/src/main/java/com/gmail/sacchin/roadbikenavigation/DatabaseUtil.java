package com.gmail.sacchin.roadbikenavigation;

import android.database.sqlite.SQLiteException;

import com.activeandroid.Model;
import com.activeandroid.query.Select;

/**
 * Created by sacchin on 2015/04/21.
 */
public class DatabaseUtil{

    public static int getMaxId(Class<? extends RoadbikeModel> sample ){
        int id = 0;
        try {
            RoadbikeModel object = new Select().from(sample).orderBy("id desc").executeSingle();
            id = (object != null) ? object.id + 1 : 0;
        }catch (SQLiteException e){
            id = 0;
        }
        return id;
    }
}

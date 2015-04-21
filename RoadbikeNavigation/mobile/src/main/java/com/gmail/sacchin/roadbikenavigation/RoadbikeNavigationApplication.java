package com.gmail.sacchin.roadbikenavigation;

import com.activeandroid.ActiveAndroid;

/**
 * Created by sacchin on 2015/04/19.
 */
public class RoadbikeNavigationApplication extends com.activeandroid.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}

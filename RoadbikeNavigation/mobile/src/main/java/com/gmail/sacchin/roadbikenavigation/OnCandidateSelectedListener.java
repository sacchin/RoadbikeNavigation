package com.gmail.sacchin.roadbikenavigation;

import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sacchin on 2015/04/11.
 */
public class OnCandidateSelectedListener implements OnMapLongClickListener {
    protected MapsActivity activity = null;
    protected static enum State{
        BEFORE_NAVIGATION,
        DURING_NAVIGATION,
        AFTER_NAVIGATION
    }

    protected State state = State.BEFORE_NAVIGATION;

    public OnCandidateSelectedListener(MapsActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onMapLongClick(LatLng point) {
        switch (state){
            case BEFORE_NAVIGATION:
                activity.onDestinationSelected(point);
                break;
            case DURING_NAVIGATION:
                activity.forwardStep();
                break;
            case AFTER_NAVIGATION:
                break;
            default:
        }
    }

    public void start(){
        state = State.DURING_NAVIGATION;
    }

    public void end(){
        state = State.AFTER_NAVIGATION;
    }
}


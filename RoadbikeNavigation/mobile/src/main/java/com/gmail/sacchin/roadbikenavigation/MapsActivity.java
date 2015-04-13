package com.gmail.sacchin.roadbikenavigation;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    protected Marker latestMarker = null;

    protected ExecutorService executorService = Executors.newCachedThreadPool();
    protected LocationManager locationManager = null;
    protected Location myLocate = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        myLocate = locationManager.getLastKnownLocation("gps");

        setUpMapIfNeeded();

        Intent intent = getIntent();
        if(intent != null){
            String message = intent.getStringExtra("returnMessage");
            if(message != null){
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        LatLng latLng = new LatLng(myLocate.getLatitude(), myLocate.getLongitude());

        float zoomLevel = 15.0f; //ズームレベル
        float tilt = 0.0f; // 0.0 - 90.0  //チルトアングル
        float bearing = 0.0f; //向き

        CameraPosition pos = new CameraPosition(latLng, zoomLevel, tilt, bearing);
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(pos);
        mMap.moveCamera(camera);

        mMap.setOnMapLongClickListener(new OnDestinationSelectedListener(this));
    }

    public void destinationSelected(LatLng point){
        if(latestMarker != null){
            latestMarker.remove();
        }

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(100);

        MarkerOptions options = new MarkerOptions();
        options.position(point);
        latestMarker = mMap.addMarker(options);


        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng destination = marker.getPosition();
                LatLng origin = new LatLng(myLocate.getLatitude(), myLocate.getLongitude());

                final Handler handler = new Handler();
                executorService.execute(
                        new RouteDownloader(destination, origin, handler, activity));

                return false;
            }
        });
    }

    public void onReceiveRoute(String jsonString){
        Log.d("onReceiveRoute", jsonString);

        Gson gson = new Gson();
        DirectionsResponse directionsResponse = gson.fromJson(jsonString, DirectionsResponse.class);

        for(Route route : directionsResponse.getRoutes()){
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.width(10).color(Color.BLUE);

            for(Leg leg : route.getLegs()){
                for(Step step : leg.getSteps()){
                    List<LatLng> polyline = step.getPolyline().getPoints();
                    for(int i = 0 ; i < polyline.size() - 1 ; i++){
                        polylineOptions.add(polyline.get(i), polyline.get(i + 1));
                    }
                }
            }

            latestPolyline = mMap.addPolyline(polylineOptions);

            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(route.getBounds().getLatLngBounds(), 15));
        }
    }
}


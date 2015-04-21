package com.gmail.sacchin.roadbikenavigation;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    private GoogleMap mMap;
    protected Marker latestMarker = null;
    protected Polyline latestPolyline = null;

    protected ExecutorService executorService = Executors.newCachedThreadPool();

    protected DirectionsResponse directionsResponse = null;

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingSuccess = true;

    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
    private LocationRequest locationRequest;
    private Location location;
    private RoadbikeNavigationApplication roadbikeNavigationApplication = new RoadbikeNavigationApplication();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setUpMapIfNeeded();

        Intent intent = getIntent();
        if (intent != null) {
            String message = intent.getStringExtra("returnMessage");
            if (message != null) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }

//        StepModel step = new Select().from(StepModel.class).orderBy("id desc").executeSingle();
//        int id = 1;
//        if(step != null){
//            id = step.id + 1;
//        }
//
//        StepModel stepModel = new StepModel();
//        stepModel.id = id;
//        stepModel.routeId = 1;
//        stepModel.startTime = System.currentTimeMillis();
//        stepModel.endTime = System.currentTimeMillis();
//        stepModel.save();
//
//        stepModel = new StepModel();
//        stepModel.id = id + 1;
//        stepModel.routeId = 1;
//        stepModel.startTime = System.currentTimeMillis();
//        stepModel.endTime = System.currentTimeMillis();
//        stepModel.save();



//
//        route = new Select().from(RouteModel.class).orderBy("id asc").executeSingle();
//        Toast.makeText(this, route.toString(), Toast.LENGTH_SHORT).show();
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
                MapCameraUtil.moveCamera(mMap, new LatLng(35.681106, 139.766928));
                MapCameraUtil.zoomCamera(mMap, 15.0f);
                mMap.setOnMapLongClickListener(new OnCandidateSelectedListener(this));
            }
        }
    }

    public void onDestinationSelected(LatLng point) {
        if (latestMarker != null) {
            latestMarker.remove();
            latestMarker = null;
        }
        if (latestPolyline != null) {
            latestPolyline.remove();
            latestPolyline = null;
        }

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(100);

        MarkerOptions options = new MarkerOptions();
        options.position(point);
        latestMarker = mMap.addMarker(options);

        LatLng destination = latestMarker.getPosition();
        LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());

        final Handler handler = new Handler();
        executorService.execute(
                new RouteDownloader(destination, origin, handler, this));

        mMap.setOnMarkerClickListener(new OnDestinationSelectedListener(this));
    }

    public void onReceiveRoute(String jsonString) {
        Gson gson = new Gson();
        directionsResponse = gson.fromJson(jsonString, DirectionsResponse.class);

        for (Route route : directionsResponse.getRoutes()) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.width(10).color(Color.BLUE);

            for (Leg leg : route.getLegs()) {
                for (Step step : leg.getSteps()) {
                    List<LatLng> polyline = step.getPolyline().getPoints();
                    for (int i = 0; i < polyline.size() - 1; i++) {
                        polylineOptions.add(polyline.get(i), polyline.get(i + 1));
                    }
                }
            }

            latestPolyline = mMap.addPolyline(polylineOptions);

            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(route.getBounds().getLatLngBounds(), 15));
        }
    }

    public void onStartNavigation() {
        if(directionsResponse == null){
            Toast.makeText(this, "Can't start navigation!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Start navigation!", Toast.LENGTH_SHORT).show();

        RouteModel routeModel = new RouteModel();
        routeModel.id = DatabaseUtil.getMaxId(RouteModel.class);
        routeModel.startTime = System.currentTimeMillis();
        routeModel.endTime = 0;
        routeModel.save();


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }











    @Override
    protected void onStart() {
        super.onStart();
        if (mResolvingSuccess) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        fusedLocationProviderApi.removeLocationUpdates(mGoogleApiClient, MapsActivity.this);
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location currentLocation = fusedLocationProviderApi.getLastLocation(mGoogleApiClient);
        if (currentLocation != null && currentLocation.getTime() > 20000) {
            location = currentLocation;
            MapCameraUtil.moveCamera(mMap, new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        GeoPointModel geoPointModel = new GeoPointModel();
        geoPointModel.stepId = 1;
        geoPointModel.time = System.currentTimeMillis();
        geoPointModel.lat = location.getLatitude();
        geoPointModel.lng = location.getLongitude();

        Toast.makeText(this, geoPointModel.toString(), Toast.LENGTH_SHORT).show();
        MapCameraUtil.moveCamera(mMap, new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            Log.d("onConnectionFailed","ConnectionResult has resolution but don't resolve an error");
        } else {
            mResolvingSuccess = false;
            Log.d("onConnectionFailed","connection error is occurred");
        }
    }
}

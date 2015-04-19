package com.gmail.sacchin.roadbikenavigation;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);

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
            }
        }
    }

    public void destinationSelected(LatLng point) {
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
        Log.d("onReceiveRoute", jsonString);

        Gson gson = new Gson();
        DirectionsResponse directionsResponse = gson.fromJson(jsonString, DirectionsResponse.class);

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
        if(directionsResponse != null){
            Toast.makeText(this, "Can't start navigation!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Can't start navigation!", Toast.LENGTH_SHORT).show();

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
//            textLog += "Accuracy="+ String.valueOf(location.getAccuracy())+"\n";
//            textLog += "Altitude="+ String.valueOf(location.getAltitude())+"\n";
//            textLog += "Time="+ String.valueOf(location.getTime())+"\n";
//            textLog += "Speed="+ String.valueOf(location.getSpeed())+"\n";
//            textLog += "Bearing="+ String.valueOf(location.getBearing())+"\n";
            Toast.makeText(this, String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();
            MapCameraUtil.moveCamera(mMap, new LatLng(location.getLatitude(), location.getLongitude()));
        }
        fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
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

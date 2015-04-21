package com.gmail.sacchin.roadbikenavigation;

import android.content.Intent;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapsActivity extends FragmentActivity implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    private GoogleMap mMap;
    protected Marker latestMarker = null;
    protected Polyline latestPolyline = null;

    protected ExecutorService executorService = Executors.newCachedThreadPool();
    protected FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;


    protected OnCandidateSelectedListener onCandidateSelectedListener = null;
    private boolean mResolvingSuccess = true;

    private NavigationLogic navigationLogic = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        navigationLogic = new NavigationLogic();
        navigationLogic.mGoogleApiClient = new GoogleApiClient.Builder(this)
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
                onCandidateSelectedListener = new OnCandidateSelectedListener(this);
                mMap.setOnMapLongClickListener(onCandidateSelectedListener);
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
        LatLng origin = new LatLng(navigationLogic.location.getLatitude(), navigationLogic.location.getLongitude());

        final Handler handler = new Handler();
        executorService.execute(
                new RouteDownloader(destination, origin, handler, this));

        mMap.setOnMarkerClickListener(new OnDestinationSelectedListener(this));
    }

    public void onReceiveRoute(String jsonString) {
        navigationLogic.setDirectionsResult(jsonString);
        for (PolylineOptions polylineOptions : navigationLogic.createPolylineOptions()) {
            latestPolyline = mMap.addPolyline(polylineOptions);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(navigationLogic.getLatLngBounds(), 15));
    }

    public void onStartNavigation() {
        if(navigationLogic.startNavigation()){
            Toast.makeText(this, "Start navigation!", Toast.LENGTH_SHORT).show();

            onCandidateSelectedListener.start();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(1000);
            fusedLocationProviderApi.requestLocationUpdates(navigationLogic.mGoogleApiClient, locationRequest, this);
        }else{
            Toast.makeText(this, "Can't start navigation!", Toast.LENGTH_SHORT).show();
        }
    }

    public void forwardStep(){
        navigationLogic.forwardStep();
        Toast.makeText(this, "forward step", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mResolvingSuccess) {
            navigationLogic.mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        fusedLocationProviderApi.removeLocationUpdates(navigationLogic.mGoogleApiClient, MapsActivity.this);
        navigationLogic.mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location currentLocation = fusedLocationProviderApi.getLastLocation(navigationLogic.mGoogleApiClient);
        if (currentLocation != null && currentLocation.getTime() > 20000) {
            navigationLogic.location = currentLocation;
            MapCameraUtil.moveCamera(mMap, new LatLng(navigationLogic.location.getLatitude(), navigationLogic.location.getLongitude()));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        navigationLogic.onLocationChanged(location);
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

package com.gmail.sacchin.roadbikenavigation;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Camera;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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

        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        Location myLocate = locationManager.getLastKnownLocation("gps");
        LatLng latLng = new LatLng(myLocate.getLatitude(), myLocate.getLongitude());

        float zoom = 15.0f; //ズームレベル
        float tilt = 0.0f; // 0.0 - 90.0  //チルトアングル
        float bearing = 0.0f; //向き

        CameraPosition pos = new CameraPosition(latLng, zoom, tilt, bearing);
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(pos);
        mMap.moveCamera(camera);

        final Context context = getBaseContext();

        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                String posinfo = "clickpos\n" + "latitude=" + point.latitude + ", longitude=" + point.longitude;
                Toast.makeText(getApplicationContext(), posinfo, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("returnMessage", "hogehoge");
                PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

                Notification n = new NotificationCompat.Builder(context)
                        .setContentTitle("次の交差点が近づきました")
                        .setContentText(posinfo)
                        .setSmallIcon(R.drawable.ic_plusone_medium_off_client)
                        .setContentIntent(pIntent)
                        .addAction(R.drawable.ic_plusone_medium_off_client, "通過", pIntent)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(0, n);
            }

        });
    }
}

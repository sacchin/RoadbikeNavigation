package com.gmail.sacchin.roadbikenavigation;

import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sacchin on 2015/04/11.
 */
public class RouteDownloader implements Runnable {
    protected final String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    protected Handler handler = null;
    protected LatLng destination = null;
    protected LatLng origin = null;
    protected MapsActivity activity = null;

    public RouteDownloader(LatLng destination, LatLng origin, Handler handler, MapsActivity activity){
        this.handler = handler;
        this.destination = destination;
        this.origin = origin;
        this.activity = activity;
    }

    protected String getRoute(LatLng destination, LatLng origin) {
        String urlString = buildURL(destination, origin);
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            return InputStreamToString(con.getInputStream());
        } catch(MalformedURLException ex) {
            Log.e("RouteDownloader", ex.toString());
        } catch(IOException ex) {
            Log.e("RouteDownloader", ex.toString());
        }
        return "";
    }

    protected String buildURL(LatLng destination, LatLng origin) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DIRECTIONS_URL);
        stringBuilder.append("origin=");
        stringBuilder.append(origin.latitude + "," + origin.longitude);
        stringBuilder.append("&destination=");
        stringBuilder.append(destination.latitude + "," + destination.longitude);
        stringBuilder.append("&sensor=false&mode=driving&units=metric&region=jp");

        return stringBuilder.toString();
    }

    static String InputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
    @Override
    public void run() {
        final String jsonRoute = getRoute(destination, origin);

        handler.post(new Runnable() {
            public void run() {
                activity.onReceiveRoute(jsonRoute);
            }
        });
    }
}


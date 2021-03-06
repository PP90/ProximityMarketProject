package com.example.p3.myapp.ActivitiesPackage;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

/**
 * Created by p3 on 17/05/2016.
 */
/*
* This GPSClass class exploits the Google API in order to get the coarser and the finer location of GPS.
* It works with API version lower or equal than 21 and bigger or equal than 16.
* */
public class GPSClass implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, ResultCallback<Status>,LocationListener {

        protected static final String TAG = "GPSClass";
        protected GoogleApiClient mGoogleApiClient; // Provides the entry point to Google Play services.
        public Location mCurrentLoc; //this is the last location valid
        private Context context;
        private double latitude;
        private double longitude;
        private final int MIN_SDK_VERSION=16;//TODO: GET IN SOMEWAY FROM THE MANIFEST
        private final int FASTEST_INTERVAL_MS=5000;
        private final int INTERVAL_MS=10000;

    public GPSClass (Context context){
        this.context=context;
    }
    protected synchronized void buildGoogleApiClient() {
        if ( Build.VERSION.SDK_INT >= MIN_SDK_VERSION &&
                ContextCompat.checkSelfPermission( this.context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            Log.i(TAG, "build GoogleApiClient is not possible");
            return  ;
        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder( this.context).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
            //createLocationRequest();

        }
    }

    /*
    * These three following methods are called when the activity of the Android application
     * calls the methods OnCreate, OnStart and OnStop respectively.
    * */
public void onCreateActivity(Context context){
    buildGoogleApiClient();
}

    public void onStartActivity(){
        mGoogleApiClient.connect();
    }

    public void onStopActivity(){
        if (mGoogleApiClient.isConnected())  mGoogleApiClient.disconnect();
    }


    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL_MS);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL_MS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        return mLocationRequest;
    }

/*
* This function is not used, but it was not removed because future works.
* */

    public void positionOnDemand(){

       LocationRequest lr = createLocationRequest();

    }

/*
* First to connect to Google API client, some check permissions has been performed.
* After that the position is set to latitude and longitude respectively.
*
* */
    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission( this.context, Manifest.permission.ACCESS_FINE_LOCATION) //ActivityCompat -> use ContextCompat as this has compatibility with older API levels
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission( this.context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            Log.i(TAG, "Connection to GoogleApiClient is not possible, you needs permissions");
            return;
        }

        Log.i(TAG, "Connected to GoogleApiClient");
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,LR, (com.google.android.gms.location.LocationListener) this);
        mCurrentLoc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLoc != null) {
            latitude=mCurrentLoc.getLatitude();
            longitude=+mCurrentLoc.getLongitude();
         //   Toast.makeText(context, "Your position is: LAT: "+latitude+" LONG: "+longitude, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText( this.context, "No location detected. Please Turn on your GPS.", Toast.LENGTH_LONG).show();
        }
        return;
    }

    /*
    * Get functions
    * */
    double getLatitude(){        return latitude;    }
    double getLongitude(){        return longitude;    }
/*
* onConnected() will be called again automatically when the service reconnects
* */

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude=location.getLongitude();
        latitude=location.getLatitude();
        Log.i(TAG,"Position changed. Latitude: "+latitude+" Longitude: "+longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onResult(Status status) {

    }
}

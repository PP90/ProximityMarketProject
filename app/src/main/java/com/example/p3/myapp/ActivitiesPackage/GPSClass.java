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
import android.widget.TextView;
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
public class GPSClass implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, ResultCallback<Status>,LocationListener {

        protected static final String TAG = "MainActivityGps";
        protected GoogleApiClient mGoogleApiClient; // Provides the entry point to Google Play services.
        public Location mCurrentLoc; //this is the last location valid
        private Context context;
        double latitude;
        double longitude;

    public GPSClass (Context context){
        this.context=context;
    }
    protected synchronized void buildGoogleApiClient() {
        if ( Build.VERSION.SDK_INT >= 16 &&
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
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000); // 5 seconds ogni update = molto veloce
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        return mLocationRequest;
    }


    public void positionOnDemand(){//TO BE IMPLEMENTED ????

       LocationRequest LR = createLocationRequest();
    }
    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission( this.context, Manifest.permission.ACCESS_FINE_LOCATION) //ActivityCompat -> use ContextCompat as this has compatibility with older API levels
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission( this.context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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
            Toast.makeText( this.context, "No location detected", Toast.LENGTH_LONG).show();
        }
        return;
    }

    double getLatitude(){
        return latitude;
    }

    double getLongitude(){
        return longitude;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        // onConnected() will be called again automatically when the service reconnects
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

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

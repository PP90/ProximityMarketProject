package com.example.p3.myapp.ActivitiesPackage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p3.myapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;


public class no_MainActivityGps extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, ResultCallback<Status>,LocationListener {

    protected static final String TAG = "no_MainActivityGps";


    protected GoogleApiClient mGoogleApiClient; // Provides the entry point to Google Play services.
    public Location mCurrentLoc; //this is the last location valid
    private TextView tvlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_activity_main_activity_gps);
        tvlocation = (TextView) findViewById(R.id.tvLocation);
        buildGoogleApiClient();
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        if ( Build.VERSION.SDK_INT >= 16 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            Log.i(TAG, "build GoogleApiClient is not possible");
            return  ;
        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
            //createLocationRequest();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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
    private LocationRequest LR = createLocationRequest();
    /**
     @Override
     protected void onPause() {
     super.onPause();
     stopLocationUpdates();
     }
     protected void stopLocationUpdates() {
     LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
     }
     **/

    // Runs when a GoogleApiClient object successfully connects.
    @Override
    public void onConnected(Bundle connectionHint) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) //ActivityCompat -> use ContextCompat as this has compatibility with older API levels
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
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
            tvlocation.setText("Location: " + "Latitude "
                    + String.valueOf(mCurrentLoc.getLatitude()) + " , Longitude "
                    + String.valueOf(mCurrentLoc.getLongitude()));



        }else {
            Toast.makeText(this, "No location detected", Toast.LENGTH_LONG).show();
        }
        return;
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");
        // onConnected() will be called again automatically when the service reconnects
        mGoogleApiClient.connect();
    }

    /**
     * public void setmGPSButton(){
     try {
     // quello che serve, tipo insert AD, o search AD...quindi inviare distanza al server
     //......
     } catch (SecurityException securityException) {
     // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
     logSecurityException(securityException);
     }
     }
     private void logSecurityException(SecurityException securityException) {
     Log.e(TAG, "Invalid location permission. " +
     "You need to use ACCESS_FINE_LOCATION", securityException);
     }
     **/

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
    public void onResult(@NonNull Status status) {

    }
}

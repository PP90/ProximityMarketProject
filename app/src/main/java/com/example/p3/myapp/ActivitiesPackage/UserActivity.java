package com.example.p3.myapp.ActivitiesPackage;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.p3.myapp.ConnectionToServer;
import com.example.p3.myapp.R;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    private String distance;
    private GPSClass gps;
    private int nNearAds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Button addNewAd=(Button) findViewById(R.id.button_add_ads);
        Button seeMyAds=(Button) findViewById(R.id.button_see_my_ads);
        Button seeNearAds=(Button) findViewById(R.id.buttonSeeNearAds);
        gps=new GPSClass(this);
        gps.onCreateActivity(this);
        seeNearAds.setOnClickListener(this);
        addNewAd.setOnClickListener(this);
        seeMyAds.setOnClickListener(this);


        SeekBar seekBar=(SeekBar)findViewById(R.id.distance);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("UserActivity","Progress: "+progress);
                TextView meters=(TextView) findViewById(R.id.meters);
                distance=String.valueOf(progress);
                meters.setText(distance);
                if(progress==5000) Log.i("UserActivity","Progress max");


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        gps.onStartActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gps.onStopActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_add_ads:
                Intent goToAddNewAddActivity=new Intent(this, InsertAd.class);
                Log.i("UserActivity", "Add new ad button pressed");
                startActivity(goToAddNewAddActivity);
                break;

            case R.id.buttonSeeNearAds:
                Intent goToSeeNearAdActivity=new Intent(this, SeeNearAds.class);
                Log.i("UserActivity", "See near ad button pressed");
                //The result of the query must be passed to the next activity
                goToSeeNearAdActivity.putExtra("searchResult",9); // questa non va più

                String latitude=String.valueOf(gps.getLatitude());
                String longitude=String.valueOf(gps.getLongitude());
                SearchNearAds searchNearAds=new SearchNearAds();
                searchNearAds.execute(latitude,longitude,distance);
                nNearAds=6;
                goToSeeNearAdActivity.putExtra("searchResult",nNearAds);
                startActivity(goToSeeNearAdActivity);
                break;

            case R.id.button_see_my_ads:
                Intent goToSeeMyAdActivity=new Intent(this, MyAds.class);
                Log.i("UserActivity", "My ads button pressed");
                startActivity(goToSeeMyAdActivity);
                break;

            default:
                break;
        }
    }
// TODO : e questa dovrebbe per caso preparare i file che ricevera la see near ads?
    private class SearchNearAds extends AsyncTask<String, Void, Integer>{


        @Override
        protected Integer doInBackground(String... params) {
            ConnectionToServer connToServer=new ConnectionToServer();
            connToServer.connectToTheServer(true, true);
            String seeNearAdsString=connToServer.getStringtoSendToServer("AD,SEE_NEAR,"+UserStatus.username, params);
            if(connToServer.sendToServer(seeNearAdsString)==ConnectionToServer.OK){


            }
            //
            return null;
        }
    }
}

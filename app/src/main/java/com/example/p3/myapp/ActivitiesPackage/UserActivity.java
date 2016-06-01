package com.example.p3.myapp.ActivitiesPackage;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.p3.myapp.ConnectionToServer;
import com.example.p3.myapp.R;

public class UserActivity extends AppCompatActivity implements View.OnClickListener,SearchView.OnQueryTextListener {
    private String distance;
    private GPSClass gps;
    private int nNearAds;
    public String searchV; // questa è la stringa del campo di ricerca che passo
    public EditText searchVV; //questo è l'edittext d'appoggio
    public SearchView searchView; // questa è la variabile in cui l'utente inserisce il dato

    //TODO: These below four message must be put in the Format message library
    private final String BUY="buy";
    private final String SELL="sell";
    private final String EXCHANGE="exchange";
    private final String DONATE="donate";
    private final String TAG="UserActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        searchView= (SearchView) findViewById(R.id.searchView_ads);
        searchV="";
        searchVV=null;
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
    public boolean onQueryTextSubmit(String query) {
        // User pressed the search button
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text in the search button
        return false;
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
        switch (v.getId()) {
            case R.id.button_add_ads:
                Intent goToAddNewAddActivity = new Intent(this, InsertAd.class);
                Log.i("UserActivity", "Add new ad button pressed");
                startActivity(goToAddNewAddActivity);
                break;

            case R.id.buttonSeeNearAds:
                Intent goToSeeNearAdActivity = new Intent(this, SeeNearAds.class);
                Log.i("UserActivity", "See near ad button pressed");
                //The result of the query must be passed to the next activity
                goToSeeNearAdActivity.putExtra("searchResult", 9);

                String latitude = String.valueOf(gps.getLatitude());
                String longitude = String.valueOf(gps.getLongitude());
                SearchNearAds searchNearAds = new SearchNearAds();
                UserStatus.username="pippo@pippo.it";
                Log.i(TAG,"username is: "+UserStatus.username);
                searchNearAds.execute(UserStatus.username, getTypology(), latitude, longitude, getDistance());
                startActivity(goToSeeNearAdActivity);
                break;

            case R.id.button_see_my_ads:
                Intent goToSeeMyAdActivity = new Intent(this, MyAds.class);
                Log.i("UserActivity", "My ads button pressed");
                startActivity(goToSeeMyAdActivity);
                break;

            default:
                break;
        }
    }

    private String getDistance(){
        if(distance==null)distance="1000";
        Log.i(TAG,"Distance is: "+distance);
        return distance;
    }
    private String getTypology(){
        RadioButton buy=(RadioButton) findViewById(R.id.radioButton_buy);
        if(buy.isChecked()) return BUY;

        RadioButton sell=(RadioButton) findViewById(R.id.radioButton_sell);
        if(sell.isChecked()) return SELL;

        RadioButton exchange=(RadioButton) findViewById(R.id.radioButton_exchange);
        if(exchange.isChecked()) return EXCHANGE;

        RadioButton donate=(RadioButton) findViewById(R.id.radioButton_donate);
        if(donate.isChecked()) return DONATE;

        return "Generic";
    }



// TODO : e questa dovrebbe per caso preparare i file che ricevera la see near ads?
    private class SearchNearAds extends AsyncTask<String, Void, Integer>{


        @Override
        protected Integer doInBackground(String... params) {
            ConnectionToServer connToServer=new ConnectionToServer();
            connToServer.connectToTheServer(true, true);
            String seeNearAdsString=connToServer.getStringtoSendToServer("AD,SEE_NEAR", params);
            if(connToServer.sendToServer(seeNearAdsString)==ConnectionToServer.OK){


            }
            //
            return null;
        }
    }
}

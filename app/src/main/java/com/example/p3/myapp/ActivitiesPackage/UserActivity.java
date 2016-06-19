package com.example.p3.myapp.ActivitiesPackage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p3.myapp.ConnectionToServer;
import com.example.p3.myapp.R;
import java.util.ArrayList;

import EntityClasses.FormatMessage;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    private String distance;
    private GPSClass gps;

    private final String TAG="UserActivity";
    private final String DEFAULT_DISTANCE="1000"; //Expressed in meters
    private ArrayList<String> adList;

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        Button addNewAd=(Button) findViewById(R.id.button_add_ads);
        Button seeNearAds=(Button) findViewById(R.id.buttonSeeNearAds);
        SeekBar seekBar=(SeekBar)findViewById(R.id.distance);

        gps=new GPSClass(this);
        gps.onCreateActivity(this);
        seeNearAds.setOnClickListener(this);
        addNewAd.setOnClickListener(this);

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
        switch (v.getId()) {
            case R.id.button_add_ads:
                Intent goToAddNewAddActivity = new Intent(this, InsertAd.class);
                Log.i("UserActivity", "Add new ad button pressed");
                startActivity(goToAddNewAddActivity);
                break;

            case R.id.buttonSeeNearAds:
                String latitude = String.valueOf(gps.getLatitude());
                String longitude = String.valueOf(gps.getLongitude());
                Log.i(TAG,"username is: "+pref.getString("username", null));

                SearchNearAds searchNearAds = new SearchNearAds();
                    if(searchNearAds.getStatus() == AsyncTask.Status.PENDING) {//In order
                     searchNearAds.execute(pref.getString("username", null), getTypology(),
                            getKeywords(), latitude, longitude, getDistance());
                    }
                break;

            default:
                break;
                }
        }

    private String getKeywords(){
        EditText keyword=(EditText) findViewById(R.id.searchView_ads);
        String kw=keyword.getText().toString();
       if(kw!=null)  return kw;
        else return "";
    }

    private String getDistance(){
        if(distance==null)distance=DEFAULT_DISTANCE;
        Log.i(TAG,"Distance is: "+distance);
        return distance;
    }

    private String getTypology(){
        RadioButton buy=(RadioButton) findViewById(R.id.radioButton_buy);
        if(buy.isChecked()) return FormatMessage.BUY;

        RadioButton sell=(RadioButton) findViewById(R.id.radioButton_sell);
        if(sell.isChecked()) return FormatMessage.SELL;

        RadioButton exchange=(RadioButton) findViewById(R.id.radioButton_exchange);
        if(exchange.isChecked()) return FormatMessage.EXCHANGE;

        RadioButton donate=(RadioButton) findViewById(R.id.radioButton_donate);
        if(donate.isChecked()) return FormatMessage.DONATE;

        return "Generic";
    }


    private class SearchNearAds extends AsyncTask<String, Void, Integer>{
        private final int OK_RESULT=100;
        private final int NO_RESULT=-100;
        @Override
        protected Integer doInBackground(String... params) {
            ConnectionToServer connToServer=new ConnectionToServer();
            connToServer.connectToTheServer(true, true);
            String seeNearAdsString=connToServer.getStringtoSendToServer("AD,SEE_NEAR", params);

            if(connToServer.sendToServer(seeNearAdsString)==ConnectionToServer.OK){
             adList=connToServer.receiveFromServer();
                connToServer.closeConnection();
               return OK_RESULT;

            }else {
                connToServer.closeConnection();
                return NO_RESULT;
            }
        }

        protected void onPostExecute(Integer result) {
            //TODO: Check if the adList is empty or null
            if(result==OK_RESULT) {
                Intent goToSeeNearAdActivity = new Intent(getBaseContext(), SeeNearAds.class);
                goToSeeNearAdActivity.putExtra("numberOfAds", adList.size()/SeeNearAds.N_PARAMS_AD);
                goToSeeNearAdActivity.putStringArrayListExtra("adList",adList);
                startActivity(goToSeeNearAdActivity);
            }
            else if(result==NO_RESULT) Toast.makeText(getApplicationContext(), "Error connection", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.p3.myapp.ActivitiesPackage;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import EntityClasses.FormatMessage;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    private String distance;
    private GPSClass gps;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference childRef;
    private StorageReference imageRef;

    private final String TAG="UserActivity";
    private final String DEFAULT_DISTANCE="1000"; //Expressed in meters
    private ArrayList<String> adList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Button addNewAd=(Button) findViewById(R.id.button_add_ads);
        Button seeNearAds=(Button) findViewById(R.id.buttonSeeNearAds);
        SeekBar seekBar=(SeekBar)findViewById(R.id.distance);

        gps=new GPSClass(this);
        gps.onCreateActivity(this);
        seeNearAds.setOnClickListener(this);
        addNewAd.setOnClickListener(this);

        storage=FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(InsertAd.STORAGE_REF);
        childRef=storageRef.child(InsertAd.CHILD_REF);


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
                ReceiveImageAsyncTask riat=new ReceiveImageAsyncTask();
                riat.execute("https://firebasestorage.googleapis.com/v0/b/proximitymarketdb.appspot.com/o/images%2F2016-06-14+18%3A02%3A59?alt=media&token=fd1c885d-0fcc-496e-af05-59194d033ff9");
                Intent goToSeeNearAdActivity = new Intent(this, SeeNearAds.class);
                Log.i("UserActivity", "See near ad button pressed");
                String latitude = String.valueOf(gps.getLatitude());
                String longitude = String.valueOf(gps.getLongitude());
                SearchNearAds searchNearAds = new SearchNearAds();
                UserStatus.username="pippo@pippo.it";
                Log.i(TAG,"username is: "+UserStatus.username);
                searchNearAds.execute(UserStatus.username, getTypology(), getKeywords(), latitude, longitude, getDistance());
                //The result of the query must be passed to the next activity

                goToSeeNearAdActivity.putExtra("searchResult", 9);
                goToSeeNearAdActivity.putStringArrayListExtra("adList",getAdList());//HARDCODED
                startActivity(goToSeeNearAdActivity);
                break;

            default:
                break;
        }
    }




    private ArrayList<String> getAdList(){
        ArrayList<String> adList=new ArrayList<>();
        adList.add(0,"Description1,buy,10,20,30,40");
        return adList;

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


    //TODO: This task must be tested with the timestamp filename
    private class ReceiveImageAsyncTask extends AsyncTask<String, Void, Integer>{

        private final String TAG="ReceiverImage";

        @Override
        protected Integer doInBackground(String... params) {


                try {
                    Bitmap image=Picasso.with(getApplicationContext()).load(params[0]).get();
                    Log.i(TAG,"bytes: "+image.getByteCount());
                    return 1;
                } catch (IOException e) {
                    e.printStackTrace();
                    return -1;
                }

        }
        protected void onPostExecute(Integer a) {

        }
        //TODO: implement onPostExecuteMethod in which a message is shown to the user
    }


    private class SearchNearAds extends AsyncTask<String, Void, Integer>{

        @Override
        protected Integer doInBackground(String... params) {
            ConnectionToServer connToServer=new ConnectionToServer();
            connToServer.connectToTheServer(true, true);
            String seeNearAdsString=connToServer.getStringtoSendToServer("AD,SEE_NEAR", params);
            if(connToServer.sendToServer(seeNearAdsString)==ConnectionToServer.OK){
               Log.i(TAG,connToServer.receiveFromServer().toString());
                return 1;
            }else return -1;

        }
        protected void onPostExecute(Integer result) {
            if(result==1)  Toast.makeText(getApplicationContext(), "Connection established with the server", Toast.LENGTH_SHORT).show();
            else  Toast.makeText(getApplicationContext(), "Error connection", Toast.LENGTH_SHORT).show();
        }
    }
}

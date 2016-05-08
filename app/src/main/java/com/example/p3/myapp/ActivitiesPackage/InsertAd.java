package com.example.p3.myapp.ActivitiesPackage;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.p3.myapp.ConnectionToServer;
import com.example.p3.myapp.R;

public class InsertAd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_ad);
    }




    private class RegisterNewAd extends AsyncTask<String,Void,Integer>{


        @Override
        protected Integer doInBackground(String... params) {
            //1. The connection to the server must be established
            //2. The fields from the activity must be get
            //3. The data must be in the correct format
            //4. The image must be added, too and then compressed
            //5. Send all stuff to the server
            ConnectionToServer connectionToServer=new ConnectionToServer();
            connectionToServer.connectToTheServer(true, true);
            connectionToServer.sendToServer("AD, new_AD, ... and the other fileds");

            return -1;
        }
    }
}

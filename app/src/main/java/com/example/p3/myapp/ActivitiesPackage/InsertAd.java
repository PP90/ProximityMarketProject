package com.example.p3.myapp.ActivitiesPackage;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.p3.myapp.ConnectionToServer;
import com.example.p3.myapp.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class InsertAd extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_ad);
        Button sendAd=(Button) findViewById(R.id.buttonSendAd);
        sendAd.setOnClickListener(this);
    }




    private String getAdTitle(){
        EditText titleEditText=(EditText) findViewById(R.id.EditTextTitle);
         return titleEditText.getText().toString();
    }

    private String getAdDescription(){
        EditText descriptionEditText=(EditText) findViewById(R.id.DescriptionEditText);
        return descriptionEditText.getText().toString();
    }

    @Override
    public void onClick(View v) {
        int idView=v.getId();
        switch(idView){
            case R.id.buttonSendAd:
                RegisterNewAd registerNewAd=new RegisterNewAd();

                registerNewAd.execute(getAdTitle(),  getAdDescription(),imageBase64String);
                break;

        }
    }


    private class RegisterNewAd extends AsyncTask<String,Void,Integer>{


        @Override
        protected Integer doInBackground(String... params) {


            //3. The data must be in the correct format
            //4. The image must be added, too and then compressed
            //5. Send all stuff to the server
            //1. The connection to the server must be established
            ConnectionToServer connectionToServer=new ConnectionToServer();
            connectionToServer.connectToTheServer(true, true);
            //2. The fields from the activity must be get
            connectionToServer.sendToServer("AD, new_AD, ... and the other fileds");

            return -1;
        }
    }
}

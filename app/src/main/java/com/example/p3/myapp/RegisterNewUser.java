package com.example.p3.myapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class RegisterNewUser extends AppCompatActivity implements View.OnClickListener {
    static final String TAG="RegisterNewUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);
        Button b = (Button) findViewById(R.id.new_user_button);
        b.setOnClickListener(this);
        Log.i(TAG, "onCreate Method");
    }

    @Override
    public void onClick(View v) {
        AddNewUser addNewUser= new AddNewUser();
        if(addNewUser.getStatus() == AsyncTask.Status.PENDING){
            addNewUser.execute("gesu", "DiNazgul","a","a","1");
        }
    }

    private class AddNewUser extends AsyncTask<String, Void, Integer>{
        private final String INSERT_USER="INSERT_USER";
        @Override
        protected Integer doInBackground(String... params) {
            ArrayList<String> dataFromServer;
            ConnectionToServer connectionToServer=new ConnectionToServer();
            String insertUserString=connectionToServer.getStringtoSendToServer(INSERT_USER, params);
            connectionToServer.connectToTheServer();
            connectionToServer.sendToServer(insertUserString);
            int connServerResult=connectionToServer.sendToServer(insertUserString);
            if(connServerResult==ConnectionToServer.OK) {
                // System.out.println("Data sent correctly");
                dataFromServer = connectionToServer.receiveFromServer();
                //   System.out.println("Data received correctly"+dataFromServer.toString());
            }else return connServerResult;
            connectionToServer.closeConnection();
            if (dataFromServer.get(1).equals("OK")) return 1;
            else return -1;

        }
    }
}

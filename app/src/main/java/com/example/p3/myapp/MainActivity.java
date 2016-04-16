package com.example.p3.myapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b = (Button) findViewById(R.id.async_button);
        b.setOnClickListener(this);
        Log.i(TAG, "onCreate Method");
    }

    @Override
    public void onClick(View v) {

        LoginTask loginTask= new LoginTask();
        if(loginTask.getStatus() == AsyncTask.Status.PENDING){
            loginTask.execute("pippo", "pippo");
        }


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }


    private class LoginTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... params) {
                ArrayList<String> dataFromServer;
                ConnectionToServer connToServer=new ConnectionToServer();
                String myLoginString=connToServer.getStringtoSendToServer("LOGIN", params);
                 connToServer.connectToTheServer();
                int connServerResult=connToServer.sendToServer(myLoginString);
                if(connServerResult==ConnectionToServer.OK) {
                   // System.out.println("Data sent correctly");
                    dataFromServer = connToServer.receiveFromServer();
                 //   System.out.println("Data received correctly"+dataFromServer.toString());
                }else return connServerResult;
            connToServer.closeConnection();
                if (dataFromServer.get(1).equals("OK")) return 1;
                 else return -1;


        }

        protected void onPostExecute(Integer a) {
            if (a == 1) Toast.makeText(getApplicationContext(), "Login correct", Toast.LENGTH_SHORT).show();
            else if (a == -1) Toast.makeText(getApplicationContext(), "Login incorrect", Toast.LENGTH_SHORT).show();
            else if (a == ConnectionToServer.TIMEOUT_EXCEPTION) Toast.makeText(getApplicationContext(), "The server maybe is down", Toast.LENGTH_SHORT).show();
            else if (a == ConnectionToServer.IO_EXCEPTION) Toast.makeText(getApplicationContext(), "Error during login", Toast.LENGTH_SHORT).show();
        }
    }
}

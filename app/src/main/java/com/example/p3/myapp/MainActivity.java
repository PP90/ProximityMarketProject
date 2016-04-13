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
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b = (Button) findViewById(R.id.async_button);
        b.setOnClickListener(this);
        Log.i(TAG, "onCreate Method");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onClick(View v) {
        new ClientConnection().execute("pippo", "pippo");

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.p3.myapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.p3.myapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    private class ClientConnection extends AsyncTask<String, Void, Integer> {
        private final static String SERVER_NAME = "192.168.0.112";//TO MODIFY EACH TIME
        private final static int PORT = 8080;
        private final static String TAG = "ClientConnection";
        private final String DELIMITS = "[,]";
        private final int TIMEOUT=10000;

        private ArrayList<String> getParsedDataFromBuffer(String dataFromBuffer) {
            ArrayList<String> parsedInput = new ArrayList<>();
            if (dataFromBuffer != null) {

                System.out.println("From the client:" + dataFromBuffer);
                String[] myData = dataFromBuffer.split(DELIMITS);
                parsedInput.addAll(Arrays.asList(myData));
                return parsedInput;

            } else {
                return null;
            }
        }


        private String getMyIP() {
            try {
                return Inet4Address.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return null;
            }

        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... params) {
            //try {
                String username = params[0];
                String pwd = params[1];
                String myLoginString = "LOGIN," + username + "," + pwd;//
                ConnectionToServer connToServer=new ConnectionToServer();
            ArrayList<String> dataFromServer=new ArrayList<>();
                if(connToServer.sendToServer(myLoginString)) {
                    System.out.println("Data sent correctly");
                    dataFromServer = connToServer.receiveFromServer();
                    System.out.println("Data received correctly");
                    System.out.println(dataFromServer.toString());
                }
                connToServer.closeConnection();
                if (dataFromServer.get(1).equals("OK")) {
                    return 1;
                } else {
                    return -1;

                }
            //}
             /*catch (SocketTimeoutException ste){
                ste.printStackTrace();
                return -3;
            } catch (IOException e) {
                e.printStackTrace();
                return -2;
            }*/
        }

        protected void onPostExecute(Integer a) {
            if (a == 1) Toast.makeText(getApplicationContext(), "Login correct", Toast.LENGTH_SHORT).show();
            else if (a == -1) Toast.makeText(getApplicationContext(), "Login incorrect", Toast.LENGTH_SHORT).show();
            else if (a == -2) Toast.makeText(getApplicationContext(), "Error during login", Toast.LENGTH_SHORT).show();
            else if (a == -3) Toast.makeText(getApplicationContext(), "Error during login. Server down", Toast.LENGTH_SHORT).show();
        }
    }
}

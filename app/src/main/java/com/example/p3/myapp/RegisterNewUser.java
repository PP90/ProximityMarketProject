package com.example.p3.myapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class RegisterNewUser extends AppCompatActivity implements View.OnClickListener {
    static final String TAG="RegisterNewUser";
    private final String INSERT_USER="INSERT_USER";
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
            addNewUser.execute("Fava", "vdvd","w","qq","0");
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
            int connServerResult=connectionToServer.sendToServer(insertUserString);
            if(connServerResult==ConnectionToServer.OK) {
                // System.out.println("Data sent correctly");
                dataFromServer = connectionToServer.receiveFromServer();
                //   System.out.println("Data received correctly"+dataFromServer.toString());
            }else return connServerResult;
            connectionToServer.closeConnection();
            if(dataFromServer.get(0).equals(INSERT_USER)) {
                if (dataFromServer.get(1).equals("OK")) return 1;
                else if (dataFromServer.get(1).equals("NO")) return -1;
                else if (dataFromServer.get(1).equals("DUPLICATE")) return -2;
            }
            return -1;
        }

        protected void onPostExecute(Integer a) {
            if (a == 1) Toast.makeText(getApplicationContext(), "Registration goes good", Toast.LENGTH_SHORT).show();
            else if (a == -2) Toast.makeText(getApplicationContext(), "Username already taken", Toast.LENGTH_SHORT).show();
            else if (a == ConnectionToServer.TIMEOUT_EXCEPTION) Toast.makeText(getApplicationContext(), "The server maybe is down", Toast.LENGTH_SHORT).show();
            else if (a == ConnectionToServer.IO_EXCEPTION) Toast.makeText(getApplicationContext(), "Error during login", Toast.LENGTH_SHORT).show();
       }
    }
}

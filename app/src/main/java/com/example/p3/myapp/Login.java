package com.example.p3.myapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.p3.myapp.ActivitiesPackage.RegisterNewUser;

import java.util.ArrayList;

public class Login extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginButton = (Button) findViewById(R.id.button_login);
        loginButton.setOnClickListener(this);
        Button registerButton= (Button) findViewById(R.id.button_registerNewAccount);
        registerButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.button_login){
        LoginTask loginTask= new LoginTask();
        if(loginTask.getStatus() == AsyncTask.Status.PENDING){
            EditText user=(EditText) findViewById(R.id.editText_insertEmailAsUsername);
            EditText pwd=(EditText) findViewById(R.id.editText_insert_password);
            loginTask.execute( user.getText().toString(), pwd.getText().toString());
            }
        }

        if(v.getId()==R.id.button_registerNewAccount){
            Intent i=new Intent(this, RegisterNewUser.class);
            startActivity(i);
        }
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
            Intent goToProfile=new Intent(getBaseContext(), UserActivity.class);

            if (a == 1)  startActivity(goToProfile);
            else if (a == -1) Toast.makeText(getApplicationContext(), "Login incorrect", Toast.LENGTH_SHORT).show();
            else if (a == ConnectionToServer.TIMEOUT_EXCEPTION) Toast.makeText(getApplicationContext(), "The server maybe is down", Toast.LENGTH_SHORT).show();
            else if (a == ConnectionToServer.IO_EXCEPTION) Toast.makeText(getApplicationContext(), "Error during login", Toast.LENGTH_SHORT).show();
        }
    }


}

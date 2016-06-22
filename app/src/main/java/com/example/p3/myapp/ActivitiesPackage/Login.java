package com.example.p3.myapp.ActivitiesPackage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.p3.myapp.ConnectionToServer;
import com.example.p3.myapp.R;

import java.util.ArrayList;
/*
* This class implements the Login activity of the Android application. It shows the edit text
* of username and password. It shows also the button to permit the user to register a new account.
* */
public class Login extends AppCompatActivity implements View.OnClickListener {

    private final String TAG="LOGIN";
    private String uname;
    private String upwd;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();


        Button loginButton = (Button) findViewById(R.id.button_login);
        Button registerButton= (Button) findViewById(R.id.button_registerNewAccount);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    //Check on the email format.
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_login:
                LoginTask loginTask= new LoginTask();
                EditText user=(EditText) findViewById(R.id.editText_insertEmailAsUsername);
                EditText pwd=(EditText) findViewById(R.id.editText_insert_password);
                if(loginTask.getStatus() == AsyncTask.Status.PENDING){
                    // Check e-mail correctness
                    boolean emailOK= isValidEmail(user.getText().toString());
                    if(emailOK){
                        uname=user.getText().toString();
                        upwd=pwd.getText().toString();
                        loginTask.execute(uname,upwd);
                    } else Toast.makeText(getApplicationContext(),"Invalid or null email address",Toast.LENGTH_SHORT).show();

                }
                break;

            case R.id.button_registerNewAccount:
                Intent i2=new Intent(this, RegisterNewUser.class);
                startActivity(i2);
                break;

        }


    }


    /*
    * /With this async task is established a connection to the server.
    * The credentials are sent in order to verify the login correctness.
    * If it goes good, the user can use the application
    * */
    private class LoginTask extends AsyncTask<String, Void, Integer> {

        private final int OK=1;
        private final int NO=-1;
        private final int NO_CONNECTION=7;
        @Override
        protected Integer doInBackground(String... params) {
            ArrayList<String> dataFromServer;

            ConnectionToServer connToServer=new ConnectionToServer();
            String myLoginString=connToServer.getStringtoSendToServer("LOGIN", params);
            boolean serverConn=connToServer.connectToTheServer(true, true);

            if(!serverConn)return NO_CONNECTION;

            int connServerResult=connToServer.sendToServer(myLoginString);

            if(connServerResult==ConnectionToServer.OK) {
                // System.out.println("Data sent correctly");
                dataFromServer = connToServer.receiveFromServer();
                connToServer.closeConnection();//Added.
                //   System.out.println("Data received correctly"+dataFromServer.toString());
            }
            else {
                connToServer.closeConnection();
                return connServerResult;
            }

            if (dataFromServer.get(1).equals("OK")) return OK;
            else return NO;
        }

        protected void onPostExecute(Integer a) {
            Intent goToProfile=new Intent(getBaseContext(), UserActivity.class);

            if (a == OK){
                startActivity(goToProfile);
                editor.putString("username", uname); // Storing uname
                editor.putString("pwd", upwd); // Storing pw
                editor.commit(); // commit changes
            }
            else if (a == NO) Toast.makeText(getApplicationContext(), "Login incorrect", Toast.LENGTH_SHORT).show();
            else if (a == NO_CONNECTION) Toast.makeText(getApplicationContext(), "No internet connection. Turn on the Wi-fi or data mobile", Toast.LENGTH_LONG).show();
            else if (a == ConnectionToServer.TIMEOUT_EXCEPTION | a == ConnectionToServer.IO_EXCEPTION) Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
       }
    }
}

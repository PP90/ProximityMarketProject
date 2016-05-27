package com.example.p3.myapp.ActivitiesPackage;

import android.content.Intent;
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

public class Login extends AppCompatActivity implements View.OnClickListener {

    private final String TAG="LOGIN";
    String uname;
    String upwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // ConnectionToServer.getServerIP();
        Button loginButton = (Button) findViewById(R.id.button_login);
        Button registerButton= (Button) findViewById(R.id.button_registerNewAccount);
        Button goOnButton=(Button) findViewById(R.id.GoOnButton);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        goOnButton.setOnClickListener(this);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    @Override
    public void onClick(View v) {//TODO: when the application comes back to this activity and the login button is pressed with the
        //TODO: correct username and password, the Login doesn't work. BUG TO FIX.
        switch(v.getId()){
            case R.id.button_login:
                LoginTask loginTask= new LoginTask();
                EditText user=(EditText) findViewById(R.id.editText_insertEmailAsUsername);
                EditText pwd=(EditText) findViewById(R.id.editText_insert_password);
                if(loginTask.getStatus() == AsyncTask.Status.PENDING){
                    // added email check - controllo correttezza email aggiunto
                    boolean emailOK= isValidEmail(user.getText().toString());
                    if(emailOK){
                        uname=user.getText().toString();
                        upwd=pwd.getText().toString();
                        loginTask.execute(uname,upwd);
                    } else Toast.makeText(getApplicationContext(),"Invalid or null email address",Toast.LENGTH_SHORT).show();

                }
                break;

            case R.id.button_registerNewAccount:
                Intent i=new Intent(this, RegisterNewUser.class);
                startActivity(i);
                break;

            case R.id.GoOnButton:
                Intent i3=new Intent(this, UserActivity.class);
                startActivity(i3);
                break;
        }


    }

    private class LoginTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            ArrayList<String> dataFromServer;
            ConnectionToServer connToServer=new ConnectionToServer();
            String myLoginString=connToServer.getStringtoSendToServer("LOGIN", params);
            connToServer.connectToTheServer(true, true);
            int connServerResult=connToServer.sendToServer(myLoginString);
            if(connServerResult==ConnectionToServer.OK) {
                // System.out.println("Data sent correctly");
                dataFromServer = connToServer.receiveFromServer();
                connToServer.closeConnection();//Added.
                //   System.out.println("Data received correctly"+dataFromServer.toString());
            }else return connServerResult;
            connToServer.closeConnection();
            if (dataFromServer.get(1).equals("OK")) return 1;
            else return -1;


        }

        protected void onPostExecute(Integer a) {
            Intent goToProfile=new Intent(getBaseContext(), UserActivity.class);

            if (a == 1){
                UserStatus.username=uname;
                UserStatus.isLogged=true; // dove sta isLogged ?
                startActivity(goToProfile);
            }
            else if (a == -1) Toast.makeText(getApplicationContext(), "Login incorrect", Toast.LENGTH_SHORT).show();
            else if (a == ConnectionToServer.TIMEOUT_EXCEPTION) Toast.makeText(getApplicationContext(), "The server maybe is down", Toast.LENGTH_SHORT).show();
            else if (a == ConnectionToServer.IO_EXCEPTION) Toast.makeText(getApplicationContext(), "Error during login", Toast.LENGTH_SHORT).show();
        }
    }
}

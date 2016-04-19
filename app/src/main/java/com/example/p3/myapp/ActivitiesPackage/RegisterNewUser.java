package com.example.p3.myapp.ActivitiesPackage;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.FormatMessage;
import com.example.p3.myapp.ConnectionToServer;
import com.example.p3.myapp.R;


public class RegisterNewUser extends AppCompatActivity implements View.OnClickListener {
    static final String TAG="RegisterNewUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);
        Button b = (Button) findViewById(R.id.button_create_new_account);
        b.setOnClickListener(this);
        Log.i(TAG, "onCreate Method");
    }

    @Override
    public void onClick(View v) {
        System.out.println(FormatMessage.DELETE_USER);
        System.out.println(FormatMessage.DELETE_USER2);
        AddNewUserTask addNewUser= new AddNewUserTask();
        UserEntity userEntity =new UserEntity();

        EditText name=(EditText) findViewById(R.id.editText_insert_name_to_reg);
        EditText surname=(EditText) findViewById(R.id.editText_insert_surname_to_reg);
        EditText username=(EditText) findViewById(R.id.editText_email_here_to_reg);
        EditText pwd=(EditText) findViewById(R.id.editText_insert_pwd_to_reg);
        userEntity.setName(name.getText().toString());
        userEntity.setSurname(surname.getText().toString());
        userEntity.setUsername(username.getText().toString());
        userEntity.setPassword(pwd.getText().toString());
        if(addNewUser.getStatus() == AsyncTask.Status.PENDING){
            addNewUser.execute(userEntity);
        }
    }

    private class AddNewUserTask extends AsyncTask<UserEntity, Void, Integer>{

        private void sendObject(UserEntity u){
            ConnectionToServer connectionToServer=new ConnectionToServer();
            connectionToServer.genericSend("pippo",u);
        }
        @Override
        protected Integer doInBackground(UserEntity... params) {
            sendObject(params[0]);
            /*
            ArrayList<String> dataFromServer;
            ConnectionToServer connectionToServer=new ConnectionToServer();
           // String insertUserString=connectionToServer.getStringtoSendToServer(FormatMessage.INSERT_USER, params);
            connectionToServer.connectToTheServer();
           // int connServerResult=connectionToServer.sendToServer(params);
            int connServerResult=0;
            if(connServerResult==ConnectionToServer.OK) {
                // System.out.println("Data sent correctly");
                dataFromServer = connectionToServer.receiveFromServer();
                //   System.out.println("Data received correctly"+dataFromServer.toString());
            }else return connServerResult;
            connectionToServer.closeConnection();
            if(dataFromServer.get(0).equals(FormatMessage.INSERT_USER)) {
                if (dataFromServer.get(1).equals("OK")) return 1;//these 3 strings should be in format message (?)
                else if (dataFromServer.get(1).equals("NO")) return -1;
                else if (dataFromServer.get(1).equals("DUPLICATE")) return -2;
            }
            return -1;*/
            return -5;
        }

        protected void onPostExecute(Integer a) {
            if (a == 1) Toast.makeText(getApplicationContext(), "Registration goes good", Toast.LENGTH_SHORT).show();
            else if (a == -2) Toast.makeText(getApplicationContext(), "Username already taken", Toast.LENGTH_SHORT).show();
            else if (a == ConnectionToServer.TIMEOUT_EXCEPTION) Toast.makeText(getApplicationContext(), "The server maybe is down", Toast.LENGTH_SHORT).show();
            else if (a == ConnectionToServer.IO_EXCEPTION) Toast.makeText(getApplicationContext(), "Error during login", Toast.LENGTH_SHORT).show();
       }
    }
}

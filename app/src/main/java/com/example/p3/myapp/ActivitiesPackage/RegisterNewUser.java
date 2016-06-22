package com.example.p3.myapp.ActivitiesPackage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import com.example.p3.myapp.ConnectionToServer;
import com.example.p3.myapp.R;

import java.util.ArrayList;

import EntityClasses.FormatMessage;


/*
* This class is the activity relative to the "register new user" functionality.
* The user can insert: username, password, surname, sex and so on. After that it can register  to Proximity Market application.
* */
public class RegisterNewUser extends AppCompatActivity implements View.OnClickListener {
    static final String TAG="RegisterNewUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);
        Button b = (Button) findViewById(R.id.button_create_new_account);
        b.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        AddNewUserTask addNewUser= new AddNewUserTask();

        EditText name=(EditText) findViewById(R.id.editText_insert_name_to_reg);
        EditText surname=(EditText) findViewById(R.id.editText_insert_surname_to_reg);
        EditText username=(EditText) findViewById(R.id.editText_email_here_to_reg);
        EditText pwd=(EditText) findViewById(R.id.editText_insert_pwd_to_reg);
        EditText pwdC=(EditText) findViewById(R.id.editText_confirm_insert_pwd_to_reg);
        RadioButton isMale=(RadioButton) findViewById(R.id.male_radiobutton);

        String sexString;
        if(isMale.isSelected()) sexString="1";
        else sexString="0";

        if(addNewUser.getStatus() == AsyncTask.Status.PENDING){
            boolean emailOK= isValidEmail(username.getText().toString());
            boolean pwdOK= isTheSame(pwd.getText().toString(),pwdC.getText().toString());

            if(emailOK){
                if (pwdOK)addNewUser.execute(username.getText().toString(), pwd.getText().toString(), name.getText().toString(), surname.getText().toString(),sexString );
                else Toast.makeText(getApplicationContext(),"Insert the same password in both boxes",Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getApplicationContext(),"Invalid or null email address",Toast.LENGTH_SHORT).show();
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target))return false;
         else return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public final static boolean isTheSame(String pwd,String pwdC ){
        if (pwd.equalsIgnoreCase(pwdC)) return true;//TODO: it is case sensitive ?
        else return false;
    };


    private class AddNewUserTask extends AsyncTask<String, Void, Integer>{

        private final int NO_CONNECTION=7;
        @Override
        protected Integer doInBackground(String... params) {

            ArrayList<String> dataFromServer;
            ConnectionToServer connectionToServer=new ConnectionToServer();
            String insertUserString=connectionToServer.getStringtoSendToServer(FormatMessage.INSERT_USER, params);
            boolean serverConn= connectionToServer.connectToTheServer(true, true);

            if(!serverConn)return NO_CONNECTION;

            int connServerResult=connectionToServer.sendToServer(insertUserString);

            if(connServerResult==ConnectionToServer.OK){
                dataFromServer = connectionToServer.receiveFromServer();
                connectionToServer.closeConnection();
            }
            else{
                connectionToServer.closeConnection();
                return connServerResult;
            }


            if(dataFromServer.get(0).equals(FormatMessage.INSERT_USER)) {
                if (dataFromServer.get(1).equals("OK")) return 1;
                else if (dataFromServer.get(1).equals("NO")) return -1;
                else if (dataFromServer.get(1).equals("DUPLICATE")) return -2;
            }
            return -1;
        }

        protected void onPostExecute(Integer a) {

            if (a == 1) {
                Toast.makeText(getApplicationContext(), "Congratulation! You are in ProximityMarket!", Toast.LENGTH_SHORT).show();
                finish();
            }
            else if (a == NO_CONNECTION) Toast.makeText(getApplicationContext(), "No internet connection. Turn on the Wi-fi or data mobile", Toast.LENGTH_LONG).show();
            else if (a == -2) Toast.makeText(getApplicationContext(), "Username already exists", Toast.LENGTH_SHORT).show();
            else if (a == ConnectionToServer.TIMEOUT_EXCEPTION | a== ConnectionToServer.IO_EXCEPTION) Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
       }
    }
}

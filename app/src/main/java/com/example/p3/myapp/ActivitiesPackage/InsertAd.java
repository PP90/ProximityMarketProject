package com.example.p3.myapp.ActivitiesPackage;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.p3.myapp.ConnectionToServer;
import com.example.p3.myapp.R;
import java.util.ArrayList;
import java.util.Calendar;

public class InsertAd extends AppCompatActivity implements View.OnClickListener {

   static EditText dateEditFrom;
   static EditText dateEditUntil;
   static boolean fromSelected;
   static boolean untilSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_ad);
        Button sendAd=(Button) findViewById(R.id.buttonSendAd);
        dateEditFrom=(EditText)findViewById(R.id.FromEditText);
        dateEditUntil=(EditText)findViewById(R.id.UntilEditText);
        fromSelected=false;
        untilSelected=false;
        sendAd.setOnClickListener(this);
        dateEditFrom.setOnClickListener(this);
        dateEditFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    fromSelected=true;
                    untilSelected=false;
                    Log.i("TAG", "OneFocus From");
                    showTimePickerDialog(v);
                    showDatePickerDialog(v);
                }
            }
        });
        dateEditUntil.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Log.i("TAG", "OneFrom Until");
                    fromSelected=false;
                    untilSelected=true;
                    showTimePickerDialog(v);
                    showDatePickerDialog(v);
                }
            }
        });
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
                registerNewAd.execute(getAdTitle(),  getAdDescription(), dateEditFrom.getText().toString(), dateEditUntil.getText().toString());
                break;
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private class UploadPhoto extends  AsyncTask<Image, Void, Integer>{


        @Override
        protected Integer doInBackground(Image... params) {
            //TODO: This method must be called when the image must be uploaded
            return null;
        }
    }

    private class RegisterNewAd extends AsyncTask<String,Void,Integer>{
        private final String TAG="RegisterNewAD";

        @Override
        protected Integer doInBackground(String... params) {
            //3. The data must be in the correct format
            //4. The image must be added, too and then compressed. See Upload Image asyncTask
            //5. Send all stuff to the server

            //1. The connection to the server must be established
            ArrayList<String> dataFromServer;
            ConnectionToServer connectionToServer=new ConnectionToServer();
            connectionToServer.connectToTheServer(true, true);
            //2. The fields from the activity must be get
            String newAdString=connectionToServer.getStringtoSendToServer("AD,NEW,", params);
            int resultSend=connectionToServer.sendToServer(newAdString);
            if(resultSend==ConnectionToServer.OK){
                dataFromServer = connectionToServer.receiveFromServer();
                Log.i(TAG, dataFromServer.toString());
            }else return resultSend;
            connectionToServer.closeConnection();
            if (dataFromServer.get(1).equals("OK")) return 1;
            else return -1;
        }


        protected void onPostExecute(Integer a) {

           if (a == 1) Toast.makeText(getApplicationContext(), "Ad insert correctly", Toast.LENGTH_SHORT).show();
            else if (a == -1) Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();

        }
    }

    public static class  DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            if(fromSelected) dateEditFrom.setText(day + "/" + (month + 1) + "/" + year);
            if(untilSelected) dateEditUntil.setText(day + "/" + (month + 1) + "/" + year);
        }
    }


    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if(fromSelected) dateEditFrom.setText(dateEditFrom.getText() + " -" + hourOfDay + ":" + minute);
            if(untilSelected) dateEditUntil.setText(dateEditUntil.getText() + " -" + hourOfDay + ":" + minute);
        }
    }
}

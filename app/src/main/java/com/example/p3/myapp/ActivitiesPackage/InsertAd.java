package com.example.p3.myapp.ActivitiesPackage;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.p3.myapp.ConnectionToServer;
import com.example.p3.myapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class InsertAd extends AppCompatActivity implements View.OnClickListener {

    static EditText dateEditFrom;
    static EditText dateEditUntil;
    static boolean fromSelected;
    static boolean untilSelected;
    final String OLD_FORMAT = "dd/MM/yyyy HH:mm";
    final String NEW_FORMAT = "yyyy-MM-dd HH:mm:00";
    private File img;
    static final int REQUEST_TAKE_PHOTO = 1;
    static String TAG="InsertAd";
    private static final int SELECT_PICTURE = 1;
    private String pathImage;
    private String nameFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_ad);

        Button shot=(Button)findViewById(R.id.button_upload_img_ad);
        Button chooseFromGalley=(Button) findViewById(R.id.choseFromGallery);
        Button sendAd=(Button) findViewById(R.id.buttonSendAd);
        dateEditFrom=(EditText)findViewById(R.id.FromEditText);
        dateEditUntil=(EditText)findViewById(R.id.UntilEditText);
        fromSelected=false;
        untilSelected=false;
        sendAd.setOnClickListener(this);
        shot.setOnClickListener(this);
        chooseFromGalley.setOnClickListener(this);

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

    private void dispatchTakePictureIntent() {
        Log.i("dispatchTakePicIntent", "startPIC");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Ensure that there's a camera activity to handle the intent

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
                //photoFile = createImageFile();
                photoFile = createImageFile2();

            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
            else{
                Log.i(TAG, "Photofile is NULL");

            }
        }
    }


    private void setThumbtail(String pathImage){
        Bitmap image=BitmapFactory.decodeFile(pathImage);
        Log.i(TAG, "Size of image(byte): " + image.getByteCount());
        ImageView imageView=(ImageView) findViewById(R.id.thumbtailImage);
        imageView.setImageBitmap(image);
        Log.i(TAG, "The image has been set");

    }

    //TODO: Since that the image is created in the default folder pictures, it has to be changed.
    private File createImageFile() throws IOException {
        Log.i(TAG, "start");
        Long tsLong = System.currentTimeMillis()/1000;
        String timeStamp= tsLong.toString();
        nameFile = "JPEG_" + timeStamp;
        Log.i(TAG,"The filename is " + nameFile);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        pathImage=storageDir.getPath();
        Log.i(TAG, "Il path è:" + pathImage);

        File image = File.createTempFile(
                nameFile,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        img = image;
        return image;
    }


    private File createImageFile2(){

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pathDir = storageDir.getPath() + "/ProximityMarket";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        nameFile="JPEG_"+timeStamp+".jpg";
        File file = new File(pathDir, nameFile);
        Log.i(TAG, "The file path is "+file.getPath());
      // FileOutputStream fos = new FileOutputStream(file);
        img=file;
        return file;
    }

    private String getAdTitle(){
        EditText titleEditText=(EditText) findViewById(R.id.EditTextTitle);
         return titleEditText.getText().toString();
    }

    private String getAdDescription(){
        EditText descriptionEditText=(EditText) findViewById(R.id.DescriptionEditText);
        return descriptionEditText.getText().toString();
    }


    public  String changeDateFormat(String oldDateFormat){
        Date date = null;
        try {
            date = new SimpleDateFormat(OLD_FORMAT).parse(oldDateFormat);
        } catch (ParseException e) {
            Log.i("Error", "Bad format error");
            e.printStackTrace();
        }
        String dateNewFormat = new SimpleDateFormat(NEW_FORMAT).format(date);
        Log.i("420","The new format is: "+dateNewFormat);
        return dateNewFormat;
    }

    @Override
    public void onClick(View v) {
        int idView=v.getId();
        switch(idView){
            case R.id.buttonSendAd:
                RegisterNewAd registerNewAd=new RegisterNewAd(); // che roba è costi ?
                String fromDBFormat= changeDateFormat(dateEditFrom.getText().toString());
                String untilDBFormat= changeDateFormat(dateEditFrom.getText().toString());
                RadioButton findRadioButton=(RadioButton) findViewById(R.id.findradioButton);
                boolean find=findRadioButton.isChecked();
                String findString=String.valueOf(find);registerNewAd.execute(getAdTitle(),
                    getAdDescription(),findString, fromDBFormat, untilDBFormat);
                break;
            case R.id.button_upload_img_ad:
                dispatchTakePictureIntent();
                break;
            case R.id.choseFromGallery:
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
              //  setThumbtail();TODO: GET IMAGE PATH AND SET IT AS THUMBTAIL
                //   Uri selectedImageUri = data.getData();
                //   selectedImagePath = getPath(selectedImageUri);
            }
            ///TODO ARRIVER EFEGJEIJGIEJIEJGI
            if(requestCode==REQUEST_TAKE_PHOTO){
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String path=storageDir.getPath()+"/ProximityMarket/"+nameFile;
                Log.i(TAG, "The path to retrieve the image from is: "+path);
                setThumbtail(path);

             }
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
            String newAdString=connectionToServer.getStringtoSendToServer("AD,NEW", params);
            int resultSend=connectionToServer.sendToServer(newAdString);
            if(resultSend==ConnectionToServer.OK){
                dataFromServer = connectionToServer.receiveFromServer();//It produces error
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
            if(fromSelected) dateEditFrom.setText(dateEditFrom.getText() + " " + hourOfDay + ":" + minute);
            if(untilSelected) dateEditUntil.setText(dateEditUntil.getText() + " " + hourOfDay + ":" + minute);
        }
    }
}

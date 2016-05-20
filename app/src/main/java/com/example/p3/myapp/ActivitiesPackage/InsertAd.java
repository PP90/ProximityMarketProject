package com.example.p3.myapp.ActivitiesPackage;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class InsertAd extends AppCompatActivity implements View.OnClickListener {

    static EditText dateEditFrom;
    static EditText dateEditUntil;
    static EditText priceEditText;
    static boolean fromSelected;
    static boolean untilSelected;

    final String OLD_FORMAT = "dd/MM/yyyy HH:mm";
    final String NEW_FORMAT = "yyyy-MM-dd HH:mm:00";
    final private String directoryName="/ProximityMarket";
    static final int REQUEST_TAKE_PHOTO = 1;

    static String TAG="InsertAd";

    private static final int SELECT_PICTURE = 10;

    private String nameFile;
    private ImageView thumbnail;
    private GPSClass gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_ad);
        gps=new GPSClass(this);
        gps.onCreateActivity(this);
        Button shot=(Button)findViewById(R.id.button_upload_img_ad);
        Button chooseFromGalley=(Button) findViewById(R.id.choseFromGallery);
        Button sendAd=(Button) findViewById(R.id.buttonSendAd);
        dateEditFrom=(EditText)findViewById(R.id.FromEditText);
        dateEditUntil=(EditText)findViewById(R.id.UntilEditText);
        thumbnail=(ImageView) findViewById(R.id.thumbtailImage);
        priceEditText=(EditText)findViewById(R.id.priceEditText);
        fromSelected=false;
        untilSelected=false;
        sendAd.setOnClickListener(this);
        shot.setOnClickListener(this);
        chooseFromGalley.setOnClickListener(this);
        dateEditFrom.setOnClickListener(this);
        //If there is a focus on the date edit texts, then the date and time picker are shown
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

    @Override
    protected void onStart() {
        super.onStart();
        gps.onStartActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gps.onStopActivity();
    }
    private void dispatchTakePictureIntent() {
        Log.i(TAG, "startPIC");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Ensure that there's a camera activity to handle the intent

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = createImageFile();

            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
            else{
                Log.i(TAG, "photo file is NULL");

            }
        }
    }


    private void setThumbnail(String pathImage){
        Bitmap image=BitmapFactory.decodeFile(pathImage);
        Log.i(TAG, "Size of image(byte): " + image.getByteCount());
        thumbnail.setImageBitmap(image);
        Log.i(TAG, "The image has been set");

    }

    //An image view is converted to byte array in order to be sent to the server through the socket.
    private byte[] imageViewToByteConverter(ImageView imageView){
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bm = imageView.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Log.i(TAG,"The size is:"+byteArray.length);
        return byteArray;
    }

    //A file is created in the ProximityMarket folder. It is under the directory picture folder.
    //The file format is shown below.
    private File createImageFile(){
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pathDir = storageDir.getPath() + directoryName;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        nameFile="JPEG_"+timeStamp+".jpg";
        File file = new File(pathDir, nameFile);
        Log.i(TAG, "The file path is "+file.getPath());
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

    @Override
    public void onClick(View v) {
        int idView=v.getId();

        switch(idView){
            case R.id.buttonSendAd:
                RegisterNewAd registerNewAd=new RegisterNewAd(); // che roba è costi ?//Sara fanculizzati :D
                String fromDBFormat=Util.changeDateFormat(dateEditFrom.getText().toString());
                String untilDBFormat= Util.changeDateFormat(dateEditUntil.getText().toString());
                RadioButton findRadioButton=(RadioButton) findViewById(R.id.findradioButton);
                String priceString=(priceEditText.getText().toString());
                String latitude=String.valueOf(gps.getLatitude());
                String longitude=String.valueOf(gps.getLongitude());
                boolean find=findRadioButton.isChecked();
                String findOfferString=String.valueOf(find);
                registerNewAd.execute(getAdTitle(),//title
                    getAdDescription(),//description
                        findOfferString,//true or false
                        priceString,//the price
                        latitude,longitude,//the position
                        fromDBFormat, untilDBFormat);//The parsed data
                break;

            case R.id.button_upload_img_ad:
                dispatchTakePictureIntent();
                break;

            case R.id.choseFromGallery:
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                thumbnail.setImageURI(selectedImageUri);
                byte[] byteArray=imageViewToByteConverter(thumbnail);
                UploadPhoto uploadPhoto=new UploadPhoto();
                if(uploadPhoto.getStatus()== AsyncTask.Status.PENDING) uploadPhoto.execute(byteArray);
                return;
            }

            if(requestCode==REQUEST_TAKE_PHOTO){
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String path=storageDir.getPath()+directoryName + nameFile;
                Log.i(TAG, "The path to retrieve the image from is: " + path);
                setThumbnail(path);
                byte[] byteArray=imageViewToByteConverter(thumbnail);
                UploadPhoto uploadPhoto=new UploadPhoto();
                if(uploadPhoto.getStatus()== AsyncTask.Status.PENDING) uploadPhoto.execute(byteArray);
                return;
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

    private class UploadPhoto extends  AsyncTask<byte[], Void, Integer>{


        @Override
        protected Integer doInBackground(byte[]... params) {
                String strImage;
                ConnectionToServer connectionToServer=new ConnectionToServer();
                connectionToServer.connectToTheServer(true, true);

                strImage = Base64.encodeToString(params[0] , Base64.DEFAULT);
                Log.i(TAG,"The size of image sent is: "+params[0].length);

            if(connectionToServer.sendToServer("AD,IMG,"+strImage)==ConnectionToServer.OK) {
                    connectionToServer.closeConnection();
                }


            return null;
        }
    }

    //This async task is called when the user wants insert a new AD.
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
            UserStatus.username="pippo"; //TODO: to delete when it works
            String newAdString=connectionToServer.getStringtoSendToServer("AD,NEW,"+UserStatus.username, params);
            int resultSend=connectionToServer.sendToServer(newAdString);
            if(resultSend==ConnectionToServer.OK){
                dataFromServer = connectionToServer.receiveFromServer();//It produces error
//                Log.i(TAG, dataFromServer.toString());
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

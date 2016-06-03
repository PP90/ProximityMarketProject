package com.example.p3.myapp.ActivitiesPackage;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
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
    static final int COMPRESSION_RATIO=100;
    final String OLD_FORMAT = "dd/MM/yyyy HH:mm";
    final String NEW_FORMAT = "yyyy-MM-dd HH:mm:00";
    final private String directoryName="/ProximityMarket";
    static final int REQUEST_TAKE_PHOTO = 1;

    static String TAG="InsertAd";
    //TODO: These below four message must be put in the Format message library
    private final String BUY="buy";
    private final String SELL="sell";
    private final String EXCHANGE="exchange";
    private final String DONATE="donate";

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
       // gps.onStartActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gps.onStopActivity();
    }
    private void dispatchTakePictureIntent() {
        Log.i(TAG, "startPIC");
        // Ensure that there's a camera activity to handle the intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

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

    //Giving the path image as input the thumbnail of the image is set
    private void setThumbnail(String pathImage){
        Bitmap image=BitmapFactory.decodeFile(pathImage);
        Log.i(TAG, "Size of image(byte): " + image.getByteCount());
        thumbnail.setImageBitmap(image);
        Log.i(TAG, "The image has been set");

    }

    //An image view is converted to byte array in order to be sent to the server through the socket.
    private byte[] imageViewToByteConverter(ImageView imageView){
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_RATIO, stream);
        byte[] imageInByte=stream.toByteArray();
        return imageInByte;
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

    //Depending which radio button is selected, then this function return the relative string that specifies the typology of offer
    private String getTypology(){
       RadioButton buy=(RadioButton) findViewById(R.id.radioButton_buy2);
        if(buy.isChecked()) return BUY;

        RadioButton sell=(RadioButton) findViewById(R.id.radioButton_sell2);
        if(sell.isChecked()) return SELL;

        RadioButton exchange=(RadioButton) findViewById(R.id.radioButton_exchange2);
        if(exchange.isChecked()) return EXCHANGE;

        RadioButton donate=(RadioButton) findViewById(R.id.radioButton_donate2);
        if(donate.isChecked()) return DONATE;

        return "Generic";
    }

    private String getAdDescription(){
        EditText descriptionEditText=(EditText) findViewById(R.id.DescriptionEditText);
        if(descriptionEditText.getText().toString().isEmpty()) return "No description";
        else return descriptionEditText.getText().toString();
    }

    private String getPrice(){
        String priceString=(priceEditText.getText().toString());
        if(priceString==null | priceString.isEmpty())priceString= "0";
        return priceString;
    }

    private String getFrom(){
        //If the string is empty, then is taken the actual timestamp
        return null;
    }
    private String getUntil(){
        //If the string is empty, then is taken the farest timestamp
        return null;
    }


    @Override
    public void onClick(View v) {
        int idView=v.getId();

        switch(idView){
            case R.id.buttonSendAd:
                RegisterNewAd registerNewAd=new RegisterNewAd();
                String fromDBFormat=Util.changeDateFormat(dateEditFrom.getText().toString());
                String untilDBFormat= Util.changeDateFormat(dateEditUntil.getText().toString());
                String latitude=String.valueOf(gps.getLatitude());
                String longitude=String.valueOf(gps.getLongitude());
                registerNewAd.execute(getTypology(),//title
                        getAdDescription(),//description
                        getPrice(),//the price
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
                //TODO: it must be checked if the async task of the upload image has been finished
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
                //TODO: it must be checked if the async task of the upload image has been finished
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
            ConnectionToServer connectionToServer=new ConnectionToServer();
            connectionToServer.connectToTheServer(true, true);
            String strImage = Base64.encodeToString(params[0] , Base64.DEFAULT);
            Log.i(TAG,"The size of image sent is: "+params[0].length);
            UserStatus.username="pippo@pippo.it";
            int result=connectionToServer.sendToServer("AD,IMG,"+UserStatus.username+","+strImage);
            if(result==ConnectionToServer.OK) {
                connectionToServer.closeConnection();
            }
            return result;
        }

        protected void onPostExecute(Integer a) {
            if (a == ConnectionToServer.NULL_POINTER_EXC |
                    a== ConnectionToServer.TIMEOUT_EXCEPTION |
                    a== ConnectionToServer.IO_EXCEPTION){ Toast.makeText(getApplicationContext(), "Network error. Retry", Toast.LENGTH_SHORT).show();
        }

        }
    }

    //This async task is called when the user wants insert a new AD.
    private class RegisterNewAd extends AsyncTask<String,Void,Integer>{
        private final String TAG="RegisterNewAD";

        @Override
        protected Integer doInBackground(String... params) {
            ArrayList<String> dataFromServer;
            //1. The connection to the server is established
            ConnectionToServer connectionToServer=new ConnectionToServer();
            connectionToServer.connectToTheServer(true, true);
            //2. The fields from the activity must be get
            UserStatus.username="pippo"; //TODO: to delete when it works
            String newAdString=connectionToServer.getStringtoSendToServer("AD,NEW,"+UserStatus.username, params);
            int resultSend=connectionToServer.sendToServer(newAdString);
            if(resultSend==ConnectionToServer.OK){
                dataFromServer = connectionToServer.receiveFromServer();
//                Log.i(TAG, dataFromServer.toString());
            }else return resultSend;
            connectionToServer.closeConnection();
            if (dataFromServer.get(2).equals("OK")) return 1;
            else return -1;
        }


        protected void onPostExecute(Integer a) {
            if (a == ConnectionToServer.OK) {
                Toast.makeText(getApplicationContext(), "Ad insert correctly", Toast.LENGTH_SHORT).show();
                finish();
            }
            else if (a == ConnectionToServer.NULL_POINTER_EXC |
                    a== ConnectionToServer.TIMEOUT_EXCEPTION |
                    a== ConnectionToServer.IO_EXCEPTION) Toast.makeText(getApplicationContext(), "Network error. Retry", Toast.LENGTH_SHORT).show();
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
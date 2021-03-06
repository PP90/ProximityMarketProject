package com.example.p3.myapp.ActivitiesPackage;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.p3.myapp.ConnectionToServer;
import com.example.p3.myapp.R;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import EntityClasses.FormatMessage;

/*
* This class is an activity class. It permits to insert an advertisement.
* The advertisement is inserted in this way:
* First the user fills the fields in which he\she specifies
* the details of advertisement: Description, Type of, price, valid from, valid until and optionally
* a photo.
*
* Second, the photo will be automatically uploaded to an hosting service called Firebase.
* When the user clicks on "Publish advertisement", a request is sent to the server, and the advertisement is registered.
*
* Third, the position is taken in a transparent manner.
*
*
* */
public class InsertAd extends AppCompatActivity implements View.OnClickListener {

    private static EditText dateEditFrom;
    private static EditText dateEditUntil;
    private static EditText priceEditText;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference childRef;
    private StorageReference imageRef;
    private String uriIMG;

    static final String STORAGE_REF="gs://proximitymarketdb.appspot.com/";
    static final String CHILD_REF="images/";
    static final private String directoryName="/ProximityMarket/";

    private final int COMPRESSION_RATIO=100;

    static final int REQUEST_TAKE_PHOTO = 1;
    private final int SELECT_PICTURE = 2;

    static String TAG="InsertAd";

    private static boolean fromSelected;
    private static boolean untilSelected;
    private String nameFile;
    private ImageView thumbnail;
    private GPSClass gps;

    private double progressUpload=-1;

    private SharedPreferences pref;
    UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_ad);

        Log.i(TAG,"OnCreateInsertAd");
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        gps=new GPSClass(this);
        gps.onCreateActivity(this);

        Firebase.setAndroidContext(this);
        storage=FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(STORAGE_REF);
        childRef=storageRef.child(CHILD_REF);
        imageRef = childRef.child(Util.getCurrentTs());

        ImageButton shot=(ImageButton)findViewById(R.id.takeAPhotoImage);
        ImageButton chooseFromGalley=(ImageButton) findViewById(R.id.choseFromGalleryImage);
        Button sendAd=(Button) findViewById(R.id.buttonSendAd);

        priceEditText=(EditText)findViewById(R.id.priceEditText);
        dateEditFrom=(EditText)findViewById(R.id.FromEditText);
        dateEditUntil=(EditText)findViewById(R.id.UntilEditText);

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
                    showTimePickerDialog(v);
                    showDatePickerDialog(v);
                }
            }
        });
        dateEditUntil.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    fromSelected=false;
                    untilSelected=true;
                    showTimePickerDialog(v);
                    showDatePickerDialog(v);
                }
            }
        });
    }

    @Override
    protected  void onPause(){
        super.onPause();
        if(uploadTask!=null)   uploadTask.pause();//Added

    }

    @Override
    protected  void onResume(){
        super.onResume();
        if(uploadTask!=null) uploadTask.resume();//Added
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

/*
* If the user decides to take a picture, this method will be called
* */
    private void dispatchTakePictureIntent() {
        Log.i(TAG, "startPIC");
        // Ensure that there's a camera activity to handle the intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = createImageFile();
            Log.i(TAG,"The image is created");
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                return;
            }
            else{
                Log.i(TAG, "photo file is NULL");
            }
        }
    }

    //Giving the path image as input the thumbnail of the image is set
    private void setThumbnail(String pathImage){
        Bitmap image=BitmapFactory.decodeFile(pathImage);
        thumbnail.setImageBitmap(image);

    }

/*
* This function is called when the user either selects an image from the gallery or take a picture.
* After that the image is uploaded to the Firebase server.
* According to the on-line Firebase documentation, the upload task is asynchronous respect to the activity.
* For further implementation see this following reference: https://firebase.google.com/docs/reference/android/com/google/firebase/storage/UploadTask
* */
    private void uploadImageOnFirebase(byte[] data) {
        uploadTask = imageRef.putBytes(data);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                progressUpload = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.i(TAG,""+progressUpload);
            }
        });

        uploadTask.addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i(TAG, exception.toString());
                Toast.makeText(getApplicationContext(), "Error network during the image upload", Toast.LENGTH_SHORT).show();
            }

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                uriIMG=downloadUrl.toString();
                Log.i(TAG,"URI: "+uriIMG);
                Toast.makeText(getApplicationContext(), "Image upload correctly", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //An image view is converted to byte array in order to be sent to Firebase
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
        String timeStamp = new SimpleDateFormat(Util.TS_FORMAT).format(new Date());
        nameFile="JPEG_"+timeStamp+".jpg";
        File file = new File(pathDir, nameFile);
        Log.i(TAG, "The file path is "+file.getPath());
        return file;
    }

    //Depending which radio button is selected, then this function return the relative string that specifies the typology of offer
    private String getTypology(){
       RadioButton buy=(RadioButton) findViewById(R.id.radioButton_buy2);
        if(buy.isChecked()) return FormatMessage.BUY;

        RadioButton sell=(RadioButton) findViewById(R.id.radioButton_sell2);
        if(sell.isChecked()) return FormatMessage.SELL;

        RadioButton exchange=(RadioButton) findViewById(R.id.radioButton_exchange2);
        if(exchange.isChecked()) return FormatMessage.EXCHANGE;

        RadioButton donate=(RadioButton) findViewById(R.id.radioButton_donate2);
        if(donate.isChecked()) return FormatMessage.DONATE;

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

    private String getFromDate(){
        String from=dateEditFrom.getText().toString();
       if(from==null){ return Util.getCurrentTs();}
        else if (from.isEmpty()){ return Util.getCurrentTs();}
        else{ return Util.changeDateFormat(dateEditFrom.getText().toString());}
        }

    private String getUntilDate(){
        String until=dateEditUntil.getText().toString();
        if(until==null) return Util.MAX_DATE;
        else if (until.isEmpty()){ return Util.MAX_DATE;}
        else return Util.changeDateFormat(dateEditFrom.getText().toString());
    }

    private String getUriIMG(){
        if(uriIMG==null) return "";
        if(uriIMG.isEmpty()) return "";
        return uriIMG;
    }

    @Override
    public void onClick(View v) {
        int idView=v.getId();

        if(idView==R.id.choseFromGalleryImage){
            Intent intentGallery = new Intent( Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentGallery, SELECT_PICTURE);
            return;
        }

        switch(idView){
            case R.id.buttonSendAd:
                RegisterNewAd registerNewAd=new RegisterNewAd();
                String latitude=String.valueOf(gps.getLatitude());
                String longitude=String.valueOf(gps.getLongitude());
                registerNewAd.execute(getTypology(),//title
                        getAdDescription(),//description
                        getUriIMG(),//UriIMG
                        getPrice(),//the price
                        latitude,longitude,//the position
                        getFromDate(), getUntilDate());//The parsed data
                break;

            case R.id.takeAPhotoImage:
                Log.i(TAG,"take a photo");
                dispatchTakePictureIntent();
                Log.i(TAG,"The photocamera gallery has been started");
                break;

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"On Activity Result");
        thumbnail=(ImageView) findViewById(R.id.thumbtailImage);
        if (resultCode == RESULT_OK) {
            Log.i(TAG,"Result is ok");
            //If the image is taken from thee photocamera, then the image is set
            if (requestCode == SELECT_PICTURE) {
                Log.i(TAG,"The request code is the request select picture");
                Uri selectedImageUri = data.getData();
               Log.i(TAG,"The local uri of image is:" +selectedImageUri.toString());
                thumbnail.setImageURI(selectedImageUri);
                byte[] byteArray=imageViewToByteConverter(thumbnail);
                uploadImageOnFirebase(byteArray);
                return;
            }

            if(requestCode==REQUEST_TAKE_PHOTO){
                Log.i(TAG,"The request code is the request take photo");
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String path=storageDir.getPath()+directoryName + nameFile;
                setThumbnail(path);
                byte[] byteArray=imageViewToByteConverter(thumbnail);
                uploadImageOnFirebase(byteArray);
                return;
            }
        }
    }

    //A dialog about the time about data is created
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    //A dialog about the date about data is created
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    //This async task is called when the user wants insert a new advertisement.
    private class RegisterNewAd extends AsyncTask<String,Void,Integer>{
        private final String TAG="RegisterNewAD";
        private final int NOT_UPLOAD_YET=99;
        private int progress;
        private final int NO_CONNECTION=7;

        @Override
        protected Integer doInBackground(String... params) {

            //Before to connect to the server a check of the image is performed.
            //If the image URI is not empty and the image is not uploaded yet, then the async task returns.

            Log.i(TAG,"The progress is: "+progressUpload);
            progress=(int)progressUpload;
            if( progress>-1 && progress<100){
                Log.i(TAG,"Image not upload yet");
                return NOT_UPLOAD_YET;
            }
        //From this point the connection to the server is established if the image is uploaded into Firebase cloud
            ArrayList<String> dataFromServer;
            ConnectionToServer connectionToServer=new ConnectionToServer();

            boolean serverConn= connectionToServer.connectToTheServer(true, true);

            if(!serverConn)return NO_CONNECTION;

            String newAdString=connectionToServer.getStringtoSendToServer("AD,NEW,"+pref.getString("username", null), params);

            Log.i(TAG,"Ad to send: "+newAdString);

            int resultSend=connectionToServer.sendToServer(newAdString);

            if(resultSend==ConnectionToServer.OK){
                dataFromServer = connectionToServer.receiveFromServer();
                connectionToServer.closeConnection();
//                Log.i(TAG, dataFromServer.toString());

            }else {
                connectionToServer.closeConnection();
                return resultSend;
            }

            if (dataFromServer.get(2).equals("OK")) return 1;
            else return -1;

        }

        protected void onPostExecute(Integer a) {
            if (a == ConnectionToServer.OK) {
                Toast.makeText(getApplicationContext(), "Advertisement inserted correctly!", Toast.LENGTH_SHORT).show();
                finish();
            }
            else if (a == NO_CONNECTION) Toast.makeText(getApplicationContext(), "No internet connection. Turn on the Wi-fi or data mobile", Toast.LENGTH_LONG).show();
            else if (a == NOT_UPLOAD_YET)  Toast.makeText(getApplicationContext(), "Please wait uploading image.. "+progress+"%", Toast.LENGTH_SHORT).show();
            else if (a == ConnectionToServer.NULL_POINTER_EXC |
                    a== ConnectionToServer.TIMEOUT_EXCEPTION |
                    a== ConnectionToServer.IO_EXCEPTION) Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
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
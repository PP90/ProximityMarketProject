package com.example.p3.myapp.ActivitiesPackage;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.p3.myapp.ConnectionToServer;
import com.example.p3.myapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyAds extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);
        ReceiveImageFromServer receiveImageFromServer=new ReceiveImageFromServer();
        if(receiveImageFromServer.getStatus() == AsyncTask.Status.PENDING){
            receiveImageFromServer.execute();

        }
    }

    //TODO: This task must be tested with the timestamp filename
    private class ReceiveImageFromServer extends AsyncTask<Void, Void, Integer>{

        private final String TAG="ReceiverImage";

        private int receiveImage(){
            try {
                byte[] receivedImage=null;
                ConnectionToServer connectionToServer=new ConnectionToServer();
                connectionToServer.connectToTheServer(true, true);
                int reqMyIdImages= connectionToServer.sendToServer("AD, MY_AD");
                if(reqMyIdImages==ConnectionToServer.OK) {
                     receivedImage=connectionToServer.receiveImageFromTheServer();
                }
                //The path of images is get and then the file is saved there.
                if(receivedImage!=null) {
                    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    String pathDir = storageDir.getPath() + "/ProximityMarket";
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    Log.i(TAG, "The buffer size is " + receivedImage.length + "\nThe path is " + pathDir);
                    File file = new File(pathDir, "JPEG_"+timeStamp+".jpg");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(receivedImage);
                    fos.flush();
                    fos.close();
                    connectionToServer.closeConnection();
                    return 1;
                }else{
                    connectionToServer.closeConnection();
                    return -1;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return -2;
            }


        }
        @Override
        protected Integer doInBackground(Void... params) {
            return receiveImage();
        }

        //TODO: implement onPostExecuteMethod in which a message is shown to the user
    }
}

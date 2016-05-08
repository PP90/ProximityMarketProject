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
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyAds extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);
        ReceiveImageFromServer rifs=new ReceiveImageFromServer();
        if(rifs.getStatus() == AsyncTask.Status.PENDING){
            rifs.execute();

        }
    }


    private class ReceiveImageFromServer extends AsyncTask<Void, Void, Void>{

    private final String SERVER_ADDRESS="192.168.0.3";
        private final int PORT=8086;
        private final int TIMEOUT=10000;
        private final String TAG="ReceiverImage";

        private void receiveImage(){
            try {
                byte[] receivedImage=null;
                ConnectionToServer connectionToServer=new ConnectionToServer();
                connectionToServer.connectToTheServer(true, true);
                int reqMyIdImages= connectionToServer.sendToServer("AD, MY_AD");
                if(reqMyIdImages==ConnectionToServer.OK) {
                     receivedImage=connectionToServer.receiveImageFromTheServer();
                }

               /* Socket s=new Socket();
                s.connect(new InetSocketAddress(SERVER_ADDRESS, PORT), TIMEOUT);
                ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
                byte[] buffer= (byte[]) ois.readObject();*/

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
                }
                connectionToServer.closeConnection();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        @Override
        protected Void doInBackground(Void... params) {
            receiveImage();
            return null;
        }
    }
}

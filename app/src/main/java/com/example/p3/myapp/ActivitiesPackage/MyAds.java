package com.example.p3.myapp.ActivitiesPackage;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.p3.myapp.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

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


        private void receiveImage(){
            try {
                Socket s=new Socket();
                s.connect(new InetSocketAddress("192.168.0.3", 8086), 10000);
                ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
                byte[] buffer= (byte[]) ois.readObject();
                FileOutputStream fos=new FileOutputStream("pippo.png");
                fos.write(buffer);
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
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

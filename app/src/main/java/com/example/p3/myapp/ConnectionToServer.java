package com.example.p3.myapp;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ConnectionToServer {
    private final static String SERVER_NAME = "192.168.0.112";//TO MODIFY EACH TIME
    private final static int PORT = 8080;
    private final int TIMEOUT=10000;
    private final String DELIMITS = "[,]";
    private Socket client;

    private OutputStream outToServer;
    DataOutputStream out;

    InputStream inFromServer;
    DataInputStream in;

    public ConnectionToServer(){
       client = new Socket();
        try {
            client.connect(new InetSocketAddress(SERVER_NAME, PORT), TIMEOUT);
            outToServer = client.getOutputStream();
            out = new DataOutputStream(outToServer);
            inFromServer = client.getInputStream();
            in = new DataInputStream(inFromServer);


        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendToServer(String sendToServer){
        try {
            this.out.writeUTF(sendToServer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public ArrayList<String> receiveFromServer(){
        try {
            String dataFromServer=in.readUTF();
            ArrayList<String> data=getParsedDataFromBuffer(dataFromServer);
            System.out.println("Received"+data.toString());
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean closeConnection(){
        try {
            client.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private ArrayList<String> getParsedDataFromBuffer(String dataFromBuffer) {
        ArrayList<String> parsedInput = new ArrayList<>();
        if (dataFromBuffer != null) {

            System.out.println("From the client:" + dataFromBuffer);
            String[] myData = dataFromBuffer.split(DELIMITS);
            parsedInput.addAll(Arrays.asList(myData));
            return parsedInput;

        } else {
            return null;
        }
    }
}

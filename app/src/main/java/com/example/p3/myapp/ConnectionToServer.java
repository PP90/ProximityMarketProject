package com.example.p3.myapp;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class ConnectionToServer {
    private final static String SERVER_NAME = "192.168.0.3";//TO MODIFY EACH TIME OPEN YOUR IDE
    private final static int PORT = 8085;
    private final int TIMEOUT=10000;
    private final String DELIMITS = "[,]";
    private Socket client;
    public static final int OK=1;
    public static final int TIMEOUT_EXCEPTION=-5;
    public static final int IO_EXCEPTION=-3;

    private OutputStream outToServer;
    private DataOutputStream out;

    private InputStream inFromServer;
    private DataInputStream in;
    private boolean isConnected;
    public ConnectionToServer(){
        isConnected=false;
    }
    private final static String TAG="ConnectionToTheServer";

    public void connectToTheServer(){
        client = new Socket();
        try {
            Log.i(TAG,"Try to connect to "+SERVER_NAME+" at "+PORT);
            client.connect(new InetSocketAddress(SERVER_NAME, PORT), TIMEOUT);
            Log.i(TAG, "Connected to " + SERVER_NAME + " at " + PORT);
            outToServer = client.getOutputStream();
            out = new DataOutputStream(outToServer);
            inFromServer = client.getInputStream();
            in = new DataInputStream(inFromServer);


        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int sendToServer(String sendToServer){
        System.out.println("Send to server string"+sendToServer);
        try {
            out.writeUTF(sendToServer);
            return OK;

        }

        catch(NullPointerException npe){
            System.out.println("Timeout reached");
            npe.printStackTrace();
            return TIMEOUT_EXCEPTION;
        }
        catch (SocketTimeoutException ste){
            System.out.println("Timeout reached");
            ste.printStackTrace();
            return this.TIMEOUT_EXCEPTION;
        } catch (IOException e) {
            System.out.println("IOERROR");
            e.printStackTrace();
            return this.IO_EXCEPTION;
        }

    }

    public ArrayList<String> receiveFromServer(){
        try {
            String dataFromServer=in.readUTF();
            ArrayList<String> data=getParsedDataFromBuffer(dataFromServer);
            System.out.println("Received" + data.toString());
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private String getServerIP() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
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
    public String getStringtoSendToServer(String typeOfMsg, String ...params){
        for(int i=0; i<params.length; i++){
            typeOfMsg+=","+params[i];
        }
        System.out.println("The parsed message is: "+typeOfMsg);
        return typeOfMsg;
    }
}

package com.example.p3.myapp;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

/*
* Connection to the server class. This class is used to manage, send to and receive from the server.
* Before to send the data, are parsed correctly in order to respect the format of the message.
* */
public class ConnectionToServer {
    private final static String SERVER_NAME = "192.168.0.2";//TO MODIFY EACH TIME THE SERVER CHANGES ITS IP
    private final static String HOST_NAME="proxmarserver.ddns.net";
    private final static int PORT = 8080;
    private final int TIMEOUT=10000;

    public static final int TIMEOUT_EXCEPTION=-5;
    public static final int IO_EXCEPTION=-3;
    public static final int NULL_POINTER_EXC=-7;
    public static final int OK=1;

    private final String DELIMITS = "[,]";//Delimiter to parse the data

    private Socket client;

    private OutputStream outToServer;
    private DataOutputStream out;

    private InputStream inFromServer;
    private DataInputStream in;

    private final static String TAG="ConnectionToTheServer";

    public ConnectionToServer(){    }

    public boolean connectToTheServer(boolean inputBuffer, boolean outputBuffer){
        client = new Socket();
        try {
            /*Try to resolve the hostname*/
            String serverAddress=getServerIP();
            if(serverAddress==null) serverAddress=SERVER_NAME;

            Log.i(TAG, "Try to connect to "+serverAddress+" at "+PORT);
            client.connect(new InetSocketAddress(serverAddress, PORT), TIMEOUT);
            Log.i(TAG, "Connected to " + serverAddress + " at " + PORT);

            if(inputBuffer){
                inFromServer = client.getInputStream();
                in = new DataInputStream(inFromServer);
            }

            if(outputBuffer){
                outToServer = client.getOutputStream();
                out = new DataOutputStream(outToServer);
            }
            return true;

        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public int sendToServer(String sendToServer){
        Log.i(TAG,"Send to server string: "+sendToServer);
        try {
            out.writeUTF(sendToServer);
            return OK;
        }

        catch(NullPointerException npe){
            System.out.println("Null point exception after send to server string");
            npe.printStackTrace();
            return NULL_POINTER_EXC;
        }

        catch (SocketTimeoutException ste){
            System.out.println("Timeout reached");
            ste.printStackTrace();
            return this.TIMEOUT_EXCEPTION;
        }

        catch (IOException e) {
            System.out.println("IO ERROR");
            e.printStackTrace();
            return this.IO_EXCEPTION;
        }

    }

    public ArrayList<String> receiveFromServer(){
        try {
            String dataFromServer=in.readUTF();
            ArrayList<String> data=getParsedDataFromBuffer(dataFromServer);
            System.out.println("Received from the server: " + data.toString());
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

   // This function should get the server IP but it doesn't work
    public static String getServerIP() {
        try {

            InetAddress address= InetAddress.getByName(HOST_NAME);
            Log.i(TAG,"The server address is: "+address.getHostAddress());
            return address.getHostAddress();
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
            Log.i(TAG,"Error: can't close connection");
            e.printStackTrace();
            return false;
        }
    }

    private ArrayList<String> getParsedDataFromBuffer(String dataFromBuffer) {
        ArrayList<String> parsedInput = new ArrayList<>();
        if (dataFromBuffer != null) {
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

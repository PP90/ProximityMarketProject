package com.example.p3.myapp;

import android.util.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class ConnectionToServer {
    private final static String SERVER_NAME = "192.168.0.104";//TO MODIFY EACH TIME OPEN YOUR IDE
    private final static int PORT = 8080;
    private final int TIMEOUT=10000;
    private final String DELIMITS = "[,]";//Delimiter to parse the data
    private Socket client;
    public static final int OK=1;
    public static final int TIMEOUT_EXCEPTION=-5;
    public static final int IO_EXCEPTION=-3;
    public static final int NULL_POINTER_EXC=-7;
    private OutputStream outToServer;
    private DataOutputStream out;

    private InputStream inFromServer;
    private ObjectInputStream ois;

    private DataInputStream in;

    private boolean isConnected;

    private final static String TAG="ConnectionToTheServer";


    public ConnectionToServer(){
        isConnected=false;
    }

    public boolean connectToTheServer(boolean inputBuffer, boolean outputBuffer){
        client = new Socket();
        try {
            Log.i(TAG, "Try to connect to "+SERVER_NAME+" at "+PORT);
            client.connect(new InetSocketAddress(SERVER_NAME, PORT), TIMEOUT);
            Log.i(TAG, "Connected to " + SERVER_NAME + " at " + PORT);

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
            System.out.println("Null point exception");
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

    public byte[] receiveImageFromTheServer(){//TODO: this function has to be modified. It is better to get the encoded base 64 string instead of the byte array.
        try {
            //A socket is opened and after to have established the connection with the server, the image is received.
            ois=new ObjectInputStream(client.getInputStream());
            byte[] buffer= (byte[]) ois.readObject();
            Log.i(TAG,"Buffer correctly received");
            return buffer;

        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "IO Error");
            return null;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "Class not found");
            return null;
        }

    }
   // TODO: It doesn't work. delete ?
    public static void getServerIP() {
        try {
            String myIP;
         //  Log.i(TAG,Inet4Address.getLocalHost().getAddress().toString());
            Log.i(TAG, Inet4Address.getLocalHost().getHostAddress().toString());

        } catch (UnknownHostException e) {
            e.printStackTrace();

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

    //TODO: Is this function used ??
    public String getStringtoSendToServer(String typeOfMsg, String ...params){
        for(int i=0; i<params.length; i++){
            typeOfMsg+=","+params[i];
        }
        System.out.println("The parsed message is: "+typeOfMsg);
        return typeOfMsg;
    }
}

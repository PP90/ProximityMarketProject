package com.example.p3.myapp;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class ConnectionToServer {
    private final static String SERVER_NAME = "192.168.0.107";//TO MODIFY EACH TIME
    private final static int PORT = 8080;
    private final int TIMEOUT=10000;
    private final String DELIMITS = "[,]";
    private Socket client;
    static final int OK=1;
    static final int TIMEOUT_EXCEPTION=-2;
    static final int IO_EXCEPTION=-3;

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

    public int sendToServer(String sendToServer){
        System.out.println("Send to server string"+sendToServer);
        try {
            out.writeUTF(sendToServer);
            return this.OK;

        }
        catch(NullPointerException npe){
            System.out.println("Timeout reached");
            npe.printStackTrace();
            return this.TIMEOUT_EXCEPTION;
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


    private String getMyIP() {
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
}

package com.example.p3.myapp;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by p3 on 15/04/2016.
 */
public class Parser {
    private final String DELIMITS = "[,]";
    public Parser(){

    }

    public ArrayList<String> getParsedDataFromBuffer(String dataFromBuffer) {
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

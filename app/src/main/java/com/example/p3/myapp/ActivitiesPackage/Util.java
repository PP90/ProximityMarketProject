package com.example.p3.myapp.ActivitiesPackage;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by p3 on 19/05/2016.
 */
public class Util {

    static final String OLD_FORMAT = "dd/MM/yyyy HH:mm";
    static final String NEW_FORMAT = "yyyy-MM-dd HH:mm:00";
    static final String TAG="Util";

    //The date format get from the editText is different from the date format in the DB. For this reason must be converted.
    static public  String changeDateFormat(String oldDateFormat){
        Date date = null;
        try {
            date = new SimpleDateFormat(OLD_FORMAT).parse(oldDateFormat);
        } catch (ParseException e) {
            Log.i(TAG, "Bad format error");
            e.printStackTrace();
        }
        String dateNewFormat = new SimpleDateFormat(NEW_FORMAT).format(date);
        Log.i(TAG,"The new format is: "+dateNewFormat);
        return dateNewFormat;
    }
}

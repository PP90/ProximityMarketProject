package com.example.p3.myapp.ActivitiesPackage;

import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Util {

    static final String OLD_FORMAT = "dd/MM/yyyy HH:mm";
    static final String NEW_FORMAT = "yyyy-MM-dd HH:mm:ss";
    static final String TS_FORMAT="yyyyMMdd_HHmmss";
    static final String TAG="Util";
    static final String MAX_DATE="2099-12-31 23:59:59";

    //The date format get from the editText is different from the date format in the DB. For this reason must be converted.
    static public  String changeDateFormat(String oldDateFormat){
        Date date = null;
        try {
            date = new SimpleDateFormat(OLD_FORMAT).parse(oldDateFormat);
        }
        catch (ParseException e) {
            Log.i(TAG, "Bad format error");
            e.printStackTrace();
        }

        String dateNewFormat = new SimpleDateFormat(NEW_FORMAT).format(date);
        Log.i(TAG,"The new format is: "+dateNewFormat);
        return dateNewFormat;
    }

    static public  String newToOldDateFormat(String newDateFormat){
        Date date = null;
        try {
            date = new SimpleDateFormat(NEW_FORMAT).parse(newDateFormat);
        }
        catch (ParseException e) {
            Log.i(TAG, "Bad format error");
            e.printStackTrace();
        }
        String dateNewFormat = new SimpleDateFormat(OLD_FORMAT).format(date);
        Log.i(TAG,"The old format is: "+dateNewFormat);
        return dateNewFormat;
    }


    static String getCurrentTs(){
        SimpleDateFormat s = new SimpleDateFormat(NEW_FORMAT);
        return s.format(new Date());
    }
}

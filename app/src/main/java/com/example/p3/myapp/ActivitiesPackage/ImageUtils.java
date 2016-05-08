package com.example.p3.myapp.ActivitiesPackage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;



public class ImageUtils {
    private static String TAG="ImageUtil";

    public static String ImageToBase64Converter(String filename){
        filename="Filename11";
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    String filePath= Environment.DIRECTORY_PICTURES + "/ProximityMarket/"+filename+".jpg";
    Bitmap selectedImage =  BitmapFactory.decodeFile(filePath);
   // selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
    byte[] byteArray = stream.toByteArray();
    String strBase64= Base64.encodeToString(byteArray, 0);
    Log.i(TAG, strBase64);
        return strBase64;
        }

}


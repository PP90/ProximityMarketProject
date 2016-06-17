package com.example.p3.myapp;

/**
 * Created by fa on 17/06/16.
 */

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class ProximityMarketProject extends Application  {
    @Override
    public void onCreate(){
        super.onCreate();
       // Firebase.setAndroidContext(this);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        //needed for larger apks
        MultiDex.install(this);
    }
}

package com.example.p3.myapp.ActivitiesPackage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.p3.myapp.R;

public class SeeNearAds extends AppCompatActivity {

    static final String TAG="SeeNearAds";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_near_ads);
        Intent intent = getIntent();
        Log.i(TAG, "The value passed from previous activity is " + intent.getIntExtra("searchResult", 1));
    }
}

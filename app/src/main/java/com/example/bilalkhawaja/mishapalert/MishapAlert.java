package com.example.bilalkhawaja.mishapalert;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.firebase.client.Firebase;



/**
 * Created by Bilal Khawaja on 3/29/2017.
 */
public class MishapAlert extends Application{

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Fresco.initialize(this);


    }
}

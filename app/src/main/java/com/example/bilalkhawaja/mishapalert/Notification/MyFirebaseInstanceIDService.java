package com.example.bilalkhawaja.mishapalert.Notification;

import android.app.Service;
import android.util.Log;
import android.widget.Toast;

import com.example.bilalkhawaja.mishapalert.CheckIntenet.CheckNetwork;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hasnain Ali on 8/6/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    FirebaseUser user;
    DatabaseReference databaseReference;

    private static final String TAG = "MyFirebaseIIDService";
    String refreshedToken;

    @Override
    public void onTokenRefresh() {


        //Getting registration token
         refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

       // Toast.makeText(this, "REG Token" + refreshedToken, Toast.LENGTH_SHORT).show();

        //calling the method store token and passing token
        storeToken(refreshedToken);
    }

    private void storeToken(String token) {
        //we will save the token in sharedpreferences later


    }



}

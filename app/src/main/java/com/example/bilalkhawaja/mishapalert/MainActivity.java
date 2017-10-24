package com.example.bilalkhawaja.mishapalert;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilalkhawaja.mishapalert.CheckIntenet.CheckNetwork;
import com.example.bilalkhawaja.mishapalert.GPS.GPSTracker;
import com.example.bilalkhawaja.mishapalert.GPS.Myservice;
import com.example.bilalkhawaja.mishapalert.NewFeeds.Home;
import com.example.bilalkhawaja.mishapalert.Registration.Login;
import com.example.bilalkhawaja.mishapalert.Registration.SignUp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TextView signUp, sin;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        Intent intent = new Intent(this, Myservice.class);
        startService(intent);


            mAuth = FirebaseAuth.getInstance();

            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() != null) {
                        startActivity(new Intent(MainActivity.this, Home.class));


                    }
                }
            };


            signUp = (TextView) findViewById(R.id.signUp);
            sin = (TextView) findViewById(R.id.sin);

            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this,SignUp.class);
                    startActivity(intent);
                    finish();
                }
            });

            sin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();

                }
            });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (CheckNetwork.isInternetAvailable(this))
        {

        mAuth.addAuthStateListener(mAuthStateListener);
    }else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    
}

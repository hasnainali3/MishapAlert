package com.example.bilalkhawaja.mishapalert.Registration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilalkhawaja.mishapalert.Profiles.setprofile;
import com.example.bilalkhawaja.mishapalert.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmail extends AppCompatActivity {

    private static final String TAG = "EmailVerification";

    private String email,pass;

    private TextView verEmailTV,afterEmailTV;

    private Button emailVerifiedBTN, gotoBTN,btnVerify;

    //Firebase Variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        email = getIntent().getExtras().getString("email");
        pass = getIntent().getExtras().getString("pass");

        afterEmailTV = (TextView) findViewById(R.id.afterEmailTV);

        verEmailTV = (TextView) findViewById(R.id.verEmailTV);

        verEmailTV.setText(email);

        gotoBTN = (Button) findViewById(R.id.gotoBTN);

        emailVerifiedBTN = (Button) findViewById(R.id.sendEmailBTN);
        btnVerify = (Button)findViewById(R.id.btnVerify);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };



        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful())
                        {
                            Toast.makeText(VerifyEmail.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                        }

                        else if(user.isEmailVerified()) {
                            Intent intent = new Intent(VerifyEmail.this,setprofile.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(VerifyEmail.this, "Email is not verified. \nCheck your email inbox", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        });

        emailVerifiedBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user.sendEmailVerification().addOnCompleteListener(VerifyEmail.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {

                            Toast.makeText(VerifyEmail.this, "Verification email sent to:" + email, Toast.LENGTH_SHORT).show();
                            btnVerify.setVisibility(View.VISIBLE);
                            emailVerifiedBTN.setVisibility(View.INVISIBLE);

                        }
                        else {
                            Toast.makeText(VerifyEmail.this, "Failed to send Verification email", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

        gotoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VerifyEmail.this, setprofile.class);
                i.putExtra("email",email);
                startActivity(i);

            }
        });

    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}

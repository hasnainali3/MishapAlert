package com.example.bilalkhawaja.mishapalert.Registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilalkhawaja.mishapalert.CheckIntenet.CheckNetwork;
import com.example.bilalkhawaja.mishapalert.NewFeeds.Home;
import com.example.bilalkhawaja.mishapalert.PasswordReset.Passwordreset;
import com.example.bilalkhawaja.mishapalert.R;
import com.example.bilalkhawaja.mishapalert.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText etUsername, etPassword;
    Button btnLogin;
    ImageView imgvBack;
    TextView tvforgetpass;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();


        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvforgetpass = (TextView)findViewById(R.id.tvforgetpass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        progressDialog = new ProgressDialog(this);
        imgvBack = (ImageView) findViewById(R.id.imgvBack);

        imgvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    progressDialog.setMessage("Setting Profile");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.create();
                    progressDialog.show();

                    Toast.makeText(Login.this, "User Logged In", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    startActivity(new Intent(Login.this, Home.class));


                }
            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CheckNetwork.isInternetAvailable(Login.this)) {

                    Signin();
                } else {
                    Toast.makeText(Login.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }


            }
        });

        tvforgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mAuth.sendPasswordResetEmail(etUsername.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!TextUtils.isEmpty(etUsername.getText()))
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Login.this, "Password verification link has been sent", Toast.LENGTH_SHORT).show();
                            }
                            else {

                                Toast.makeText(Login.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            etUsername.setError("Enter Email.");

                        }


                    }
                });
            }
        });
    }





    @Override
    protected void onStart() {
        super.onStart();

            mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void Signin()
    {

        progressDialog.setMessage("Logging in...");
        progressDialog.create();
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        String username, password;
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();


        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password))
        {
            progressDialog.dismiss();
            Toast.makeText(Login.this,"Fields are Empty",Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful())
                    {
                        Toast.makeText(Login.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.cancel();
                }
            });



        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}

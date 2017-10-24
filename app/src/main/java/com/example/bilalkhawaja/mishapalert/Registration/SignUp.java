package com.example.bilalkhawaja.mishapalert.Registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bilalkhawaja.mishapalert.Profiles.setprofile;
import com.example.bilalkhawaja.mishapalert.R;
import com.example.bilalkhawaja.mishapalert.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    private EditText etEmail, etpassword, etConfirmPassword;
    Button btnVerify;
    ImageView imgvBack;
    private ProgressDialog progressDialog;
    private FirebaseAuth mfirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    String mverificationid;

    @Override
    protected void onStart() {
        super.onStart();
        mfirebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etpassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmpass);
        imgvBack = (ImageView) findViewById(R.id.imgvBack);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        progressDialog = new ProgressDialog(this);
        mfirebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(SignUp.this, setprofile.class);
                    startActivity(intent);
                    finish();
                }
            }
        };


        imgvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Signing Up");
                progressDialog.setCancelable(false);
                progressDialog.create();
                progressDialog.show();

                final String email = etEmail.getText().toString();
                final String pass = etpassword.getText().toString();
                final String confirmpass = etConfirmPassword.getText().toString();

                if (email.isEmpty()) {
                    progressDialog.dismiss();
                    etEmail.setError("Enter Email");
                } else if (pass.isEmpty()) {
                    progressDialog.dismiss();
                    etpassword.setError("Enter Password");
                } else if (confirmpass.isEmpty()) {
                    progressDialog.dismiss();
                    etConfirmPassword.setError("Enter Confirm Password");
                } else if (!pass.equals(confirmpass) ){
                    progressDialog.dismiss();
                    Toast.makeText(SignUp.this, "Password does not match", Toast.LENGTH_SHORT).show();
                } else {

                    mfirebaseAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Intent i = new Intent(SignUp.this, VerifyEmail.class);
                                        i.putExtra("email", email);
                                        i.putExtra("pass", pass);
                                        startActivity(i);
                                    } else {

                                        Toast.makeText(SignUp.this, "Email already exists", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }

                                }
                            });


                }


            }

        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignUp.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

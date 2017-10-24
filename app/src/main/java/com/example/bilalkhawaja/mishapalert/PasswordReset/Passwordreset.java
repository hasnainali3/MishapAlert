package com.example.bilalkhawaja.mishapalert.PasswordReset;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilalkhawaja.mishapalert.Profiles.EditProfile;
import com.example.bilalkhawaja.mishapalert.R;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Passwordreset extends AppCompatActivity {
    Firebase ref;
    EditText etEmail, etOldpassword, etNewassword;
    Button btnSetpassword;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordreset);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etOldpassword = (EditText) findViewById(R.id.etOldpassword);
        etNewassword = (EditText) findViewById(R.id.etNewpass);
        btnSetpassword = (Button) findViewById(R.id.btnSetpassword);


        user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();


        btnSetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AuthCredential credential = EmailAuthProvider.getCredential(etEmail.getText().toString(), etOldpassword.getText().toString());
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(etNewassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Passwordreset.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Passwordreset.this, "Error password not updated", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(Passwordreset.this, "Error password not updated \n Check your Email and Old password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

//

            }
        });


    }
}

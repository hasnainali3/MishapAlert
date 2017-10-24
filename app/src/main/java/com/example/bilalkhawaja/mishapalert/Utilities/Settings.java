package com.example.bilalkhawaja.mishapalert.Utilities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilalkhawaja.mishapalert.NewFeeds.Home;
import com.example.bilalkhawaja.mishapalert.PasswordReset.Passwordreset;
import com.example.bilalkhawaja.mishapalert.Profiles.EditProfile;
import com.example.bilalkhawaja.mishapalert.Profiles.Profile;
import com.example.bilalkhawaja.mishapalert.R;
import com.example.bilalkhawaja.mishapalert.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Settings extends AppCompatActivity {
    ListView listView;
    EditText etRadius;
    ImageView backArrow;
    DatabaseReference databaseReference;



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Settings.this, Home.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        etRadius = new EditText(Settings.this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users/"+userid);
        String[] settings = {"Profile", "Edit Profile","Change Password", "Privacy Policy", "Set Radius", "Log Out"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, settings);

        backArrow = (ImageView)findViewById(R.id.backArrow);
        listView = (ListView) findViewById(R.id.lvSettings);
        listView.setAdapter(adapter);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this,Home.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (listView.getItemAtPosition(position) == "Profile")
                {
                    Intent intent = new Intent(Settings.this,Profile.class);
                    startActivity(intent);
                }
                if (listView.getItemAtPosition(position) == "Edit Profile")
                {
                    Intent intent = new Intent(Settings.this,EditProfile.class);
                    startActivity(intent);
                }
                if (listView.getItemAtPosition(position) == "Change Password")
                {
                    Intent intent = new Intent(Settings.this, Passwordreset.class);
                    startActivity(intent);
                    finish();
                }
                if (listView.getItemAtPosition(position) == "Privacy Policy")
                {
                    Toast.makeText(Settings.this,"Privacy Policy", Toast.LENGTH_SHORT).show();
                }
                if (listView.getItemAtPosition(position) == "Set Radius")
                {

                    builder.setCancelable(false);

                    if(etRadius.getParent()!=null)
                    {
                        ((ViewGroup)etRadius.getParent()).removeView(etRadius);
                        builder.setView(etRadius);
                    }
                    builder.setView(etRadius);
                    builder.setTitle("Set Radius.");
                    builder.setMessage("Radius should be between 10 to 50 Km.");
                    builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String regexStr = "^[1-9]\\d*(\\.\\d+)?$";
                            if(etRadius.getText().toString().trim().matches(regexStr)) {

                                if (Double.valueOf(etRadius.getText().toString()) >= 10 && Double.valueOf(etRadius.getText().toString()) <= 50) {
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("radius", etRadius.getText().toString());
                                    databaseReference.updateChildren(map);
                                    Toast.makeText(Settings.this, "Radius Updated", Toast.LENGTH_SHORT).show();
                                    etRadius.setText("");
                                } else {

                                    Toast.makeText(Settings.this, "Radius should be between 10 to 50 Km", Toast.LENGTH_SHORT).show();
                                }
                            }

                            else {
                                Toast.makeText(Settings.this, "Enter Interger value", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder.create();
                    builder.show();
                }
                if (listView.getItemAtPosition(position) == "Log Out")
                {

                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(Settings.this);
                    alertdialog.setTitle("Log Out");
                    alertdialog.setMessage("Are you sure you want to log out?");
                    alertdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(Settings.this, MainActivity.class);
                            startActivity(intent);


                        }
                    });
                    alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    alertdialog.create();
                    alertdialog.show();
                }
            }
        });



    }


}

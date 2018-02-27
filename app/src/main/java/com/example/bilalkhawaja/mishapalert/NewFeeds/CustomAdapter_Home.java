package com.example.bilalkhawaja.mishapalert.NewFeeds;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.bilalkhawaja.mishapalert.Followers.OtherUser;
import com.example.bilalkhawaja.mishapalert.MapActivity.MapsActivity2;
import com.example.bilalkhawaja.mishapalert.Profiles.Profile;
import com.example.bilalkhawaja.mishapalert.R;
import com.example.bilalkhawaja.mishapalert.Registration.PostModel;
import com.example.bilalkhawaja.mishapalert.Utilities.SendMail;
import com.example.bilalkhawaja.mishapalert.Utilities.imagezoom;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hasnain Ali on 10/30/2017.
 */

public class CustomAdapter_Home extends ArrayAdapter {

    Context context;
    ArrayList<PostModel> list;
    ArrayList<String> images;
    FirebaseUser user;
    ImagePopup imagePopup;
    Dialog dialog, image_dialog;
    VideoView videoview;
    ImageView ivzoom, popPlay;
    Uri imageuri = null;
    WindowManager.LayoutParams lp;
    ProgressDialog progressDialog;
    RelativeLayout RLreport;

    public CustomAdapter_Home(Context context, ArrayList<PostModel> list) {
        super(context, R.layout.customlayout, list);
        this.context = context;
        this.list = list;
        imagePopup = new ImagePopup(context);
        imagePopup.setWindowHeight(1550); // Optional
        imagePopup.setWindowWidth(800);
        //imagePopup.setBackgroundColor(Color.BLACK);  // Optional
        imagePopup.setHideCloseIcon(false);  // Optional
        imagePopup.setImageOnClickClose(true);  // Optional
        images = new ArrayList<>();


    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater.from(getContext()));
        convertView = layoutInflater.inflate(R.layout.customlayot_homescreen, parent, false);
        TextView username = (TextView) convertView.findViewById(R.id.tvUsername);
        TextView date = (TextView) convertView.findViewById(R.id.tvDate);
        TextView description = (TextView) convertView.findViewById(R.id.tvDescription);
        TextView severity = (TextView) convertView.findViewById(R.id.tvSeverity);
        SimpleDraweeView profilepicture = (SimpleDraweeView) convertView.findViewById(R.id.ivProfilePic);
        final SimpleDraweeView postpicture = (SimpleDraweeView) convertView.findViewById(R.id.ivPost);
        final TextView lat = (TextView) convertView.findViewById(R.id.tvlat);
        final TextView lon = (TextView) convertView.findViewById(R.id.tvlong);
        ImageView loc = (ImageView) convertView.findViewById(R.id.ivmap);
        final VideoView video = (VideoView) convertView.findViewById(R.id.video);
        user = FirebaseAuth.getInstance().getCurrentUser();
        final ImageView play = (ImageView) convertView.findViewById(R.id.play);
        ImageView fullscreen = (ImageView) convertView.findViewById(R.id.fullscreen);
        TextView report = (TextView) convertView.findViewById(R.id.tvreport);


        progressDialog = new ProgressDialog(context);
        //Video pop
        dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.videopopp);
        lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.dimAmount = 0;
        videoview = (VideoView) dialog.findViewById(R.id.vvPopup);
        popPlay = (ImageView) dialog.findViewById(R.id.play);


        //Image pop
        image_dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        image_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        image_dialog.setContentView(R.layout.imagezoom);
        final WindowManager.LayoutParams lp1 = new WindowManager.LayoutParams();
        lp1.copyFrom(image_dialog.getWindow().getAttributes());
        lp1.dimAmount = 0;
        ivzoom = (ImageView) image_dialog.findViewById(R.id.imagezoom);


        final PostModel data = list.get(position);
        // Picasso.with(getContext()).load(data.getProfileImage()).fit().centerCrop().into(profilepicture);
        description.setText(data.getDescription());
        date.setText(data.getDateTime());
        severity.setText(data.getSeverity());
        username.setText(data.getName());
        profilepicture.setImageURI(data.getUri());


        date.setText(data.getDateTime());

        lat.setText(data.getLat());
        lon.setText(data.getLon());
        final Uri uri = Uri.parse(data.getPosturi());


        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Report");
                builder.setMessage("Are you sure you want to report this post?");
                builder.setView(R.layout.report);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int fake = data.getFake();
                        if (data.getReport() != null) {

                            if (data.getReport().get(user.getUid()) == null) {

                                Map<String, Object> map1 = new HashMap<String, Object>();
                                fake = fake +1;
                                map1.put("fake", fake);
                                FirebaseDatabase.getInstance().getReference().child("Users/" + data.getId() + "/posts/" + data.getPostid()).updateChildren(map1);

                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put(data.getId(), "reported");
                                FirebaseDatabase.getInstance().getReference().child("Users/" + data.getId() + "/posts/" + data.getPostid()).child("report").updateChildren(map);

                               // Toast.makeText(context, fake+"" , Toast.LENGTH_SHORT).show();
                                if(fake == 5)
                                {
                                    FirebaseDatabase.getInstance().getReference().child("Users/" + data.getId() + "/posts/" + data.getPostid()).removeValue();
                                    FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });

                                    //Creating SendMail object
                                    SendMail sm = new SendMail(context, data.getEmail(), "Mishap Alert Report ", "Mishp Alert Message");

                                    //Executing sendmail to send email
                                    sm.execute();
                                }

                                Toast.makeText(context, "Reported", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "You have already reported", Toast.LENGTH_SHORT).show();
                            }
                        }



                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create();
                builder.show();
            }
        });

        if (data.getMetadata().startsWith("video")) {
            video.setVisibility(View.VISIBLE);
            play.setVisibility(View.VISIBLE);
            video.setVideoURI(Uri.parse(data.getPosturi()));
            fullscreen.setVisibility(View.VISIBLE);


        } else {

            postpicture.setVisibility(View.VISIBLE);
            play.setVisibility(View.INVISIBLE);
            // Picasso.with(getContext()).load(data.getPostImage()).fit().centerCrop().into(postpicture);
            postpicture.setImageURI(Uri.parse(data.getPosturi()));

            video.getHolder();
            imageuri = Uri.parse(data.getPosturi());
            images.add(data.getPosturi());


        }


        //Full screen video options
        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Opening Video...");
                progressDialog.create();
                progressDialog.show();
                dialog.getWindow().setAttributes(lp);
                dialog.show();
                popPlay.setImageResource(android.R.color.transparent);
                videoview.setVideoURI(uri);
                videoview.start();


            }
        });

        videoview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                videoview.pause();
                popPlay.setImageResource(R.drawable.ic_play);
                return false;
            }
        });

        popPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popPlay.setImageResource(android.R.color.transparent);
                videoview.start();

            }
        });

        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                popPlay.setImageResource(R.drawable.ic_play);
            }
        });

        //main screen video options
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                video.start();
                play.setVisibility(View.INVISIBLE);


            }
        });
        video.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                video.pause();
                play.setVisibility(View.VISIBLE);
                return false;
            }
        });


        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                play.setVisibility(View.VISIBLE);
            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (data.getId().equals(user.getUid())) {
                    Intent intent = new Intent(context, Profile.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                } else {
                    Intent intent = new Intent(context, OtherUser.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("UserID", data.getId());
                    context.startActivity(intent);
                }

            }
        });

        profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.getId().equals(user.getUid())) {
                    Toast.makeText(context, data.getId(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, Profile.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                } else {
                    Intent intent = new Intent(context, OtherUser.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("UserID", data.getId());
                    context.startActivity(intent);
                }

            }
        });


        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity2.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("lat", lat.getText().toString());
                intent.putExtra("lon", lon.getText().toString());
                intent.putExtra("radius", data.getRadius());
                context.startActivity(intent);

            }
        });


        postpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, imagezoom.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("image", data.getPosturi());
                context.startActivity(i);

            }
        });


        return convertView;
    }
}



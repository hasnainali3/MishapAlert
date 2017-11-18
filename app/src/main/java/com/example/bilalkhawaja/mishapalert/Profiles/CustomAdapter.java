package com.example.bilalkhawaja.mishapalert.Profiles;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.bilalkhawaja.mishapalert.Followers.OtherUser;
import com.example.bilalkhawaja.mishapalert.MapActivity.MapsActivity2;
import com.example.bilalkhawaja.mishapalert.R;
import com.example.bilalkhawaja.mishapalert.Registration.User;
import com.example.bilalkhawaja.mishapalert.Utilities.imagezoom;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Bilal Khawaja on 22/06/2017.
 */

public class CustomAdapter extends ArrayAdapter {
    Context context;
    ArrayList<DataModel> list;
    ArrayList<String> images ;
    FirebaseUser user;
    ImagePopup imagePopup;
    Dialog dialog, image_dialog;
    VideoView videoview;
    ImageView ivzoom, popPlay;

    Uri imageuri = null;
    WindowManager.LayoutParams lp;
    ImageViewer imageViewer;
    public CustomAdapter(Context context, ArrayList<DataModel> list) {
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
        LayoutInflater layoutInflater =  (LayoutInflater.from(getContext()));
        convertView = layoutInflater.inflate(R.layout.customlayout,parent,false);
        TextView username = (TextView)convertView.findViewById(R.id.tvUsername);
        TextView date = (TextView)convertView.findViewById(R.id.tvDate);
        TextView description = (TextView)convertView.findViewById(R.id.tvDescription);
        TextView severity = (TextView)convertView.findViewById(R.id.tvSeverity);
        SimpleDraweeView profilepicture = (SimpleDraweeView)convertView.findViewById(R.id.ivProfilePic);
        final SimpleDraweeView postpicture = (SimpleDraweeView) convertView.findViewById(R.id.ivPost);
        final TextView lat = (TextView)convertView.findViewById(R.id.tvlat);
        final TextView lon = (TextView)convertView.findViewById(R.id.tvlong);
        ImageView loc = (ImageView) convertView.findViewById(R.id.ivmap);
        final VideoView video = (VideoView)convertView.findViewById(R.id.video);
        user = FirebaseAuth.getInstance().getCurrentUser();
        final ImageView play = (ImageView)convertView.findViewById(R.id.play);
        ImageView fullscreen =  (ImageView)convertView.findViewById(R.id.fullscreen);

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
        ivzoom = (ImageView)image_dialog.findViewById(R.id.imagezoom);





        final DataModel data = list.get(position);

       // Picasso.with(getContext()).load(data.getProfileImage()).fit().centerCrop().into(profilepicture);
        profilepicture.setImageURI(data.getProfileImage());
        username.setText(data.getName());
        date.setText(data.getTime());
        description.setText(data.getDescription());
        severity.setText(data.getSeverity());
        lat.setText(data.getLat());
        lon.setText(data.getLon());
        final Uri uri = Uri.parse(data.getPostImage());


        if(data.getMetadata().startsWith("video"))
        {
            video.setVisibility(View.VISIBLE);
            play.setVisibility(View.VISIBLE);
            video.setVideoURI(Uri.parse(data.getPostImage()));
            fullscreen.setVisibility(View.VISIBLE);



        }
        else {

            postpicture.setVisibility(View.VISIBLE);
            play.setVisibility(View.INVISIBLE);
          // Picasso.with(getContext()).load(data.getPostImage()).fit().centerCrop().into(postpicture);
            postpicture.setImageURI(Uri.parse(data.getPostImage()));

            video.getHolder();
            imageuri = Uri.parse(data.getPostImage());
            images.add(data.getPostImage());


        }





        //Toast.makeText(context, images.size()+"", Toast.LENGTH_SHORT).show();

      /*  imageViewer = new ImageViewer.Builder<>(context,images)
                            .setStartPosition(1).show();*/

       /* new ImageViewer.Builder(context, images)
                .setStartPosition(0)
                .show();
*/


        //Full screen video options
        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                if(data.getId().equals(user.getUid()))
                {
                    Intent intent = new Intent(context, Profile.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
                else
                {
                    Intent intent = new Intent(context, OtherUser.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("UserID",data.getId());
                    context.startActivity(intent);
                }

            }
        });

        profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data.getId().equals(user.getUid()))
                {
                    Toast.makeText(context, data.getId(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, Profile.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
                else
                {
                    Intent intent = new Intent(context, OtherUser.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("UserID",data.getId());
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
                intent.putExtra("radius", data.getRadius().toString());
                context.startActivity(intent);

            }
        });

        
        postpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, imagezoom.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("image", data.getPostImage());
                context.startActivity(i);

            }
        });




        return  convertView;
    }
}

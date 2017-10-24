package com.example.bilalkhawaja.mishapalert.Notification;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bilalkhawaja.mishapalert.Followers.OtherUser;
import com.example.bilalkhawaja.mishapalert.Notification.Notification_datamodel;
import com.example.bilalkhawaja.mishapalert.Profiles.Profile;
import com.example.bilalkhawaja.mishapalert.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Hasnain Ali on 8/14/2017.
 */

public class Notification_adapter extends ArrayAdapter {

    Context context;
    ArrayList<Notification_datamodel> list;
    FirebaseUser user;

    public Notification_adapter(Context context, ArrayList<Notification_datamodel> list) {
        super(context, R.layout.notification_customview, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater.from(getContext()));
        convertView = layoutInflater.inflate(R.layout.notification_customview, parent, false);

        TextView title = (TextView) convertView.findViewById(R.id.tvTitle);
        TextView description = (TextView) convertView.findViewById(R.id.tvDescription);
        final TextView postid = (TextView) convertView.findViewById(R.id.tvPostid);
        final TextView userid = (TextView) convertView.findViewById(R.id.tvUserid);
        final TextView type = (TextView) convertView.findViewById(R.id.type);
        SimpleDraweeView circleImageView = (SimpleDraweeView) convertView.findViewById(R.id.circleImageView);
        user = FirebaseAuth.getInstance().getCurrentUser();
        final Notification_datamodel notification_datamodel = list.get(position);

       // Picasso.with(getContext()).load(notification_datamodel.getImage()).fit().centerCrop().into(circleImageView);
        circleImageView.setImageURI(notification_datamodel.getImage());
        title.setText(notification_datamodel.getTitle());
        description.setText(notification_datamodel.getDescription());
        postid.setText(notification_datamodel.getPostid());
        userid.setText(notification_datamodel.getUserid());
        type.setText(notification_datamodel.getType());

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(userid.getText().equals(user.getUid()))
                {
                    Intent intent = new Intent(context, Profile.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(context, OtherUser.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("UserID",userid.getText());
                    context.startActivity(intent);
                }
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(userid.getText().equals(user.getUid()))
                {
                    Intent intent = new Intent(context, Profile.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(context, OtherUser.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("UserID",userid.getText());
                    context.startActivity(intent);
                }

            }
        });


        return convertView;
    }
}

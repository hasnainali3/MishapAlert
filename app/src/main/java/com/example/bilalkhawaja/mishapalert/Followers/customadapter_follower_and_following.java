package com.example.bilalkhawaja.mishapalert.Followers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bilalkhawaja.mishapalert.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Bilal Khawaja on 22/07/2017.
 */

public class customadapter_follower_and_following extends ArrayAdapter {
    Context context;
    ArrayList<DataModel_Followers_Following> list;


    public customadapter_follower_and_following(Context context, ArrayList<DataModel_Followers_Following> list) {
        super(context, R.layout.customlayout_follower_and_following, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater =  (LayoutInflater.from(getContext()));
        convertView = layoutInflater.inflate(R.layout.customlayout_follower_and_following,parent,false);
        TextView name = (TextView) convertView.findViewById(R.id.tvName);
        TextView id = (TextView) convertView.findViewById(R.id.tvid);
        ImageView image = (ImageView) convertView.findViewById(R.id.ivProfileimage);
        // RelativeLayout rluser = (RelativeLayout)convertView.findViewById(R.id.rlUser);

        final DataModel_Followers_Following data = list.get(position);

        Picasso.with(getContext()).load(data.getPicture()).fit().centerCrop().into(image);
        name.setText(data.getName());
        id.setText(data.getId());

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OtherUser.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("UserID", data.getId());
                context.startActivity(intent);

            }
        });


        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OtherUser.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("UserID", data.getId());
                context.startActivity(intent);


            }
        });

//        rluser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//        Intent intent = new Intent(context, OtherUser.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("UserID", data.getId());
//                intent.putExtra("name", data.getName());
//                intent.putExtra("pic", data.getImage());
//        context.startActivity(intent);
//
//
//            }
//        });


        return convertView;
    }
}

package com.example.bilalkhawaja.mishapalert.Utilities;

import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.bilalkhawaja.mishapalert.R;
import com.squareup.picasso.Picasso;

import me.relex.photodraweeview.PhotoDraweeView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.R.attr.data;

public class imagezoom extends AppCompatActivity {

    PhotoDraweeView image;
    PhotoViewAttacher photoViewAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_imagezoom);

        image = (PhotoDraweeView) findViewById(R.id.image);

         image.setPhotoUri(Uri.parse(getIntent().getStringExtra("image")));

      /*  Picasso.with(this).load(getIntent().getStringExtra("image")).fit().centerCrop().into(image);*/

        Log.d("image", getIntent().getStringExtra("image"));

       /* photoViewAttacher = new PhotoViewAttacher(image, true);
        photoViewAttacher.update();*/


    }
}

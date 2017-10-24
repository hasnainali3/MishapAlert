package com.example.bilalkhawaja.mishapalert.Utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.example.bilalkhawaja.mishapalert.Posts.Post;
import com.example.bilalkhawaja.mishapalert.R;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Locale;

import static com.example.bilalkhawaja.mishapalert.R.id.imageView;

/**
 * Created by Hasnain Ali on 8/21/2017.
 */

public class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

    Context mContext;
    String filePath = null;
    private final AsyncResponse delegate;


    public VideoCompressAsyncTask(Context context) {

       delegate = (AsyncResponse) context;
        mContext = context;
    }




    @Override
    public String doInBackground(String... paths) {

        try {

            filePath = SiliCompressor.with(mContext).compressVideo(paths[0], paths[1]);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return filePath;

    }

    @Override
    protected void onPostExecute(String s) {
        delegate.processFinish(s);
        super.onPostExecute(s);
    }

}


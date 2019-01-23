package com.bxtore.dev.bxt.Others;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

/**
 * Created by Deepak Prasad on 28-09-2018.
 */

public class ImageCompressor {

    Context context;

    public ImageCompressor(Context context){
        this.context = context;
    }

    public Uri compressImage(Uri picUri){
        File originalImage = new File(picUri.getPath());

        try {
            originalImage = new Compressor(this.context).compressToFile(originalImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  picUri;
    }
}

package com.sixbynine.infosessions.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by stevenkideckel on 2014-06-09.
 */
public class CompanyImageLoader {

    private static Map<String, Bitmap> sCompanyImages;

    public interface Callback{
        public void onImageLoaded(Bitmap img);
        public void onError(Throwable e);
    }

    public static void getImage(String url, Callback callback){
        if(url == null) throw new IllegalArgumentException("url cannot be null");
        if(callback == null) throw new IllegalArgumentException("callback cannot be null");
        if(sCompanyImages == null) sCompanyImages = new HashMap<String, Bitmap>();
        if(sCompanyImages.containsKey(url)){ //try to load from map
            callback.onImageLoaded(sCompanyImages.get(url));
        }else{
            Bitmap savedImage = loadImage(url); //try to load from storage
            if(savedImage == null){
                new AsyncInternetImageLoader(url, callback).execute(); //try to load from internet
            }else{
                sCompanyImages.put(url, savedImage);
                callback.onImageLoaded(savedImage);
            }
        }
    }

    private static Bitmap loadImage(String url){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/infosessions");
        myDir.mkdirs();
        File file = new File (myDir, url);
        if (file.exists ()){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            return bitmap;
        }else{
            return null;
        }
    }

    private static void saveImage(String url, Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/infosessions");
        myDir.mkdirs();
        File file = new File (myDir, url);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class AsyncInternetImageLoader extends AsyncTask<Void, Void, Bitmap>{

        private String url;
        private Callback callback;

        public AsyncInternetImageLoader(String url, Callback callback){
            this.url = url;
            this.callback = callback;
        }

        @Override
        protected Bitmap doInBackground(Void... args) {
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                callback.onError(e);
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null){
                sCompanyImages.put(url, bitmap);
                saveImage(url, bitmap);
                callback.onImageLoaded(bitmap);
            }else{
                callback.onError(new Exception("result was null!"));
            }
        }
    }
}

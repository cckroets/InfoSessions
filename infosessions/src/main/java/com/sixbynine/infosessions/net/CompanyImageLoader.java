package com.sixbynine.infosessions.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.sixbynine.infosessions.object.company.Company;

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

    public interface Callback {
        public void onImageLoaded(Bitmap img);

        public void onError(Throwable e);
    }

    public static void getImage(Company company, Callback callback) {
        if (company == null) throw new IllegalArgumentException("url cannot be null");
        if (callback == null) throw new IllegalArgumentException("callback cannot be null");
        if (company.getPermalink() == null)
            throw new IllegalArgumentException("company permalink field not populated");
        if (sCompanyImages == null) sCompanyImages = new HashMap<String, Bitmap>();
        if (sCompanyImages.containsKey(company.getPermalink())) { //try to load from map
            callback.onImageLoaded(sCompanyImages.get(company.getPermalink()));
        } else {
            Bitmap savedImage = loadImage(company.getPermalink()); //try to load from storage
            if (savedImage == null) {
                new AsyncInternetImageLoader(company.getPrimaryImageUrl(), callback).execute(); //try to load from internet
            } else {
                sCompanyImages.put(company.getPermalink(), savedImage);
                callback.onImageLoaded(savedImage);
            }
        }
    }

    private static Bitmap loadImage(String permalink) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/infosessions");
        myDir.mkdirs();
        File file = new File(myDir, permalink + ".bmp");
        if (file.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * saves the image to the external storage
     *
     * @param permalink   the permalink of the company
     * @param finalBitmap the bitmap to save
     *                    The
     */
    private static void saveImage(String permalink, Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/infosessions");
        myDir.mkdirs();
        File file = new File(myDir, permalink + ".bmp");
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class AsyncInternetImageLoader extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private Callback callback;

        public AsyncInternetImageLoader(String url, Callback callback) {
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
            if (bitmap != null) {
                sCompanyImages.put(url, bitmap);
                saveImage(url, bitmap);
                callback.onImageLoaded(bitmap);
            } else {
                callback.onError(new Exception("result was null!"));
            }
        }
    }
}

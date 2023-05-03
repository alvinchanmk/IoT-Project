package com.example.iotv1app;
import android.Manifest;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class BackgroundNetwork extends AsyncTask<String, Void, String> {

    private static final String TAG = "UploadImageTask";
    private static String SERVER_URL = "http://192.168.142.1:5000/";

    private OnTaskCompletedListener mListener;


    @Override
    protected String doInBackground(String... params) {

        String action = params[0];
        String imagePath = null;
        String to_return = null;
        String model = null;

        switch (action) {
            case "upload_train":
                imagePath = params[1];
                model = params[2];
                to_return =  upload_image(imagePath,action,model);
                break;
            case "upload_filter":
                imagePath = params[1];
                model = params[2];
                to_return = upload_image(imagePath,action,model);
                break;
            case "get_models":
                to_return = get_models(action);
                break;
            case "get_image":
                String filename = params[1];
                get_image(action,filename);
                break;
            default:
                System.out.println("Invalid day of the week");
                break;
        }
        return to_return;
    }

    protected String upload_image(String imagePath, String destination,String model) {
        try {
            File imageFile = new File(imagePath);
            return HttpManager.uploadImage(SERVER_URL+destination, imageFile,model);
        } catch (IOException e) {
            Log.e(TAG, "Failed to upload image", e);
            return null;
        }
    }
    protected void get_image(String destination, String filename) {


        String download_path ="/storage/emulated/0/Pictures/"+filename;
        try {
            HttpManager.downloadImage(SERVER_URL+destination +"/" + filename, download_path);
        } catch (IOException e) {
            Log.e(TAG, "Failed to get image", e);
        }
    }

    protected String get_models(String destination) {
        try {
            return HttpManager.sendGetRequest(SERVER_URL+destination);
        } catch (IOException e) {
            Log.e(TAG, "Failed to get models", e);
            return null;
        }
    }


    public void setOnTaskCompletedListener(OnTaskCompletedListener listener) {
        mListener = listener;
    }
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result.toString());
        if (mListener != null) {
            mListener.onTaskCompleted(result);
        }
    }

    public interface OnTaskCompletedListener {
        void onTaskCompleted(Bitmap result);
    }

}


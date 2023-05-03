package com.example.iotv1app;

import static android.app.Activity.RESULT_OK;
import static com.example.iotv1app.TrainFragment.PICK_IMAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment implements BackgroundNetwork.OnTaskCompletedListener {
    String[] items = new String[]{"Item 1", "Item 2", "Item 3", "Item 4"};
    String selected_model = null;

    String base_save_location = "/storage/emulated/0/Pictures/";

    String uploaded_file = null;

    public void update_models() {

        String models = null;
        BackgroundNetwork backgroundNetwork = new BackgroundNetwork();
        backgroundNetwork.execute("get_models");
        try {
            models = backgroundNetwork.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(models);

        String[] stringArray = new String[0];
        try {
            JSONObject jsonObject = new JSONObject(models);
            stringArray = new String[jsonObject.length()];

            for (int i = 0; i < jsonObject.length(); i++) {
                String value = jsonObject.optString(String.valueOf(i), "");
                stringArray[i] = value;
            }

            // Do something with the string array
            // ...
        } catch (JSONException e) {
            e.printStackTrace();
        }

        items = new String[]{"changed"};
        items = stringArray;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ImageView uploadedImg = view.findViewById(R.id.uploadedImg);

        Button uploadImgBtn = (Button) view.findViewById(R.id.uploadImgBtn);
        uploadImgBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the uploadImgBtn");

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

            }


        });

        update_models();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);

        Spinner selectFilterBtn = (Spinner) view.findViewById(R.id.selectFilterBtn);
        selectFilterBtn.setAdapter(adapter);
        selectFilterBtn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("User selected model:");
                System.out.println(items[i]);


                String filenameWithExtension = items[i];
                String[] parts = filenameWithExtension.split("\\.");
                String filename = parts[0]; // "arnold"
                String extension = parts[1]; // "jpg"

                selected_model = filename;


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button applyBtn = (Button) view.findViewById(R.id.applyBtn);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the applyBtn");

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Permission not granted, request it
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }

                String filenameWithExtension = uploaded_file;
                String[] parts = filenameWithExtension.split("\\.");
                String filename = parts[0]; // "arnold"
                String extension = parts[1]; // "jpg"

                String new_file_name = filename + "_" + selected_model +".jpg";

                BackgroundNetwork backgroundNetwork = new BackgroundNetwork();
                backgroundNetwork.setOnTaskCompletedListener(HomeFragment.this);
                backgroundNetwork.execute("get_image", new_file_name);

                ImageView imageView = getActivity().findViewById(R.id.downloadedImg);
                String image_path = base_save_location + new_file_name;
                Bitmap bitmap = BitmapFactory.decodeFile(image_path);
                imageView.setImageBitmap(bitmap);


            }

        });

        return view;
    }

    @Override
    public void onTaskCompleted(Bitmap result) {
        System.out.println("running on task completed");
        // Update your ImageView with the downloaded image
        ImageView imageView = getActivity().findViewById(R.id.downloadedImg);
        String image_path = base_save_location + "hz.jpg";
        Bitmap bitmap = BitmapFactory.decodeFile(image_path);
        imageView.setImageBitmap(bitmap);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("Activity result");

        System.out.println(data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {

            Uri imageUri = data.getData();

            String image_path = getPathFromUri(null, imageUri);

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, so we can access the file
                File externalDir = Environment.getExternalStorageDirectory();
                File file = new File(externalDir, "Pictures/IMG_20230430_040833.jpg");
                // Do something with the file
            } else {
                // Permission is not granted, so we need to request it from the user
                ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 1000);
            }

            Context context = getContext();
            File cwd = context.getApplicationContext().getFilesDir();
            String cwdPath = cwd.getAbsolutePath();
            Log.d("CWD", cwdPath);
            System.out.println(image_path);

            if (image_path != null) {
                System.out.println(image_path);
                File imageFile = new File(image_path);
                uploaded_file = imageFile.getName();
                System.out.println(uploaded_file);
                ImageView imageView = getActivity().findViewById(R.id.uploadedImg);
                Bitmap bitmap = BitmapFactory.decodeFile(image_path);
                imageView.setImageBitmap(bitmap);

                BackgroundNetwork backgroundNetwork = new BackgroundNetwork();
                backgroundNetwork.execute("upload_filter",image_path,selected_model);
            }
        }
    }


    @SuppressLint("Range")
    private String getPathFromUri(Context context, Uri uri) {

        context = getContext();
        String path = null;
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            cursor.moveToFirst();

            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        } finally {
            cursor.close();
        }

        return path;
    }

}
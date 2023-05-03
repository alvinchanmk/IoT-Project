package com.example.iotv1app;


import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;


import java.io.File;
import java.io.IOException;



public class TrainFragment extends Fragment {
    public static final int PICK_IMAGE = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_train, container, false);

        Button uploadBaseImgBtn = (Button) view.findViewById(R.id.uploadBaseImgBtn);
        uploadBaseImgBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the uploadBaseImgBtn");

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);


            }
        });


        Button trainBtn = (Button) view.findViewById(R.id.trainBtn);
        trainBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the trainBtn");
            }
        });

        return view;
    }


    @Override
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
//                uploaded_file = imageFile.getName();
//                System.out.println(uploaded_file);
                ImageView imageView = getActivity().findViewById(R.id.trainImg);
                Bitmap bitmap = BitmapFactory.decodeFile(image_path);
                imageView.setImageBitmap(bitmap);


                BackgroundNetwork backgroundNetwork = new BackgroundNetwork();
                backgroundNetwork.execute("upload_train",image_path,"uwu");
            }
        }
    }


    @SuppressLint("Range")
    private String getPathFromUri(Context context, Uri uri) {

        context = getContext();
        String path = null;/*from  w ww  . j av  a2s  .c o m*/
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



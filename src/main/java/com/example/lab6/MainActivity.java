package com.example.lab6;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.Manifest;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> ImageCaptureActivityResultLauncher;
    String currentPhotoPath;
    private Uri photoURI;

    private File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try{
            photoFile = createImageFile();
        }catch(IOException ex){

        }

        if(photoFile != null)
        {
            photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                    BuildConfig.APPLICATION_ID + ".provider", photoFile);
//            photoURI = FileProvider.getUriForFile(this,
//                    "com.example.android.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            ImageCaptureActivityResultLauncher.launch(takePictureIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.image1);
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view){dispatchTakePictureIntent();}
                }
        );

        ActivityResultLauncher<String[]> cameraPermissionRequest =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                        result -> {});
        cameraPermissionRequest.launch(new String[]{Manifest.permission.CAMERA});

        ImageCaptureActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){

                            Bitmap imageBitmap = null;
                            try{
                                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                                ImageDecoder.Source source = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    source = ImageDecoder.createSource(getContentResolver(), photoURI);
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    imageBitmap = ImageDecoder.decodeBitmap(source);
                                }
                            } catch(IOException e){
                                e.printStackTrace();
                            }

                            imageView.setImageBitmap(imageBitmap);
                        }
                    }
                }
        );
    }// end ONCREATE

}
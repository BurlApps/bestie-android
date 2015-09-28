package com.gmail.nelsonr462.bestie.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gmail.nelsonr462.bestie.R;
import com.isseiaoki.simplecropview.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CropPhotoActivity extends AppCompatActivity implements EditPhotosFragment.OnFragmentInteractionListener {
    private final String TAG = CropPhotoActivity.class.getSimpleName();
    protected Uri mMediaUri;

    protected CropImageView mCropImageView;
    protected Button mCropButton;
    protected Button mRotateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_photo);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        toolbar.setBackgroundColor(getResources().getColor(R.color.tabsScrollColor));
        toolbar.setTitleTextColor(getResources().getColor(R.color.tabsScrollColor));

        mCropImageView = (CropImageView) findViewById(R.id.cropImageView);
        mCropButton = (Button) findViewById(R.id.cropButton);
        mRotateButton = (Button) findViewById(R.id.rotateButton);

        Intent cropIntent = getIntent();
        mMediaUri = getOutputMediaFileUri();

        if(mMediaUri == null) {
            Toast.makeText(this, "There was an error accessing your internal storage", Toast.LENGTH_LONG).show();
        } else {
            Bitmap bitmap;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), cropIntent.getData());
                mCropImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCropImageView.setHandleShowMode(CropImageView.ShowMode.SHOW_ALWAYS);
            mCropImageView.setGuideShowMode(CropImageView.ShowMode.SHOW_ON_TOUCH);
            mCropImageView.setCropMode(CropImageView.CropMode.RATIO_1_1);

        }


        mCropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appName = getApplicationContext().getString(R.string.app_name);
                File mediaStorageDir = new File(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        appName);
                // Create subdirectory
                if(! mediaStorageDir.exists()) {
                    if( ! mediaStorageDir.mkdirs() ) {
                        Log.e(TAG, "Failed to create directory");
                        return;
                    }
                }

                File mediaFile;
                Date now = new Date();
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);
                String path = mediaStorageDir.getPath() + File.separator;
                mediaFile = new File(path + "IMG_" + timestamp + ".jpg");

                OutputStream fOut = null;
                try {
                    fOut = new FileOutputStream(mediaFile);
                    mCropImageView.getCroppedBitmap().compress(Bitmap.CompressFormat.PNG, 50, fOut); // obtaining the Bitmap

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    if(fOut != null) {
                        fOut.flush();
                        fOut.close(); // do not forget to close the stream
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(mediaFile));
                sendBroadcast(mediaScanIntent);

                Toast.makeText(CropPhotoActivity.this, "Photo cropped!", Toast.LENGTH_SHORT).show();
                finish();

            }
        });

        mRotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            }
        });

    }

    private Uri getOutputMediaFileUri() {
        if(isExternalStorageAvailable()) {
            // Get URI
            // Get external storage directory
            String appName = this.getString(R.string.app_name);
            File mediaStorageDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    appName);
            // Create subdirectory
            if(! mediaStorageDir.exists()) {
                if( ! mediaStorageDir.mkdirs() ) {
                    Log.e(TAG, "Failed to create directory");
                    return null;
                }
            }
            // Create a file name
            // Create the file
            File mediaFile;
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);
            String path = mediaStorageDir.getPath() + File.separator;
                mediaFile = new File(path + "IMG_" + timestamp + ".jpg");


            Log.d(TAG, "File: "+ Uri.fromFile(mediaFile));

            // Return the file's URI

            return Uri.fromFile(mediaFile);
        } else {
            return null;
        }
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onFragmentInteraction(String id) {

    }
}

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

import com.gmail.nelsonr462.bestie.ParseImageUploader;
import com.gmail.nelsonr462.bestie.R;
import com.isseiaoki.simplecropview.CropImageView;
import com.parse.ConfigCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;

import java.io.ByteArrayOutputStream;
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
        toolbar.setTitleTextColor(getResources().getColor(R.color.tabsScrollColor));

        mCropImageView = (CropImageView) findViewById(R.id.cropImageView);
        mCropButton = (Button) findViewById(R.id.cropButton);
        mRotateButton = (Button) findViewById(R.id.rotateButton);

        Intent cropIntent = getIntent();

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



        mCropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mCropImageView.getCroppedBitmap().compress(Bitmap.CompressFormat.JPEG, 70, stream);
                final byte[] imageBitmap = stream.toByteArray();


                /*  IMAGE UPLOADER BEGIN  */
                ParseConfig.getInBackground(new ConfigCallback() {
                    @Override
                    public void done(ParseConfig parseConfig, ParseException e) {
                        ParseImageUploader imageUploader = new ParseImageUploader(parseConfig);
                        imageUploader.newParseImage(imageBitmap);

                        finish();
                    }
                });

            }
        });

        mRotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            }
        });

    }


    @Override
    public void onFragmentInteraction(String id) {

    }
}

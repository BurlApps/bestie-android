package com.gmail.nelsonr462.bestie.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gmail.nelsonr462.bestie.helpers.ParseImageUploader;
import com.gmail.nelsonr462.bestie.R;
import com.isseiaoki.simplecropview.CropImageView;
import com.parse.ConfigCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CropPhotoActivity extends AppCompatActivity {
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

        assert  getSupportActionBar() != null;
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
}

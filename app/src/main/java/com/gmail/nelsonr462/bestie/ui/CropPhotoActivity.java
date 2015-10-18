package com.gmail.nelsonr462.bestie.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.nelsonr462.bestie.helpers.ParseImageUploader;
import com.gmail.nelsonr462.bestie.R;
import com.isseiaoki.simplecropview.CropImageView;
import com.parse.ConfigCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CropPhotoActivity extends AppCompatActivity {
    private final String TAG = CropPhotoActivity.class.getSimpleName();

    protected CropImageView mCropImageView;
    protected Button mCropButton;
    protected Button mRotateButton;
    private ContentResolver mContentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_photo);

        Drawable back = getResources().getDrawable(R.mipmap.ic_chevron_left_white_24dp);
        assert back!=null;
        back.setColorFilter(getResources().getColor(R.color.bestieRed), PorterDuff.Mode.MULTIPLY);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setNavigationIcon(back);
        setSupportActionBar(toolbar);

        assert  getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tabsScrollColor));

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Bariol_Regular.ttf");

        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setTypeface(typeface);

        mCropImageView = (CropImageView) findViewById(R.id.cropImageView);
        mCropButton = (Button) findViewById(R.id.cropButton);
        mRotateButton = (Button) findViewById(R.id.rotateButton);
        mRotateButton.setTypeface(typeface);
        mCropButton.setTypeface(typeface);
        mContentResolver = this.getContentResolver();

        Intent cropIntent = getIntent();

        Bitmap bitmap = getBitmap(cropIntent.getData().toString());

        mCropImageView.setImageBitmap(bitmap);

        mCropImageView.setHandleShowMode(CropImageView.ShowMode.SHOW_ALWAYS);
        mCropImageView.setGuideShowMode(CropImageView.ShowMode.SHOW_ON_TOUCH);
        mCropImageView.setCropMode(CropImageView.CropMode.RATIO_1_1);
        mCropImageView.setInitialFrameScale(1.0f);
        mCropImageView.setHandleSizeInDp(8);
        mCropImageView.setTouchPaddingInDp(12);

        mCropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCropButton.setEnabled(false);
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

    private Bitmap getBitmap(String path) {

        Uri uri = Uri.parse(path);
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = mContentResolver.openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d(TAG, "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = mContentResolver.openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d(TAG, "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d(TAG, "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }
}

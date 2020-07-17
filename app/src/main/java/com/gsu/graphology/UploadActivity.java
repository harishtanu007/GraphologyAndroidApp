package com.gsu.graphology;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class UploadActivity extends AppCompatActivity {

    private static final String TAG = UploadActivity.class.getSimpleName();

    private String email;
    public static final int REQUEST_IMAGE1 = 100;
    public static final int REQUEST_IMAGE2 = 200;
    public static final int REQUEST_IMAGE3 = 300;
    private ImageView imgView1;
    private ImageView imgView2;
    private ImageView imgView3;
    private TextView filename1;
    private TextView filename2;
    private TextView filename3;
    private LinearLayout layout1;
    private LinearLayout layout2;
    private LinearLayout layout3;
    private TextView uniqueID;
    private String uniqueIDText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        imgView1 = findViewById(R.id.imageviewMain1);
        imgView2 = findViewById(R.id.imageviewMain2);
        imgView3 = findViewById(R.id.imageviewMain3);
        filename1 = findViewById(R.id.filename1);
        filename2 = findViewById(R.id.filename2);
        filename3 = findViewById(R.id.filename3);
        layout1 = findViewById(R.id.r_layout1);
        layout2 = findViewById(R.id.r_layout2);
        layout3 = findViewById(R.id.r_layout3);
        uniqueID=findViewById(R.id.uniqueIDView);
        getSupportActionBar().setTitle("Upload Images");
        Intent intent = getIntent();
        email = intent.getStringExtra("emailID");
        uniqueIDText = intent.getStringExtra("uniqueID");

        uniqueID.setText(uniqueIDText);

        loadProfileDefault();
        // Clearing older images from cache directory
        // don't call this line if you want to choose multiple images in the same activity
        // call this once the bitmap(s) usage is over
        ImagePickerActivity.clearCache(this);

        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick(REQUEST_IMAGE1);
            }
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick(REQUEST_IMAGE2);
            }
        });
        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick(REQUEST_IMAGE3);
            }
        });
    }

    private void loadProfile(String url, int REQUEST_IMAGE) {
        Log.d(TAG, "Image cache path: " + url);


        if (REQUEST_IMAGE == REQUEST_IMAGE1) {
            filename1.setText(url.substring( url.lastIndexOf('/')+1, url.length() ));

            imgView1.setVisibility(View.VISIBLE);
            Glide.with(this).load(url)
                    .into(imgView1);
            imgView1.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));

        }
        if (REQUEST_IMAGE == REQUEST_IMAGE2) {
            filename2.setText(url.substring( url.lastIndexOf('/')+1, url.length() ));
            imgView2.setVisibility(View.VISIBLE);
            Glide.with(this).load(url)
                    .into(imgView2);
            imgView2.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
        }
        if (REQUEST_IMAGE == REQUEST_IMAGE3) {
            filename3.setText(url.substring( url.lastIndexOf('/')+1, url.length() ));
            imgView3.setVisibility(View.VISIBLE);
            Glide.with(this).load(url)
                    .into(imgView3);
            imgView3.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
        }
    }

    private void loadProfileDefault() {
        Glide.with(this).load(R.mipmap.ic_launcher)
                .into(imgView1);
        imgView1.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
    }


    void onImageClick(final int REQUEST_IMAGE) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions(REQUEST_IMAGE);
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions(final int REQUEST_IMAGE) {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent(REQUEST_IMAGE);
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent(REQUEST_IMAGE);
            }
        });
    }

    private void launchCameraIntent(int REQUEST_IMAGE) {
        Intent intent = new Intent(UploadActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent(int REQUEST_IMAGE) {
        Intent intent = new Intent(UploadActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE1) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    // loading profile image from local cache
                    loadProfile(uri.toString(), REQUEST_IMAGE1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == REQUEST_IMAGE2) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    // loading profile image from local cache
                    loadProfile(uri.toString(), REQUEST_IMAGE2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == REQUEST_IMAGE3) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    // loading profile image from local cache
                    loadProfile(uri.toString(), REQUEST_IMAGE3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openSettings();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

}
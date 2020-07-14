package com.gsu.graphology;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private int PICK_IMAGE_REQUEST1 = 1;
    private int PICK_IMAGE_REQUEST2 = 2;
    private int PICK_IMAGE_REQUEST3 = 3;
    private int PICK_IMAGE_REQUEST4 = 4;
    private int PICK_IMAGE_REQUEST5 = 5;
    private int PICK_IMAGE_REQUEST6 = 6;

    private ImageButton button1;
    private ImageButton button2;
    private ImageButton button3;
    private ImageButton button4;
    private String email;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        button1=findViewById(R.id.but1);
        button2=findViewById(R.id.but2);
        button3=findViewById(R.id.but3);
        button4=findViewById(R.id.but4);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        getSupportActionBar().setTitle("Upload Images");
        Intent intent = getIntent();
        email = intent.getStringExtra("emailID");

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.but1:
                // Do something
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    }
                    else {
                        selectImage(this, PICK_IMAGE_REQUEST1, PICK_IMAGE_REQUEST2);
                    }
                }
                //chooseImage(1);
                break;
            case R.id.but2:
                // Do something
                selectImage(this,PICK_IMAGE_REQUEST3,PICK_IMAGE_REQUEST4);
                break;
            case R.id.but3:
                // Do something
                selectImage(this,PICK_IMAGE_REQUEST5,PICK_IMAGE_REQUEST6);
                break;
            case R.id.but4:
                // Do something
                selectImage(this,1,2);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, PICK_IMAGE_REQUEST1);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void selectImage(Context context, final int image_request1, final int image_request2) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Upload Image");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(takePicture, image_request1);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , image_request2);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, String.valueOf(resultCode), Toast.LENGTH_SHORT).show();
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            String realPath = ImageFilePath.getPath(UploadActivity.this, data.getData());
            ImageView imageView;

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                switch (requestCode)
                {
                    case 1:
                    case 2:

                        imageView = findViewById(R.id.imageviewMain1);
                        break;
                    case 3:
                    case 4:
                        imageView = findViewById(R.id.imageviewMain2);
                        break;
                    case 5:
                    case 6:
                        imageView = findViewById(R.id.imageviewMain3);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + requestCode);
                }

                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
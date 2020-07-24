package com.gsu.graphology;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.gsu.graphology.model.MySingleton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


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
    private ConstraintLayout constraintLayout;
    private TextView uniqueID;
    private String uniqueIDText;
    private Button uploadBtn;
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmap3;
    private String imagePath1;
    private String imagePath2;
    private String imagePath3;
    private String imageName1;
    private String imageName2;
    private String imageName3;
    private LinearLayout image_uploaded_view1;
    private LinearLayout image_uploaded_view2;
    private LinearLayout image_uploaded_view3;
    private LinearLayout image_not_uploaded_view1;
    private LinearLayout image_not_uploaded_view2;
    private LinearLayout image_not_uploaded_view3;


    ProgressDialog progressDialog;
    String URL = "http://graphology.eastus.azurecontainer.io/graphology/Applied";

    private String baseUrl;

    private String UploadURL = "";


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
        uniqueID = findViewById(R.id.uniqueIDView);
        uploadBtn = findViewById(R.id.upload_btn);
        constraintLayout=findViewById(R.id.parentLayout);
        image_uploaded_view1=findViewById(R.id.image_uploaded_view);
        image_uploaded_view2=findViewById(R.id.image_uploaded_view2);
        image_uploaded_view3=findViewById(R.id.image_uploaded_view3);
        image_not_uploaded_view1=findViewById(R.id.image_not_uploaded_view);
        image_not_uploaded_view2=findViewById(R.id.image_not_uploaded_view2);
        image_not_uploaded_view3=findViewById(R.id.image_not_uploaded_view3);
        getSupportActionBar().setTitle("Upload Images");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        email = intent.getStringExtra("emailID");
        uniqueIDText = intent.getStringExtra("uniqueID");

        baseUrl = "http://graphology.eastus.azurecontainer.io";


        uniqueID.setText(uniqueIDText);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

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
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imagePath1!=null && imagePath2!=null && imagePath3!=null) {
                    uploadToServer();
                }
                else {
                    Snackbar.make(findViewById(R.id.parentLayout), "Please select 3 images", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static class NetworkClient {

        private static final String BASE_URL = "http://graphology.eastus.azurecontainer.io/";
        private static Retrofit retrofit;

        public static Retrofit getRetrofitClient(Context context) {
            if (retrofit == null) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
                okHttpClient.readTimeout(1000, TimeUnit.SECONDS);
                okHttpClient.connectTimeout(500, TimeUnit.SECONDS);
                okHttpClient.addInterceptor(logging);
                okHttpClient.retryOnConnectionFailure(true);

                retrofit = new Retrofit.Builder()

                        .baseUrl(BASE_URL)
                        .client(okHttpClient.build())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public interface UploadAPIs {
        @Multipart
        @POST("graphology/Applied")
        Call<ResponseBody> uploadImage(@Header("accept") String type, @Part("email") RequestBody email, @Part("uniquekey") RequestBody uniqueKey, @Part MultipartBody.Part file1, @Part("file") RequestBody requestBody1, @Part MultipartBody.Part file2, @Part("file2") RequestBody requestBody2, @Part MultipartBody.Part file3, @Part("file3") RequestBody requestBody3);
    }


    private void uploadToServer() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(UploadActivity.this);
        //progressDialog.setMax(100);
        progressDialog.setMessage("Calculating results");
        progressDialog.setTitle("Please wait...");
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        // show it
        progressDialog.show();
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        UploadAPIs uploadAPIs = retrofit.create(UploadAPIs.class);
        //Create a file object using file path
        File file1 = new File(imagePath1.replace("file://", ""));
        File file2 = new File(imagePath2.replace("file://", ""));
        File file3 = new File(imagePath3.replace("file://", ""));
        //String email = "hkunta1@student.gsu.edu";
        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
        //String uniqueKey = "000063404";
        RequestBody uniqueKeyBody = RequestBody.create(MediaType.parse("text/plain"), uniqueIDText);
        // Create a request body with file and image media type
        RequestBody fileReqBody1 = RequestBody.create(MediaType.parse("image/*"), file1);
        RequestBody fileReqBody2 = RequestBody.create(MediaType.parse("image/*"), file2);
        RequestBody fileReqBody3 = RequestBody.create(MediaType.parse("image/*"), file3);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part1 = MultipartBody.Part.createFormData("file", file1.getName(), fileReqBody1);
        //Create request body with text description and text media type
        RequestBody description1 = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        MultipartBody.Part part2 = MultipartBody.Part.createFormData("file2", file2.getName(), fileReqBody2);
        //Create request body with text description and text media type
        RequestBody description2 = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        MultipartBody.Part part3 = MultipartBody.Part.createFormData("file3", file3.getName(), fileReqBody3);
        //Create request body with text description and text media type
        RequestBody description3 = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        //
        Call<ResponseBody> call = uploadAPIs.uploadImage("application/json", emailBody, uniqueKeyBody, part1, description1, part2, description2, part3, description3);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Log.e(TAG, response.body().toString());
                }
                Log.e(TAG + "testing", response.message());
                Log.e(TAG + "testing", String.valueOf(response.code()));
                Log.e(TAG, response.raw().toString());
                Log.e(TAG, response.body().toString());
                Log.e(TAG, response.body().source().toString());

                try {
                    response.body().source().request(Long.MAX_VALUE); // Buffer the entire body.
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Buffer buffer = response.body().source().buffer();
                String responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"));
                responseBodyString=responseBodyString.replace(getString(R.string.htmlheader),"");
                //Log.e(TAG, responseBodyString);
                int maxLogSize = 1000;
                for(int i = 0; i <= responseBodyString.length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i+1) * maxLogSize;
                    end = end > responseBodyString.length() ? responseBodyString.length() : end;
                    Log.e(TAG, responseBodyString.substring(start, end));
                }
                Intent resultIntent = new Intent(UploadActivity.this, ResultsActivity.class);
                resultIntent.putExtra("html", responseBodyString);
                progressDialog.dismiss();
                startActivity(resultIntent);


            }


            @Override
            public void onFailure(Call call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Log.e(TAG, t.getMessage());
            }
        });
    }


    private void loadProfile(String url, int REQUEST_IMAGE) {


        if (REQUEST_IMAGE == REQUEST_IMAGE1) {
            imageName1 = url.substring(url.lastIndexOf('/') + 1, url.length());
            filename1.setText(url.substring(url.lastIndexOf('/') + 1, url.length()));
            filename1.setGravity(Gravity.LEFT|Gravity.START);
            image_uploaded_view1.setVisibility(View.VISIBLE);
            image_not_uploaded_view1.setVisibility(View.GONE);
            Glide.with(this).load(url)
                    .into(imgView1);
            imgView1.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));

        }
        if (REQUEST_IMAGE == REQUEST_IMAGE2) {
            imageName2 = url.substring(url.lastIndexOf('/') + 1, url.length());
            filename2.setGravity(Gravity.LEFT|Gravity.START);
            filename2.setText(url.substring(url.lastIndexOf('/') + 1, url.length()));
            image_uploaded_view2.setVisibility(View.VISIBLE);
            image_not_uploaded_view2.setVisibility(View.GONE);
            Glide.with(this).load(url)
                    .into(imgView2);
            imgView2.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
        }
        if (REQUEST_IMAGE == REQUEST_IMAGE3) {
            imageName3 = url.substring(url.lastIndexOf('/') + 1, url.length());
            filename3.setGravity(Gravity.LEFT|Gravity.START);
            filename3.setText(url.substring(url.lastIndexOf('/') + 1, url.length()));
            image_uploaded_view3.setVisibility(View.VISIBLE);
            image_not_uploaded_view3.setVisibility(View.GONE);
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
                    bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    // loading profile image from local cache
                    imagePath1 = uri.toString();
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
                    bitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    // loading profile image from local cache
                    imagePath2 = uri.toString();
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
                    bitmap3 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    // loading profile image from local cache
                    imagePath3 = uri.toString();
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
package com.gsu.graphology;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static androidx.core.content.FileProvider.getUriForFile;


public class ImagePickerActivity extends AppCompatActivity {
    private static final String TAG = ImagePickerActivity.class.getSimpleName();
    public static final String INTENT_IMAGE_PICKER_OPTION = "image_picker_option";
    public static final String INTENT_ASPECT_RATIO_X = "aspect_ratio_x";
    public static final String INTENT_ASPECT_RATIO_Y = "aspect_ratio_Y";
    public static final String INTENT_LOCK_ASPECT_RATIO = "lock_aspect_ratio";
    public static final String INTENT_IMAGE_COMPRESSION_QUALITY = "compression_quality";
    public static final String INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height";
    public static final String INTENT_BITMAP_MAX_WIDTH = "max_width";
    public static final String INTENT_BITMAP_MAX_HEIGHT = "max_height";


    public static final int REQUEST_IMAGE_CAPTURE = 0;
    public static final int REQUEST_GALLERY_IMAGE = 1;

    private boolean lockAspectRatio = false, setBitmapMaxWidthHeight = false;
    private int ASPECT_RATIO_X = 16, ASPECT_RATIO_Y = 9, bitmapMaxWidth = 1000, bitmapMaxHeight = 1000;
    private int IMAGE_COMPRESSION = 100;
    //public static String fileName;

    File photoFile = null;

    public interface PickerOptionListener {
        void onTakeCameraSelected();

        void onChooseGallerySelected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_LONG).show();
            return;
        }

        ASPECT_RATIO_X = intent.getIntExtra(INTENT_ASPECT_RATIO_X, ASPECT_RATIO_X);
        ASPECT_RATIO_Y = intent.getIntExtra(INTENT_ASPECT_RATIO_Y, ASPECT_RATIO_Y);
        IMAGE_COMPRESSION = intent.getIntExtra(INTENT_IMAGE_COMPRESSION_QUALITY, IMAGE_COMPRESSION);
        lockAspectRatio = intent.getBooleanExtra(INTENT_LOCK_ASPECT_RATIO, false);
        setBitmapMaxWidthHeight = intent.getBooleanExtra(INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, false);
        bitmapMaxWidth = intent.getIntExtra(INTENT_BITMAP_MAX_WIDTH, bitmapMaxWidth);
        bitmapMaxHeight = intent.getIntExtra(INTENT_BITMAP_MAX_HEIGHT, bitmapMaxHeight);

        int requestCode = intent.getIntExtra(INTENT_IMAGE_PICKER_OPTION, -1);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            takeCameraImage();
        } else {
            chooseImageFromGallery();
        }
    }

    public static void showImagePickerOptions(final Context context, final PickerOptionListener listener) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // add a list
        final String[] animals = {context.getString(R.string.lbl_take_camera_picture), context.getString(R.string.lbl_choose_from_gallery)};
        builder.setItems(animals, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (animals[item].equals(context.getString(R.string.lbl_take_camera_picture))) {
                    listener.onTakeCameraSelected();

                } else if (animals[item].equals(context.getString(R.string.lbl_choose_from_gallery))) {
                    listener.onChooseGallerySelected();

                }
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void takeCameraImage() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            //fileName = System.currentTimeMillis() + ".jpg";
                            Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

//                                Uri photoURI = FileProvider.getUriForFile(ImagePickerActivity.this,
//                                        "com.gsu.graphology.fileprovider",
//                                        photoFile);
//
//                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, MyFileContentProvider.CONTENT_URI);
//
//                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
////                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//
//                                File pictureFile = null;
//                                try {
//                                    pictureFile = getPictureFile();
//                                } catch (IOException ex) {
//                                    Toast.makeText(getApplicationContext(),
//                                            "Photo file can't be created, please try again",
//                                            Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
//                                if (pictureFile != null) {
//                                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
//                                            BuildConfig.APPLICATION_ID + ".provider",
//                                            pictureFile);
//                                    takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mCurrentPhotoPath)));
//                                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                                }
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                if (photoFile != null) {
                                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                            BuildConfig.APPLICATION_ID + ".fileprovider",
                                            photoFile);
                                    //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    Toast.makeText(ImagePickerActivity.this, "Here", Toast.LENGTH_SHORT).show();
                                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                }
                            }

//                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    String mCurrentPhotoPath;
    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "ZOFTINO_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    String imageFileName;

    private File createImageFile() throws IOException {
//        File storageDir = Environment.getExternalStorageDirectory();
//        File image = File.createTempFile(
//                String.valueOf(System.currentTimeMillis()),  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//        mCurrentPhotoPath = image.getAbsolutePath();
//        return image;
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File root = Environment.getExternalStorageDirectory();


        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                root      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void chooseImageFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
//                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                            Toast.makeText(ImagePickerActivity.this, "picked", Toast.LENGTH_SHORT).show();
//                            startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE);
                            Intent galleryIntent = new Intent();
                            galleryIntent.setType("image/*");

                            galleryIntent.setAction(Intent.ACTION_PICK);

                            //galleryIntent.setAction(Intent.ACTION_PICK);


                            startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), REQUEST_GALLERY_IMAGE);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Log.e("Image path1", mCurrentPhotoPath);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(ImagePickerActivity.this, String.valueOf(requestCode), Toast.LENGTH_SHORT).show();
//        Toast.makeText(ImagePickerActivity.this, String.valueOf(resultCode), Toast.LENGTH_SHORT).show();
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    File out = photoFile;

                    if(!out.exists()) {

                        Toast.makeText(getBaseContext(),

                                "Error while capturing image", Toast.LENGTH_LONG)

                                .show();

                        return;

                    }
                    mCurrentPhotoPath=out.getAbsolutePath();
                    Uri myUri = Uri.fromFile(out);
                    Toast.makeText(ImagePickerActivity.this, String.valueOf(myUri), Toast.LENGTH_SHORT).show();
                    galleryAddPic();
                    cropImage(myUri);
                } else {
                    setResultCancelled();
                }
                break;
            case REQUEST_GALLERY_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    Toast.makeText(ImagePickerActivity.this, String.valueOf(data.getData()), Toast.LENGTH_SHORT).show();
                    //CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);
                    cropImage(imageUri);
                } else {
                    setResultCancelled();
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                    File thumb_filepath = new File(resultUri.getPath());
                    setResultOk(resultUri);
                } else {
                    setResultCancelled();
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(ImagePickerActivity.this, "result okay", Toast.LENGTH_SHORT).show();
                    handleUCropResult(data);
                } else {
                    Toast.makeText(ImagePickerActivity.this, "result not okay", Toast.LENGTH_SHORT).show();
                    setResultCancelled();
                }
                break;
            case UCrop.RESULT_ERROR:
                final Throwable cropError = UCrop.getError(data);
                Log.e(TAG, "Crop error: " + cropError);
                setResultCancelled();
                break;
            default:
                setResultCancelled();
        }
    }

    private void cropImage(Uri sourceUri) {
        Log.e("path", sourceUri.toString());
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), queryName(getContentResolver(), sourceUri)));
        //Uri destinationUri = sourceUri;
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(IMAGE_COMPRESSION);

        // applying UI theme
        //options.setAspectRatioOptions(1,new AspectRatio(null, 1, 1));
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.white));


        if (lockAspectRatio)
            //options.withAspectRatio(ASPECT_RATIO_X, ASPECT_RATIO_Y);
            //options.setFreeStyleCropEnabled(true);
            //options.setShowCropGrid(true);
            options.setHideBottomControls(true);
        if (setBitmapMaxWidthHeight)
            options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight);

        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .start(this);
    }

    private void handleUCropResult(Intent data) {
        if (data == null) {
            Toast.makeText(this, "null data", Toast.LENGTH_SHORT).show();
            setResultCancelled();
            return;
        }
        final Uri resultUri = UCrop.getOutput(data);
        Toast.makeText(this, resultUri.toString(), Toast.LENGTH_SHORT).show();
        setResultOk(resultUri);
    }

    private void setResultOk(Uri imagePath) {
        Intent intent = new Intent();
        intent.putExtra("path", imagePath);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void setResultCancelled() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return getUriForFile(ImagePickerActivity.this, getPackageName() + ".provider", image);
    }

    private String queryName(ContentResolver resolver, Uri uri) {
//        String name = "";
//        try {
//            Cursor returnCursor =
//                    resolver.query(uri, null, null, null, null);
//            assert returnCursor != null;
//            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//            returnCursor.moveToFirst();
//            name = returnCursor.getString(nameIndex);
//            returnCursor.close();
//
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        return name;
        String fileName = "";
        String scheme = uri.getScheme();
        if (scheme.equals("file")) {
            fileName = uri.getLastPathSegment();
        } else if (scheme.equals("content")) {
            String[] proj = {MediaStore.Images.Media.TITLE};
            Cursor cursor = getApplicationContext().getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null && cursor.getCount() != 0) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                cursor.moveToFirst();
                fileName = cursor.getString(columnIndex);
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName;
    }

    /**
     * Calling this will delete the images from cache directory
     * useful to clear some memory
     */
    public static void clearCache(Context context) {
        File path = new File(context.getExternalCacheDir(), "camera");
        if (path.exists() && path.isDirectory()) {
            for (File child : path.listFiles()) {
                child.delete();
            }
        }
    }
}

package sample.com.tesimage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;


public class TesPhoto extends AppCompatActivity {

    private String file_path_str="0";

    private ImageButton buttonChoose,buttonUpload,buttonCamera;
    private ImageView imageView;

    //Image request code
    private static final  int PICK_IMAGE_REQUEST = 3;
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    int TIPE_SOURCE_IMG=0;

    public static final int RESULT_CANCELED    = 0;
    public static final int RESULT_OK           = -1;
    private Bitmap bitmap_upload;
    private Uri filePath;
    Boolean isuploadd = false;


    private RequestPermissionHandler mRequestPermissionHandler;
    LinearLayout lyGalery,lyKamera,lyCancel,lyUpload;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRequestPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }




    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Photo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        isuploadd=false;

        buttonUpload= (ImageButton) findViewById(R.id.imgBtnUpload);
        buttonChoose= (ImageButton) findViewById(R.id.imgBtnFoto);
        buttonCamera= (ImageButton) findViewById(R.id.imgBtnCamera);
        imageView= (ImageView)findViewById(R.id.img_photo1);

        lyGalery= (LinearLayout)findViewById(R.id.lyGalery);
        lyKamera= (LinearLayout)findViewById(R.id.lyKamera);
        lyCancel= (LinearLayout)findViewById(R.id.lyCancel);
        lyUpload= (LinearLayout)findViewById(R.id.lyUpload);


        mRequestPermissionHandler = new RequestPermissionHandler();
        Utility.checkPermissionAll(mRequestPermissionHandler, TesPhoto.this);


        buttonChoose.setOnClickListener(v -> {
            showFileChooser();
            TIPE_SOURCE_IMG=1;
        });

        buttonCamera.setOnClickListener(v -> {
            TIPE_SOURCE_IMG=2;
            if (checkCameraHardware( TesPhoto.this)){
                if (CameraUtils.checkPermissions( TesPhoto.this)) {
                    captureImage();
                } else {
                    requestCameraPermission( 1);
                }
            }else{
                Toast.makeText(TesPhoto.this, "Camera not available", Toast.LENGTH_SHORT).show();
            }

        });

        ((ImageButton) findViewById(R.id.imgBtnCancel)).setOnClickListener(v -> {

            lyCancel.setVisibility(View.GONE);
            lyUpload.setVisibility(View.GONE);
            lyGalery.setVisibility(View.VISIBLE);
            lyKamera.setVisibility(View.VISIBLE);


        });

    }




    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),Utility.GALLERY_DIRECTORY_NAME);
        mediaStorageDir = mediaStorageDir.getAbsoluteFile();

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator+ "IMG_" + System.currentTimeMillis() + ".jpg");
        file_path_str = mediaFile.toString();
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                System.out.println("Oops! Failed create directory");
            }
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            intent.setClipData(ClipData.newRawUri("", filePath));
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION| Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            fileUri = FileProvider.getUriForFile(TesPhoto.this, BuildConfig.APPLICATION_ID.concat(".provider"), mediaFile);
        }else{
            fileUri = Uri.fromFile(mediaFile); // 3
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // 4
        }

        filePath=fileUri;
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void requestCameraPermission(final int type) {
        Dexter.withActivity(TesPhoto.this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new BaseMultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == 1) {
                                captureImage();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            DialogsUtil.confirmDialog(TesPhoto.this, "Permissions required!", "Please grant app to use camera", "Open Setting", "" +
                                    "BATAL", new DialogConfirmInterface() {
                                @Override
                                public void onPositiveButtonClicked() {
                                    CameraUtils.openSettings(TesPhoto.this);
                                }

                                @Override
                                public void onNegativeButtonClicked() {
                                }
                            });
                        }
                    }

//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                        token.continuePermissionRequest();
//                    }
                }).check();
    }


    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }



    //    private File actualImage;
    private File fileUploaded;
    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode ==  RESULT_OK  && data != null && data.getData() != null) {


            filePath = data.getData();
            try {
                fileUploaded = Tools.from(getApplicationContext(), filePath);
                file_path_str = fileUploaded.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Uri sourceUri = data.getData(); // 1
            Uri destinationUri = Uri.fromFile(fileUploaded);  // 3
            UCrop.of(sourceUri, destinationUri) // sourceUri, destinationUri
                    .withMaxResultSize(640, 640)
                    .withAspectRatio(5f, 5f)
                    .start(TesPhoto.this);


        }else if (requestCode ==CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode ==  RESULT_OK) {
//                CameraUtils.refreshGallery(ChangePhotoProfile.this, imageStoragePath);
                try {
                    isuploadd=true;
                    fileUploaded = Tools.from(getApplicationContext(),filePath);


                    Uri sourceUri = filePath;
                    Uri destinationUri = Uri.fromFile(fileUploaded);  // 3
                    UCrop.of(sourceUri, destinationUri) // sourceUri, destinationUri
                            .withMaxResultSize(640, 640)
                            .withAspectRatio(5f, 5f)
                            .start(TesPhoto.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode ==  RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(TesPhoto.this,
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(TesPhoto.this,
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {

            final Uri uri = UCrop.getOutput(data);
            try {
                fileUploaded = Tools.from(getApplicationContext(),uri);
                bitmap_upload = BitmapFactory.decodeFile(fileUploaded.getAbsolutePath());
                Glide.with(TesPhoto.this)
                        .load(Uri.fromFile(fileUploaded))
                        .into(imageView);

                fileUploaded = new Compressor(getApplicationContext())
                        .setMaxWidth(640)
                        .setMaxHeight(640)
                        .setQuality(90)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+"/"+Utility.GALLERY_DIRECTORY_NAME).getAbsolutePath())
                        .compressToFile(fileUploaded);
                file_path_str = fileUploaded.getAbsolutePath();
                lyCancel.setVisibility(View.VISIBLE);

                lyUpload.setVisibility(View.GONE);

                lyGalery.setVisibility(View.GONE);
                lyKamera.setVisibility(View.GONE);

            } catch (IOException e) {
                e.printStackTrace();
            }



        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }




    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_back, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
            
}

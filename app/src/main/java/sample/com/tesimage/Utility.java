package sample.com.tesimage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;


public class Utility {

    public static void checkPermissionAll(final RequestPermissionHandler reqHandler, final Context context) {

        Activity activity = (Activity) context;
        reqHandler.requestPermission(activity, new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE
                ,Manifest.permission.CAMERA
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, 123, new RequestPermissionHandler.RequestPermissionListener() {
            @Override
            public void onSuccess() {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)

                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {

                }


            }

            @Override
            public void onFailed() {
            }
        });


    }

    public static final String GALLERY_DIRECTORY_NAME = "GERAIDAYA";
    public static final String IMAGE_EXTENSION = "jpg";
    public static final int MEDIA_TYPE_IMAGE = 1;



}

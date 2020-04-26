package com.baidu.speech.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class PermissionManager {

    /**
     * 系统相机权限
     * @param mContext
     * @param mActivity
     * @param REQUEST_CAMERA
     * @return
     */
    public static boolean checkCameraPermission(Context mContext, Activity mActivity, int REQUEST_CAMERA) {
        System.out.println("here");
        if (ContextCompat.checkSelfPermission(mContext,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CAMERA);
            }
            if(ContextCompat.checkSelfPermission(mContext,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA);
            }
            return true;
        }
    }

    /**
     * 本地图库权限
     * @param mContext
     * @param mActivity
     * @param REQUEST_CHOOSE_PHOTO
     * @return
     */
    public static boolean checkPhotoPermission(Context mContext, Activity mActivity, int REQUEST_CHOOSE_PHOTO) {

        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CHOOSE_PHOTO);
            return true;
        } else {
            return false;
        }
    }
}

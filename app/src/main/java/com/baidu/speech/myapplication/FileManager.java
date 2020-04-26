package com.baidu.speech.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class FileManager {

    private static final String SD_PATH = "/sdcard/android/pic/";
    private static final String IN_PATH = "/android/pic/";

    // sd_card保存图片
    public static File createFileIfNeed(String fileName) throws IOException {
        String fileA = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/android";
        File fileJA = new File(fileA);
        if (!fileJA.exists()) {
            fileJA.mkdirs();
        }
        File file = new File(fileA, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    // 从存储的路径中获取文件
    public static String readpic() {
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/android/" + "filepath.png";

        return filePath;
    }

    //随机产生文件名
    public static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    //保存bitmap 到本地
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + IN_PATH;
        }
        try {
            filePic = new File(savePath + generateFileName() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return filePic.getAbsolutePath();
    }
}

package com.baidu.speech.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Message {
    public OkHttpClient okHttpClient = new OkHttpClient();

    public String post (RequestBody requestBody, String url) throws IOException {
        Request request = new Request.Builder().url(url).post(requestBody).build();
//        System.out.println(requestBody.s);
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    public Bitmap getPhoto (RequestBody requestBody, String url) throws IOException {
        Request request = new Request.Builder().url(url).post(requestBody).build();
//        System.out.println(requestBody.s);
        Bitmap bitmap;
        Response response = okHttpClient.newCall(request).execute();
        InputStream inputStream= response.body().byteStream();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        byte[] bmp_buffer;
        int len = 0;
        while( (len=inputStream.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inputStream.close();
        bmp_buffer=outStream.toByteArray();
        bitmap = BitmapFactory.decodeByteArray(bmp_buffer, 0, bmp_buffer.length);
        return bitmap;
    }
}

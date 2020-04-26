package com.baidu.speech.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.UnicodeSetSpanner;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class login extends AppCompatActivity implements View.OnClickListener {

    public ImageView imageView;
    public Button loginBtn;
    public Button registerBtn;
    public EditText editText1;
    public EditText editText2;
    public Integer code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editText1 = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        loginBtn = findViewById(R.id.button);
        registerBtn = findViewById(R.id.button2);

        registerBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.button:{
                final Message message = new Message();
                final RequestBody requestBody = new FormBody.Builder()
                        .add("name",editText1.getText().toString())
                        .add("password",editText2.getText().toString())
                        .build();
//                final Integer code;
                Thread t =new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String result = message.post(requestBody,url.getPath("/user/login"));
                            Gson gson = new Gson();
                            response response = gson.fromJson(result, com.baidu.speech.myapplication.response.class);
                            code = response.code;
                            System.out.println(result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
                try {
                    t.join();
                    judgeCode2();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                break;
            }
            case R.id.button2:{
                final Message message = new Message();
                final RequestBody requestBody = new FormBody.Builder()
                        .add("name",editText1.getText().toString())
                        .add("password",editText2.getText().toString())
                        .build();
//                final Integer code;
                Thread t =new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String result = message.post(requestBody,url.getPath("/user/insert"));
                            Gson gson = new Gson();
                            response response = gson.fromJson(result, com.baidu.speech.myapplication.response.class);
                            code = response.code;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
                try {
                    t.join();
                    judgeCode();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    private void judgeCode2() {
        if(code == 0){
            SharedPreferences sp = login.this.getSharedPreferences("userIfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("name",editText1.getText().toString());
            editor.putString("password",editText2.getText().toString());
            editor.commit();
//            Looper.prepare();
            Toast.makeText(login.this,sp.getString("name","")+" 登录成功", Toast.LENGTH_LONG).show();
//            Looper.loop();
            this.finish();
//            this.onDestroy();
//            Intent intent = new Intent(this,UserFragment.class);
//            startActivity(intent);
        }else if(code == 1){
//            Looper.prepare();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(login.this);
            alertDialog.setMessage("该用户不存在");
            alertDialog.setNegativeButton("取消",null);
            alertDialog.show();
//            Looper.loop();
        }else if(code == 2){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(login.this);
            alertDialog.setMessage("密码错误");
            alertDialog.setNegativeButton("取消",null);
            alertDialog.show();
        }
    }
    private void judgeCode() {
        if(code == 0){
            SharedPreferences sp = login.this.getSharedPreferences("userIfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("name",editText1.getText().toString());
            editor.putString("password",editText2.getText().toString());

            editor.commit();
//            Looper.prepare();
            Toast.makeText(login.this,sp.getString("name","")+" 注册成功", Toast.LENGTH_LONG).show();
//            Looper.loop();
            this.finish();
//            this.onDestroy();
//            Intent intent = new Intent(this,UserFragment.class);
//            startActivity(intent);
        }else if(code == 1){
//            Looper.prepare();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(login.this);
            alertDialog.setMessage("该用户已存在");
            alertDialog.setNegativeButton("取消",null);
            alertDialog.show();
//            Looper.loop();
        }
    }



}

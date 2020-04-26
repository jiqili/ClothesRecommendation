package com.baidu.speech.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class matchEdit extends AppCompatActivity implements View.OnClickListener {

    List<HashMap<String,String>> data2 = new ArrayList<>();
    SimpleAdapter simpleAdapter;
    public String[] season = {"全年","春秋","夏季","冬季"};
    public String[] situation = {"办公","休闲","运动","约会"};
    public static final String KEY = "key";
    public String content = "";
    public Bitmap bitmap;
    public response<Double> response;
    public ClothesMatch clothesMatch = new ClothesMatch();
    Bundle bundle;
    String filePath;
    private String[] styleList = new String[]{"少女","少男","前卫","优雅","自然","知性","浪漫","戏剧","摩登"};

    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_edit);
        ImageButton backBtn;
        ImageView imageView;
        String [] data;
        ListView listView;
        data = new String[]{"标签","季节","形体参数"};
        backBtn = findViewById(R.id.matchBackBtn);
        Button handleBtn = findViewById(R.id.matchHandleBtn);
        editText = findViewById(R.id.matchContentEdit);
        imageView = findViewById(R.id.matchEditImg);
        listView = findViewById(R.id.matchEditList);
        SharedPreferences sp = this.getSharedPreferences("userIfo", Context.MODE_PRIVATE);
        clothesMatch.user_id = Integer.parseInt(sp.getString("id",""));
        final Intent intent = getIntent();
        bundle = intent.getExtras();
        if(bundle.get("Id")!=null){
            Object object = new Object();
            synchronized (object){
                getInfo((int)bundle.get("Id"));
            }
            synchronized (object){
                getPhoto(clothesMatch.img,imageView);
                editText.setText(clothesMatch.content);
            }
            synchronized (object){
                String[] data1 = new String[]{"["+getStyleList(clothesMatch.style)+" "+clothesMatch.situation+"]",
                        clothesMatch.season,clothesMatch.body+" "+clothesMatch.skinColor+" "+clothesMatch.height+" "+clothesMatch.faceCircle+" "+clothesMatch.faceWeight};
                for(int i=0;i<data.length;i++){
                    HashMap<String,String> map = new HashMap<>();
                    map.put("title",data[i]);
                    map.put("content",data1[i]);
                    data2.add(map);
                }
                initAdapter(listView);
            }
        }else {
            for(int i=0;i<data.length;i++){
                HashMap<String,String> map = new HashMap<>();
                map.put("title",data[i]);
                map.put("content","");
                data2.add(map);
            }
            File file = new File((String)bundle.get(WardrobeFragment.KEY));
            System.out.println("-----------"+file.length());
            filePath = BitmapUtil.compressImage(file.getPath());
            System.out.println("-----------"+file.length());
            imageView.setImageURI(Uri.fromFile(file));
            initAdapter(listView);
        }
        backBtn.setOnClickListener(this);
        handleBtn.setOnClickListener(this);
    }

    private void initAdapter(ListView listView) {
        simpleAdapter = new SimpleAdapter(this,
                data2,
                R.layout.clothes_edit_item,
                new String[]{"title","content"},
                new int []{R.id.clothes_edit_text1,R.id.clothes_edit_text2});

        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> m = (HashMap<String,String>)parent.getItemAtPosition(position);
                switch (m.get("title")){
                    case "标签": {
                        Intent intent = new Intent(matchEdit.this, edit5.class);
                        startActivityForResult(intent,1);
                        break;
                    }
                    case "季节": {
                        Intent intent = new Intent(matchEdit.this, edit2.class);
                        intent.putExtra(KEY,season);
                        startActivityForResult(intent,2);
                        break;
                    }
                    case "形体参数": {
                        Intent intent = new Intent(matchEdit.this, edit6.class);
                        startActivityForResult(intent,3);
                        break;
                    }
                }
            }
        });
    }

    private void getInfo(int id) {
        final Message message = new Message();
        final RequestBody requestBody = new FormBody.Builder()
                .add("id",String.valueOf(id))
                .build();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = message.post(requestBody,url.getPath("/match/findById"));
                    Gson gson = new Gson();
                    response = gson.fromJson(result, com.baidu.speech.myapplication.response.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
            if(response.code == 0){
                Gson gson = new Gson();
                clothesMatch = gson.fromJson(gson.toJson(response.data),ClothesMatch.class);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    private void getPhoto(String name,ImageView imageView) {
        final Message message = new Message();
        if(name == null) return;
        final RequestBody requestBody = new FormBody.Builder()
                .add("name",name)
                .build();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmap = message.getPhoto(requestBody,url.getPath("/file/findImg"));
//                    System.out.println(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
            imageView.setImageDrawable(bitmapDrawable);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    private void update(String res,String title,int i) {
        HashMap<String,String> map = new HashMap<>();
        map.put("title",title);
        map.put("content",res);
        data2.set(i,map);
        simpleAdapter.notifyDataSetChanged();
    }
    private String getStyleList(int style) {
        String res="";
        int k = 0;
        while (style!=0){
            if(style%2 == 1) res += styleList[k] + " ";
            k++;
            style/=2;
        }
        return res;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null) return;
        if(requestCode == 2 && resultCode == 7){
            update(data.getStringExtra("res"),"季节",1);
        }
        else if(requestCode == 1 && resultCode == 5){
            update(data.getStringExtra("res"),"标签",0);
        }
        else if(requestCode == 3 && resultCode == 5){
            update(data.getStringExtra("res"),"形体参数",2);
        }
    }
    void f(){
        this.finish();
    }
    private int handle(Integer id){
        final Message message = new Message();

        final RequestBody requestBody = new FormBody.Builder()
                .add("id",String.valueOf(id))
                .add("style",String.valueOf(clothesMatch.style))
                .add("situation",clothesMatch.situation)
                .add("season",clothesMatch.season)
                .add("height",String.valueOf(clothesMatch.height))
                .add("body",clothesMatch.body)
                .add("face_circle",clothesMatch.faceCircle)
                .add("face_weight",clothesMatch.faceWeight)
                .add("skin_color",clothesMatch.skinColor)
                .add("content",clothesMatch.content)
                .build();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = message.post(requestBody,url.getPath("/match/update"));
//                    System.out.println(result);
                    Gson gson = new Gson();
                    response = gson.fromJson(result, com.baidu.speech.myapplication.response.class);
//                    System.out.println(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
            return response.code;
        }catch (InterruptedException e){
            e.printStackTrace();
            return 1;
        }
    }
    private int handle2(String id){
        final Message message = new Message();

        final RequestBody requestBody = new FormBody.Builder()
                .add("user_id",id)
                .add("style",String.valueOf(clothesMatch.style))
                .add("situation",clothesMatch.situation)
                .add("season",clothesMatch.season)
                .add("height",String.valueOf(clothesMatch.height))
                .add("body",clothesMatch.body)
                .add("face_circle",clothesMatch.faceCircle)
                .add("face_weight",clothesMatch.faceWeight)
                .add("skin_color",clothesMatch.skinColor)
                .add("content",clothesMatch.content)
                .build();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = message.post(requestBody,url.getPath("/match/insert"));
//                    System.out.println(result);
                    Gson gson = new Gson();
                    response = gson.fromJson(result, com.baidu.speech.myapplication.response.class);
//                    System.out.println(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
            if(response.code == 0) clothesMatch.id = response.data.intValue();
            return response.code;
        }catch (InterruptedException e){
            e.printStackTrace();
            return 1;
        }
    }
    private void sendFile(final String filepath, final String url, final String id) {
        System.out.println(id);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                File file = new File(BitmapUtil.compressImage(filePath));
                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("id",id)
                        .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"filename\""),
                                RequestBody.create(null,"photo"))
                        .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"file\";filename=\""+"photo.jpg"+"\""),fileBody)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("text","upload failed");
                        System.out.println("upload failed");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        System.out.println(json);
                        System.out.println("upload successfully");
                    }
                });
            }
        }).start();
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.matchBackBtn :{
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setMessage("返回不会保存未提交的内容").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                f();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                break;
            }
            case R.id.matchHandleBtn :{
                clothesMatch.season = data2.get(1).get("content");
                clothesMatch.content = String.valueOf(editText.getText());
                int style = 0;
                for(int i=styleList.length-1;i>=0;i--){
                    if(data2.get(0).get("content").contains(styleList[i])){
                        style*=2;
                        style++;
                    }
                    else style*=2;
                }
                clothesMatch.style = style;
                String[] s= new String[]{"办公","休闲","运动","约会"};
                String[] s2= new String[]{"色调较冷","色调中等","色调较暖"};
                String[] s3= new String[]{"轮廓较直","轮廓中等","轮廓较圆"};
                String[] s4= new String[]{"量感较小","量感中等","量感较大"};
                String[] s5= new String[]{"X型","Y型","A型","H型","O型"};
                for(int i=0;i < s.length;i++)
                    if(data2.get(0).get("content").contains(s[i]))
                        clothesMatch.situation = s[i];
                for(int i=0;i < s2.length;i++)
                    if(data2.get(2).get("content").contains(s2[i]))
                        clothesMatch.skinColor = s2[i];
                for(int i=0;i < s3.length;i++)
                    if(data2.get(2).get("content").contains(s3[i]))
                        clothesMatch.faceCircle = s3[i];
                for(int i=0;i < s4.length;i++)
                    if(data2.get(2).get("content").contains(s4[i]))
                        clothesMatch.faceWeight = s4[i];
                for(int i=0;i < s5.length;i++)
                    if(data2.get(2).get("content").contains(s5[i]))
                        clothesMatch.body = s5[i];
                    String pattern = "\\d+";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(data2.get(2).get("content"));
                    if(m.find()) clothesMatch.height = Integer.parseInt(m.group());

                if(bundle.get("Id")==null){
                    SharedPreferences sp = this.getSharedPreferences("userIfo", Context.MODE_PRIVATE);
//                    System.out.println(sp.getString("id","100"));
                    if(handle2(sp.getString("id","")) == 0){
                        Toast.makeText(this,"发表成功",Toast.LENGTH_LONG).show();
                    }
                    sendFile(filePath,url.getPath("/file/addMatch"),String.valueOf(clothesMatch.id));
                }
                else {
                    if(handle(clothesMatch.id) == 0){
                        Toast.makeText(this,"发表成功",Toast.LENGTH_LONG).show();
                    }
                }
                this.finish();
                break;
            }
        }
    }
}

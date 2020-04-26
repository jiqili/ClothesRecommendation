package com.baidu.speech.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

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

public class clothesEdit extends AppCompatActivity implements View.OnClickListener {

    List<HashMap<String,String>> data2 = new ArrayList<>();
    SimpleAdapter simpleAdapter;
    public String[] season = {"全年","春秋","夏季","冬季"};
    public String[] color = {"白色","黑色","蓝色","黄色","粉红色","紫色"};
    public static final String KEY = "key";
    public Bitmap bitmap;
    public response<Double> response;
    public Clothes clothes = new Clothes();
    String filePath;
    Bundle bundle;
    private String[] styleList = new String[]{"少女","少男","前卫","优雅","自然","知性","浪漫","戏剧","摩登"};
//    public Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clothes_edit);

        ImageButton backBtn;
        ImageView imageView;
        ListView listView;
        String [] data;
        ArrayAdapter<String> adapter;
        SharedPreferences sp = this.getSharedPreferences("userIfo", Context.MODE_PRIVATE);
        clothes.user_id = Integer.parseInt(sp.getString("id","1"));
        backBtn = findViewById(R.id.clothesBackBtn);
        Button handleBtn = findViewById(R.id.clothesHandleBtn);
        imageView = findViewById(R.id.clothesEditImg);
        listView = findViewById(R.id.clothesEditList);
        data = new String[]{"分类","颜色","季节","品牌","价格","备注","标签"};
        final Intent intent = getIntent();
        bundle = intent.getExtras();
        if(bundle.get("Id")!=null){
            Object object = new Object();
            synchronized (object){
                getInfo((Integer)bundle.get("Id"));
            }
            synchronized (object){
                getPhoto(clothes.img,imageView);
            }
            synchronized (object){
                String[] data1 = new String[]{clothes.kind,clothes.color,clothes.season,clothes.brand,String.valueOf(clothes.price)
                ,clothes.content,"["+clothes.style+" "+clothes.detail+"]"};
                for(int i=0;i<data.length;i++){
                    HashMap<String,String> map = new HashMap<>();
                    map.put("title",data[i]);
                    map.put("content",data1[i]);
                    data2.add(map);
                }
                initAdapter(listView);
            }
        }
        else {
            for(int i=0;i<data.length;i++){
                HashMap<String,String> map = new HashMap<>();
                map.put("title",data[i]);
                map.put("content","");
                data2.add(map);
            }
            File file = new File((String)bundle.get(WardrobeFragment.KEY));
            filePath = file.getPath();
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
                    case "分类": {
                        Intent intent = new Intent(clothesEdit.this, edit3.class);
                        startActivityForResult(intent,1);
                        break;
                    }
                    case "颜色": {
                        Intent intent = new Intent(clothesEdit.this, edit2.class);
                        intent.putExtra(KEY,color);
                        startActivityForResult(intent,2);
                        break;
                    }
                    case "季节": {
                        Intent intent = new Intent(clothesEdit.this,edit2.class);
                        intent.putExtra(KEY,season);
                        startActivityForResult(intent,3);
                        break;
                    }
                    case "品牌": {
                        Intent intent = new Intent(clothesEdit.this, edit.class);
                        startActivityForResult(intent,4);
                        break;
                    }
                    case "价格": {
                        Intent intent = new Intent(clothesEdit.this,edit.class);
                        startActivityForResult(intent,5);
                        break;
                    }
                    case "备注": {
                        Intent intent = new Intent(clothesEdit.this,edit.class);
                        startActivityForResult(intent,6);
                        break;
                    }
                    case "标签": {
                        Intent intent = new Intent(clothesEdit.this, edit4.class);
                        startActivityForResult(intent,7);
                        break;
                    }
                }
            }
        });
    }
    private void sendFile(final String filepath, final String url, final String id) {
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
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                    }
                });
            }
        }).start();
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
                    String result = message.post(requestBody,url.getPath("/clothes/findById"));
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
                clothes = gson.fromJson(gson.toJson(response.data),Clothes.class);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null) return;
        if(requestCode == 4 && resultCode == 9){
            update(data.getStringExtra("res"),"品牌",3);
        }
        else if(requestCode == 5 && resultCode == 9){
            update(data.getStringExtra("res"),"价格",4);
        }
        else if(requestCode == 6 && resultCode == 9){
            update(data.getStringExtra("res"),"备注",5);
        }
        else if(requestCode == 3 && resultCode == 7){
            update(data.getStringExtra("res"),"季节",2);
        }
        else if(requestCode == 2 && resultCode == 7){
            update(data.getStringExtra("res"),"颜色",1);
        }
        else if(requestCode == 1 && resultCode == 6){
            update(data.getStringExtra("res"),"分类",0);
        }
        else if(requestCode == 7 && resultCode == 5){
            update(data.getStringExtra("res"),"标签",6);
        }
    }

    private void update(String res,String title,int i) {
        HashMap<String,String> map = new HashMap<>();
        map.put("title",title);
        map.put("content",res);
        data2.set(i,map);
        simpleAdapter.notifyDataSetChanged();
    }
    void f(){
        this.finish();
    }
    private int handle(){
        final Message message = new Message();

        final RequestBody requestBody = new FormBody.Builder()
                .add("id",String.valueOf(clothes.id))
                .add("style",String.valueOf(clothes.style))
                .add("color",clothes.color)
                .add("season",clothes.season)
                .add("price",String.valueOf(clothes.price))
                .add("content",clothes.content)
                .add("detail",clothes.detail)
                .add("kind",clothes.kind)
                .add("brand",clothes.brand)
                .build();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = message.post(requestBody,url.getPath("/clothes/update"));
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
                .add("style",String.valueOf(clothes.style))
                .add("color",clothes.color)
                .add("season",clothes.season)
                .add("price",String.valueOf(clothes.price))
                .add("content",clothes.content)
                .add("detail",clothes.detail)
                .add("kind",clothes.kind)
                .add("brand",clothes.brand)
                .build();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = message.post(requestBody,url.getPath("/clothes/insert"));
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
            System.out.println(response.data);
            clothes.id = response.data.intValue();
            return response.code;
        }catch (InterruptedException e){
            e.printStackTrace();
            return 1;
        }
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.clothesBackBtn :{
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
            case R.id.clothesHandleBtn :{
                clothes.kind = data2.get(0).get("content");
                clothes.color = data2.get(1).get("content");
                clothes.season = data2.get(2).get("content");
                clothes.brand = data2.get(3).get("content");
                clothes.price = Integer.parseInt(data2.get(4).get("content"));
                clothes.content = data2.get(5).get("content");

                for(int i=0;i < styleList.length;i++)
                    if(data2.get(6).get("content").contains(styleList[i]))
                        clothes.style = styleList[i];

                ArrayList<String> details_data = new ArrayList<>();
                details_data.add("横条纹");
                details_data.add("竖条纹");
                details_data.add("斜条纹");
                details_data.add("花纹");
                details_data.add("豹纹");
                details_data.add("格子");
                details_data.add("波点");
                details_data.add("拼接");
                details_data.add("单色");
                details_data.add("荧光");
                details_data.add("铆钉");
                clothes.detail = "";
                for(int i=0;i < details_data.size();i++)
                    if(!clothes.detail.contains(details_data.get(i)) && data2.get(6).get("content").contains(details_data.get(i))){
                        clothes.detail += details_data.get(i);
                        clothes.detail += " ";
                    }

                if(handle() == 0)    Toast.makeText(this,"发表成功",Toast.LENGTH_LONG).show();
                if(bundle.get("Id")==null){
                    SharedPreferences sp = this.getSharedPreferences("userIfo", Context.MODE_PRIVATE);
                    if(handle2(sp.getString("id","")) == 0)    Toast.makeText(this,"发表成功",Toast.LENGTH_LONG).show();
                    sendFile(filePath,url.getPath("/file/addClothes"),String.valueOf(clothes.id));
                }

                this.finish();
                break;
            }
        }
    }
}

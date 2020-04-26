package com.baidu.speech.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class clothes_show extends AppCompatActivity implements View.OnClickListener {

    public ImageButton backBtn;
    public ImageButton menuBtn;
    public ImageView imageView;
    public ListView listView;
    public String[] data;
    SimpleAdapter simpleAdapter;
    public PopupWindow popupWindow;
    public List<HashMap<String,String>> data2 = new ArrayList<>();
    public Bundle bundle;
    public response<Clothes> response;
    public Clothes clothes;
    Bitmap bitmap;
    private String[] styleList = new String[]{"少女","少男","前卫","优雅","自然","知性","浪漫","戏剧","摩登"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_show);

        backBtn = findViewById(R.id.clothesShowBtn);
        menuBtn = findViewById(R.id.clothesMenuBtn);
        imageView = findViewById(R.id.clothesShowImg);
        listView = findViewById(R.id.clothesShowListView);

        final Intent intent = getIntent();
        bundle = intent.getExtras();

        Object object = new Object();
        synchronized (object){
            getInfo((int)bundle.get("key"));
        }
        synchronized (object){
            initData();
        }
        backBtn.setOnClickListener(this);
        menuBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hidePopWindow();
        Object object = new Object();
        synchronized (object){
            getInfo((int)bundle.get("key"));
        }
        synchronized (object){
            initData();
        }
//        Object object = new Object();
//        synchronized (object){
//            getInfo((int)bundle.get("key"));
//        }
//        synchronized (object){
//            initData();
//        }
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
    private void getPhoto(String name,ImageView img) {
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
            img.setImageDrawable(bitmapDrawable);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    private void initData() {
        data = new String[]{"分类","颜色","季节","品牌","价格","备注","标签"};
        String[] data1 = new String[]{clothes.kind,clothes.color,clothes.season,clothes.brand,
                String.valueOf(clothes.price),clothes.content,"["+clothes.style+" "+clothes.detail+"]"};
        getPhoto(clothes.img,imageView);
        data2.clear();
        for(int i=0;i<data.length;i++){
            HashMap<String,String> map = new HashMap<>();
            map.put("title",data[i]);
            map.put("content",data1[i]);
            data2.add(map);
        }
        simpleAdapter = new SimpleAdapter(this,
                data2,
                R.layout.clothes_edit_item,
                new String[]{"title","content"},
                new int []{R.id.clothes_edit_text1,R.id.clothes_edit_text2});
        listView.setAdapter(simpleAdapter);
    }
    private int handle(Integer id){
        final Message message = new Message();

        final RequestBody requestBody = new FormBody.Builder()
                .add("id",String.valueOf(id))
                .build();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = message.post(requestBody,url.getPath("/clothes/delete"));
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
    public void hidePopWindow(){
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = 1.0f;
        this.getWindow().setAttributes(lp);
        if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.clothesShowBtn: {
                this.finish();
                break;
            }
            case R.id.clothesMenuBtn: {
                showPopWindow();
                break;
            }
            case R.id.matchMenuEdit: {
                Intent intent = new Intent(this,clothesEdit.class);
                intent.putExtra("Id",clothes.id);
                startActivity(intent);
                break;
            }
            case R.id.matchMenuRemove: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setMessage("确定要删除吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(handle(clothes.id) == 0){
                                    Toast.makeText(clothes_show.this,"删除成功",Toast.LENGTH_LONG).show();
                                    clothes_show.this.finish();
                                }
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
        }
    }

    private void showPopWindow() {
        hidePopWindow();
        View contentView = LayoutInflater.from(this).inflate(R.layout.item4,null);
        popupWindow = new PopupWindow(contentView,WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setContentView(contentView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                hidePopWindow();
            }
        });

        TextView tv1 = contentView.findViewById(R.id.matchMenuEdit);
        TextView tv2 = contentView.findViewById(R.id.matchMenuRemove);
        View v = findViewById(R.id.clothesMenuBtn);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = 0.6f;
        this.getWindow().setAttributes(lp);
        popupWindow.showAsDropDown(v);
    }
}

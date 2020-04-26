package com.baidu.speech.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.UnicodeSetSpanner;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.donkingliang.labels.LabelsView;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class match_show extends AppCompatActivity implements View.OnClickListener {

    public ImageButton backBtn;
    public ImageButton menuBtn;
    public ImageButton goodBtn;
    public ImageView imageView;
    public TextView textView;
    public LabelsView labelsView;
    public EditText contentText;
    public ArrayList<String> labelData;
    public PopupWindow popupWindow;
    public Bitmap bitmap;
    public Bundle bundle;
    public ClothesMatch clothesMatch;
    public response<ClothesMatch> response;
    private String[] styleList = new String[]{"少女","少男","前卫","优雅","自然","知性","浪漫","戏剧","摩登"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_show);

        backBtn = findViewById(R.id.matchShowBtn);
        menuBtn = findViewById(R.id.matchMenuBtn);
        goodBtn = findViewById(R.id.matchGoodBtn);
        imageView = findViewById(R.id.matchShowImg);
        textView = findViewById(R.id.goodNum);
        labelsView = findViewById(R.id.label);
        contentText = findViewById(R.id.matchShowContent);
        contentText.setHint("说说穿搭心得和感受，可以给他人提供参考");
        final Intent intent = getIntent();
        bundle = intent.getExtras();
        if((String.valueOf(bundle.get("flag"))).equals("1")){
            menuBtn.setEnabled(false);
            menuBtn.setVisibility(View.INVISIBLE);
        }

        Object object = new Object();
        synchronized (object){
            getInfo((int)bundle.get("key"));
        }
        synchronized (object){
            initData();
        }
        backBtn.setOnClickListener(this);
        menuBtn.setOnClickListener(this);
        goodBtn.setOnClickListener(this);
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


    private void initData() {
        textView.setText(String.valueOf(clothesMatch.like_num));
        labelData = new ArrayList<>();
        getPhoto(clothesMatch.img,imageView);
        labelData.add(clothesMatch.faceCircle);
        labelData.add(clothesMatch.faceWeight);
        labelData.add(clothesMatch.body);
        labelData.add(clothesMatch.season);
        labelData.add(clothesMatch.skinColor);
        labelData.add(String.valueOf(clothesMatch.height));
        labelData.add(clothesMatch.situation);
        labelData.add(getStyleList(clothesMatch.style));
        labelsView.setLabels(labelData);
        contentText.setText(clothesMatch.content);
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
    public void hidePopWindow(){
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = 1.0f;
        this.getWindow().setAttributes(lp);
        if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
            popupWindow = null;
        }
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
    private int handle(Integer id){
        final Message message = new Message();

        final RequestBody requestBody = new FormBody.Builder()
                .add("id",String.valueOf(id))
                .build();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = message.post(requestBody,url.getPath("/match/delete"));
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
    private int handleGood(Integer id){
        final Message message = new Message();

        final RequestBody requestBody = new FormBody.Builder()
                .add("id",String.valueOf(id))
                .add("like_num","1")
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
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.matchShowBtn: {
                this.finish();
                break;
            }
            case R.id.matchMenuBtn: {
                showPopWindow();
                break;
            }
            case R.id.matchGoodBtn: {
                if(handleGood(clothesMatch.id) == 0){
                    textView.setText(String.valueOf(Integer.parseInt(String.valueOf(textView.getText()))+1));
                }

                break;
            }
            case R.id.matchMenuEdit: {
                Intent intent = new Intent(this,matchEdit.class);
                intent.putExtra("Id",clothesMatch.id);
                startActivity(intent);
                break;
            }
            case R.id.matchMenuRemove: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setMessage("确定要删除吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(handle(clothesMatch.id) == 0){
                                    Toast.makeText(match_show.this,"删除成功",Toast.LENGTH_LONG).show();
                                    match_show.this.finish();
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
        View v = findViewById(R.id.matchMenuBtn);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = 0.6f;
        this.getWindow().setAttributes(lp);
        popupWindow.showAsDropDown(v);
    }
}

package com.baidu.speech.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static android.support.constraint.Constraints.TAG;
import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;

public class WardrobeFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private View view;
    private PopupWindow popupWindow;
    private CheckBox checkBox;
    private CheckBox checkBox2;
    private CheckBox checkBox3;
    private String select1;
    private String select2;
    private String select3;
    public ImageButton imageButton;
    public static final String KEY = "key";
    private  Uri uri;
    private String[] left_data;
    private Map<String,String[]> right_data;
    private response<List<Clothes>> response;

    public RecyclerView recyclerView;
    private ArrayList<Item> itemArrayList = new ArrayList<Item>();
    private RecyclerAdapter recyclerAdapter = new RecyclerAdapter(getContext(),itemArrayList);
    private List<Clothes> clothesList = new LinkedList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.wardrobe_fragment, container, false);
        if(initData()) initRecyclerView();
        System.out.println("create");
        checkBox = view.findViewById(R.id.checkBox4);
        checkBox2 = view.findViewById(R.id.checkBox3);
        checkBox3 = view.findViewById(R.id.checkBox);
        imageButton = view.findViewById(R.id.wardrobe_imageButton);

        imageButton.setOnClickListener(this);

        checkBox.setOnCheckedChangeListener(this);
        checkBox2.setOnCheckedChangeListener(this);
        checkBox3.setOnCheckedChangeListener(this);
        return view;
    }
    private void getInfo(String id) {
        if(id.equals("")){
            return;
        }
        final Message message = new Message();
        final RequestBody requestBody = new FormBody.Builder()
                .add("user_id",id)
                .build();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = message.post(requestBody,url.getPath("/clothes/find"));
                    System.out.println(result);
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
            if(response.code == 0){
                Gson gson = new Gson();
                System.out.println(gson.toJson(response.data));
                Type type = new TypeToken<List<Clothes>>() {}.getType();
                clothesList = gson.fromJson(gson.toJson(response.data),type);
                itemArrayList.clear();
                for(int i=0;i<clothesList.size();i++){
                    Item item = new Item();
                    Clothes clothes = clothesList.get(i);
                    System.out.println(clothes.img);
                    item.setImgPath(clothes.img);
                    item.setId(clothes.id);
                    item.setTitle(clothes.kind + " " + clothes.color + " " + clothes.season + " "
                    + clothes.brand +" " + clothes.detail);
                    itemArrayList.add(item);
                }
                recyclerAdapter.notifyDataSetChanged();
//                    System.out.println(clothesList.get(i).img);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        System.out.println("resume");
//        SharedPreferences sp = getContext().getSharedPreferences("userIfo", Context.MODE_PRIVATE);
        select1=null;
        select2=null;
        select3=null;
        if(initData()) initRecyclerView();
//        recyclerAdapter.notifyDataSetChanged();
    }
    private void initRecyclerView() {
        recyclerView = view.findViewById(R.id.wardrobe_recyclerview);
        for(int i=0;i<itemArrayList.size();i++) System.out.println(itemArrayList.get(i).imgPath);
        recyclerAdapter = new RecyclerAdapter(getActivity(),itemArrayList);
        recyclerView.setAdapter(recyclerAdapter);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.HORIZONTAL));
        recyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Item data) {
                Intent intent = new Intent(getActivity(),clothes_show.class);
                intent.putExtra("key",data.getId());
                startActivity(intent);
            }
        });
    }
    private boolean initData() {
        left_data = new String[]{"全部","上衣","外套","连衣裙","半身裙","裤子","鞋子","包","帽子"};
        right_data = new HashMap<>();
        right_data.put("全部",new String[]{"全部"});
        right_data.put("上衣",new String[]{"全部","T恤","衬衫","POLO衫","卫衣"});
        right_data.put("外套",new String[]{"全部","西装","夹克","运动服","风衣","牛仔外套","羽绒服","棉服"});
        right_data.put("连衣裙",new String[]{"全部","短裙","中长裙","长裙","小礼裙"});
        right_data.put("半身裙",new String[]{"全部","短裙","长裙","牛仔裙"});
        right_data.put("裤子",new String[]{"全部","西裤","休闲裤","直筒裤","九分裤","牛仔裤","运动裤","皮裤"});
        right_data.put("鞋子",new String[]{"全部","皮鞋","休闲鞋","凉鞋","高跟鞋","短靴","布鞋","运动鞋"});
        right_data.put("包",new String[]{"全部","单肩包","手提包","双肩包","腰包"});
        right_data.put("帽子",new String[]{"全部","太阳帽","运动帽","礼帽"});
        SharedPreferences sp = getContext().getSharedPreferences("userIfo", Context.MODE_PRIVATE);
        if(sp == null) return false;
        getInfo(sp.getString("id",""));
        return true;
//        for(int i=1;i<=10;i++){
//            Item item = new Item();
//            item.setImgPath(R.drawable.me);
//            item.setTitle("帅气衣服"+i);
//            itemArrayList.add(item);
//        }
    }

    public void hidePopWindow(){
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 1.0f;
        getActivity().getWindow().setAttributes(lp);
        if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
    private void showPopWindow() {
        hidePopWindow();

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.item1,null);
        popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setContentView(contentView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                hidePopWindow();
                checkBox.setChecked(false);
            }
        });
        TextView tv1 = contentView.findViewById(R.id.pop_text1);
        TextView tv2 = contentView.findViewById(R.id.pop_text2);
        TextView tv3 = contentView.findViewById(R.id.pop_text3);
        TextView tv4 = contentView.findViewById(R.id.pop_text4);

        View v = view.findViewById(R.id.view);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.6f;
        getActivity().getWindow().setAttributes(lp);
        popupWindow.showAsDropDown(v);
    }
    private void showPopWindow3() {
        hidePopWindow();


        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.item3,null);
        ArrayAdapter<String> left_adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,left_data);
        ListView listView = contentView.findViewById(R.id.leftView);
        listView.setAdapter(left_adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String result = parent.getItemAtPosition(position).toString();
                ArrayAdapter<String> right_adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,right_data.get(result));
                GridView gridView = contentView.findViewById(R.id.rightView);
                gridView.setNumColumns(2);
                gridView.setAdapter(right_adapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String str = parent.getItemAtPosition(position).toString();
                        if(str.equals("全部")){
                            checkBox3.setText(result);
                            select1 = null;
                        }
                        else {
                            checkBox3.setText(str);
                            select1 = str;
                        }
                        checkBox3.setChecked(false);
                        hidePopWindow();
                    }
                });
            }
        });
        popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, 800,true);

        popupWindow.setContentView(contentView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                hidePopWindow();
                checkBox3.setChecked(false);
            }
        });
        View v = view.findViewById(R.id.view);
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.6f;
        getActivity().getWindow().setAttributes(lp);
        popupWindow.showAsDropDown(v);
    }
    private void showPopWindow2() {
        hidePopWindow();

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.item2,null);
        popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setContentView(contentView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                hidePopWindow();
                checkBox2.setChecked(false);
            }
        });
        TextView tv5 = contentView.findViewById(R.id.pop_text5);
        TextView tv6 = contentView.findViewById(R.id.pop_text6);
        TextView tv7= contentView.findViewById(R.id.pop_text7);
        TextView tv8 = contentView.findViewById(R.id.pop_text8);
        TextView tv9 = contentView.findViewById(R.id.pop_text9);
        TextView tv10 = contentView.findViewById(R.id.pop_text10);

        View v = view.findViewById(R.id.view);
        tv5.setOnClickListener(this);
        tv6.setOnClickListener(this);
        tv7.setOnClickListener(this);
        tv8.setOnClickListener(this);
        tv9.setOnClickListener(this);
        tv10.setOnClickListener(this);

        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.6f;
        getActivity().getWindow().setAttributes(lp);
        popupWindow.showAsDropDown(v);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {

            case R.id.wardrobe_imageButton:{
//                Intent intent = new Intent(getActivity(),clothesEdit.class);
//                startActivity(intent);
                if(!PermissionManager.checkCameraPermission(getContext(),getActivity(),1))
                {
                    try {
                        //打开相机
                        openCamera();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.d("kjkl","here");
                }
                break;
            }
            case R.id.pop_text1:{
//                Toast.makeText(getActivity(),"全部",Toast.LENGTH_SHORT).show();
                checkBox.setText("全部");
                select3 = null;
                checkBox.setChecked(false);
                hidePopWindow();

                break;
            }
            case R.id.pop_text2:{
//                Toast.makeText(getActivity(),"春秋",Toast.LENGTH_SHORT).show();
                checkBox.setText("春秋");
                select3 = "春秋";
                checkBox.setChecked(false);
                hidePopWindow();
                break;
            }
            case R.id.pop_text3:{
//                Toast.makeText(getActivity(),"夏季",Toast.LENGTH_SHORT).show();
                checkBox.setText("夏季");
                select3 = "夏季";
                checkBox.setChecked(false);
                hidePopWindow();
                break;
            }
            case R.id.pop_text4:{
//                Toast.makeText(getActivity(),"冬季",Toast.LENGTH_SHORT).show();
                checkBox.setText("冬季");
                select3 = "冬季";
                checkBox.setChecked(false);
                hidePopWindow();
                break;
            }
            case R.id.pop_text5:{
//                Toast.makeText(getActivity(),"白色",Toast.LENGTH_SHORT).show();
                checkBox2.setText("白色");
                select2 = "白色";
                checkBox2.setChecked(false);
                hidePopWindow();
                break;
            }
            case R.id.pop_text6:{
//                Toast.makeText(getActivity(),"黑色",Toast.LENGTH_SHORT).show();
                checkBox2.setText("黑色");
                select2 = "黑色";
                checkBox2.setChecked(false);
                hidePopWindow();
                break;
            }
            case R.id.pop_text7:{
//                Toast.makeText(getActivity(),"蓝色",Toast.LENGTH_SHORT).show();
                checkBox2.setText("蓝色");
                select2 = "蓝色";
                checkBox2.setChecked(false);
                hidePopWindow();
                break;
            }
            case R.id.pop_text8:{
//                Toast.makeText(getActivity(),"黄色",Toast.LENGTH_SHORT).show();
                checkBox2.setText("黄色");
                select2 = "黄色";
                checkBox2.setChecked(false);
                hidePopWindow();
                break;
            }
            case R.id.pop_text9:{
//                Toast.makeText(getActivity(),"粉红色",Toast.LENGTH_SHORT).show();
                checkBox2.setText("粉红色");
                select2 = "粉红色";
                checkBox2.setChecked(false);
                hidePopWindow();
                break;
            }
            case R.id.pop_text10:{
//                Toast.makeText(getActivity(),"紫色",Toast.LENGTH_SHORT).show();
                checkBox2.setText("紫色");
                select2 = "紫色";
                checkBox2.setChecked(false);
                hidePopWindow();
                break;
            }
        }
    }

    private void openCamera() throws IOException{
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = FileManager.createFileIfNeed("filepath.png");
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            uri = Uri.fromFile(file);
        } else {
            uri = FileProvider.getUriForFile(getContext(), "com.baidu.speech.myapplication.provider", file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, 80);
    }

    public void showInfo(String kind,String color,String season){
        SharedPreferences sp = getContext().getSharedPreferences("userIfo", Context.MODE_PRIVATE);
        final Message message = new Message();
        FormBody.Builder f = new FormBody.Builder().add("user_id",String.valueOf(sp.getString("id","")));
        if(kind!=null) f.add("kind",kind);
        if(color!=null)f.add("color",color);
        if(season!=null)f.add("season",season);
        final RequestBody requestBody = f.build();

        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = message.post(requestBody,url.getPath("/clothes/find"));
                    System.out.println(result);
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
                System.out.println(gson.toJson(response.data));
                Type type = new TypeToken<List<Clothes>>() {}.getType();
                clothesList = gson.fromJson(gson.toJson(response.data),type);
                itemArrayList.clear();
                for(int i=0;i<clothesList.size();i++){
                    Item item = new Item();
                    Clothes clothes = clothesList.get(i);
                    item.setImgPath(clothes.img);
                    item.setId(clothes.id);
                    item.setTitle(clothes.kind + " " + clothes.color + " " + clothes.season + " "
                            + clothes.brand +" " + clothes.detail);
                    itemArrayList.add(item);
                }
                recyclerAdapter.notifyDataSetChanged();
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.checkBox4:{
                if(checkBox.isChecked()){
                    showPopWindow();
                }
                else {
                    showInfo(select1,select2,select3);
                    hidePopWindow();
                }
                break;
            }
            case R.id.checkBox3:{
                if(checkBox2.isChecked()){
                    showPopWindow2();
                }
                else {
                    showInfo(select1,select2,select3);
                    hidePopWindow();
                }
                break;
            }
            case R.id.checkBox:{
                if(checkBox3.isChecked()){
                    showPopWindow3();
                }
                else {
                    showInfo(select1,select2,select3);
                    hidePopWindow();
                }
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d("----","dkfjkdjf");
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 0 )return;
        Intent intent = new Intent(getContext(),clothesEdit.class);
//        Log.d("---------", String.valueOf(resultCode));
        intent.putExtra(KEY, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/android/"+"filepath.png");
        startActivity(intent);
    }
}

package com.baidu.speech.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class HomeFragment extends Fragment {

    private View view;
    public RecyclerView recyclerView;
    private ArrayList<Item>  itemArrayList = new ArrayList<Item>();
    private RecyclerAdapter recyclerAdapter;
    private response<List<ClothesMatch>> response;
    private response<user> userResponse;
    private List<ClothesMatch> clothesMatchList = new LinkedList<>();
    private user u;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);
        initData();
        initRecyclerView();



        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        initRecyclerView();

    }

    private void getInfo(String body, String face_circle, String style, String skin_color, String height, String season, String face_weight) {
        final Message message = new Message();

        final RequestBody requestBody = new FormBody.Builder()
                .add("body",body)
                .add("face_circle",face_circle)
                .add("style",style)
                .add("skin_color",skin_color)
                .add("height",height)
                .add("season",season)
                .add("face_weight",face_weight)
                .build();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = message.post(requestBody,url.getPath("/match/recommendation"));
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
            if(response == null) return;
            if(response.code == 0){
                Gson gson = new Gson();
//                System.out.println(gson.toJson(response.data));
                Type type = new TypeToken<List<ClothesMatch>>() {}.getType();
                clothesMatchList = gson.fromJson(gson.toJson(response.data),type);
                itemArrayList.clear();
                for(int i=0;i<clothesMatchList.size();i++){
                    Item item = new Item();
                    ClothesMatch clothesMatch = clothesMatchList.get(i);
                    item.setImgPath(clothesMatch.img);
                    item.setId(clothesMatch.id);
                    item.setTitle(clothesMatch.situation+clothesMatch.season+clothesMatch.body+clothesMatch.faceCircle+clothesMatch.faceWeight+clothesMatch.skinColor);
                    itemArrayList.add(item);
//                    System.out.println(item.imgPath);
                }
//                    System.out.println(clothesList.get(i).img);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    private void getInfo2(String name) {
        if(name.equals("")) {
            getInfo("X型","轮廓中等",String.valueOf(0),"色调中等",String.valueOf(170),"夏季","量感中等");
            return;
        }
        final Message message = new Message();
        final RequestBody requestBody = new FormBody.Builder()
                .add("name",name)
                .build();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = message.post(requestBody,url.getPath("/user/find"));
                    System.out.println(result);
                    Gson gson = new Gson();
                    userResponse = gson.fromJson(result, com.baidu.speech.myapplication.response.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
            if(userResponse.code == 0){
                Gson gson = new Gson();

                u = gson.fromJson(gson.toJson(userResponse.data),user.class);
                SharedPreferences sp = getActivity().getSharedPreferences("userIfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("id",String.valueOf(u.id));
                editor.commit();
                System.out.println(sp.getString("id","123"));
                getInfo(u.body,u.faceCircle,String.valueOf(u.style),u.skinColor,String.valueOf(u.height),"夏季",u.faceWeight);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    private boolean initData() {
        SharedPreferences sp = getContext().getSharedPreferences("userIfo", Context.MODE_PRIVATE);
        getInfo2(sp.getString("name",""));
        return true;
    }

    private void initRecyclerView() {
            recyclerView = view.findViewById(R.id.recyclerView);
            recyclerAdapter = new RecyclerAdapter(getActivity(),itemArrayList);
            recyclerView.setAdapter(recyclerAdapter);

            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.HORIZONTAL));
            recyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(View view, Item data) {
                    Intent intent = new Intent(getActivity(),match_show.class);
                    intent.putExtra("key",data.getId());
                    intent.putExtra("flag",1);
                    startActivity(intent);
                }
            });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

}

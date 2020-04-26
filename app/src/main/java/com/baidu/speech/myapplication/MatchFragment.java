package com.baidu.speech.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class MatchFragment extends Fragment {

    private View view;
    public RecyclerView recyclerView;
    private ArrayList<Item> itemArrayList = new ArrayList<Item>();
    private RecyclerAdapter recyclerAdapter;
    public static final String KEY = "key";
    private response<List<ClothesMatch>> response;
    private List<ClothesMatch> clothesMatchList = new LinkedList<>();
    private  Uri uri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.match_fragment, container, false);
        //Toolbar toolbar = view.findViewById(R.id.toolbar);
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if(initData()) initRecyclerView();
        ImageButton button = view.findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(),matchEdit.class);
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
            }
        });
        initRecyclerView();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        recyclerAdapter.notifyDataSetChanged();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 0 )return;
        Intent intent = new Intent(getContext(),matchEdit.class);
//        Log.d("---------", String.valueOf(resultCode));
        intent.putExtra(KEY, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/android/"+"filepath.png");
        startActivity(intent);
    }
    private void getInfo(String id) {
        if(id.equals("")) {
            return;
        }
        itemArrayList.clear();
        final Message message = new Message();
        final RequestBody requestBody = new FormBody.Builder()
                .add("user_id",String.valueOf(id))
                .build();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = message.post(requestBody,url.getPath("/match/find"));
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
                Type type = new TypeToken<List<ClothesMatch>>() {}.getType();
                clothesMatchList = gson.fromJson(gson.toJson(response.data),type);
                for(int i=0;i<clothesMatchList.size();i++){
                    Item item = new Item();
                    ClothesMatch clothesMatch = clothesMatchList.get(i);
                    item.setImgPath(clothesMatch.img);
                    item.setId(clothesMatch.id);
                    item.setTitle(clothesMatch.situation+clothesMatch.season+clothesMatch.body+clothesMatch.faceCircle+clothesMatch.faceWeight+clothesMatch.skinColor);
                    itemArrayList.add(item);

                }
//                    System.out.println(clothesList.get(i).img);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    private boolean initData() {
        SharedPreferences sp = getContext().getSharedPreferences("userIfo", Context.MODE_PRIVATE);

        getInfo(sp.getString("id",""));
        return true;
    }

    private void openCamera() throws IOException {
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
    private void initRecyclerView() {
        recyclerView = view.findViewById(R.id.recyclerView2);
        recyclerAdapter = new RecyclerAdapter(getActivity(),itemArrayList);
        recyclerView.setAdapter(recyclerAdapter);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Item data) {
                Intent intent = new Intent(getActivity(),match_show.class);
                intent.putExtra("key",data.getId());
                intent.putExtra("flag",0);
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

package com.baidu.speech.myapplication;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class UserFragment extends Fragment {

    private UserViewModel userViewModel;
    private ListView listView;
    private TextView textView;
    private Button button;
    private ImageView imageView;
    private String [] data = {"版本","登录"};
    private TextView textViewClothes;
    private TextView textViewMatch;
    private TextView textViewPrice;
    private response<user> response;
    private user u;
    private Bitmap bitmap;
    private
    ArrayAdapter<String> adapter;
    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment, container, false);
        listView = view.findViewById(R.id.listView);
        textView = view.findViewById(R.id.textView);
        imageView = view.findViewById(R.id.imageView2);
        textViewClothes = view.findViewById(R.id.textView12);
        textViewMatch = view.findViewById(R.id.textView11);
        textViewPrice = view.findViewById(R.id.textView13);
        button = view.findViewById(R.id.button3);
//        SharedPreferences sp = getContext().getSharedPreferences("userIfo", Context.MODE_PRIVATE);
//        if(sp.getString("name",null)!=null) {
//            textView.setText(sp.getString("name",""));
//            data[3] = "退出登录";
//        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),editUserInfo.class);
                intent.putExtra("photo",u.name);
                intent.putExtra("phone",u.phoneNum);
                intent.putExtra("age",u.age);
                intent.putExtra("sex",u.sex);
                intent.putExtra("style",u.style);
                intent.putExtra("body",u.body);
                intent.putExtra("height",u.height);
                intent.putExtra("faceCircle",u.faceCircle);
                intent.putExtra("faceWeight",u.faceWeight);
                intent.putExtra("skinColor",u.skinColor);
                startActivity(intent);
            }
        });
        adapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Intent intent = new Intent(getActivity(),version.class);
                    startActivity(intent);
                }
                if(position == 1){
                    SharedPreferences sp = getContext().getSharedPreferences("userIfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("name",null);
                    editor.putString("password",null);
                    editor.commit();
//                    textView.setText(sp.getString("name",null));
                    Intent intent = new Intent(getActivity(),login.class);
                    startActivity(intent);

                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("resume");
        SharedPreferences sp = getContext().getSharedPreferences("userIfo", Context.MODE_PRIVATE);
        if(sp.getString("name",null)!=null) {
            System.out.println("here");
            button.setEnabled(true);
            textView.setText(sp.getString("name",""));
            data[1] = "退出登录";
            adapter.notifyDataSetChanged();
            getInfo(sp.getString("name",""));
            getPhoto(sp.getString("name",""));
        }
        else {
            button.setEnabled(false);
            textView.setText("用户名");
            data[1]="登录";
            adapter.notifyDataSetChanged();
            textViewClothes.setText("0");
            textViewMatch.setText("0");
            textViewPrice.setText("0");
        }
    }
    private void getPhoto(String name) {
        final Message message = new Message();
        final RequestBody requestBody = new FormBody.Builder()
                .add("name",name)
                .build();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmap = message.getPhoto(requestBody,url.getPath("/file/find"));
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
    private void getInfo(String name) {
        final Message message = new Message();
        System.out.println(name);
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

                u = gson.fromJson(gson.toJson(response.data),user.class);
                SharedPreferences sp = getContext().getSharedPreferences("userIfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("id",String.valueOf(u.id));
                editor.commit();
                textViewClothes.setText(Integer.toString(u.clothesNum));
                textViewMatch.setText(Integer.toString(u.matchNum));
                textViewPrice.setText(Integer.toString(u.clothesTotalPrice));
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

    }
}

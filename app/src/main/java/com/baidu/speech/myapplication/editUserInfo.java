package com.baidu.speech.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class editUserInfo extends AppCompatActivity implements View.OnClickListener {

    private ImageButton button;
    private RecyclerView recyclerView;
    private ArrayList<item2> list = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private response response;
    private Context context;
    private UserIfoAdapter userIfoAdapter;
    private int selectIndex;
    private Bundle bundle;
    private int styleFromBundle;
    private String[] styleList = new String[]{"少女","少男","前卫","优雅","自然","知性","浪漫","戏剧","摩登"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        context = this;
        final Intent intent = getIntent();
        bundle = intent.getExtras();
        sharedPreferences = this.getSharedPreferences("userIfo",Context.MODE_PRIVATE);


//        File file = new File((String)bundle.get("photo"));

        list.add(new item2("头像",(String)bundle.get("photo")));
        list.add(new item2("用户名",sharedPreferences.getString("name","")));
        list.add(new item2("密码",""));
        list.add(new item2("电话",(String)bundle.get("phone")));
        list.add(new item2("年龄",(String)bundle.get("age")));
        list.add(new item2("性别",(String)bundle.get("sex")));
        for(int i=0;i<list.size();i++) System.out.println(list.get(i).getContent());
        styleFromBundle = (int)bundle.get("style");
        list.add(new item2("风格",getStyleList(styleFromBundle)));
        list.add(new item2("体型",(String)bundle.get("body")));
        if(bundle.get("height")==null) list.add(new item2("身高",null));
        else    list.add(new item2("身高",String.valueOf(bundle.get("height"))));
        list.add(new item2("面部曲度",(String)bundle.get("faceCircle")));
        list.add(new item2("面部量感",(String)bundle.get("faceWeight")));
        list.add(new item2("肤色",(String)bundle.get("skinColor")));



//        list.add(new item2("电话",))
        button = findViewById(R.id.userInfoBackBtn);
        recyclerView = findViewById(R.id.userInfoRVList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        userIfoAdapter = new UserIfoAdapter(this,list);
        recyclerView.setAdapter(userIfoAdapter);
        userIfoAdapter.setOnItemClickListener(new UserIfoAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, item2 data) {
                if(data.getTitle().equals("头像")){
                    if(!PermissionManager.checkPhotoPermission(editUserInfo.this,editUserInfo.this,1)){
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,1);
                    }
                }else if(data.getTitle().equals("用户名")){
                    showNameInputDialog();
                }else if(data.getTitle().equals("密码")) {
                    showInputDialog("密码", "password",2);
                }else if(data.getTitle().equals("电话")){
                    showInputDialog("电话","phoneNum",3);
                }else if(data.getTitle().equals("年龄")){
                    showInputDialog("年龄","age",4);
                }else if(data.getTitle().equals("性别")){
                    if(list.get(5).getContent()!=null && list.get(5).getContent().equals("男"))
                        showSingleChoiceDialog(new String[]{"男","女"},"性别",0,"sex",5);
                    else
                        showSingleChoiceDialog(new String[]{"男","女"},"性别",1,"sex",5);
                }
                else if(data.getTitle().equals("风格")){
                    showMultiChoiceDialog();
                }else if(data.getTitle().equals("体型")){
                    String[] s = {"Y型","X型","O型","A型","H型"};
                    for(int i=0;i<5;i++)
                        if(s[i].equals(list.get(7).getContent())){
                            showSingleChoiceDialog(s,"体型",i,"body",7);
                        }
                }else if(data.getTitle().equals("身高")){
                    showInputDialog("身高","height",8);
                }else if(data.getTitle().equals("面部曲度")){
                    String[] s = {"轮廓较圆","轮廓中等","轮廓较直"};
                    for(int i=0;i<3;i++)
                        if(s[i].equals(list.get(9).getContent())){
                            showSingleChoiceDialog(s,"面部曲度",i,"faceCircle",9);
                        }
                }else if(data.getTitle().equals("面部量感")){
                    String[] s = {"量感较小","量感中等","量感较大"};
                    for(int i=0;i<3;i++)
                        if(s[i].equals(list.get(10).getContent())){
                            showSingleChoiceDialog(s,"面部量感",i,"faceWeight",10);
                        }
                }else if(data.getTitle().equals("肤色")){
                    String[] s = {"色调较冷","色调中等","色调较暖"};
                    for(int i=0;i<3;i++)
                        if(s[i].equals(list.get(11).getContent())){
                            showSingleChoiceDialog(s,"肤色",i,"skinColor",11);
                        }
                }
            }
        });
        button.setOnClickListener(this);
    }
    private void showMultiChoiceDialog() {
        // 设置默认选中的选项，全为false默认均未选中
        final boolean[] initChoiceSets=getStyleIndexList(styleFromBundle);
        AlertDialog.Builder multiChoiceDialog =
                new AlertDialog.Builder(this);
        multiChoiceDialog.setTitle("选择风格");
        multiChoiceDialog.setMultiChoiceItems(styleList, initChoiceSets,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (isChecked) {
                            initChoiceSets[which] = true;
                        } else {
                            initChoiceSets[which] = false;
                        }
                    }
                });
        multiChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int res = 0;
                        for (int i = 8; i >=0; i--) {
                            res*=2;
                            if(initChoiceSets[i])res+=1;
                        }
                        if(handle(sharedPreferences.getString("name",""),String.valueOf(res),"style") == 0){
                            Toast.makeText(context,"修改风格成功",Toast.LENGTH_LONG).show();
                            styleFromBundle = res;
                            list.set(6,new item2("风格",getStyleList(res)));
                            userIfoAdapter.notifyDataSetChanged();
                        }
                    }
                });
        multiChoiceDialog.show();
    }
    private boolean[] getStyleIndexList(int style){
        boolean [] res = new boolean[9];
        int k =0;
        while (style!=0){
            if(style%2 == 1) res[k]=true;
            k++;
            style/=2;
        }
        return res;
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

    private int handle(String name,String content,String title){
        final Message message = new Message();

        final RequestBody requestBody = new FormBody.Builder()
                .add("name",name)
                .add(title,content)
                .build();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = message.post(requestBody,url.getPath("/user/update"));
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
    private void showSingleChoiceDialog(final String[] items, final String title, final int index,final String para,final int i){
        selectIndex = -1;
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(this);
        singleChoiceDialog.setTitle("选择"+title);
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, index,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectIndex = which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectIndex!=-1)
                       if(handle(sharedPreferences.getString("name",""),items[selectIndex],para) == 0){
                           Toast.makeText(context,"修改"+title+"成功",Toast.LENGTH_LONG).show();
                           list.set(i,new item2(title,items[selectIndex]));
                           userIfoAdapter.notifyDataSetChanged();
                       }
                    }
                });
        singleChoiceDialog.show();
    }
    private void showInputDialog(final String title,final String content,final int index){
        final EditText inputServer = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(editUserInfo.this);
        builder.setTitle("修改"+title).setView(inputServer)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String mMeetName = inputServer.getText().toString();
                if(title.equals("身高")){
                    for(int i=0;i<mMeetName.length();i++)
                        if(!Character.isDigit(mMeetName.charAt(i))){
                            Toast.makeText(context,"格式不对",Toast.LENGTH_LONG).show();
                            return;
                        }
                }
                int res = handle(sharedPreferences.getString("name",""),mMeetName,content);
                if(res == 0) {
                    Toast.makeText(context,"修改"+title+"成功",Toast.LENGTH_LONG).show();
                    if(title.equals("密码")){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("password",mMeetName);
                        editor.commit();
                    }
                    else {
                        list.set(index,new item2(title,mMeetName));
                        userIfoAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        builder.show();
    }
    private int editUserName(String name,String new_name){
        final Message message = new Message();
        final RequestBody requestBody = new FormBody.Builder()
                .add("name",name)
                .add("new_name",new_name)
                .build();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = message.post(requestBody,url.getPath("/user/updateName"));
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
    private void showNameInputDialog(){
        final EditText inputServer = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(editUserInfo.this);
        builder.setTitle("修改用户名").setView(inputServer)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String mMeetName = inputServer.getText().toString();
                int res = editUserName(sharedPreferences.getString("name",""),mMeetName);
                if(res == 0) {
                    Toast.makeText(context,"修改用户名成功",Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name",mMeetName);
                    editor.commit();
                    list.set(1,new item2("用户名",mMeetName));
                    userIfoAdapter.notifyDataSetChanged();
                }
                else if(res == 1) Toast.makeText(context,"该用户名被占用",Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // TODO 自动生成的方法存根
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            //查询我们需要的数据
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            //拿到了图片的路径picturePath可以自行使用
//            img_view.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            System.out.println(picturePath);
            ImageView imageView = findViewById(R.id.imageView5);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            sendFile(picturePath,url.getPath("/file/add"),sharedPreferences.getString("name",""));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendFile(final String filepath, final String url, final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(filepath+"*****************"+name);
                OkHttpClient client = new OkHttpClient();
                File file = new File(BitmapUtil.compressImage(filepath));
                System.out.println(file.getPath()+"--------"+name);
                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("name",name)
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
                            Log.e("text",e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.i("text","success upload");
                            String json = response.body().string();
                            Log.i("success-----------","成功"+json);
                        }
                    });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        int id =v.getId();
        switch (id){
            case R.id.userInfoBackBtn:{
                this.finish();
            }
        }
    }
}

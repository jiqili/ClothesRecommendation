package com.baidu.speech.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.myViewHolder> {
    private Context context;
    private ArrayList<Item> itemArrayList;
    private Bitmap bitmap;
    public RecyclerAdapter(Context context, ArrayList<Item> itemArrayList){
        this.context = context;
        this.itemArrayList = itemArrayList;
        if(this.itemArrayList == null) System.out.println("-----------");
    }

    public myViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View itemView = View.inflate(context,R.layout.item_layout,null);
        return new myViewHolder(itemView);
    }

    public void onBindViewHolder(myViewHolder holder,int position){
        Item data = itemArrayList.get(position);
        holder.title.setText(data.title);
        getPhoto(data.imgPath,holder.img);
//        holder.img.setImageResource(data.imgPath);
        //holder.img.setImageURI(Uri.fromFile(new File(data.imgPath)));
       // holder.img.setImageURI(Uri.fromFile(new File("../../../res/drawable/me.jpg")));
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
    public int getItemCount(){
        return  itemArrayList.size();
    }
    class myViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView title;

        public myViewHolder(View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imageView3);
            title = itemView.findViewById(R.id.textView3);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.OnItemClick(v,itemArrayList.get(getLayoutPosition()));
                    }
                }
            });
        }


    }
    public interface OnItemClickListener {
        public void OnItemClick(View view, Item data);
    }
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}

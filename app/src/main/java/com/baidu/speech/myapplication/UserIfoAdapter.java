package com.baidu.speech.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.Inflater;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class UserIfoAdapter extends RecyclerView.Adapter {
    public Context context;
    public ArrayList<item2> item2ArrayList;
    private int TYPE_ONE = 1;
    private int TYPE_TWO = 2;
    private Bitmap bitmap;

    public UserIfoAdapter(Context context, ArrayList<item2> item2ArrayList) {
        this.context = context;
        this.item2ArrayList = item2ArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if(i == TYPE_ONE){
            view = LayoutInflater.from(context).inflate(R.layout.userinfoitem1,viewGroup,false);
            return new viewHolder(view);
        }
        else if(i == TYPE_TWO){
            view = LayoutInflater.from(context).inflate(R.layout.userinfoitem2,viewGroup,false);
            return new viewHolderTwo(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        item2 i = item2ArrayList.get(position);
        if(viewHolder instanceof viewHolder){
            ((viewHolder)viewHolder).textView.setText(i.getTitle());
            ((viewHolder)viewHolder).textView2.setText(i.getContent());
        }
        else {
            ((viewHolderTwo)viewHolder).textView.setText(i.getTitle());
            System.out.println(i.getContent());
            setPhoto(i.getContent(),((viewHolderTwo)viewHolder).imageView);

//            ((viewHolderTwo)viewHolder).imageView.setImageResource(i.getContent());
        }
    }
    private void setPhoto(String name,ImageView imageView) {
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
    @Override
    public int getItemCount() {
        System.out.println(item2ArrayList.size());
        return item2ArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) return TYPE_TWO;
        else return TYPE_ONE;
    }
    public interface OnItemClickListener {
        public void OnItemClick(View view, item2 data);
    }
    private UserIfoAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(UserIfoAdapter.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    private class viewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private TextView textView2;
        private ImageButton imageButton;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.userInfoTitle);
            textView2 = itemView.findViewById(R.id.userInfoContent);
            imageButton = itemView.findViewById(R.id.imageButton2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.OnItemClick(v,item2ArrayList.get(getLayoutPosition()));
                    }
                }
            });
        }}
    private class viewHolderTwo extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;
        private ImageButton imageButton;
        public viewHolderTwo(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.userInfoTitle2);
            imageView = itemView.findViewById(R.id.imageView5);
            imageButton = itemView.findViewById(R.id.imageButton4);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.OnItemClick(v,item2ArrayList.get(getLayoutPosition()));
                    }
                }
            });
        }}
}

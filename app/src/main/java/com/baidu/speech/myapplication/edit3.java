package com.baidu.speech.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

public class edit3 extends AppCompatActivity implements View.OnClickListener {

    private String[] left_data;
    private Map<String,String[]> right_data;
    public ImageButton backBtn;
    public Button handleBtn;
    public String res="";
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit3);
        initData();
        setAdapter();
        backBtn = findViewById(R.id.editBackBtn3);
        handleBtn = findViewById(R.id.editHandleBtn3);
        backBtn.setOnClickListener(this);
        handleBtn.setOnClickListener(this);
    }
    private void setAdapter() {
        ArrayAdapter<String> left_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked,left_data);
        ListView listView = this.findViewById(R.id.clothesEditLeftView);
        listView.setAdapter(left_adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String result = parent.getItemAtPosition(position).toString();
                res = result;
                String [] tmp = right_data.get(result);
                for(int i=0;i<tmp.length;i++){
                    Log.d("",tmp[i]);
                }
                ArrayAdapter<String> right_adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_checked,tmp);
                GridView gridView = findViewById(R.id.clothesEditRightView);
                gridView.setNumColumns(2);
                gridView.setAdapter(right_adapter);
                gridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        res = parent.getItemAtPosition(position).toString();
                    }
                });
            }
        });
    }

    private void initData() {
        left_data = new String[]{"上衣","外套","连衣裙","半身裙","裤子","鞋子","包","帽子"};
        right_data = new HashMap<>();
        right_data.put("上衣",new String[]{"T恤","衬衫","POLO衫","卫衣"});
        right_data.put("外套",new String[]{"西装","夹克","运动服","风衣","牛仔外套","羽绒服","棉服"});
        right_data.put("连衣裙",new String[]{"短裙","中长裙","长裙","小礼裙"});
        right_data.put("半身裙",new String[]{"短裙","长裙","牛仔裙"});
        right_data.put("裤子",new String[]{"西裤","休闲裤","直筒裤","九分裤","牛仔裤","运动裤","皮裤"});
        right_data.put("鞋子",new String[]{"皮鞋","休闲鞋","凉鞋","高跟鞋","短靴","布鞋","运动鞋"});
        right_data.put("包",new String[]{"单肩包","手提包","双肩包","腰包"});
        right_data.put("帽子",new String[]{"太阳帽","运动帽","礼帽"});
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.editBackBtn3: {
                Intent intent = new Intent();
                this.setResult(8,intent);
                finish();
                break;
            }
            case R.id.editHandleBtn3: {
                Intent intent = new Intent();
                intent.putExtra("res",res);
                this.setResult(6,intent);
                finish();
                break;
            }
        }
    }
}

package com.baidu.speech.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;

public class edit6 extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public ImageButton backBtn;
    public Button handleBtn;
    public String[] res = {"","","","",""};
    public Spinner spinner1;
    public Spinner spinner2;
    public Spinner spinner3;
    public Spinner spinner4;
    public Spinner spinner5;
    public ArrayAdapter<String> adapter1;
    public ArrayAdapter<String> adapter2;
    public ArrayAdapter<String> adapter3;
    public ArrayAdapter<String> adapter4;
    public ArrayAdapter<String> adapter5;
    public String[] data1={"Y型","H型","A型","X型","O型"};
    public String[] data2={"色调较冷","色调中等","色调较暖"};
    public String[] data3=getData3();
    public String[] data4={"轮廓较直","轮廓中等","轮廓较圆"};
    public String[] data5={"量感较小","量感中等","量感较大"};
    public Animation myAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit6);

        backBtn = findViewById(R.id.editBackBtn6);
        handleBtn = findViewById(R.id.editHandleBtn6);
        backBtn.setOnClickListener(this);
        handleBtn.setOnClickListener(this);

        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        spinner3 = findViewById(R.id.spinner3);
        spinner4 = findViewById(R.id.spinner4);
        spinner5 = findViewById(R.id.spinner5);

        adapter1 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,data1);
        adapter2 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,data2);
        adapter3 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,data3);
        adapter4 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,data4);
        adapter5 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,data5);

        myAnimation  = AnimationUtils.loadAnimation(this,R.anim.my_anim);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);
        spinner3.setAdapter(adapter3);
        spinner4.setAdapter(adapter4);
        spinner5.setAdapter(adapter5);

        spinner1.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);
        spinner3.setOnItemSelectedListener(this);
        spinner4.setOnItemSelectedListener(this);
        spinner5.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.editBackBtn6: {
                Intent intent = new Intent();
                this.setResult(8,intent);
                finish();
                break;
            }
            case R.id.editHandleBtn6: {
                Intent intent = new Intent();
                intent.putExtra("res",res[0]+','+res[1]+','+res[2]+','+res[3]+','+res[4]);
                this.setResult(5,intent);
                finish();
                break;
            }
        }
    }

    public String[] getData3(){
        String[] res =new String[100];
        for(int i=0;i<res.length;i++) res[i] = String.valueOf(i+100);
        return res;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int i = parent.getId();
        switch (i) {
            case R.id.spinner1: {
                res[0] = data1[position];
                break;
            }
            case R.id.spinner2: {
                res[1] = data2[position];
                break;
            }
            case R.id.spinner3: {
                res[2] = data3[position];
                break;
            }
            case R.id.spinner4: {
                res[3] = data4[position];
                break;
            }
            case R.id.spinner5: {
                res[4] = data5[position];
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

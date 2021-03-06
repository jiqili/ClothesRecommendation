package com.baidu.speech.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.donkingliang.labels.LabelsView;

import java.util.ArrayList;

public class edit5 extends AppCompatActivity implements View.OnClickListener{

    public ImageButton backBtn;
    public Button handleBtn;
    public ArrayList<String> select_labels;
    public LabelsView style_label;
    public LabelsView situation_label;
    public TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit4);

        select_labels = new ArrayList<>();
        backBtn = findViewById(R.id.editBackBtn4);
        handleBtn = findViewById(R.id.editHandleBtn4);
        style_label = findViewById(R.id.style_label);
        situation_label = findViewById(R.id.details_label);
        textView = findViewById(R.id.select_items);
        ArrayList<String> style_data = new ArrayList<>();
        ArrayList<String> details_data = new ArrayList<>();
        initData(style_data, details_data);
        TextView labelTextView = findViewById(R.id.textView16);
        labelTextView.setText("情景");
        style_label.setLabels(style_data);
        situation_label.setLabels(details_data);
        style_label.setSelectType(LabelsView.SelectType.MULTI);
        situation_label.setSelectType(LabelsView.SelectType.SINGLE);
        situation_label.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
            @Override
            public void onLabelSelectChange(TextView label, Object data, boolean isSelect, int position) {
                if(isSelect){
//                    Log.d(data.toString()," checked");
                    select_labels.add(data.toString());
                    textView.setText(select_labels.toString());
                }
                else {
//                    Log.d(data.toString()," unchecked");
                    select_labels.remove(data.toString());
                    textView.setText(select_labels.toString());
                }
            }
        });
        style_label.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
            @Override
            public void onLabelSelectChange(TextView label, Object data, boolean isSelect, int position) {
                if(isSelect){
//                    Log.d(data.toString()," checked");
                    select_labels.add(data.toString());
                    textView.setText(select_labels.toString());
                }
                else {
//                    Log.d(data.toString()," unchecked");
                    select_labels.remove(data.toString());
                    textView.setText(select_labels.toString());
                }
            }
        });
        backBtn.setOnClickListener(this);
        handleBtn.setOnClickListener(this);
    }

    private void initData(ArrayList<String> style_data, ArrayList<String> situation_label) {
        style_data.add("少女");
        style_data.add("少年");
        style_data.add("前卫");
        style_data.add("优雅");
        style_data.add("自然");
        style_data.add("知性");
        style_data.add("浪漫");
        style_data.add("戏剧");
        style_data.add("摩登");
        situation_label.add("办公");
        situation_label.add("休闲");
        situation_label.add("运动");
        situation_label.add("约会");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.editBackBtn4: {
                Intent intent = new Intent();
                this.setResult(8,intent);
                finish();
                break;
            }
            case R.id.editHandleBtn4: {
                Intent intent = new Intent();
                intent.putExtra("res",textView.getText());
                this.setResult(5,intent);
                finish();
                break;
            }
        }
    }
}

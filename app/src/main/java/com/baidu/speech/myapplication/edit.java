package com.baidu.speech.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


public class edit extends AppCompatActivity implements View.OnClickListener {

    public EditText editText;
    public ImageButton backBtn;
    public Button handleBtn;
    public String[] data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editText = findViewById(R.id.add_content);
        backBtn = findViewById(R.id.editBackBtn);
        handleBtn = findViewById(R.id.editHandleBtn);
        backBtn.setOnClickListener(this);
        handleBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.editBackBtn: {
                Intent intent = new Intent();
                intent.putExtra("res",editText.toString());
                this.setResult(8,intent);
                finish();
                break;
            }
            case R.id.editHandleBtn: {
                Intent intent = new Intent();
                intent.putExtra("res",editText.getText().toString());
                this.setResult(9,intent);
                finish();
                break;
            }
        }
    }
}

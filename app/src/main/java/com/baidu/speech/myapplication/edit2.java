package com.baidu.speech.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

public class edit2 extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public ImageButton backBtn;
    public Button handleBtn;
    public String [] data={""};
    public ArrayAdapter<String> arrayAdapter;
    public ListView listView;
    public String res="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit2);

        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        data = (String[])bundle.get(clothesEdit.KEY);
        backBtn = findViewById(R.id.editBackBtn2);
        handleBtn = findViewById(R.id.editHandleBtn2);
        listView = findViewById(R.id.clothesEditListView);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked,data);
        listView.setAdapter(arrayAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(this);
        backBtn.setOnClickListener(this);
        handleBtn.setOnClickListener(this);
    }
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.editBackBtn2: {
                Intent intent = new Intent();
                this.setResult(8,intent);
                finish();
                break;
            }
            case R.id.editHandleBtn2: {
                Intent intent = new Intent();
                intent.putExtra("res",res);
                this.setResult(7,intent);
                finish();
                break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        res = parent.getItemAtPosition(position).toString();
    }
}

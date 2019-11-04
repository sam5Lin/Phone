package com.example.phone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        phone = findViewById(R.id.m_number);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        phone.setText(bundle.getString("number"));
        phone.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back1:
                //数据是使用Intent返回
                Intent intent = new Intent();
                //把返回数据存入Intent
                intent.putExtra("result", "My name is linjiqin");
                //设置返回数据
                MessageActivity.this.setResult(RESULT_OK, intent);
                //关闭Activity
                MessageActivity.this.finish();
        }

    }
}

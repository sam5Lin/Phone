package com.example.phone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText phone;
    private EditText content;
    private Button send;
    private String number;
    private String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        phone = findViewById(R.id.m_number);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        number = bundle.getString("number");
        phone.setText(number);

        content = findViewById(R.id.m_content);
        send = findViewById(R.id.send);
        findViewById(R.id.back1).setOnClickListener(this);
        content.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back1:
                //数据是使用Intent返回
                Intent intent = new Intent();
                //设置返回数据
                MessageActivity.this.setResult(RESULT_OK, intent);
                //关闭Activity
                MessageActivity.this.finish();
                break;
            case R.id.send:
                number = phone.getText().toString();
                message = content.getText().toString();
                Intent intent1 = new Intent();                        //创建 Intent 实例
                intent1.setAction(Intent.ACTION_SENDTO);             //设置动作为发送短信
                intent1.setData(Uri.parse("smsto:"+number));           //设置发送的号码
                intent1.putExtra("sms_body", message);              //设置发送的内容
                startActivity(intent1);                               //启动 Activity
                break;
        }

    }
}

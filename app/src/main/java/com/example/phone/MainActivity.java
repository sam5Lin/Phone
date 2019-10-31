package com.example.phone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView number;
    private StringBuffer str;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        number = findViewById(R.id.number);
        str = new StringBuffer();

        findViewById(R.id.call).setOnClickListener(this);
        findViewById(R.id.message).setOnClickListener(this);
        findViewById(R.id.one).setOnClickListener(this);
        findViewById(R.id.two).setOnClickListener(this);
        findViewById(R.id.three).setOnClickListener(this);
        findViewById(R.id.four).setOnClickListener(this);
        findViewById(R.id.five).setOnClickListener(this);
        findViewById(R.id.six).setOnClickListener(this);
        findViewById(R.id.seven).setOnClickListener(this);
        findViewById(R.id.eight).setOnClickListener(this);
        findViewById(R.id.nine).setOnClickListener(this);
        findViewById(R.id.zero).setOnClickListener(this);
        findViewById(R.id.star).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.one:
           case R.id.two:
           case R.id.three:
           case R.id.four:
           case R.id.five:
           case R.id.six:
           case R.id.seven:
           case R.id.eight:
           case R.id.nine:
           case R.id.zero:
           case R.id.star:
               btn = findViewById(v.getId());
               str.append(btn.getText());
               number.setText(str);
               break;
           case R.id.back:
               int len = str.length();
               if(len > 0){
                   str.deleteCharAt(str.length() - 1);
                   number.setText(str);
               }
               break;

           case R.id.call:
               if (ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    //首先判断否获取了权限
                   if (ActivityCompat.shouldShowRequestPermissionRationale( this,Manifest.permission.CALL_PHONE)) {
                    //让用户手动授权
                       Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                       Uri uri = Uri.fromParts("package", getPackageName(), null);
                       intent.setData(uri);
                       startActivity(intent);
                   }else{
                       ActivityCompat.requestPermissions( this,new String[]{Manifest.permission.CALL_PHONE},1);
                   }
               }else {
                   Intent intent = new Intent();
                   intent.setAction(Intent.ACTION_CALL);
                   intent.setData(Uri.parse("tel:" + str));
                   startActivity(intent);
               }

               break;

           case R.id.message:
               Log.e("eeeee", "onClick: message" );
               break;
       }
    }
}

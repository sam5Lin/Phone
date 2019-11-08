package com.example.phone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PERMISS_CONTACT = 1;
    private static final int PERMISS_SMS = 2;
    private TextView number;
    private StringBuffer str;
    private Button btn;
    private ArrayAdapter<String> adapter;
    private List<String> contactsList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactsInit();
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
        findViewById(R.id.back).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                str.delete(0, str.length());
                number.setText(str);
                return true;
            }
        });



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
               call();
               break;

           case R.id.message:
                   Intent intent = new Intent(this, MessageActivity.class);
                   Bundle bundle = new Bundle();
                   bundle.putCharSequence("number", str);
                   intent.putExtras(bundle);
                   startActivity(intent);
               break;
       }
    }

    private void contactsInit(){
        //获取到listview并且设置适配器
        ListView contactsView= (ListView) findViewById(R.id.contacts_view);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,contactsList);
        contactsView.setAdapter(adapter);


        //判断是否开启读取通讯录的权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager
                .PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
        }else {
            readContacts();
        }

        contactsList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String[] nn1 = o1.split("\n");
                String[] nn2 = o2.split("\n");
                return PinyinUtils.sort(nn1[0], nn2[0]);
            }
        });


        contactsView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String n = contactsList.get(position);
                String[] nn = n.split("\n");
                str.delete(0,str.length());
                str.append(nn[1]);
                number.setText(str);
                call();

            }
        });
    }

    private void call(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            //首先判断否获取了权限
            if (ActivityCompat.shouldShowRequestPermissionRationale( this,Manifest.permission.CALL_PHONE)) {
                //让用户手动授权
                Log.e("e--->", "call: 手动授权" );
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

            }else{
                ActivityCompat.requestPermissions( this,new String[]{Manifest.permission.CALL_PHONE},2);
            }
        }else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + str));
            startActivity(intent);
        }
    }

    private void readContacts() {
        Cursor cursor=null;
        try {
            //查询联系人数据,使用了getContentResolver().query方法来查询系统的联系人的数据
            //CONTENT_URI就是一个封装好的Uri，是已经解析过得常量
            cursor=getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            //对cursor进行遍历，取出姓名和电话号码

            if (cursor!=null){
                while (cursor.moveToNext()){
                    //获取联系人姓名
                    String displayName=cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    ));
                    //获取联系人手机号
                    String number=cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    )).trim();

                    //把取出的两类数据进行拼接，中间加换行符，然后添加到listview中
                    contactsList.add(displayName + '\n' + number );
                }
                //刷新listview
                adapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //记得关掉cursor
            if (cursor!=null){
                cursor.close();
            }
        }
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    readContacts();
                }else {
                    Toast.makeText(this,"读取通讯录没有权限", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

}

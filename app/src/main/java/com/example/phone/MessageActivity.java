package com.example.phone;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_CONTACT = 1;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    private EditText phone;
    private EditText content;
    private ImageButton send;
    private String number;
    private String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_message);

        phone = findViewById(R.id.m_number);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        number = bundle.getString("number");
        phone.setText(number);

        content = findViewById(R.id.m_content);

        findViewById(R.id.back1).setOnClickListener(this);
        content.setOnClickListener(this);

        send = findViewById(R.id.send);
        send.setOnClickListener(this);

        if(phone.getText().toString().equals("")){
            phone.requestFocus();
            phone.setFocusable(true);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        else if(content.getText().toString().equals("")){
            content.requestFocus();
            content.setFocusable(true);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        findViewById(R.id.find).setOnClickListener(this);
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
                if(!"".equals(number) && !"".equals(message)){
                    ArrayList<String > textContent = SmsManager.getDefault().divideMessage(message);
                    for(String text : textContent){
                        SmsManager.getDefault().sendTextMessage(number, null, text, null, null);
                    }
                }
                break;

            case R.id.find:
                Intent intent2 = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent2, PICK_CONTACT);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_CONTACT:
                Intent mIntent;
                mIntent = data;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    //申请授权，第一个参数为要申请用户授权的权限；第二个参数为requestCode 必须大于等于0，主要用于回调的时候检测，匹配特定的onRequestPermissionsResult。
                    //可以从方法名requestPermissions以及第二个参数看出，是支持一次性申请多个权限的，系统会通过对话框逐一询问用户是否授权。
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);

                }else{
                    //如果该版本低于6.0，或者该权限已被授予，它则可以继续读取联系人。
                    getContacts(data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void getContacts(Intent data) {
        if (data == null) {
            return;
        }

        Uri contactData = data.getData();
        if (contactData == null) {
            return;
        }

        String phoneNumber = "";

        Uri contactUri = data.getData();
        Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
        if (cursor.moveToFirst()) {
            String hasPhone = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            String id = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            if (hasPhone.equalsIgnoreCase("1")) {
                hasPhone = "true";
            } else {
                hasPhone = "false";
            }
            if (Boolean.parseBoolean(hasPhone)) {
                Cursor phones = getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = " + id, null, null);
                while (phones.moveToNext()) {
                    phoneNumber = phones
                            .getString(phones
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                phones.close();
            }
            cursor.close();

            phone.setText(phoneNumber);
        }
    }
}

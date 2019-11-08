package com.example.phone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import java.security.acl.Permission;
import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_CONTACT = 1;
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

        if("".equals(phone.getText().toString())){
            phone.requestFocus();
            phone.setFocusable(true);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        else if("".equals(content.getText().toString())){
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
                if (!"".equals(number) && !"".equals(message)) {


                    Uri smsToUri = Uri.parse("smsto:" + number);

                    Intent intent1 = new Intent(Intent.ACTION_SENDTO, smsToUri);

                    intent1.putExtra("sms_body", message);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);

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
                getContacts(data);
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

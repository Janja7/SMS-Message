package com.example.android.smsproject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private ImageView back;
    private ImageView profile, send, imageForProfile;
    private TextView numberPhone, textMessage;
    private EditText enterMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicComponent();
        addListener();

        String number = getIntent().getStringExtra("number");
        String message = getIntent().getStringExtra("message");

        numberPhone.setText(getContactName(getApplicationContext(), number));
        textMessage.setText(message);

        if(getContactPhoto(getApplicationContext(), number) == null){
            imageForProfile.setImageResource(R.drawable.image);
        }
        else {
            imageForProfile.setImageBitmap(getContactPhoto(getApplicationContext(), number));
        }

    }

    private void inicComponent(){
        back = (ImageView) findViewById(R.id.backButton);
        profile = (ImageView) findViewById(R.id.imageButtonProfile);
        send = (ImageView) findViewById(R.id.imageSend);
        numberPhone = (TextView) findViewById(R.id.textViewNumber);
        textMessage = (TextView) findViewById(R.id.textViewMessageReceive);
        imageForProfile = (ImageView) findViewById(R.id.imageViewimage);
        enterMessage = (EditText) findViewById(R.id.editTextMessage);
    }

    private void addListener(){

        enterMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.equals("") || enterMessage.getText().toString().isEmpty()){
                    send.setEnabled(false);
                    send.setColorFilter(Color.parseColor("#acacac"));
                }
                else {
                    send.setColorFilter(Color.parseColor("#000000"));
                    send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String sms = enterMessage.getText().toString();
                            String number = getIntent().getStringExtra("number");

                            sendSMS(number, sms);

                            enterMessage.setText("");
                            send.setColorFilter(Color.parseColor("#acacac"));
                        }
                    });
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public String getContactName(Context context, String phoneNumber) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        Cursor cursor = contentResolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }

        String contactName = phoneNumber;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public Bitmap getContactPhoto(Context context, String phoneNumber) {
        Uri photoUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(photoUri, new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID},
                                                                null, null, null);

        String contactID = "";
        Bitmap my_bitmap = null;

        if(cursor.moveToFirst()){
            contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
            Uri myContantUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactID));
            InputStream photoStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), myContantUri);
            BufferedInputStream buff = new BufferedInputStream(photoStream);

            my_bitmap = BitmapFactory.decodeStream(buff);
        }
        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }

        return my_bitmap;
    }
}

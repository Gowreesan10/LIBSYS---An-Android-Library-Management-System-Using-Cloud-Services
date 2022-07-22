package com.code10.libsys;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class send extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send);

//        Button button = findViewById(R.id.button);
//        EditText tokenet = findViewById(R.id.token);
//        EditText messageet = findViewById(R.id.messageet);
//        EditText tv = findViewById(R.id.textView);
//
//        FCMReceiver.setCurrentToken(tv);
//
//        button.setOnClickListener(v -> {
//            String message = messageet.getText().toString();
//            String token = tokenet.getText().toString();
//
//            if (!message.isEmpty()) {
//                FCMSend.pushNotification(getApplicationContext(), token, "Title", message);
//
//            } else {
//                Toast.makeText(send.this, "enter text", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
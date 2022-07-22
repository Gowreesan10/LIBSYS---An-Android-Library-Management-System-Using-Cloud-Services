package com.code10.libsys;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.code10.libsys.Admin.AdminSharedPreference;
import com.code10.libsys.Admin.DashBoardAdmin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class FCMReceiver extends FirebaseMessagingService {

    int NOTIFICATION_ID = 0;
    String CHANNEL_ID = "FCM";
    String CHANNEL_NAME = "FCM NOTIFICATION";

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fcmToken", "empty");
    }

    public static void shareTokenToServer(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();

        if (currentUser != null) {
            if (currentUser.getDisplayName().endsWith("Librarian")) {
                String LibName = AdminSharedPreference.getLibraryDetails().getLibraryName();
                Map<String, Object> library = new HashMap<>();
                library.put("token", getToken(context));

                db.collection("Libraries").document(LibName).set(library, SetOptions.merge()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.v("New Token updated", "");
                    } else {
                        Log.v("Token update failure", "");
                    }
                });
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("Token", "Refreshed token: " + token);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fcmToken", token).apply();
        shareTokenToServer(getApplicationContext());
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        String Title = message.getNotification().getTitle();
        String Body = message.getNotification().getBody();

        Gson gson = new Gson();
        Message messageFromJS = gson.fromJson(Body, Message.class);

        createNotificationChannel();
        buildNotification(Title, messageFromJS.getRequester() + " Send a Borrow Request");
        AdminSharedPreference.getInstance(getApplicationContext()).saveRequest(messageFromJS);
//        Log.v(Title,"Borrow Request");
//        if (Objects.equals(Title, "Borrow Request")) {
//            Intent intent = new Intent(this, AdminRequestFragment.class);
//            intent.setAction("com.myApp.CUSTOM_EVENT");
//            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//            intent.putExtra("Message",messageFromJS);
//            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//            Log.v("Broadcast ","sent");
//        }
    }

    private void buildNotification(String Title, String Body) {
        Intent intent = new Intent(this, DashBoardAdmin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(Title)
                .setContentText(Body)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }
}
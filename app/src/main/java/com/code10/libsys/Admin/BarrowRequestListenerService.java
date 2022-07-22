//package com.code10.libsys.Admin;
//
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.core.app.NotificationCompat;
//
//import com.google.firebase.firestore.DocumentChange;
//import com.google.firebase.firestore.EventListener;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.firestore.QuerySnapshot;
//
//public class BarrowRequestListenerService extends Service {
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.v("Start", "Service");
//        createNotificationChannel();
//
//        Notification notification = new NotificationCompat.Builder(this, "CHANNEL_ID")
//                .setContentTitle("Foreground Service")
//                .setContentText("input")
//                .setAutoCancel(true)
//                .build();
//        startForeground(1, notification);
//        myJob();
//        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Toast.makeText(this, "Service destroyed by user.", Toast.LENGTH_LONG).show();
//    }
//
//    private void createNotificationChannel() {
//        NotificationChannel serviceChannel = new NotificationChannel(
//                "CHANNEL_ID",
//                "Foreground Service Channel",
//                NotificationManager.IMPORTANCE_NONE
//        );
//        NotificationManager manager = getSystemService(NotificationManager.class);
//        manager.createNotificationChannel(serviceChannel);
//    }
//
//    public void myJob() {
//        db.collection("Requests").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w("FOREGROUND SERVICE", "onEvent:error", e);
//                    onError(e);
//                    return;
//                }
//                String x = "FOREGROUND SERVICE FROM ";
//                if (documentSnapshots.getMetadata().isFromCache()) {
//                    x = x + "Cache";
//                } else {
//                    x = x + "server";
//                }
//
//                // Dispatch the event
//                Log.d("FOREGROUND SERVICE", "onEvent:numChanges:" + documentSnapshots.getDocumentChanges().size());
//                for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
//                    Log.v("FOREGROUND SERVICE", "" + x + "  : " + change.getType());
//                    switch (change.getType()) {
//                        case ADDED:
//                            onDocumentAdded(change);
//                            break;
//                        case MODIFIED:
////                    onDocumentModified(change);
//                            break;
//                        case REMOVED:
////                    onDocumentRemoved(change);
//                            break;
//                    }
//                }
//
//                onDataChanged();
//            }
//
//            private void onError(FirebaseFirestoreException e) {
//                Log.w("FOREGROUND SERVICE TAG", "onError", e);
//            }
//
//            private void onDataChanged() {
//            }
//
//            private void onDocumentAdded(DocumentChange change) {
//                Log.v("Foreground service", change.getDocument().toString());
////                AdminSharedPreference.getInstance(getApplicationContext()).saveRequestData(change.getDocument());
//            }
//        });
//    }
//}
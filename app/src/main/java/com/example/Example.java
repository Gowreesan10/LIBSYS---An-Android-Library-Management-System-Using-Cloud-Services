//package com.example;
//
//import com.google.android.gms.common.api.internal.zacb;
//import com.google.api.core.ApiFuture;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.cloud.Timestamp;
//import com.google.cloud.firestore.Firestore;
//import com.google.cloud.firestore.FirestoreOptions;
//import com.google.cloud.firestore.QueryDocumentSnapshot;
//import com.google.cloud.firestore.QuerySnapshot;
//import com.google.cloud.functions.HttpFunction;
//import com.google.cloud.functions.HttpRequest;
//import com.google.cloud.functions.HttpResponse;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.firestore.Query;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.google.firebase.messaging.Message;
//import com.google.firebase.messaging.Notification;
//
//import java.io.IOException;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.ExecutionException;
//import java.util.logging.Logger;
//
//public class Example implements HttpFunction {
//
//    private static final Logger logger = Logger.getLogger(Example.class.getName());
//    private static final Firestore FIRESTORE = FirestoreOptions.getDefaultInstance().getService();
//    private FirebaseOptions options;
//    private FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
//
//    {
//        try {
//            options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.getApplicationDefault())
//                    .setProjectId("lib-sys-8c1ae")
//                    .build();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void service(HttpRequest request, HttpResponse response) throws Exception {
//        logger.info("Function successfully started");
//        LocalTime time = LocalTime.now(); // Gets the current time
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//
//        try {
//            ArrayList<String> statusArrayList = new ArrayList<>();
//            statusArrayList.add("Pending");
//            statusArrayList.add("Issued");
//            ApiFuture<QuerySnapshot> future = FIRESTORE.collection("Borrow Requests").whereIn("status",statusArrayList).get();
//            List<QueryDocumentSnapshot> qds = future.get().getDocuments();
//
//            for (QueryDocumentSnapshot result : qds) {
//                String Requester = result.getString("requester");
//                String status = result.getString("status");
//                Timestamp requestTime = result.getTimestamp("requestTime");
//                Timestamp returnDate = result.getTimestamp("returnDate");
//                String registrationToken = result.getString("borrowerToken");
//                logger.info(Requester + status + requestTime + returnDate + registrationToken);
//
//                if (status.equals("Pending") && ((requestTime.getSeconds() + 3 * 60 * 60) < Timestamp.now().getSeconds())) {
//                    FIRESTORE.collection("Borrow Requests").document(result.getId()).update("status","Timeout");
//                    sendMessage(registrationToken, "Request Time Out");
//                } else if (status.equals("Issued")) {
//                    if ((returnDate.getSeconds() - 24 * 60 * 60 + 5 * 60 > Timestamp.now().getSeconds()) &&
//                            ((returnDate.getSeconds() - 24 * 60 * 60 - 5 * 60) <= Timestamp.now().getSeconds())) {
//                        sendMessage(registrationToken, "Return tomorrow");
//                    } else if ((returnDate.getSeconds() >= Timestamp.now().getSeconds()) &&
//                            ((returnDate.getSeconds() - 300) <= Timestamp.now().getSeconds())) {
//                        sendMessage(registrationToken, "Return book today");
//                    } else if (Objects.equals(time.format(formatter), "09:00")) {
//                        long Second = Timestamp.now().getSeconds() - returnDate.getSeconds();
//                        long day = Second / (24 * 3600);
//                        Second = Second % (24 * 3600);
//                        long hour = Second / 3600;
//                        sendMessage(registrationToken, day == 0 ? "Over due by" + hour + " Hours" : "Over due by" + day + " Days" + hour + " Hours");
//                    }
//                }
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void sendMessage(String registrationToken, String requestTime_out) throws FirebaseMessagingException {
//
//        Message message = Message.builder()
//                .setNotification(Notification.builder()
//                        .setTitle("LIB SYS")
//                        .setBody(requestTime_out)
//                        .build())
//                .setToken(registrationToken)
//                .build();
//        String MessageResponse = FirebaseMessaging.getInstance(firebaseApp).send(message);
//        logger.info("Successfully sent message: " + MessageResponse);
//        logger.info("Function success: " + "");
//    }
//}
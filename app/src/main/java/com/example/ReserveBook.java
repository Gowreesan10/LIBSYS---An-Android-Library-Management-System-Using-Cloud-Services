//package com.example;
//
//import com.example.ReserveBook.FirestoreEvent;
//import com.google.api.core.ApiFuture;
//import com.google.cloud.Timestamp;
//import com.google.cloud.firestore.DocumentSnapshot;
//import com.google.cloud.firestore.FieldValue;
//import com.google.cloud.firestore.Firestore;
//import com.google.cloud.firestore.FirestoreOptions;
//import com.google.cloud.firestore.WriteBatch;
//import com.google.cloud.functions.BackgroundFunction;
//import com.google.cloud.functions.Context;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ExecutionException;
//import java.util.logging.Logger;
//
//public class ReserveBook implements BackgroundFunction<FirestoreEvent> {
//
//    private static final Logger logger = Logger.getLogger(ReserveBook.class.getName());
//    private static final Firestore FIRESTORE = FirestoreOptions.getDefaultInstance().getService();
//    private final Firestore firestore;
//
//    public ReserveBook() {
//        this(FIRESTORE);
//    }
//
//    ReserveBook(Firestore firestore) {
//        this.firestore = firestore;
//    }
//
//    @Override
//    public void accept(FirestoreEvent event, Context context) {
//        logger.info("Function triggered by change to: " + event.value.name);
//        String requestDoc = event.value.name.split("/documents/")[1].replace("\"", "");
//
//        try {
//            ApiFuture<DocumentSnapshot> dSApiFuture = FIRESTORE.document(requestDoc).get();
//            DocumentSnapshot result = dSApiFuture.get();
//            String LibName = result.getString("libName");
//            String Book = result.getString("bookName");
//            String Requester = result.getString("requester");
//            Timestamp timestamp = result.getTimestamp("requestTime");
//
//            WriteBatch batch = FIRESTORE.batch();
//            batch.update(FIRESTORE.collection("Copies").document(Book),
//                    LibName + ".No of Copies",
//                    FieldValue.increment(-1));
//            batch.update(FIRESTORE.collection("IssueDetails").document(LibName),
//                    Book + ".Available Copies",
//                    FieldValue.increment(-1));
//            Map<String, Object> bookDet = new HashMap<>();
//            bookDet.put("id","#123");
//            bookDet.put("User Name", Requester);
//            bookDet.put("TimeStamp", FieldValue.serverTimestamp());
////            Map<String, Object> id = new HashMap<>();
////            id.put("id", bookDet);
//
//            batch.update(FIRESTORE.collection("IssueDetails").document(LibName),
//                    Book + ".Reserved For",
//                    FieldValue.arrayUnion(bookDet));
//
//
////            Map<String, Object> book = new HashMap<>();
////            book.put(Book, id);
////
////
////            batch.set(FIRESTORE.collection("IssueDetails").document(LibName), book,SetOptions.merge());
//            batch.commit();
//            logger.info("Function success: " + "");
//
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public static class FirestoreEvent {
//        FirestoreValue value;
//        FirestoreValue oldValue;
//    }
//
//    public static class FirestoreValue {
//        String createTime;
//        String name;
//    }
//}
//
////            ApiFuture<WriteResult> updateCopiesResult = FIRESTORE.collection("Copies")
////                    .document(Book)
////                    .update(LibName + ".No of Copies", FieldValue.increment(-1));
////            WriteResult writeCopiesResult = updateCopiesResult.get();
////
////            ApiFuture<WriteResult> updateAvailableCopiesResult = FIRESTORE.collection("IssueDetails")
////                    .document(LibName)
////                    .update(Book + ".Available Copies", FieldValue.increment(-1));
////            WriteResult writeAvailableCopiesResult = updateAvailableCopiesResult.get();
////
////            ApiFuture<WriteResult> updateReservedResult = FIRESTORE.collection("IssueDetails")
////                    .document(LibName + "/Reserved for")
////                    .set(reserver, SetOptions.merge());
////            WriteResult reserved = updateReservedResult.get();
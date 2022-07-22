//package com.code10.libsys.Admin;
//
//import android.os.AsyncTask;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.firebase.jobdispatcher.JobService;
//import com.google.firebase.firestore.DocumentChange;
//import com.google.firebase.firestore.EventListener;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.firestore.QuerySnapshot;
//
//public class MyService extends JobService {
//
//    BackgroundTask backgroundTask;
//
//    public static void myJob() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("Requests").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w("SERVICE", "onEvent:error", e);
//                    onError(e);
//                    return;
//                }
//                String x = "SERVICE FROM ";
//                if (documentSnapshots.getMetadata().isFromCache()) {
//                    x = x + "Cache";
//                } else {
//                    x = x + "server";
//                }
//
//                // Dispatch the event
//                Log.d("SERVICE", "onEvent:numChanges:" + documentSnapshots.getDocumentChanges().size());
//                for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
//                    Log.v("SERVICE", "" + x + "  : " + change.getType());
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
//                Log.w("SERVICE TAG", "onError", e);
//            }
//
//            private void onDataChanged() {
//            }
//
//            private void onDocumentAdded(DocumentChange change) {
//                Log.v("service", change.getDocument().toString());
//                //  AdminSharedPreferenceRequest.getInstance(getApplicationContext()).saveRequestData(change.getDocument());
//            }
//        });
//    }
//
//
//    @Override
//    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters job) {
//        backgroundTask = new BackgroundTask() {
//            @Override
//            protected void onPostExecute(String s) {
//            }
//        };
//        backgroundTask.execute();
//        return true;
//    }
//
//    @Override
//    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
//        Toast.makeText(this, "Service destroyed.", Toast.LENGTH_LONG).show();
//        return true;
//    }
//
//    public static class BackgroundTask extends AsyncTask<Void, Void, String> {
//
//        @Override
//        protected String doInBackground(Void... voids) {
//            myJob();
//            return null;
//        }
//    }
//
//}

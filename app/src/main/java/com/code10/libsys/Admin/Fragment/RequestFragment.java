package com.code10.libsys.Admin.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.Admin.Activity.CodeScanner;
import com.code10.libsys.Admin.AdminSharedPreference;
import com.code10.libsys.Admin.Model.Copy;
import com.code10.libsys.General.Adapter.RequestsListenerAdapter;
import com.code10.libsys.General.Model.Message;
import com.code10.libsys.General.Service.FCMSend;
import com.code10.libsys.General.Utility;
import com.code10.libsys.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestFragment extends Fragment implements Utility.callBack {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    int LAUNCH_SCANNER_ACTIVITY = 101;
    String[] requesterAndTime;
    private RequestsListenerAdapter requestsListenerAdapter;
    private String IssuingRequester = null;

    public RequestFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_request, container, false);
        String LibName = AdminSharedPreference.getInstance(getActivity().getApplicationContext()).getLibraryDetails().getLibraryName();
        ArrayList<String> status = new ArrayList<>();
        status.add("Pending");
        status.add("Issued");
        status.add("Denied");
        status.add("Returned");
        //Query query = firebaseFirestore.collection("Borrow Requests").whereEqualTo("libName", LibName).whereNotEqualTo("status", "Timeout").orderBy("status", Query.Direction.DESCENDING).orderBy("requestTime", Query.Direction.DESCENDING);
//        Query query = firebaseFirestore.collection("Borrow Requests").whereEqualTo("libName", LibName).orderBy("requestTime", Query.Direction.DESCENDING);
        Query query = firebaseFirestore.collection("Borrow Requests").whereEqualTo("libName", LibName).whereIn("status", status).orderBy("requestTime", Query.Direction.DESCENDING);
        RecyclerView requestsRecyclerView = view.findViewById(R.id.recyclerViewRequest);
        requestsListenerAdapter = new RequestsListenerAdapter(getContext(), R.layout.admin_request_card, this);
        requestsRecyclerView.setAdapter(requestsListenerAdapter);
        requestsListenerAdapter.setQuery(query);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

    @Override
    public void call() {

    }

    @Override
    public void callExistBook(String Title) {
        Intent i = new Intent(getActivity(), CodeScanner.class);
        startActivityForResult(i, LAUNCH_SCANNER_ACTIVITY);
        IssuingRequester = Title;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Gson GSON = new Gson();
            Copy copy = GSON.fromJson(data.getStringExtra("Copy"), Copy.class);
            Log.v("", data.getStringExtra("Copy"));
            Log.v("", IssuingRequester);
            requesterAndTime = IssuingRequester.split(",", 4);
            Log.v("array", requesterAndTime.toString());

            for (String x : requesterAndTime) {
                Log.v("arr", x);
            }

            Log.v(requesterAndTime[3], copy.getBookName());
            if (requesterAndTime[3].equals(copy.getBookName())) {
                showIssueConformation(copy);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Scan Borrower Requested Book");
                builder.setNeutralButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    private void showIssueConformation(Copy copy) {
        final Dialog dialog = new Dialog(this.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout_issue_book);

        TextView bookName = dialog.findViewById(R.id.enterBookName);
        TextView borrowerName = dialog.findViewById(R.id.enterBorrowerName);
        TextView copyNo = dialog.findViewById(R.id.enterCopyNo);
        TextView libName = dialog.findViewById(R.id.enterLibName);
        ImageView bookCover = dialog.findViewById(R.id.bookCover);
        Button conformBtn = dialog.findViewById(R.id.btnConformAndIsuueBook);

        Picasso.get().load(R.drawable.canstockphoto8237186).into(bookCover);
        Log.v("", copy.getBookName());

        bookName.setText(copy.getBookName());
        borrowerName.setText(requesterAndTime[0]);
        copyNo.setText(copy.getCopyNo());
        libName.setText(copy.getLibName());

        conformBtn.setOnClickListener(v -> {
            dialog.hide();
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Issuing Book...");
            progressDialog.show();
            Log.v("Book issued to ", IssuingRequester);
            Map<String, Object> issueCopyDetails = new HashMap<>();
            issueCopyDetails.put("User", requesterAndTime[0]);
            issueCopyDetails.put("CopyNo", copy.getCopyNo());
            issueCopyDetails.put("request", requesterAndTime[1] + "," + requesterAndTime[2] + " " + requesterAndTime[0]);
            issueCopyDetails.put("issuedTime", Timestamp.now());

            Log.v("doc path", requesterAndTime[1] + " " + requesterAndTime[0]);
            WriteBatch batch = firebaseFirestore.batch();
            batch.update(firebaseFirestore.collection("Borrow Requests").document(requesterAndTime[1] + "," + requesterAndTime[2] + " " + requesterAndTime[0]),
                    "status",
                    "Issued");
            batch.update(firebaseFirestore.collection("IssueDetails").document(copy.getLibName()),
                    copy.getBookName() + ".Reserved For",
                    FieldValue.arrayRemove(requesterAndTime[0]));
            batch.update(firebaseFirestore.collection("IssueDetails").document(copy.getLibName()),
                    copy.getBookName() + ".Issued For",
                    FieldValue.arrayUnion(issueCopyDetails));
            batch.commit().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.v("Successfully removed reservation", "");
                    progressDialog.dismiss();
//                    firebaseFirestore.collection("Users").document(requesterAndTime[0] + ".User").get().addOnCompleteListener(task1 -> {
//                        if (task1.isSuccessful()) {
//                            String TOKEN1 = String.valueOf(task1.getResult().get("token"));
//                            if(!TOKEN1.equals("null")) {
                    firebaseFirestore.collection("Borrow Requests").document(requesterAndTime[1] + "," + requesterAndTime[2] + " " + requesterAndTime[0]).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String TOKEN = task.getResult().toObject(Message.class).getBorrowerToken();
                            FCMSend.pushNotification(getActivity().getApplicationContext(), TOKEN, "Book Issued ", copy.getBookName() + " is Issued");
                        }
                    });

//                            }
//                        }
//                    });
                }
            }).addOnFailureListener(e -> {
                Log.v("error", e.getMessage());
                Toast.makeText(getContext(), "Issuing Book Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
    //        AdminSharedPreference.getInstance(getActivity().getApplicationContext()).registerSharedPrefListener(this);

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        Log.v("ACTIVE LISTENER", "");
//        if (key.equals("Request ArrayList")) {
//            requestsListenerAdapter.setQuery();
//            Log.v("SET QUERY", "");
//        }
//    }


//        AdminSharedPreference.getInstance(getActivity().getApplicationContext()).unregisterSharedPrefListener(this);

}

//    private MyReceiver broadcastReceiver;

//        IntentFilter intentFilter = new IntentFilter("com.myApp.CUSTOM_EVENT");
//        broadcastReceiver = new MyReceiver(requestsListenerAdapter);
//        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(broadcastReceiver, intentFilter);


//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        IntentFilter intentFilter = new IntentFilter("com.myApp.CUSTOM_EVENT");
////        broadcastReceiver = new BroadcastReceiver() {
////            @Override
////            public void onReceive(Context context, Intent intent) {
////                set();
////            }
////        };
//        broadcastReceiver = new MyReceiver(requestsListenerAdapter);
////        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(broadcastReceiver, intentFilter);
//        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(broadcastReceiver,intentFilter);
//    }

//    private void set() {
//        Log.v("MESSAGE", "RECEIVED");
//    }

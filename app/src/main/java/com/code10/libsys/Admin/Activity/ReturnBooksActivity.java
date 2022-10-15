package com.code10.libsys.Admin.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.code10.libsys.Admin.Model.Copy;
import com.code10.libsys.General.Model.Message;
import com.code10.libsys.General.Service.FCMSend;
import com.code10.libsys.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ReturnBooksActivity extends AppCompatActivity {
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    int overDueDays;
    int LAUNCH_SCANNER_ACTIVITY = 2002;
    String LIBNAME, COPYNO, BOOKNAME, REQUEST;
    Message message;
    Map<String, Object> issuedCopy = null;
    ProgressBar progressBar;
    AutoCompleteTextView penaltyStatus;
    String p;
    TextView tdetailspenaltycost;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Returning Book....");
        Intent i = new Intent(this, CodeScanner.class);
        startActivityForResult(i, LAUNCH_SCANNER_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.VISIBLE);
        if (resultCode == Activity.RESULT_OK) {
            Gson GSON = new Gson();
            Copy copy = GSON.fromJson(data.getStringExtra("Copy"), Copy.class);

            LIBNAME = copy.getLibName();
            COPYNO = copy.getCopyNo();
            BOOKNAME = copy.getBookName();

            firebaseFirestore.collection("IssueDetails").document(LIBNAME).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Map<String, Object> BOOKDET = (Map<String, Object>) documentSnapshot.get(BOOKNAME);
                    ArrayList<Map<String, Object>> arrayList = (ArrayList<Map<String, Object>>) BOOKDET.get("Issued For");

                    if (arrayList != null) for (Map<String, Object> copyDet : arrayList) {
                        if (copyDet.get("CopyNo").toString().equals(COPYNO)) {
                            issuedCopy = copyDet;
                            REQUEST = (String) copyDet.get("request");
                            firebaseFirestore.collection("Borrow Requests").document(REQUEST).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    message = task1.getResult().toObject(Message.class);
                                    setViews(message);
                                }
                            });
                            break;
                        }
                    }

                    if (issuedCopy == null) {
                        Toast.makeText(this, "Book is not issued", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
    }

    private void setViews(Message message) {

        setContentView(R.layout.activity_return_books);
        ImageView imageView = findViewById(R.id.imageGroup2);
        imageView.setOnClickListener(v -> finish());

        TextView txtBorrowername = findViewById(R.id.txtBorrowername);
        txtBorrowername.setText(message.getRequester());
        TextView txtBorrowerBook = findViewById(R.id.txtBorrowerBook);
        txtBorrowerBook.setText(message.getBookName());
        TextView txtRequestedTime = findViewById(R.id.txtRequestedTime);
        txtRequestedTime.setText(message.getRequestTime().toDate().toString());
        TextView txtlibName = findViewById(R.id.txtlibName);
        txtlibName.setText(message.getLibName());
        TextView txtBorrowMethod = findViewById(R.id.txtBorrowMethod);
        txtBorrowMethod.setText(message.getBorrowMethod());

        long diff = message.getReturnDate().getTime() - message.getRequestTime().toDate().getTime();
        TimeUnit timeUnit = TimeUnit.DAYS;
        String borrowDays = MessageFormat.format("{0}", timeUnit.convert(diff, TimeUnit.MILLISECONDS));

        TextView txtBorrowDays = findViewById(R.id.txtBorrowDays);
        txtBorrowDays.setText(borrowDays);
        TextView tdetailtxtreturndate = findViewById(R.id.tdetailtxtreturndate);
        tdetailtxtreturndate.setText(Timestamp.now().toDate().toString());

//        long overdue = Timestamp.now().getSeconds() * 1000 - message.getReturnDate().getTime();
        long overdue = Timestamp.now().toDate().getTime() - message.getReturnDate().getTime();
        overDueDays = overdue < 0 ? 0 : Integer.parseInt(MessageFormat.format("{0}", timeUnit.convert(overdue, TimeUnit.MILLISECONDS)));
        TextView tdetailsoverdue = findViewById(R.id.tdetailsoverdue);
        tdetailsoverdue.setText(overDueDays + "");
        tdetailspenaltycost = findViewById(R.id.tdetailspenaltycost);
        tdetailspenaltycost.setText(overDueDays * 10 + "");


        penaltyStatus = findViewById(R.id.PenaltyPayment);
        ArrayList<String> payment = new ArrayList<>();
        payment.add("Paid");
        payment.add("Not Paid");
        ArrayAdapter<String> status = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, payment);
        penaltyStatus.setAdapter(status);

        penaltyStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                p = adapterView.getItemAtPosition(position).toString();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if (overDueDays == 0) {
            findViewById(R.id.PaymentStatus).setClickable(false);
            penaltyStatus.setClickable(false);
            penaltyStatus.setEnabled(false);
            p = "Paid";
        }
        Button btnAddReturnedBookOne = findViewById(R.id.btnAddReturnedBookOne);
        btnAddReturnedBookOne.setOnClickListener(v -> {
            returnBook(message.getRequester());
        });
        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> finish());
        progressBar.setVisibility(View.GONE);
    }

    private void returnBook(String requester) {

        progressDialog.show();
        if (!p.isEmpty()) {
            if (!p.equals("Paid")) {
                Log.v("", requester + LIBNAME);
                int x = overDueDays * 10;
                DocumentReference washingtonRef = firebaseFirestore.collection("Penalty").document(requester);
                washingtonRef.update(LIBNAME, FieldValue.increment(x))
                        .addOnSuccessListener(task -> {
                            writefirestore();
                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(ReturnBooksActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                writefirestore();
            }
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Select Payment Status", Toast.LENGTH_SHORT).show();
        }
    }

    public void writefirestore() {
        WriteBatch batch = firebaseFirestore.batch();
        batch.update(firebaseFirestore.collection("Borrow Requests").document(REQUEST),
                "status",
                "Returned");
        batch.update(firebaseFirestore.collection("Borrow Requests").document(REQUEST),
                "notificationViewed",
                false);
        batch.update(firebaseFirestore.collection("IssueDetails").document(LIBNAME),
                BOOKNAME + ".Issued For",
                FieldValue.arrayRemove(issuedCopy));
        batch.update(firebaseFirestore.collection("Copies").document(BOOKNAME),
                LIBNAME + ".No of Copies",
                FieldValue.increment(1));
        batch.update(firebaseFirestore.collection("IssueDetails").document(LIBNAME),
                BOOKNAME + ".Available Copies",
                FieldValue.increment(1));
        batch.commit().addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Log.v("Successfully returned book", "");
                Toast.makeText(this, "Successfully returned book", Toast.LENGTH_SHORT).show();
                FCMSend.pushNotification(getApplicationContext(), message.getBorrowerToken(), "Book Returned ", message.getBookName() + " is returned");
            }
        });
    }


}

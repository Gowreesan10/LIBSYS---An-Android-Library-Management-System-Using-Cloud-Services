package com.code10.libsys.Admin.Activity;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.code10.libsys.Admin.AdminSharedPreference;
import com.code10.libsys.Admin.Model.Copy;
import com.code10.libsys.General.Model.BookDetails;
import com.code10.libsys.General.Model.LibraryDetails;
import com.code10.libsys.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class AddCopies extends AppCompatActivity {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    ImageView bookcover, back;
    TextView bookname, libname, initialcopies, totalcopies, viewAuthor, viewPublisher;
    AutoCompleteTextView addCopies;
    int STARTCODESCANNERFROMADDCOPY = 201;
    String noOfCopies = "", totalCopies, LIBNAME, BOOKNAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, CodeScanner.class);
        intent.putExtra("From", "Add Copies");
        startActivityForResult(intent, STARTCODESCANNERFROMADDCOPY);
        setContentView(R.layout.activity_add_copies);
        bookcover = findViewById(R.id.bookcover);
        bookname = findViewById(R.id.bookname);
        libname = findViewById(R.id.txtlibname);
        initialcopies = findViewById(R.id.txtlibinitcopies);
        totalcopies = findViewById(R.id.txtfinalcopies);
        viewAuthor = findViewById(R.id.viewAuthor);
        viewPublisher = findViewById(R.id.viewPublisher);

        String[] numArray = new String[20];
        for (int i = 1; i <= 20; i++) {
            numArray[i - 1] = i + "";
        }
        addCopies = findViewById(R.id.EnterNoOfCopies);
        ArrayAdapter<String> categoty = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                numArray);
        addCopies.setAdapter(categoty);

        back = findViewById(R.id.imageGroup2);
        back.setOnClickListener(v -> finish());

        addCopies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                noOfCopies = adapterView.getItemAtPosition(position).toString();
                totalcopies.setText(String.format("%d", Integer.parseInt(noOfCopies) + Integer.parseInt(totalCopies)));
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button cancel = findViewById(R.id.btnCancel);
        cancel.setOnClickListener(v -> finish());
        Button btnAddCopiesOne = findViewById(R.id.btnAddCopiesOne);
        btnAddCopiesOne.setOnClickListener(v -> {
            if (noOfCopies.equals("")) {
                Toast.makeText(AddCopies.this, "Select the number of copies to add", Toast.LENGTH_SHORT).show();
            } else {
                writetofirebase();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode == RESULT_OK) {
            if (requestCode == STARTCODESCANNERFROMADDCOPY) {
                Gson GSON = new Gson();
                Copy copy = GSON.fromJson(data.getStringExtra("Copy"), Copy.class);

                LIBNAME = copy.getLibName();
                BOOKNAME = copy.getBookName();

                bookname.setText(BOOKNAME);
                libname.setText(LIBNAME);
                firebaseFirestore.collection("BookDetails").document(BOOKNAME).get().addOnSuccessListener(documentSnapshot -> {
                    BookDetails bookView = documentSnapshot.toObject(BookDetails.class);
                    Picasso.get().load(bookView.getThumbnailLink()).into(bookcover);
                    viewAuthor.setText(bookView.getAuthors().toString().replace("[", "").replace("]", ""));
                    viewPublisher.setText(bookView.getPublisher());
                    Log.v("BOOK", BOOKNAME + " " + LIBNAME);
                    firebaseFirestore.collection("IssueDetails").document(LIBNAME).addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.v("error", error.getLocalizedMessage());
                        }
                        totalCopies = "";
                        Map<String, Object> x0 = value.getData();
                        Log.v("BOOK", x0.toString());
                        Map<String, Object> x = (Map<String, Object>) x0.get(BOOKNAME);
                        for (Map.Entry<String, Object> entry : x.entrySet()) {
                            if (entry.getKey().equals("Total Copies")) {
                                totalCopies = String.valueOf(entry.getValue());
                                initialcopies.setText(totalCopies);
                            }
                        }
                    });
                });

            }
        }
    }

    private void writetofirebase() {
        ProgressDialog progressDialog = new ProgressDialog(AddCopies.this);
        progressDialog.setMessage("Adding Copies");
        progressDialog.show();
        WriteBatch writeBatch = firebaseFirestore.batch();
        DocumentReference showcopies = firebaseFirestore.collection("Copies").document(BOOKNAME);
        writeBatch.update(showcopies, LIBNAME + ".No of Copies", FieldValue.increment(Integer.parseInt(noOfCopies)));
        DocumentReference issuecopies = firebaseFirestore.collection("IssueDetails").document(LIBNAME);
        writeBatch.update(issuecopies, BOOKNAME + ".Available Copies", FieldValue.increment(Integer.parseInt(noOfCopies)));
        writeBatch.update(issuecopies, BOOKNAME + ".Total Copies", FieldValue.increment(Integer.parseInt(noOfCopies)));
        writeBatch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                LibraryDetails libraryDetails = AdminSharedPreference.getInstance(getApplicationContext()).getLibraryDetails();
                Intent intent = new Intent(this, QRCodeGenerator.class);
                intent.putExtra("LIB NAME", libraryDetails.getLibraryName());
                intent.putExtra("BOOK NAME", BOOKNAME);
                intent.putExtra("COPIES", Integer.parseInt(totalCopies) + "");
                progressDialog.dismiss();
                startActivity(intent);
                finish();
            }
        });
    }
}

package com.code10.libsys.User.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import androidx.appcompat.app.AppCompatActivity;

import com.code10.libsys.General.Model.BookDetails;
import com.code10.libsys.General.Model.Message;
import com.code10.libsys.General.Service.FCMReceiver;
import com.code10.libsys.General.Service.FCMSend;
import com.code10.libsys.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class BorrowBook extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
    String userName = currentUser.getDisplayName().replace(".User", "");
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String receiptUri;
    int LAUNCH_GALLERY_ACTIVITY = 1000;
    Uri imageUri;
    String borrowMed = null;
    String selectedBoDays;
    TextView click;
    String LibName;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_book);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Sending Request....");

        BookDetails Book = (BookDetails) getIntent().getSerializableExtra("BOOK");
        LibName = getIntent().getStringExtra("LIBRARY");

        Button sendRequest = findViewById(R.id.sendBarrowRequest);
        ImageView image = findViewById(R.id.imageRectangle27);
        Picasso.get().load(Book.getThumbnailLink()).into(image);

        TextView title = findViewById(R.id.txtBOOKTITLE1234);
        title.setText(Book.getTitle());
        TextView subTitle = findViewById(R.id.txtSubTitle);
        subTitle.setText(Book.getSubTitle());
        TextView author = findViewById(R.id.txtAuthors);
        author.setText(Book.getAuthors().toString().replace("[", "").replace("]", ""));
        TextView pages = findViewById(R.id.txt125Pages);
        pages.setText(Book.getPageCount());

        TextView isbn13 = findViewById(R.id.txt_ISBN_13);
        isbn13.setText(Book.getISBN_13());
        TextView isbn10 = findViewById(R.id.txtISBN_10);
        isbn10.setText(Book.getISBN_10());
        TextView category = findViewById(R.id.txtPubName);
        category.setText(Book.getCategory());
        TextView publishedDate = findViewById(R.id.txtPDate);
        publishedDate.setText(Book.getPublishedDate());

        Button linearDownload = findViewById(R.id.linearDownload);
        linearDownload.setOnClickListener(v -> finish());

        int bPeriod;
        float Rating = Float.parseFloat(Book.getUserRating());
        if (Rating > 4.8) {
            bPeriod = 2;
        } else if (Rating > 4.5) {
            bPeriod = 5;
        } else if (Rating > 4) {
            bPeriod = 7;
        } else if (Rating > 3) {
            bPeriod = 14;
        } else if (Rating > 2) {
            bPeriod = 14;
        } else if (Rating > 1) {
            bPeriod = 21;
        } else {
            bPeriod = 28;
        }
        TextView borrowableTime = findViewById(R.id.txt20221011);
        borrowableTime.setText(bPeriod + "");


        List<String> anarray1 = new ArrayList<>();
        for (int i = 1; i <= bPeriod; i++) {
            anarray1.add(i + "");
        }
        ArrayAdapter<String> hand = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, anarray1);

        List<String> anarray2 = new ArrayList<>();
        for (int i = 3; i <= bPeriod; i++) {
            anarray2.add(i + "");
        }
        ArrayAdapter<String> post = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, anarray2);
        AutoCompleteTextView borrowDays = findViewById(R.id.borrowSelection);

        AutoCompleteTextView selectBMethod = findViewById(R.id.borrowSelect);
        String[] arr = getResources().getStringArray(R.array.borrow_Method);
        List<String> list = new ArrayList<String>(Arrays.asList(arr));

        if (bPeriod < 3) {
            list.remove("By Post");
        }
        ArrayAdapter<String> borrowMethod = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        selectBMethod.setAdapter(borrowMethod);

        selectBMethod.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                borrowMed = adapterView.getItemAtPosition(position).toString();
                if (borrowMed.equals("By hand")) {
                    borrowDays.setAdapter(hand);
                } else {
                    borrowDays.setAdapter(post);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(BorrowBook.this, "User Role not selected", Toast.LENGTH_SHORT).show();
            }
        });

        TextView address = findViewById(R.id.txtAddress2);
        TextView date = findViewById(R.id.txtTodayDate);
        TextView after = findViewById(R.id.txt5DayAfter);
        date.setText(LocalDate.now().toString());

        borrowDays.setOnClickListener(v -> {
            if (borrowMed == null) {
                Toast.makeText(getBaseContext(), "Select Borrow Method", Toast.LENGTH_SHORT).show();
            }
        });
        borrowDays.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedBoDays = adapterView.getItemAtPosition(position).toString();
                after.setText(LocalDate.now().plusDays(Long.parseLong(selectedBoDays)).toString());
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(BorrowBook.this, "User Role not selected", Toast.LENGTH_SHORT).show();
            }
        });


        sendRequest.setOnClickListener(v -> {
            progressDialog.show();
            if (receiptUri != null) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, (int) Long.parseLong(selectedBoDays));
                Message message = new Message(userName,
                        Book.getTitle(),
                        Book.getCategory(),
                        "Pending",
                        LibName,
                        FCMReceiver.getToken(getApplicationContext()),
                        currentUser.getPhotoUrl().toString(),
                        Book.getThumbnailLink(),
                        borrowMed,
                        cal.getTime(),
                        receiptUri,
                        false);
                createRequest(message);
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "Upload Receipt", Toast.LENGTH_SHORT).show();
            }
        });


        click = findViewById(R.id.txtNoFilesSelect);
        click.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, LAUNCH_GALLERY_ACTIVITY);
        });

        Button upload = findViewById(R.id.txtUploadFile);
        upload.setOnClickListener(v -> uploadImage());

        ImageView back = findViewById(R.id.imageGroup2);
        back.setOnClickListener(v -> finish());
    }

    private void createRequest(Message message) {
        db.collection("Libraries").document(LibName).get(Source.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Log.d("TAG", "document data: " + document.getData());
                String TOKEN = document.get("token").toString();
                saveRequestInFirestore(message, TOKEN);
            } else {
                progressDialog.dismiss();
                Log.d("TAG", "Cached get failed: ", task.getException());
            }
        });
    }

    private void saveRequestInFirestore(Message message, String TOKEN) {
        message.setRequestTime(Timestamp.now());
        db.collection("Borrow Requests").document(message.getRequestTime() + " " + userName).set(message).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss();
                FCMSend.pushNotification(getApplicationContext(), TOKEN, "Borrow Request", message.getRequester() + " Sent a Borrow Request");
            } else {
                Log.d("TAG", "Sending Message Failed", task.getException());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_GALLERY_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data.getData();
                Log.v("", imageUri.toString());
                click.setText("Image Selected");
            }
        }
    }

    private void uploadImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        if (imageUri != null) {

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("Receipt/" + UUID.randomUUID().toString());

            ref.putFile(imageUri)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                                getImageUrl(ref);
                            })

                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })

                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            });
        } else {
            Toast.makeText(this, "Select Receipt", Toast.LENGTH_SHORT).show();
        }
    }

    public void getImageUrl(StorageReference ref) {
        ref.getDownloadUrl().addOnSuccessListener(uri -> {
            receiptUri = uri.toString();
            Log.v("", receiptUri);
        });
    }

}
package com.code10.libsys.Admin;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.code10.libsys.BookDetails;
import com.code10.libsys.BookView;
import com.code10.libsys.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//TODO: BACK BUTTONS
public class AddBookDetails extends AppCompatActivity {

    int LAUNCH_GALLERY_ACTIVITY = 1000;
    Uri imageUri;
    int finished_Uploads;
    ImageView imageIV;
    ProgressDialog progressDialog ;
    private EditText titleET, subtitleET, publisherET, publishDateET, descriptionET, authorET, ratingET, pagecountET, keywordsET, ISBN_10ET, ISBN_13ET, ebookLinkET, noOfCopiesET;
    private String title, subtitle, publisher, publishDate, description, category = "", pagecount, ISBN_10, ISBN_13, thumbnailLink, ebookLink, noOfCopies, rating;
    private List<String> authors = new ArrayList<>(), keywords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_details);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        AutoCompleteTextView selectCategory = findViewById(R.id.selectCatagory);
        ArrayAdapter<String> categoty = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.Categories));
        selectCategory.setAdapter(categoty);

        imageIV = findViewById(R.id.IVbookCover);
        titleET = findViewById(R.id.ETtitle);
        subtitleET = findViewById(R.id.ETsubtitle);
        authorET = findViewById(R.id.ETauthor);
        publisherET = findViewById(R.id.ETpublisher);
        publishDateET = findViewById(R.id.ETpublishDate);
        keywordsET = findViewById(R.id.ETkeywords);
        ISBN_10ET = findViewById(R.id.ETISBN_10);
        ISBN_13ET = findViewById(R.id.ETISBN_13);
        pagecountET = findViewById(R.id.ETpageCount);
        ratingET = findViewById(R.id.ETrating);
        descriptionET = findViewById(R.id.ETdescription);
        noOfCopiesET = findViewById(R.id.ETcopies);
        ebookLinkET = findViewById(R.id.ETdownloadLink);
        Button addBookBtn = findViewById(R.id.btnAddBook);

        BookDetails book = (BookDetails) getIntent().getSerializableExtra("Book");

        if (book != null) {
            title = book.getTitle();
            subtitle = book.getSubTitle();
            authors = book.getAuthors();
            ISBN_10 = book.getISBN_10();
            ISBN_13 = book.getISBN_13();
//            category = book.getCategory();
            thumbnailLink = book.getThumbnailLink();
            ebookLink = book.getEbookLink();

            titleET.setText(title);
            subtitleET.setText(subtitle);
            publisherET.setText(book.getPublisher());
            publishDateET.setText(book.getPublishedDate());
            descriptionET.setText(book.getDescription());
            pagecountET.setText(book.getPageCount());
            authorET.setText(String.join(",", authors));
//            categoryET.setText(category);
            ratingET.setText(book.getUserRating());
            keywordsET.setText(String.format("%s,%s,%s,%s,%s",
                    title,
                    subtitle,
                    authors.toString().replace("[", "").replace("]", ""),
                    ISBN_10,
                    ISBN_13).replace("Not Found,", ""));
            ISBN_13ET.setText(ISBN_13);
            ISBN_10ET.setText(ISBN_10);
            ebookLinkET.setText(ebookLink);
            Picasso.get().load(thumbnailLink).into(imageIV);
        } else {
            imageIV.setOnClickListener(v -> {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, LAUNCH_GALLERY_ACTIVITY);
            });
        }

        selectCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                category = adapterView.getItemAtPosition(position).toString();
                if (book != null) {
                    keywordsET.setText(String.format("%s,%s,%s,%s,%s,%s",
                            title,
                            subtitle,
                            authors.toString().replace("[", "").replace("]", ""),
                            ISBN_10,
                            ISBN_13,
                            category).replace("Not Found,", ""));
                }
                Toast.makeText(AddBookDetails.this, "Selected " + category, Toast.LENGTH_SHORT).show();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(AddBookDetails.this, "category not selected", Toast.LENGTH_SHORT).show();
            }
        });

        addBookBtn.setOnClickListener(view -> {

            title = titleET.getText().toString().replace("/", " ");
            subtitle = subtitleET.getText().toString();
            publisher = publisherET.getText().toString();
            publishDate = publishDateET.getText().toString();
            description = descriptionET.getText().toString();

            if (authors.isEmpty()) {
                String[] elements = authorET.getText().toString().split(",");
                Collections.addAll(authors, elements);
            }

            rating = ratingET.getText().toString();
            pagecount = pagecountET.getText().toString();

            ISBN_10 = ISBN_10ET.getText().toString();
            ISBN_13 = ISBN_13ET.getText().toString();
            ebookLink = ebookLinkET.getText().toString();
            noOfCopies = noOfCopiesET.getText().toString();

            if (book == null) {
                keywordsET.setText(String.format("%s,%s,%s,%s,%s,%s",
                        title,
                        subtitle,
                        authors.toString().replace("[", "").replace("]", ""),
                        ISBN_10,
                        ISBN_13,
                        category).replace("Not Found,", ""));
                Toast.makeText(AddBookDetails.this, "Selected " + category, Toast.LENGTH_SHORT).show();
            }

            String[] keywordsArray = keywordsET.getText().toString().split(",");
            Collections.addAll(keywords, keywordsArray);

            if (title.isEmpty() || subtitle.isEmpty() || publisher.isEmpty() || publishDate.isEmpty() ||
                    description.isEmpty() || authors.isEmpty() || rating.isEmpty() || pagecount.isEmpty()
                    || keywords.isEmpty() || ISBN_10.isEmpty() || ISBN_13.isEmpty() || noOfCopies.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Enter All Details", Toast.LENGTH_SHORT).show();
            } else {
                uploadBookToFirestore();
            }

        });

        ImageView imageView = findViewById(R.id.imageGroup2);
        imageView.setOnClickListener(v -> finish());
    }

    public void uploadBookToFirestore() {
        progressDialog.show();

        LibraryDetails libraryDetails = AdminSharedPreference.getLibraryDetails();

        BookView bookView = new BookView(title.trim(),
                thumbnailLink.trim(),
                rating.trim(),
                category,
                keywords,
                Timestamp.now());

        BookDetails bookDetails = new BookDetails(title.trim(),
                subtitle.trim(),
                category.trim(),
                authors,
                publisher.trim(),
                publishDate.trim(),
                description.trim(),
                pagecount.trim(),
                rating,
                thumbnailLink.trim(),
                ISBN_10.trim(),
                ISBN_13.trim(),
                ebookLink.trim(),
                new Date().toString());

        Map<String, Object> Copies = new HashMap<>();
        Copies.put("Library", libraryDetails.getLibraryName());
        Copies.put("No of Copies", noOfCopies);
        Copies.put("Address", libraryDetails.getAddress());

        Map<String, Object> LibraryCopies = new HashMap<>();
        LibraryCopies.put(libraryDetails.getLibraryName(), Copies);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("BookView")
                .document(title)
                .set(bookView)
                .addOnSuccessListener(aVoid -> {
                    Log.d("BookView", "DocumentSnapshot successfully written!");
                    finishAddDetailsActivity();
                })
                .addOnFailureListener(e -> Log.w("BookView", "Error writing document", e));

        db.collection("BookDetails")
                .document(title)
                .set(bookDetails)
                .addOnSuccessListener(aVoid -> {
                    Log.d("BookDetails", "DocumentSnapshot successfully written!");
                    finishAddDetailsActivity();
                })
                .addOnFailureListener(e -> Log.w("BookDetails", "Error writing document", e));

        db.collection("Copies")
                .document(title)
                .set(LibraryCopies)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Copies", "DocumentSnapshot successfully written!");
                    finishAddDetailsActivity();
                })
                .addOnFailureListener(e -> Log.w("Copies", "Error writing document", e));

        db.collection("Keywords")
                .document("Keywords")
                .update("Keywords", FieldValue.arrayUnion(keywords.toArray()))
                .addOnSuccessListener(aVoid -> {
                    Log.d("Keywords", "DocumentSnapshot successfully written!");
                    finishAddDetailsActivity();
                })
                .addOnFailureListener(e -> Log.w("Keywords", "Error writing document", e));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_GALLERY_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data.getData();
                imageIV.setImageURI(imageUri);
                Log.v("", imageUri.toString());
                uploadImage();
            }
        }
    }

    private void uploadImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        if (imageUri != null) {
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            ref.putFile(imageUri)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                progressDialog.dismiss();
                                Toast.makeText(AddBookDetails.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                getImageUrl(ref);
                            })

                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddBookDetails.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })

                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            });
        }
    }

    public void getImageUrl(StorageReference ref) {
        ref.getDownloadUrl().addOnSuccessListener(uri -> {
            thumbnailLink = uri.toString();
            Log.v("", thumbnailLink);
        });
    }

    public void finishAddDetailsActivity() {
        finished_Uploads++;
        if (finished_Uploads == 4) {
            progressDialog.dismiss();
            finish();
        }
    }
}

package com.code10.libsys.User;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.BookDetails;
import com.code10.libsys.BookView;
import com.code10.libsys.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

public class UserViewDetails extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
    private URL url;
    private String fileName;
    private BookView bookView;
    private BookDetails bookDetails;
    private int bPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_bar);
        Gson gson = new Gson();
        bookView= gson.fromJson(getIntent().getStringExtra("BookView"), BookView.class);
      //  bookView = getIntent().getStringExtra("BookView");
        getBookDetails(bookView.getTitle());
    }

    public void loadDetails(DocumentSnapshot document) {
        setContentView(R.layout.activity_user_view_details);
        System.out.println(document);

        bookDetails = document.toObject(BookDetails.class);
        String link = bookDetails.getEbookLink();

        ImageView backgroundImage = findViewById(R.id.imageRectangle27);
        ImageView bookCover = findViewById(R.id.imageRectangle23);
        Button btnBorrowBook = findViewById(R.id.btnBorrowBook);
        Button btnDownloadEBook = findViewById(R.id.btnDownloadEBook);
        TextView title = findViewById(R.id.txtTitle);
        TextView subTitle = findViewById(R.id.txtSubTitle);
        TextView authors = findViewById(R.id.txtAuthors);
        TextView numRating = findViewById(R.id.txtRatingNum);
        TextView borrowPeriod = findViewById(R.id.txtBPeriod);
        TextView pageNum = findViewById(R.id.txtPagesNum);
        TextView ISBN13 = findViewById(R.id.txt_ISBN_13);
        TextView ISBN10 = findViewById(R.id.txtISBN_10);
        TextView Category = findViewById(R.id.txtCatSting);
        TextView Publisher = findViewById(R.id.txtPubName);
        TextView publishDate = findViewById(R.id.txtPDate);
        TextView description = findViewById(R.id.txtDes);

        ImageView back = (ImageView) findViewById(R.id.imageArrowright);
        title.setText(bookView.getTitle());
        subTitle.setText(bookDetails.getSubTitle());
        authors.setText(bookDetails.getAuthors().toString().replace("[","").replace("]",""));
        numRating.setText(bookView.getRating());

        float Rating = Float.parseFloat(bookView.getRating());
        if (Rating > 4.8) {
            bPeriod = 2;
        }else if (Rating > 4.5) {
            bPeriod = 5;
        }else if (Rating > 4) {
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

        borrowPeriod.setText(bPeriod + " Days");
        pageNum.setText(bookDetails.getPageCount());
        ISBN13.setText(bookDetails.getISBN_13());
        ISBN10.setText(bookDetails.getISBN_10());
        Category.setText(bookDetails.getCategory());
        Publisher.setText(bookDetails.getPublisher());
        publishDate.setText(bookDetails.getPublishedDate());
        description.setText(bookDetails.getDescription());

        RecyclerView availableLibraries = findViewById(R.id.availableLibraries);

        Picasso.get().load(bookView.getThumbnailLink()).fit().centerCrop(Gravity.TOP).into(backgroundImage);
        Picasso.get().load(bookView.getThumbnailLink()).into(bookCover);

        AvailableLibraryAdapter availableLibraryAdapter = new AvailableLibraryAdapter(getBaseContext());
        availableLibraryAdapter.setQuery(db.collection("Copies").document(bookDetails.getTitle()));
        availableLibraries.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        availableLibraries.setAdapter(availableLibraryAdapter);

        back.setOnClickListener(v -> description.setMaxLines(100));

        btnBorrowBook.setOnClickListener(view -> {
            if (availableLibraryAdapter.getSelectedItem() != 100) {
                Intent intent = new Intent(this, BorrowBook.class);
                intent.putExtra("BOOK", bookDetails);
                intent.putExtra("LIBRARY", availableLibraryAdapter.getSelectedLibrary());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Select Library", Toast.LENGTH_SHORT).show();
            }
        });

        btnDownloadEBook.setOnClickListener(v -> {
            if (!link.isEmpty()) {
                downloadBook(link);
            } else {
                Toast.makeText(this, "E-BOOK not Available", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView imageView = findViewById(R.id.imageFeatherarrowl);
        imageView.setOnClickListener(v -> finish());

    }

    //TODO : SET DOWNLOADABLE BOOKS
    //TODO :SET LIBRARIES
    private void downloadBook(String link) {
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        fileName = url.getPath();
        fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
        Log.v("FILE", fileName);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url + ""));
        request.setTitle(fileName);
        request.setMimeType("application/Pdf");
        request.allowScanningByMediaScanner();
        request.setAllowedOverMetered(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }

    public void getBookDetails(String title) {
        DocumentReference docRef = db.collection("BookDetails").document(title);

        docRef.get().addOnCompleteListener(taskCache -> {
            if (taskCache.isSuccessful() && (taskCache.getResult().exists())) {
                Log.d("TAG", "DocumentSnapshot data: " + taskCache.getResult().getData());
                loadDetails(taskCache.getResult());
            } else {
                Log.d("TAG", "get failed with ", taskCache.getException());
            }
        });
    }

}


//        Category = getIntent().getStringExtra("Category");
//        Keywords = getIntent().getStringExtra("Keywords");
//        Rating = getIntent().getStringExtra("Rating");
//        Thumbnail_Link = getIntent().getStringExtra("Thumbnail Link");
//        Title = getIntent().getStringExtra("Title");

//        MajorBookDetails book = Util.convertQueryDocumentSnaptoBookinfo(Category, Rating, document);
//        String link = book.getEbookLink();

//        Picasso.get().load(Thumbnail_Link).fit().centerCrop(Gravity.TOP).into(backgroundImage);
//        Picasso.get().load(Thumbnail_Link).into(bookCover);

//            Map<String, Object> Request = new HashMap<>();
//            Request.put("Title", Title);
//            Request.put("Requester", currentUser.getDisplayName());
//            Request.put("Thumbnail Link", Thumbnail_Link);
//            Request.put("Status", "Pending");
//            Request.put("Time", Timestamp.now());
//
//            db.collection("Requests").document(Title + " | " + currentUser.getDisplayName())
//                    .set(Request)
//                    .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
//                    .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
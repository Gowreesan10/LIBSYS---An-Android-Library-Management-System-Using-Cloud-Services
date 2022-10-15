package com.code10.libsys.General.Activity;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.code10.libsys.Admin.Adapter.CopyAdapter;
import com.code10.libsys.General.Model.BookDetails;
import com.code10.libsys.General.Model.BookView;
import com.code10.libsys.R;
import com.code10.libsys.User.Activity.BorrowBook;
import com.code10.libsys.User.Adapter.AvailableLibraryAdapter;
import com.code10.libsys.User.UserSharedPreferance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewDetails extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
    String copies;
    String copiesIntent;
    TextView txtTotalcopycount;
    TextView availableCopies;
    TextView txtreservedCopies;
    TextView txtIssuedCopies;
    RecyclerView issueRecycleview;
    AvailableLibraryAdapter availableLibraryAdapter;
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
        bookView = gson.fromJson(getIntent().getStringExtra("BookView"), BookView.class);
        copiesIntent = getIntent().getStringExtra("Copies");
        getBookDetails(bookView.getTitle());
    }

    public void getBookDetails(String title) {
        DocumentReference docRef = db.collection("BookDetails").document(title);

        docRef.get().addOnCompleteListener(taskCache -> {
            if (taskCache.isSuccessful() && (taskCache.getResult().exists())) {
                bookDetails = taskCache.getResult().toObject(BookDetails.class);
                Log.d("TAG", "DocumentSnapshot data: " + taskCache.getResult().getData());
                loadDetails();
            } else {
                Log.d("TAG", "get failed with ", taskCache.getException());
            }
        });
    }

    public void loadDetails() {
        setContentView(R.layout.activity_user_view_details);
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
        TextView availableIN = findViewById(R.id.txtAvailableIn);
        txtTotalcopycount = findViewById(R.id.txtTotalcopycount);
        availableCopies = findViewById(R.id.txtavailableCopies);
        txtreservedCopies = findViewById(R.id.txtreservedCopies);
        issueRecycleview = findViewById(R.id.copyrecyclerview);
        txtIssuedCopies = findViewById(R.id.txtIssuedCopies);
        ImageView back = findViewById(R.id.imageArrowright);
        title.setText(bookDetails.getTitle());
        subTitle.setText(bookDetails.getSubTitle());
        authors.setText(bookDetails.getAuthors().toString().replace("[", "").replace("]", ""));
        numRating.setText(bookDetails.getUserRating());

        float Rating = Float.parseFloat(bookDetails.getUserRating());
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

        borrowPeriod.setText(bPeriod + " Days");
        pageNum.setText(bookDetails.getPageCount());
        ISBN13.setText(bookDetails.getISBN_13());
        ISBN10.setText(bookDetails.getISBN_10());
        Category.setText(bookDetails.getCategory());
        Publisher.setText(bookDetails.getPublisher());
        publishDate.setText(bookDetails.getPublishedDate());
        description.setText(bookDetails.getDescription());

        RecyclerView availableLibraries = findViewById(R.id.availableLibraries);

        Picasso.get().load(bookDetails.getThumbnailLink()).fit().centerCrop(Gravity.TOP).into(backgroundImage);
        Picasso.get().load(bookDetails.getThumbnailLink()).into(bookCover);

        back.setOnClickListener(v -> description.setMaxLines(100));

        ImageView imageView = findViewById(R.id.imageFeatherarrowl);
        imageView.setOnClickListener(v -> finish());
        if (bookView != null) {
            UserSharedPreferance.getInstance(getApplicationContext()).saveLibraryDetails(bookView);
        }

        if (copiesIntent == null) {
            availableLibraryAdapter = new AvailableLibraryAdapter(getBaseContext());
            availableLibraryAdapter.setQuery(db.collection("Copies").document(bookDetails.getTitle()));
            availableLibraries.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
            availableLibraries.setAdapter(availableLibraryAdapter);

            btnBorrowBook.setOnClickListener(view -> {
                if (availableLibraryAdapter.getSelectedItem() != 100) {

                    db.collection("Penalty").document(currentUser.getDisplayName().replace(".User", "")).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Map<String, Object> penality = task.getResult().getData();
                            if (penality != null && penality.containsKey(availableLibraryAdapter.getSelectedLibrary())) {
                                int py = Integer.parseInt(String.valueOf(penality.get(availableLibraryAdapter.getSelectedLibrary())));
                                if (py == 0) {
                                    startActivity();
                                } else {
                                    Toast.makeText(this, "Please Pay the Penalty", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Map<String, Integer> penalitymap = new HashMap<>();
                                penalitymap.put(availableLibraryAdapter.getSelectedLibrary(), 0);
                                db.collection("Penalty").document(currentUser.getDisplayName().replace(".User", "")).set(penalitymap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            startActivity();
                                        }
                                    }
                                });
                            }
                        }
                    });

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

            LinearLayout layout = findViewById(R.id.linearProfileAddre);
            layout.setVisibility(View.GONE);
        } else {
            availableIN.setVisibility(View.GONE);
            btnBorrowBook.setVisibility(View.GONE);
            btnDownloadEBook.setVisibility(View.GONE);
            setIssuedetails();
        }

    }

    private void setIssuedetails() {

        String libName = currentUser.getDisplayName().replace(".Librarian", "");
        db.collection("IssueDetails").document(libName).addSnapshotListener((value, error) -> {

            if (error != null) {
                Log.v("error", error.getLocalizedMessage());
            }
            String avilableCopies = "";
            String totalCopies = "";
            ArrayList<String> reservedFor = new ArrayList<>();
            ArrayList<HashMap<String, String>> issuedFor = new ArrayList<>();
            Map<String, Object> x0 = value.getData();
            Log.v("xo", x0.toString());
            Log.v("Title", bookDetails.getTitle());
            String Title = bookDetails.getTitle();
            Map<String, Object> x = (Map<String, Object>) x0.get(Title);


            for (Map.Entry<String, Object> entry : x.entrySet()) {
                switch (entry.getKey()) {
                    case "Available Copies":
                        avilableCopies = String.valueOf(entry.getValue());
                        Log.v("available", avilableCopies);
                        break;
                    case "Total Copies":
                        totalCopies = String.valueOf(entry.getValue());
                        Log.v("totalcopies", totalCopies);
                        break;
                    case "Issued For":
                        issuedFor = (ArrayList<HashMap<String, String>>) entry.getValue();
                        Log.v("issuedfOR", issuedFor.toString());
                        break;
                    default:
                        reservedFor = (ArrayList<String>) entry.getValue();
                        Log.v("reservedFor", reservedFor.toString());
                        break;
                }
            }

            txtTotalcopycount.setText(totalCopies);
            availableCopies.setText(avilableCopies);
            txtreservedCopies.setText(reservedFor.size() + "");
            int issue = Integer.parseInt(totalCopies) - 1 - Integer.parseInt(avilableCopies) - reservedFor.size();
            txtIssuedCopies.setText(issue + "");
            CopyAdapter copyAdapter = new CopyAdapter();
            issueRecycleview.setAdapter(copyAdapter);
            copyAdapter.setQuery(issuedFor);
            issueRecycleview.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        });
    }

    private void downloadBook(String link) {
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        fileName = url.getPath();
        fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
        Log.v("FILE", fileName);
Log.v("link",link);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url + ""));
        request.setTitle(fileName);
        request.setMimeType("application/pdf");
        request.allowScanningByMediaScanner();
        request.setAllowedOverMetered(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        String userName = currentUser.getDisplayName().replace(".User", "");
        request.setDestinationInExternalFilesDir(getApplicationContext(), "EBOOK" + userName, fileName);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }

    public void startActivity() {
        Intent intent = new Intent(this, BorrowBook.class);
        intent.putExtra("BOOK", bookDetails);
        intent.putExtra("LIBRARY", availableLibraryAdapter.getSelectedLibrary());
        startActivity(intent);
    }

}

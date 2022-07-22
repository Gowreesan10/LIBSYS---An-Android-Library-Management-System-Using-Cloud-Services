package com.code10.libsys.Admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.code10.libsys.BookDetails;
import com.code10.libsys.R;
import com.code10.libsys.SearchDatabaseAdapter;
import com.code10.libsys.Util;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddBooks extends AppCompatActivity implements Util.callBack {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<BookDetails> bookInfoArrayList;
    private ProgressBar progressBar;
    private EditText searchEdt;
    private String search;
    private LinearLayout searchBox;
    private AlertDialog alertDialog;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_books);
        search = getIntent().getStringExtra("Search");
        progressBar = findViewById(R.id.idLoadingPB);
        searchEdt = findViewById(R.id.idEdtSearchBooks);
        ImageButton searchBtn = findViewById(R.id.idBtnSearch);
        searchBox = findViewById(R.id.search_box);
        text = findViewById(R.id.textDatabaseBook);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());

        searchBox.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        searchBookInDatabase(search);

        searchBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);

            if (searchEdt.getText().toString().isEmpty()) {
                searchEdt.setError("Please enter search query");
                return;
            }

            getBooksInfo(searchEdt.getText().toString());
        });

    }

    private boolean searchBookInDatabase(String search) {

        Query query = db.collection("BookView").whereArrayContains("keywords", search);
        SearchDatabaseAdapter searchDatabaseAdapter = new SearchDatabaseAdapter(getBaseContext(), this, R.layout.book_rv_item);
        RecyclerView databaseRecyclerView = findViewById(R.id.idRVBooks);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddBooks.this, RecyclerView.VERTICAL, false);
        databaseRecyclerView.setLayoutManager(linearLayoutManager);
        databaseRecyclerView.setAdapter(searchDatabaseAdapter);
        searchDatabaseAdapter.setQuery(query);
        progressBar.setVisibility(View.GONE);
        return false;
    }

    private void getBooksInfo(String query) {

        searchEdt.setText(query);
        bookInfoArrayList = new ArrayList<>();
        RequestQueue mRequestQueue = Volley.newRequestQueue(AddBooks.this);
        mRequestQueue.getCache().clear();
        String url = "https://www.googleapis.com/books/v1/volumes?q=" + query;
        RequestQueue queue = Volley.newRequestQueue(AddBooks.this);

        // below line is use to make json object request inside that we are passing url, get method and getting json object.
        JsonObjectRequest booksObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            progressBar.setVisibility(View.GONE);
            // inside on response method we are extracting all our json data.
            try {
                JSONArray itemsArray = response.getJSONArray("items");

                for (int i = 0; i < itemsArray.length(); i++) {

                    JSONObject itemsObj = itemsArray.getJSONObject(i);
                    JSONObject volumeObj = itemsObj.getJSONObject("volumeInfo");

                    JSONArray authorsArray = volumeObj.optJSONArray("authors");
                    List<String> authors = new ArrayList<>();
                    if (authorsArray != null && authorsArray.length() != 0) {
                        for (int j = 0; j < authorsArray.length(); j++) {
                            Log.v("LOG", authorsArray.optString(j));
                            authors.add(authorsArray.optString(j));
                        }
                    }
                    String category = volumeObj.optString("Category");
                    String Rating = volumeObj.optString("averageRating");
                    String title = volumeObj.optString("title");
                    String subtitle = volumeObj.optString("subtitle");
                    if (subtitle.equals("")) {
                        subtitle = "Not Found";
                    }
                    String publisher = volumeObj.optString("publisher");
                    String publishedDate = volumeObj.optString("publishedDate");
                    String description = volumeObj.optString("description");
                    String pageCount = volumeObj.optString("pageCount");

                    JSONObject imageLinks = volumeObj.optJSONObject("imageLinks");
                    String thumbnail = null;
                    if (imageLinks != null) {
                        thumbnail = imageLinks.optString("thumbnail");
                        thumbnail = thumbnail.replace("zoom=1", "zoom=3");
                    }

                    JSONArray industryIdentifiers = volumeObj.optJSONArray("industryIdentifiers");
                    String ISBN_10 = "Not found";
                    String ISBN_13 = "Not found";
                    if (industryIdentifiers != null && industryIdentifiers.length() != 0) {
                        for (int j = 0; j < industryIdentifiers.length(); j++) {
                            String type = industryIdentifiers.getJSONObject(j).optString("type");
                            String identifier = industryIdentifiers.getJSONObject(j).optString("identifier");
                            if (type.equals("ISBN_10")) {
                                ISBN_10 = identifier;
                            } else if (type.equals("ISBN_13")) {
                                ISBN_13 = identifier;
                            }
                        }
                    }

                    JSONObject accessInfo = itemsObj.optJSONObject("accessInfo");
                    JSONObject pdf = accessInfo.optJSONObject("pdf");
                    String pdfDownloadLink = "Download Link Not Available";
                    if (pdf != null) {
                        pdfDownloadLink = pdf.optString("downloadLink");
                    }

                    BookDetails bookInfo = new BookDetails(title.trim(),
                            subtitle.trim(),
                            category.trim(),
                            authors,
                            publisher.trim(),
                            publishedDate.trim(),
                            description.trim(),
                            pageCount.trim(),
                            Rating.trim(),
                            thumbnail,
                            ISBN_10,
                            ISBN_13,
                            pdfDownloadLink.trim(), Timestamp.now() + "");

                    bookInfoArrayList.add(bookInfo);
                    GBookAdapter adapter = new GBookAdapter(bookInfoArrayList, AddBooks.this);
                    RecyclerView mRecyclerView = findViewById(R.id.idRVBooks);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(AddBooks.this, RecyclerView.VERTICAL, false));
                    mRecyclerView.setAdapter(adapter);

                    progressBar.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(AddBooks.this, "No Data Found" + e, Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(AddBooks.this, "Error found is " + error, Toast.LENGTH_SHORT).show());

        queue.add(booksObjRequest); // at last we are adding our json object // request in our request queue.
    }

    @Override
    public void call() {
        AlertDialog.Builder alaBuilder = new AlertDialog.Builder(AddBooks.this);
        text.setText("G-BOOKS");
        searchBox.setVisibility(View.VISIBLE);
        alaBuilder.setTitle("Book is not in any existing Libraries Add Book Details by");

        View view1 = LayoutInflater.from(AddBooks.this).inflate(R.layout.alart_dialog_book_not_in_database, null);
        alaBuilder.setView(view1);
        alertDialog = alaBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        Button searchGBooks = view1.findViewById(R.id.searchGoogleBooks);
        Button enterManually = view1.findViewById(R.id.enterManually);

        searchGBooks.setOnClickListener(view2 -> {
            getBooksInfo(search);
            alertDialog.dismiss();
        });

        enterManually.setOnClickListener(view -> {
            startActivity(new Intent(AddBooks.this, AddBookDetails.class));
            alertDialog.dismiss();
            finish();
        });

    }

    @Override
    public void callExistBook(String Title) {
        AlertDialog.Builder alaBuilder = new AlertDialog.Builder(AddBooks.this);
        alaBuilder.setTitle("Enter No of Copies Available in this Library");

        View view1 = LayoutInflater.from(AddBooks.this).inflate(R.layout.add_copies, null);
        alaBuilder.setView(view1);
        alertDialog = alaBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        Button submit = view1.findViewById(R.id.submit);
        EditText copies = view1.findViewById(R.id.enterCopies);
        submit.setOnClickListener(v1 -> {
            alertDialog.dismiss();
            addLibraryEntry(Title, copies.getText().toString());
        });
    }

    private void addLibraryEntry(String Title, String noOfCopies) {
        ProgressDialog progressDialog = new ProgressDialog(AddBooks.this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        LibraryDetails libraryDetails = AdminSharedPreference.getLibraryDetails();
        Map<String, Object> Copies = new HashMap<>();
        Copies.put("Library", libraryDetails.getLibraryName());
        Copies.put("No of Copies", noOfCopies);
        Copies.put("Address", libraryDetails.getAddress());

        Map<String, Object> LibraryCopies = new HashMap<>();
        LibraryCopies.put(libraryDetails.getLibraryName(), Copies);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Copies")
                .document(Title)
                .set(LibraryCopies, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d("Copies", "DocumentSnapshot successfully written!");
                    progressDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    Log.w("Copies", "Error writing document", e);
                    Toast.makeText(getBaseContext(), "Upload Copies Details failure", Toast.LENGTH_SHORT).show();
                });
    }
}

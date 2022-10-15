package com.code10.libsys.Admin.Activity;

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
import com.code10.libsys.Admin.Adapter.GBookAdapter;
import com.code10.libsys.Admin.AdminSharedPreference;
import com.code10.libsys.General.Model.BookDetails;
import com.code10.libsys.General.Model.LibraryDetails;
import com.code10.libsys.General.Utility;
import com.code10.libsys.R;
import com.code10.libsys.User.Adapter.SearchDatabaseAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetBookDetails extends AppCompatActivity implements Utility.callBack {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<BookDetails> bookInfoArrayList;
    private ProgressBar progressBar;
    private EditText searchEdt;
    private String search;
    private long searchISBN;
    private LinearLayout searchBox;
    private AlertDialog alertDialog;
    private TextView text, clicktomanual;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_books);
        loadingBar = new ProgressDialog(GetBookDetails.this);
        search = getIntent().getStringExtra("Search");
        progressBar = findViewById(R.id.idLoadingPB);
        searchEdt = findViewById(R.id.idEdtSearchBooks);
        ImageButton searchBtn = findViewById(R.id.idBtnSearch);
        searchBox = findViewById(R.id.search_box);
        text = findViewById(R.id.txtUserdetails);
        clicktomanual = findViewById(R.id.clicktomanual);
        ImageView back = findViewById(R.id.imgback);

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
            getBooksInfoFromGBooks(searchEdt.getText().toString());
        });

    }

//    private void getBooksInfoFromOpenLibrary(long ISBN) {
//        String url = "https://openlibrary.org/api/books?bibkeys=ISBN:" + ISBN + "&jscmd=data&format=json";
//        bookInfoArrayList = new ArrayList<>();
//        RequestQueue mRequestQueue = Volley.newRequestQueue(GetBookDetails.this);
//        mRequestQueue.getCache().clear();
//        RequestQueue queue = Volley.newRequestQueue(GetBookDetails.this);
//
//        JsonObjectRequest booksObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
//            progressBar.setVisibility(View.GONE);
//            JSONObject jsonObject = null;
//            try {
//                Log.v("LOG", response.toString());
//                jsonObject = response.getJSONObject("ISBN:" + ISBN);
//                Log.v("LOG", jsonObject.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//                if (jsonObject == null) {
//                    Log.v("LOG", "NULL");
//                }
//                Toast.makeText(GetBookDetails.this, "No Data Found" + e, Toast.LENGTH_SHORT).show();
//            }
//        }, error -> Toast.makeText(GetBookDetails.this, "Error found is " + error, Toast.LENGTH_SHORT).show());
//
//        queue.add(booksObjRequest);
//    }

//    public void showloading() {
//        loadingBar.setMessage("Loading Books From General Database....");
//        loadingBar.setCanceledOnTouchOutside(false);
//        loadingBar.show();
//    }

    private boolean searchBookInDatabase(String search) {

        String ISBN_TYPE = search.length() == 13 ? "isbn_13" : "isbn_10";
        searchISBN = Long.parseLong(search);
        Query query = db.collection("BookDetails").whereEqualTo(ISBN_TYPE, search);

        SearchDatabaseAdapter searchDatabaseAdapter = new SearchDatabaseAdapter(getBaseContext(), this, R.layout.book_rv_item);
        RecyclerView databaseRecyclerView = findViewById(R.id.idRVBooks);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GetBookDetails.this, RecyclerView.VERTICAL, false);
        databaseRecyclerView.setLayoutManager(linearLayoutManager);
        databaseRecyclerView.setAdapter(searchDatabaseAdapter);
        searchDatabaseAdapter.setQuery(query);
        progressBar.setVisibility(View.GONE);
        return false;
    }

    private void getBooksInfoFromGBooks(String query) {

        searchEdt.setText(query);
        bookInfoArrayList = new ArrayList<>();
        RequestQueue mRequestQueue = Volley.newRequestQueue(GetBookDetails.this);
        mRequestQueue.getCache().clear();
        String url = "https://www.googleapis.com/books/v1/volumes?q=" + query;
        RequestQueue queue = Volley.newRequestQueue(GetBookDetails.this);

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
                            pdfDownloadLink.trim(),
                            Timestamp.now().toString());

                    bookInfoArrayList.add(bookInfo);
                    GBookAdapter adapter = new GBookAdapter(bookInfoArrayList, GetBookDetails.this);
                    RecyclerView mRecyclerView = findViewById(R.id.idRVBooks);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(GetBookDetails.this, RecyclerView.VERTICAL, false));
                    mRecyclerView.setAdapter(adapter);

                    progressBar.setVisibility(View.GONE);
                    loadingBar.dismiss();
                    clicktomanual.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(GetBookDetails.this, "No Data Found" + e, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                clicktomanual.setVisibility(View.VISIBLE);
            }
        }, error -> Toast.makeText(GetBookDetails.this, "Error found is " + error, Toast.LENGTH_SHORT).show());

        queue.add(booksObjRequest); // at last we are adding our json object // request in our request queue.
    }

    @Override
    public void call() {
        //getBooksInfoFromOpenLibrary(searchISBN);
//        AlertDialog.Builder alaBuilder = new AlertDialog.Builder(GetBookDetails.this);
//        text.setText("G-BOOKS");
        searchBox.setVisibility(View.VISIBLE);
        clicktomanual.setVisibility(View.GONE);
//        alaBuilder.setTitle("Book is not in any existing Libraries Add Book Details by");
//
//        View view1 = LayoutInflater.from(GetBookDetails.this).inflate(R.layout.alart_dialog_book_not_in_database, null);
//        alaBuilder.setView(view1);
//        alertDialog = alaBuilder.create();
//        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
//        alertDialog.setCanceledOnTouchOutside(false);
//        alertDialog.show();
//
//        Button searchGBooks = view1.findViewById(R.id.searchGoogleBooks);
//        Button enterManually = view1.findViewById(R.id.enterManually);
//
//        searchGBooks.setOnClickListener(view2 -> {
        //loadingBar.dismiss();
        text.setText("G-BOOKS");
        loadingBar.setMessage("Loading Books From GBooks....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        getBooksInfoFromGBooks(search);
//            alertDialog.dismiss();
//        });
//
        clicktomanual.setOnClickListener(view -> {
            startActivity(new Intent(GetBookDetails.this, AddBookDetails.class));
            finish();
        });

    }

    @Override
    public void callExistBook(String Title) {
        loadingBar.dismiss();
        AlertDialog.Builder alaBuilder = new AlertDialog.Builder(GetBookDetails.this);
        alaBuilder.setTitle("Enter No of Copies Available in the Library.");

        View view1 = LayoutInflater.from(GetBookDetails.this).inflate(R.layout.add_copies, null);
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
        ProgressDialog progressDialog = new ProgressDialog(GetBookDetails.this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        LibraryDetails libraryDetails = AdminSharedPreference.getInstance(getApplicationContext()).getLibraryDetails();
        Map<String, Object> Copies = new HashMap<>();
        Copies.put("Library", libraryDetails.getLibraryName());
        Copies.put("No of Copies", noOfCopies);
        Copies.put("Address", libraryDetails.getAddress());

        Map<String, Object> LibraryCopies = new HashMap<>();
        LibraryCopies.put(libraryDetails.getLibraryName(), Copies);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<String> reservedFor = new ArrayList<>();
        ArrayList<Map<String, Object>> issuedFor = new ArrayList<>();
        Map<String, Object> copyIssueDetails = new HashMap<>();
        copyIssueDetails.put("Available Copies", Integer.parseInt(noOfCopies) - 1);
        copyIssueDetails.put("Total Copies", Integer.parseInt(noOfCopies));
        copyIssueDetails.put("Reserved For", reservedFor);
        copyIssueDetails.put("Issued For", issuedFor);

        Map<String, Object> issueDetails = new HashMap<>();
        issueDetails.put(Title.trim(), copyIssueDetails);

        WriteBatch batch = db.batch();
        batch.set(db.collection("Copies").document(Title), LibraryCopies, SetOptions.merge());
        batch.set(db.collection("IssueDetails").document(libraryDetails.getLibraryName()), issueDetails, SetOptions.merge());
        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                getQRCode(libraryDetails.getLibraryName(), Title, noOfCopies);
            } else {
                Toast.makeText(getBaseContext(), "Failed write Document", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getQRCode(String libname, String title, String copiescount) {
        Intent intent = new Intent(this, QRCodeGenerator.class);
        intent.putExtra("LIB NAME", libname);
        intent.putExtra("BOOK NAME", title);
        intent.putExtra("COPIES", copiescount);
        startActivity(intent);
        finish();
    }
}

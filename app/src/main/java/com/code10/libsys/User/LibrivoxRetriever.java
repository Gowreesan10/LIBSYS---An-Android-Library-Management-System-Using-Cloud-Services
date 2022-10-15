package com.code10.libsys.User;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.code10.libsys.General.Model.AudioBook;
import com.code10.libsys.General.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LibrivoxRetriever extends AsyncTask<String, Void, ArrayList<AudioBook>> {
    Context context;
    private Utility.QueryFinished callback;


    public LibrivoxRetriever(Context context) {
        this.context = context;
    }

    @Override
    protected ArrayList<AudioBook> doInBackground(String... searchTerm) {
        ArrayList<AudioBook> books = new ArrayList<>();

        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            String url = searchTerm[0];

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    response -> {

                        try {
                            Log.v("AUDIO", response);
                            JSONArray librivox = new JSONObject(response).getJSONArray("books");
                            for (int i = 0; i < librivox.length(); i++) {
                                JSONObject bookData = librivox.getJSONObject(i);
                                AudioBook currentBook = new AudioBook();

                                currentBook.setTitle(bookData.optString("title"));

                                currentBook.setAuthor(bookData.optJSONArray("authors").optJSONObject(0).optString("first_name")
                                        + " " + bookData.optJSONArray("authors").optJSONObject(0).optString("last_name"));

                                String iarchive = bookData.optString("url_iarchive").substring(1 + bookData.optString("url_iarchive").lastIndexOf("/"));

                                currentBook.setChaptersURL("http://archive.org/metadata/" + iarchive);
                                currentBook.setThumbnailURL("https://archive.org/services/img/" + iarchive);

                                books.add(currentBook);
                            }
                            callback.onQueryFinished(books);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                    }, error -> {

            });

            queue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return books;
    }

//    @Override
//    protected void onPostExecute(ArrayList<AudioBook> result) {
//        super.onPostExecute(result);
//        callback.onQueryFinished(result);
//    }

    public void addOnCompleted(Utility.QueryFinished callback) {
        this.callback = callback;
    }
}


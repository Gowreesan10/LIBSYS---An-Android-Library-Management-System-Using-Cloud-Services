package com.code10.libsys.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.code10.libsys.General.Model.BookView;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class UserSharedPreferance {
    private static UserSharedPreferance singleInstance = null;
    private static SharedPreferences sharedPreferences;
    private boolean isListenerRegistered = false;

    private UserSharedPreferance(Context context) {
        sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
    }

    public static UserSharedPreferance getInstance(Context context) {
        Log.v("user get instance", "");
        if (singleInstance == null) {
            singleInstance = new UserSharedPreferance(context);
        }
        return singleInstance;
    }

    public void deleteDetails() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        singleInstance = null;
        Log.v("Sucessfully deleted user details", "success");
    }

    public ArrayList<BookView> getRecentlyViewdBooks() {
        Gson gson = new Gson();
        String savedJson = sharedPreferences.getString("Recent Books", null);
        Type type = new TypeToken<ArrayList<BookView>>() {
        }.getType();
        return gson.fromJson(savedJson, type);
    }

    public void saveLibraryDetails(BookView recentBook) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        ArrayList<BookView> newRecentBooks = new ArrayList<>();
        ArrayList<BookView> recentBooksOriginal = getRecentlyViewdBooks();
        if (recentBooksOriginal != null) {
            for (int i = 0; i < recentBooksOriginal.size(); i++) {
                BookView bookView = recentBooksOriginal.get(i);
                if (bookView.getTitle().equals(recentBook.getTitle())) {
                    recentBooksOriginal.remove(i);
                    break;
                }
            }
        }
        newRecentBooks.add(recentBook);
        if (recentBooksOriginal != null) {
            newRecentBooks.addAll(recentBooksOriginal);
        }
        String saveJson = gson.toJson(newRecentBooks);
        editor.putString("Recent Books", saveJson);
        editor.apply();
        Log.v("Save recent books", "Sucess");
    }

    public void registerSharedPrefListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        if (!isListenerRegistered) {
            sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
            isListenerRegistered = true;
        }
    }

    public void unregisterSharedPrefListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        isListenerRegistered = true;
    }

}


/*
<?xml version="1.0" encoding="UTF-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <com.google.android.material.card.MaterialCardView
        android:id="@+id/linearGroup"
        style="@style/groupStylewhite_A700"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="@dimen/_5pxh"
        android:layout_marginStart="@dimen/_10pxv"
        android:layout_marginEnd="@dimen/_10pxv"
        android:layout_marginBottom="@dimen/_3pxh"
        android:gravity="start"
        android:orientation="vertical"
        app:cardPreventCornerOverlap="true">

        <LinearLayout
            android:id="@+id/linearGroup7"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center_vertical"
            android:orientation="horizontal">

            <ImageView
                android:id="@+id/bookCoverIv"
                android:layout_width="@dimen/_49pxh"
                android:layout_height="match_parent"
                android:layout_gravity="center_vertical"
                android:src="@drawable/ic_launcher_background"
                tools:ignore="ContentDescription" />

            <LinearLayout
                android:id="@+id/linearGroup6"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_gravity="center_vertical"
                android:layout_marginStart="@dimen/_10pxv"
                android:layout_marginTop="@dimen/_1pxh"
                android:layout_marginBottom="@dimen/_3pxh"
                android:layout_weight="0.8"
                android:orientation="vertical">

                <TextView
                    android:id="@+id/Book_Name"
                    style="@style/txtPoppinsmedium16"
                    android:layout_width="match_parent"
                    android:layout_height="@dimen/_36pxh"
                    android:ellipsize="end"
                    android:singleLine="false"
                    android:text="Gowreeshan Sachchithananthan" />

                <LinearLayout
                    android:id="@+id/linearRowunlimitedannuaOne"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal">

                    <TextView
                        android:id="@+id/txtUnlimitedannuaOne"
                        style="@style/txtInterregular12"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="0.6"
                        android:ellipsize="end"
                        android:lineSpacingExtra="@dimen/_1pxh"
                        android:singleLine="true"
                        android:text="Requested  To     :" />

                    <TextView
                        android:id="@+id/requester"
                        style="@style/txtInterregular12"
                        android:layout_width="0dp"
                        android:layout_height="match_parent"
                        android:layout_weight="0.4"
                        android:ellipsize="end"
                        android:lineSpacingExtra="@dimen/_5pxh"
                        android:singleLine="false"
                        android:text="jinushan" />
                </LinearLayout>

                <LinearLayout
                    android:id="@+id/requestStatus"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal">

                    <TextView
                        android:id="@+id/status"
                        style="@style/txtInterregular12"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="0.6"
                        android:ellipsize="end"
                        android:lineSpacingExtra="@dimen/_1pxh"
                        android:singleLine="true"
                        android:text="Request  Status  :" />

                    <TextView
                        android:id="@+id/statusName"
                        style="@style/txtInterregular12"
                        android:layout_width="0dp"
                        android:layout_height="match_parent"
                        android:layout_weight="0.4"
                        android:ellipsize="end"
                        android:lineSpacingExtra="@dimen/_5pxh"
                        android:singleLine="false"
                        android:text="Time Out" />
                </LinearLayout>
            </LinearLayout>
        </LinearLayout>

    </com.google.android.material.card.MaterialCardView>
</layout>

 */
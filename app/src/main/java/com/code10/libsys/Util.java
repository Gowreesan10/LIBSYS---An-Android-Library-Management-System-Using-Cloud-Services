package com.code10.libsys;

import android.app.ProgressDialog;
import android.content.Context;

public class Util {
    private static ProgressDialog pdialog;

    public static void showLoadingBox(Context context) {
        pdialog = new ProgressDialog(context);
        pdialog.setCancelable(true);
        pdialog.setMessage("Loading ....");
        pdialog.show();
    }

    public static void hideLoadingBox() {
        pdialog.dismiss();
    }

    public interface updateRecyclerView {
        void updateCategaoryView(String category);
    }

    public interface callBack {
        void call();
        void callExistBook(String Title);
    }
}


//    public static MajorBookDetails convertQueryDocumentSnaptoBookinfo(String Category, String Rating, Map<String, Object> QDSs) {
//
//        String title = Objects.requireNonNull(QDSs.get("Title")).toString();
//        String subtitle = Objects.requireNonNull(QDSs.get("SubTitle")).toString();
//        String authors = Objects.requireNonNull(QDSs.get("Author")).toString();
//        String publisher = Objects.requireNonNull(QDSs.get("Publisher")).toString();
//        String publishedDate = Objects.requireNonNull(QDSs.get("Published Date")).toString();
//        String description = Objects.requireNonNull(QDSs.get("Description")).toString();
//        String pageCount = Objects.requireNonNull(QDSs.get("Page Count")).toString();
//        String thumbnail = Objects.requireNonNull(QDSs.get("Thumbnail Link")).toString();
//        String ISBN_10 = Objects.requireNonNull(QDSs.get("ISBN-10")).toString();
//        String ISBN_13 = Objects.requireNonNull(QDSs.get("ISBN-13")).toString();
//        String ebookLink = Objects.requireNonNull(QDSs.get("E-Book Link")).toString();
//
//        return new MajorBookDetails(title, subtitle, Category, authors, publisher, publishedDate, description, pageCount, Rating, thumbnail, ISBN_10, ISBN_13, ebookLink);
//    }

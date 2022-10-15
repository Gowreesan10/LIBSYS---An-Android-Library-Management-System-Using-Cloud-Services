package com.code10.libsys.General;

import android.app.ProgressDialog;
import android.content.Context;

public class Utility {
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

    public interface QueryFinished<T> {
        void onQueryFinished(T t);
    }
}


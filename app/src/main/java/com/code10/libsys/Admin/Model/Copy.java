package com.code10.libsys.Admin.Model;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;

public class Copy implements Serializable {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String LibName;
    String BookName;
    String CopyNo;

    public Copy() {

    }

    public Copy(String libName, String bookName, String copyNo) {
        LibName = libName;
        BookName = bookName;
        CopyNo = copyNo;
    }

    public String getLibName() {
        return LibName;
    }

    public void setLibName(String libName) {
        LibName = libName;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getCopyNo() {
        return CopyNo;
    }

    public void setCopyNo(String copyNo) {
        CopyNo = copyNo;
    }

}

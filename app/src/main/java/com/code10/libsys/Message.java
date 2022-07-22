package com.code10.libsys;

import java.io.Serializable;

public class Message implements Serializable {

    private String Requester;
    private String BookName;
    private String Status;
    private String Token;
    private String ThumbnailLink;
    private String BorrowMethod;
    private String Days;
    private String ReceiptUri;
/*
String token = FCMReceiver.getToken(getApplicationContext());

            Message message = new Message(currentUser.getDisplayName().replace(".User", ""),
                    Book.getTitle(), "Pending", token, Book.getThumbnailLink(), borrowMed, selectedBoDays, receiptUri.);
            sendRequest(LibName, message);
 */
    public Message(String requester, String bookName, String status, String token, String thumbnailLink, String borrowMethod, String days, String receiptUri) {
        Requester = requester;
        BookName = bookName;
        Status = status;
        Token = token;
        ThumbnailLink = thumbnailLink;
        BorrowMethod = borrowMethod;
        Days = days;
        ReceiptUri = receiptUri;
    }

    public Message() {
    }

    public String getRequester() {
        return Requester;
    }

    public void setRequester(String requester) {
        Requester = requester;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getThumbnailLink() {
        return ThumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        ThumbnailLink = thumbnailLink;
    }
}

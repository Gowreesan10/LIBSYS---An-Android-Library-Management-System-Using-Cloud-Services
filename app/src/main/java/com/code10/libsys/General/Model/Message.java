package com.code10.libsys.General.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {

    private String Requester;
    private String BookName;
    private String Category;
    private String Status;
    private String LibName;
    private String BorrowerToken;
    private String ProfileLink;
    private String ThumbnailLink;
    private String BorrowMethod;
    private Date returnDate;
    private String ReceiptUri;
    @ServerTimestamp
    private Timestamp requestTime;
    private Boolean isNotificationViewed;


    public Message() {
    }

    public Message(String requester, String bookName, String category, String status, String libName, String borrowerToken, String profileLink, String thumbnailLink, String borrowMethod, Date returnDate, String receiptUri, Boolean isNotificationViewed) {
        Requester = requester;
        BookName = bookName;
        Category = category;
        Status = status;
        LibName = libName;
        BorrowerToken = borrowerToken;
        ProfileLink = profileLink;
        ThumbnailLink = thumbnailLink;
        BorrowMethod = borrowMethod;
        this.returnDate = returnDate;
        ReceiptUri = receiptUri;
        this.isNotificationViewed = isNotificationViewed;
    }

    public Boolean getNotificationViewed() {
        return isNotificationViewed;
    }

    public void setNotificationViewed(Boolean notificationViewed) {
        isNotificationViewed = notificationViewed;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getThumbnailLink() {
        return ThumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        ThumbnailLink = thumbnailLink;
    }

    public String getLibName() {
        return LibName;
    }

    public void setLibName(String libName) {
        LibName = libName;
    }

    public Timestamp getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Timestamp requestTime) {
        this.requestTime = requestTime;
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

    public String getBorrowerToken() {
        return BorrowerToken;
    }

    public void setBorrowerToken(String borrowerToken) {
        this.BorrowerToken = borrowerToken;
    }

    public String getProfileLink() {
        return ProfileLink;
    }

    public void setProfileLink(String profileLink) {
        ProfileLink = profileLink;
    }

    public String getBorrowMethod() {
        return BorrowMethod;
    }

    public void setBorrowMethod(String borrowMethod) {
        BorrowMethod = borrowMethod;
    }

    public String getReceiptUri() {
        return ReceiptUri;
    }

    public void setReceiptUri(String receiptUri) {
        ReceiptUri = receiptUri;
    }
}

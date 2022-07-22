package com.code10.libsys;
//
//public class BookView {
//    private String Title;
//    private String ThumbnailLink;
//    private String rating;
//    private String Category;
//
//    public BookView(String title, String thumbnailLink, String rating, String category) {
//        Title = title;
//        ThumbnailLink = thumbnailLink;
//        this.rating = rating;
//        Category = category;
//    }
//
//    public String getTitle() {
//        return Title;
//    }
//
//    public void setTitle(String title) {
//        Title = title;
//    }
//
//    public String getThumbnailLink() {
//        return ThumbnailLink;
//    }
//
//    public void setThumbnailLink(String thumbnailLink) {
//        ThumbnailLink = thumbnailLink;
//    }
//
//    public String getRating() {
//        return rating;
//    }
//
//    public void setRating(String rating) {
//        this.rating = rating;
//    }
//
//    public String getCategory() {
//        return Category;
//    }
//
//    public void setCategory(String category) {
//        Category = category;
//    }
//}


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.List;

public class BookView {
    private String Title;
    private String ThumbnailLink;
    private String Rating;
    private String Category;
    private List<String> Keywords;
    @ServerTimestamp
    private Timestamp timestamp;

    public BookView() {
    }

    public BookView(String title, String thumbnailLink, String rating, String category, List<String> keywords, Timestamp timestamp) {
        Title = title;
        ThumbnailLink = thumbnailLink;
        Rating = rating;
        Category = category;
        Keywords = keywords;
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getThumbnailLink() {
        return ThumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        ThumbnailLink = thumbnailLink;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public List<String> getKeywords() {
        return Keywords;
    }

    public void setKeywords(List<String> keywords) {
        Keywords = keywords;
    }
}

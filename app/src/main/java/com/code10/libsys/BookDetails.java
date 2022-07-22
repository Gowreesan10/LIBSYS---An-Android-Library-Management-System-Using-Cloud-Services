package com.code10.libsys;

import java.io.Serializable;
import java.util.List;

//public class MajorBookDetails implements Serializable {
//
//    private final String title;
//    private final String subtitle;
//    private final String categories;
//    private final String authors;
//    private final String publisher;
//    private final String publishedDate;
//    private final String description;
//    private final String pageCount;
//    private final String userRating;
//    private final String thumbnail;
//    private final String ISBN_10;
//    private final String ISBN_13;
//    private final String ebookLink;
//
//    public MajorBookDetails(String title, String subtitle, String categories, String authors, String publisher, String publishedDate, String description, String pageCount, String userRating, String thumbnail, String ISBN_10, String ISBN_13, String ebookLink) {
//        this.title = title;
//        this.subtitle = subtitle;
//        this.categories = categories;
//        this.authors = authors;
//        this.publisher = publisher;
//        this.publishedDate = publishedDate;
//        this.description = description;
//        this.pageCount = pageCount;
//        this.userRating = userRating;
//        this.thumbnail = thumbnail;
//        this.ISBN_10 = ISBN_10;
//        this.ISBN_13 = ISBN_13;
//        this.ebookLink = ebookLink;
//    }
//
//    public String getISBN_10() {
//        return ISBN_10;
//    }
//
//    public String getISBN_13() {
//        return ISBN_13;
//    }
//
//    public String getEbookLink() {
//        return ebookLink;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public String getSubtitle() {
//        return subtitle;
//    }
//
//    public String getAuthors() {
//        return authors;
//    }
//
//    public String getPublisher() {
//        return publisher;
//    }
//
//    public String getPublishedDate() {
//        return publishedDate;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public String getThumbnail() {
//        return thumbnail;
//    }
//
//    public String getCategories() {
//        return categories;
//    }
//
//    public String getPageCount() {
//        return pageCount;
//    }
//
//    public String getUserRating() {
//        return userRating;
//    }
//}


public class BookDetails implements Serializable {

    private String Title;
    private String SubTitle;
    private String Category;
    private List<String> Authors;
    private String Publisher;
    private String PublishedDate;
    private String Description;
    private String PageCount;
    private String UserRating;
    private String ThumbnailLink;
    private String ISBN_10;
    private String ISBN_13;
    private String EbookLink;
    private String Timestamp;

    public BookDetails() {
    }

    public BookDetails(String title, String subTitle, String categories, List<String> authors, String publisher, String publishedDate, String description, String pageCount, String userRating, String thumbnailLink, String ISBN_10, String ISBN_13, String ebookLink, String timestamp) {
        Title = title;
        SubTitle = subTitle;
        Category = categories;
        Authors = authors;
        Publisher = publisher;
        PublishedDate = publishedDate;
        Description = description;
        PageCount = pageCount;
        UserRating = userRating;
        ThumbnailLink = thumbnailLink;
        this.ISBN_10 = ISBN_10;
        this.ISBN_13 = ISBN_13;
        EbookLink = ebookLink;
        Timestamp = timestamp;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSubTitle() {
        return SubTitle;
    }

    public void setSubTitle(String subTitle) {
        SubTitle = subTitle;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public List<String> getAuthors() {
        return Authors;
    }

    public void setAuthors(List<String> authors) {
        Authors = authors;
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        Publisher = publisher;
    }

    public String getPublishedDate() {
        return PublishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        PublishedDate = publishedDate;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPageCount() {
        return PageCount;
    }

    public void setPageCount(String pageCount) {
        PageCount = pageCount;
    }

    public String getUserRating() {
        return UserRating;
    }

    public void setUserRating(String userRating) {
        UserRating = userRating;
    }

    public String getThumbnailLink() {
        return ThumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        ThumbnailLink = thumbnailLink;
    }

    public String getISBN_10() {
        return ISBN_10;
    }

    public void setISBN_10(String ISBN_10) {
        this.ISBN_10 = ISBN_10;
    }

    public String getISBN_13() {
        return ISBN_13;
    }

    public void setISBN_13(String ISBN_13) {
        this.ISBN_13 = ISBN_13;
    }

    public String getEbookLink() {
        return EbookLink;
    }

    public void setEbookLink(String ebookLink) {
        EbookLink = ebookLink;
    }

}



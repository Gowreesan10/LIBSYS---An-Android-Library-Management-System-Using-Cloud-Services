package com.code10.libsys.Admin;

public class LibraryDetails {
    String LibraryName;
    String Address;
    String Latitude;
    String Longitude;
    String Token;
    String TelephoneNo;
    String FaxNo;

    public LibraryDetails() {
    }

    public LibraryDetails(String libraryName, String address, String latitude, String longitude, String token, String telephoneNo, String faxNo) {
        LibraryName = libraryName;
        Address = address;
        Latitude = latitude;
        Longitude = longitude;
        Token = token;
        TelephoneNo = telephoneNo;
        FaxNo = faxNo;
    }

    public String getLibraryName() {
        return LibraryName;
    }

    public void setLibraryName(String libraryName) {
        LibraryName = libraryName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getTelephoneNo() {
        return TelephoneNo;
    }

    public void setTelephoneNo(String telephoneNo) {
        TelephoneNo = telephoneNo;
    }

    public String getFaxNo() {
        return FaxNo;
    }

    public void setFaxNo(String faxNo) {
        FaxNo = faxNo;
    }
}

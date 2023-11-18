package com.ecommerce.catalog_service.OtherServiceObjects;

public class User {
    private int userid;
    private String username;
    private String userpassword;
    private String firstname;
    private String lastname;
    private String streetname;
    private int streetnumber;
    private String city;
    private String country;
    private String postalcode;

    public User() {
    }

    public User(String username, String userpassword, String firstname, String lastname, String streetname, int streetnumber, String city, String country, String postalcode) {
        this.username = username;
        this.userpassword = userpassword;
        this.firstname = firstname;
        this.lastname = lastname;
        this.streetname = streetname;
        this.streetnumber = streetnumber;
        this.city = city;
        this.country = country;
        this.postalcode = postalcode;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getStreetname() {
        return streetname;
    }

    public void setStreetname(String streetname) {
        this.streetname = streetname;
    }

    public int getStreetnumber() {
        return streetnumber;
    }

    public void setStreetnumber(int streetnumber) {
        this.streetnumber = streetnumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }
}


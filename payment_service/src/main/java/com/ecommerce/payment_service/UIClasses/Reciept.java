package com.ecommerce.payment_service.UIClasses;

public class Reciept {
    private String Fname;
    private String Lname;
    private String streetname;
    private int streetnumber;
    private String city;
    private String country;
    private String postalcode;
    private String itemname;
    private int itemid;

    private Double totalpaid;
    private int shippingdays;

    public Reciept(String fname, String lname, String streetname, int streetnumber, String city, String country, String postalcode, String itemname, int itemid, Double totalpaid, int shippingdays) {
        Fname = fname;
        Lname = lname;
        this.streetname = streetname;
        this.streetnumber = streetnumber;
        this.city = city;
        this.country = country;
        this.postalcode = postalcode;
        this.itemname = itemname;
        this.itemid = itemid;
        this.totalpaid = totalpaid;
        this.shippingdays = shippingdays;
    }

    public String getFname() {
        return Fname;
    }

    public void setFname(String fname) {
        Fname = fname;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String lname) {
        Lname = lname;
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

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public int getItemid() {
        return itemid;
    }

    public void setItemid(int itemid) {
        this.itemid = itemid;
    }

    public Double getTotalpaid() {
        return totalpaid;
    }

    public void setTotalpaid(Double totalpaid) {
        this.totalpaid = totalpaid;
    }

    public int getShippingdays() {
        return shippingdays;
    }

    public void setShippingdays(int shippingdays) {
        this.shippingdays = shippingdays;
    }
}

package com.ecommerce.payment_service.UIClasses;

public class Winner {
    private String Fname;
    private String Lname;
    private String streetname;
    private int streetnumber;
    private String city;
    private String country;
    private String postalcode;
    private String itemname;
    private int itemid;
    private int winnerid;
    private Double totalshipping;
    private Double total;

    public Winner(){
        this.Fname = "fname";
        this.Lname = "lname";
        this.streetname = "streetname";
        this.streetnumber = 123;
        this.city = "city";
        this.country = "country";
        this.postalcode = "postalcode";
        this.itemname = "itemname";
        this.itemid = 123;
        this.winnerid = 123;
        this.totalshipping = 10.0;
        this.total = 90.0;
    }

    public Winner(int itemid){

        this.Fname = "fname";
        this.Lname = "lname";
        this.streetname = "streetname";
        this.streetnumber = 123;
        this.city = "city";
        this.country = "country";
        this.postalcode = "postalcode";
        this.itemname = "itemname";
        this.itemid = itemid;
        this.winnerid = 123;
        this.totalshipping = 10.0;
        this.total = 90.0;
    }
    public Winner(String fname, String lname, String streetname, int streetnumber, String city, String country, String postalcode, String itemname, int itemid, int winnerid, Double totalshipping, Double total) {
        Fname = fname;
        Lname = lname;
        this.streetname = streetname;
        this.streetnumber = streetnumber;
        this.city = city;
        this.country = country;
        this.postalcode = postalcode;
        this.itemname = itemname;
        this.itemid = itemid;
        this.winnerid = winnerid;
        this.totalshipping = totalshipping;
        this.total = total;
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

    public int getWinnerid() {
        return winnerid;
    }

    public void setWinnerid(int winnerid) {
        this.winnerid = winnerid;
    }

    public Double getTotalshipping() {
        return totalshipping;
    }

    public void setTotalshipping(Double totalshipping) {
        this.totalshipping = totalshipping;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}

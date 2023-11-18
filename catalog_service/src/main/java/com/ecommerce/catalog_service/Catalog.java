package com.ecommerce.catalog_service;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "catalog")
public class Catalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemid;
    @Column(nullable = false)
    private int sellerid;
    @Column(nullable = false)
    private String itemname;
    @Column(nullable = false)
    private String itemdescription;
    @Column(nullable = false)
    private String auctiontype;
    @Column(nullable = false)
    private double startprice;
    @Column(nullable = false)
    private double shippingprice;
    @Column(nullable = false)
    private double expeditedcost;
    @Column(nullable = false)
    private LocalTime duration;
    @Column(nullable = false)
    private LocalDate enddate;

    public Catalog() {
    }

    public Catalog(String itemname, String itemdescription, String auctiontype, double startprice, LocalTime duration) {
        this.itemname = itemname;
        this.itemdescription = itemdescription;
        this.auctiontype = auctiontype;
        this.startprice = startprice;
        this.duration = duration;
    }

    public int getItemid() {
        return itemid;
    }

    public void setItemid(int itemid) {
        this.itemid = itemid;
    }

    public int getSellerid() {
        return sellerid;
    }

    public void setSellerid(int sellerid) {
        this.sellerid = sellerid;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getItemdescription() {
        return itemdescription;
    }

    public void setItemdescription(String itemdescription) {
        this.itemdescription = itemdescription;
    }

    public String getAuctiontype() {
        return auctiontype;
    }

    public void setAuctiontype(String auctiontype) {
        this.auctiontype = auctiontype;
    }

    public double getStartprice() {
        return startprice;
    }

    public void setStartprice(double startprice) {
        this.startprice = startprice;
    }

    public double getShippingprice() {
        return shippingprice;
    }

    public void setShippingprice(double shippingprice) {
        this.shippingprice = shippingprice;
    }

    public double getExpeditedcost() {
        return expeditedcost;
    }

    public void setExpeditedcost(double expeditedcost) {
        this.expeditedcost = expeditedcost;
    }

    public LocalTime getDuration() {
        return duration;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }

    public LocalDate getEnddate() {
        return enddate;
    }

    public void setEnddate(LocalDate enddate) {
        this.enddate = enddate;
    }
}

package com.ecommerce.payment_service.Receipt;

import jakarta.persistence.*;

@Entity
@Table(name = "receipt")
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int receiptid;
    @Column(nullable = false, unique = true)
    private int auctionid;
    @Column(nullable = false)
    private int itemid;
    @Column(nullable = false)
    private String itemname;
    @Column(nullable = false)
    private String itemdescription;
    @Column(nullable = false)
    private int sellerid;
    @Column(nullable = false)
    private int payerid;
    @Column(nullable = false)
    private String auctionstyle;
    @Column(nullable = false)
    private double submittedbid;
    @Column(nullable = false)
    private double shippingprice;
    @Column(nullable = false)
    private double expeditedcost;
    @Column(nullable = false)
    private double defaulttotal;

    public Receipt() {
    }

    public Receipt(int auctionid, int itemid, String itemname, String itemdescription, int sellerid, int payerid, String auctionstyle, double submittedbid, double shippingprice, double expeditedcost, double defaulttotal) {
        this.auctionid = auctionid;
        this.itemid = itemid;
        this.itemname = itemname;
        this.itemdescription = itemdescription;
        this.sellerid = sellerid;
        this.payerid = payerid;
        this.auctionstyle = auctionstyle;
        this.submittedbid = submittedbid;
        this.shippingprice = shippingprice;
        this.expeditedcost = expeditedcost;
        this.defaulttotal = defaulttotal;
    }

    public int getReceiptid() {
        return receiptid;
    }

    public void setReceiptid(int receiptid) {
        this.receiptid = receiptid;
    }

    public int getAuctionid() {
        return auctionid;
    }

    public void setAuctionid(int auctionid) {
        this.auctionid = auctionid;
    }

    public int getItemid() {
        return itemid;
    }

    public void setItemid(int itemid) {
        this.itemid = itemid;
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

    public int getSellerid() {
        return sellerid;
    }

    public void setSellerid(int sellerid) {
        this.sellerid = sellerid;
    }

    public int getPayerid() {
        return payerid;
    }

    public void setPayerid(int payerid) {
        this.payerid = payerid;
    }

    public String getAuctionstyle() {
        return auctionstyle;
    }

    public void setAuctionstyle(String auctionstyle) {
        this.auctionstyle = auctionstyle;
    }

    public double getSubmittedbid() {
        return submittedbid;
    }

    public void setSubmittedbid(double submittedbid) {
        this.submittedbid = submittedbid;
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

    public double getDefaulttotal() {
        return defaulttotal;
    }

    public void setDefaulttotal(double defaulttotal) {
        this.defaulttotal = defaulttotal;
    }
}
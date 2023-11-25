package com.ecommerce.auction_service.Bid;


import jakarta.persistence.*;

@Entity
@Table(name = "bid")
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bidid;
    @Column(nullable = false)
    private int bidderid;
    @Column(nullable = false)
    private double bidprice;
    @Column(nullable = false)
    private int sellerid;
    @Column(nullable = false)
    private int auctionid;

    public Bid() {
    }

    public Bid(int bidid, int bidderid, double bidprice, int sellerid, int auctionid) {
        this.bidid = bidid;
        this.bidderid = bidderid;
        this.bidprice = bidprice;
        this.sellerid = sellerid;
        this.auctionid = auctionid;
    }

    public int getBidid() {
        return bidid;
    }

    public void setBidid(int bidid) {
        this.bidid = bidid;
    }

    public int getBidderid() {
        return bidderid;
    }

    public void setBidderid(int bidderid) {
        this.bidderid = bidderid;
    }

    public double getBidprice() {
        return bidprice;
    }

    public void setBidprice(double bidprice) {
        this.bidprice = bidprice;
    }

    public int getSellerid() {
        return sellerid;
    }

    public void setSellerid(int sellerid) {
        this.sellerid = sellerid;
    }

    public int getAuctionid() {
        return auctionid;
    }

    public void setAuctionid(int auctionid) {
        this.auctionid = auctionid;
    }
}

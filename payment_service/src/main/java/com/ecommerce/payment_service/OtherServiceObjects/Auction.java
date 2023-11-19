package com.ecommerce.payment_service.OtherServiceObjects;

import java.time.LocalDate;
import java.time.LocalTime;

public class Auction {
    private int auctioneditemid;
    private double startprice;
    private String auctiontype;
    private double highestbid;
    private int highestbidderid;
    private LocalDate startdate;
    private LocalTime starttime;
    private LocalTime duration;
    private LocalTime endtime;
    private LocalDate enddate;

    public Auction() {
    }

    public Auction(int auctioneditemid, double startprice, String auctiontype, double highestbid, int highestbidderid, LocalDate startdate, LocalTime starttime, LocalTime duration, LocalTime endtime, LocalDate enddate) {
        this.auctioneditemid = auctioneditemid;
        this.startprice = startprice;
        this.auctiontype = auctiontype;
        this.highestbid = highestbid;
        this.highestbidderid = highestbidderid;
        this.startdate = startdate;
        this.starttime = starttime;
        this.duration = duration;
        this.endtime = endtime;
        this.enddate = enddate;
    }

    public int getAuctioneditemid() {
        return auctioneditemid;
    }

    public void setAuctioneditemid(int auctioneditemid) {
        this.auctioneditemid = auctioneditemid;
    }

    public double getStartprice() {
        return startprice;
    }

    public void setStartprice(double startprice) {
        this.startprice = startprice;
    }

    public String getAuctiontype() {
        return auctiontype;
    }

    public void setAuctiontype(String auctiontype) {
        this.auctiontype = auctiontype;
    }

    public double getHighestbid() {
        return highestbid;
    }

    public void setHighestbid(double highestbid) {
        this.highestbid = highestbid;
    }

    public int getHighestbidderid() {
        return highestbidderid;
    }

    public void setHighestbidderid(int highestbidderid) {
        this.highestbidderid = highestbidderid;
    }

    public LocalDate getStartdate() {
        return startdate;
    }

    public void setStartdate(LocalDate startdate) {
        this.startdate = startdate;
    }

    public LocalTime getStarttime() {
        return starttime;
    }

    public void setStarttime(LocalTime starttime) {
        this.starttime = starttime;
    }

    public LocalTime getDuration() {
        return duration;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }

    public LocalTime getEndtime() {
        return endtime;
    }

    public void setEndtime(LocalTime endtime) {
        this.endtime = endtime;
    }

    public LocalDate getEnddate() {
        return enddate;
    }

    public void setEnddate(LocalDate enddate) {
        this.enddate = enddate;
    }
}
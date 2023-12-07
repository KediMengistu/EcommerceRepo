package com.ecommerce.payment_service.IncomingRequestObjectBodies;

import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

public class PaymentInfo {

    private String username;
    private int paidauctionid;
    private int cardnum;
    private String cardfname;
    private String cardlname;
    private LocalDate expdate;
    private int securitycode;
    private Double totalpaid;

    public PaymentInfo() {
    }

    public PaymentInfo(String username, int paidauctionid, int cardnum, String cardfname, String cardlname, LocalDate expdate, int securitycode, Double totalpaid) {
        this.username = username;
        this.paidauctionid = paidauctionid;
        this.cardnum = cardnum;
        this.cardfname = cardfname;
        this.cardlname = cardlname;
        this.expdate = expdate;
        this.securitycode = securitycode;
        this.totalpaid = totalpaid;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPaidauctionid() {
        return paidauctionid;
    }

    public void setPaidauctionid(int paidauctioni) {
        this.paidauctionid = paidauctioni;
    }

    public int getCardnum() {
        return cardnum;
    }

    public void setCardnum(int cardnum) {
        this.cardnum = cardnum;
    }

    public String getCardfname() {
        return cardfname;
    }

    public void setCardfname(String cardfname) {
        this.cardfname = cardfname;
    }

    public String getCardlname() {
        return cardlname;
    }

    public void setCardlname(String cardlname) {
        this.cardlname = cardlname;
    }

    public LocalDate getExpdate() {
        return expdate;
    }

    public void setExpdate(LocalDate expdate) {
        this.expdate = expdate;
    }

    public int getSecuritycode() {
        return securitycode;
    }

    public void setSecuritycode(int securitycode) {
        this.securitycode = securitycode;
    }

    public Double getTotalpaid() {
        return totalpaid;
    }

    public void setTotalpaid(Double totalpaid) {
        this.totalpaid = totalpaid;
    }
}

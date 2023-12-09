package com.ecommerce.payment_service;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
public class Payment {
    //should change to autogenerated PK in place of using catalog items id as auctions.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentid;
    @Column(nullable = false, unique = true)
    private int receiptid;
    @Column(nullable = false)
    private int usercardnumber;
    @Column(nullable = false)
    private String usercardfname;
    @Column(nullable = false)
    private String usercardlname;
    @Column(nullable = false)
    private LocalDate usercardexpdate;
    @Column(nullable = false)
    private int usercardsecuritycode;
//
//    @Column(nullable = false)
//    private Double totalpaid;

    public Payment() {
    }

    public Payment(int receiptid, int usercardnumber, String usercardfname, String usercardlname, LocalDate usercardexpdate, int usercardsecuritycode, Double totalpaid) {
        this.receiptid = receiptid;
        this.usercardnumber = usercardnumber;
        this.usercardfname = usercardfname;
        this.usercardlname = usercardlname;
        this.usercardexpdate = usercardexpdate;
        this.usercardsecuritycode = usercardsecuritycode;
//        this.totalpaid = totalpaid;
    }

    public int getPaymentid() {
        return paymentid;
    }

    public void setPaymentid(int paymentid) {
        this.paymentid = paymentid;
    }

    public int getReceiptid() {
        return receiptid;
    }

    public void setReceiptid(int receiptid) {
        this.receiptid = receiptid;
    }

    public int getUsercardnumber() {
        return usercardnumber;
    }

    public void setUsercardnumber(int usercardnumber) {
        this.usercardnumber = usercardnumber;
    }

    public String getUsercardfname() {
        return usercardfname;
    }

    public void setUsercardfname(String usercardfname) {
        this.usercardfname = usercardfname;
    }

    public String getUsercardlname() {
        return usercardlname;
    }

    public void setUsercardlname(String usercardlname) {
        this.usercardlname = usercardlname;
    }

    public LocalDate getUsercardexpdate() {
        return usercardexpdate;
    }

    public void setUsercardexpdate(LocalDate usercardexpdate) {
        this.usercardexpdate = usercardexpdate;
    }

    public int getUsercardsecuritycode() {
        return usercardsecuritycode;
    }

    public void setUsercardsecuritycode(int usercardsecuritycode) {
        this.usercardsecuritycode = usercardsecuritycode;
    }

//    public Double getTotalpaid() {
//        return totalpaid;
//    }
//
//    public void setTotalpaid(Double totalpaid) {
//        this.totalpaid = totalpaid;
//    }
}
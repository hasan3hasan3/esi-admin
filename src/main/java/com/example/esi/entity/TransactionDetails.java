package com.example.esi.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
@Entity
@Getter
@Setter

public class TransactionDetails {
    @Id //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增
    private Long id;

    private String transactionStatus;       //:	Transaction Completed Successfully

    private String employersCodeNo;         //:	22001290260001099
    private String employersName;           //:	SENYAR INDIA PRIVATE LIMITED

    private String challanPeriod;           //:	Dec-2021

    private String challanNumber;           // :02222102596926
    private String challanCreatedDate;      //	14-01-2022 21:36:19
    private String challanSubmittedDate;    //	17-01-2022 11:49:28
    @Column(scale = 2)
    private String amountPaid;              //:	67540.00

    private String transactionNumber;       //:	73763195

    @OneToOne(cascade = CascadeType.ALL)
    private Challan challan;
}

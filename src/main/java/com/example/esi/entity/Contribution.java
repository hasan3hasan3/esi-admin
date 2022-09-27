package com.example.esi.entity;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter

public class Contribution {

    @Id //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增
    private Long id;
    private String sINo;
    private String dip;
    private String insuranceNumber;
    private String insuredPerson;
    private String noofDaysWorked;
    //@Column(scale = 2)    private Double totalMonthlyWages;
    //@Column(scale = 2)    private Double ipContribution;
    @Column(scale = 2)    private String totalMonthlyWages;
    @Column(scale = 2)    private String ipContribution;
    private String reason;

    @ManyToOne(cascade = CascadeType.ALL)
    private HistoryTotal historyTotal;
}

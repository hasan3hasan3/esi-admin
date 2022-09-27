package com.example.esi.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Getter
@Setter

public class Challan {

    @Id //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增
    private Long id;
    private String employerCode;//22001290260001099
    private String period;
    private String challanNo;//02222102596926
    private String challanDate;//14/01/2022
    private String realizedDate;//17/01/2022
    private String depositedDate;//17/01/2022
    @Column(scale = 0)
    private String amount;//67540
    private String challanType;//Contribution

    @OneToOne(cascade = CascadeType.ALL)
    private TransactionDetails transactionDetails;

}

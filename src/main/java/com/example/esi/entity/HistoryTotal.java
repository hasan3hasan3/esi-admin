package com.example.esi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter

public class HistoryTotal {

    @Id //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增
    private Long id;
    private String employerCode;

    private String period;

    @Column(scale = 2)
    private String totalIpContribution;
    @Column(scale = 2)
    private String totalEmployerContribution;
    @Column(scale = 2)
    private String totalContribution;
    @Column(scale = 2)
    private String totalGovernmentContribution;
    @Column(scale = 2)
    private String totalMonthlywages;


    private String esiPdfName;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Contribution> contribution;

}

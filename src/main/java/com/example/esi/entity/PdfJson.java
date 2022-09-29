package com.example.esi.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Getter
@Setter

public class PdfJson {
    @Id //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增
    private Long id;

    @Column(length = 500)
    private String pdfName;
    //htmlData
    @Column(columnDefinition = "LONGTEXT")
    private String json;
    //private String utime;
    //private String udate;
    //@OneToOne(cascade = CascadeType.ALL, mappedBy = "pdfJson")
    @OneToOne
    private HistoryTotal historyTotal;

}

package com.example.esi.pojo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class HtmlData {
    Map<Integer, List<PageDate>> pageDate;
    private int totalNumber;
    private int pageCount;

    private String employerCode;
    private String period;

    private TotalData totalData;
    /**
     * 1-18
     * 2-29
     * 3-29
     */

}

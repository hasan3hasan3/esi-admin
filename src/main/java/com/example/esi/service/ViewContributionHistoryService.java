package com.example.esi.service;

import cn.hutool.core.util.StrUtil;
import com.example.esi.control.AdminControl;
import com.example.esi.entity.*;
import com.example.esi.pojo.PageDate;
import com.example.esi.pojo.ViewContributionHistoryData;
import com.example.esi.repository.ChallanRepository;
import com.example.esi.repository.ContributionRepository;
import com.example.esi.repository.HistoryTotalRepository;
import com.example.esi.repository.PdfJsonRepository;
import com.example.esi.util.ExcelOperationHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ViewContributionHistoryService {
    static Logger log = LoggerFactory.getLogger(AdminControl.class);
    static int showLine = 30;
    HistoryTotalRepository historyTotalRepository;
    ContributionRepository contributionRepository;

    ChallanRepository challanRepository;

    PdfJsonRepository pdfJsonRepository;

    @Autowired
    public void setPdfJsonRepository(PdfJsonRepository pdfJsonRepository) {
        this.pdfJsonRepository = pdfJsonRepository;
    }

    @Autowired
    public void setChallanRepository(ChallanRepository challanRepository) {
        this.challanRepository = challanRepository;
    }

    @Autowired
    public void setContributionRepository(ContributionRepository contributionRepository) {
        this.contributionRepository = contributionRepository;
    }

    @Autowired
    public void setHistoryTotalRepository(HistoryTotalRepository historyTotalRepository) {
        this.historyTotalRepository = historyTotalRepository;
    }

    public static void main(String[] args) {

        String strMMM = StrUtil.sub("2022Sep", 0, 4);
        String stryyyy = StrUtil.sub("2022Sep", 4, 7);
        //log.info("{}", strMMM);
        //log.info("{}", stryyyy);

    }

    //@Transactional
    public ViewContributionHistoryData getViewContributionHistory(String period, int p, String employerCode) {

        //log.info("1111{}",period);
        if (StrUtil.isEmpty(period)) {
            period = "Sep2022";
        }
        String stryyyy = StrUtil.sub(period, 0, 4);
        String strMMM = StrUtil.sub(period, 4, 7);
        //log.info("ViewContributionHistoryData{}",strMMM+stryyyy);

        HistoryTotal historyTotal = historyTotalRepository.findByPdfViewDateAndEmployerCode(strMMM + stryyyy, employerCode);
        //todo , Sort.by("id").descending()
        Page<Contribution> contribution = contributionRepository.findByHistoryTotal(historyTotal, PageRequest.of(p - 1, showLine));
        //model.addAttribute("data", recentEcrPage);
        //log.info("historyTotal{}", historyTotal.getId());
        // log.info("contribution{}", contribution.getContent().get(0).getInsuranceNumber());
        ViewContributionHistoryData viewContributionHistoryData = new ViewContributionHistoryData();
        viewContributionHistoryData.setContribution(contribution);
        viewContributionHistoryData.setHistoryTotal(historyTotal);
        return viewContributionHistoryData;
    }


    public HistoryTotal getHistoryTotal(Long id) {
        HistoryTotal historyTotal = historyTotalRepository.findById(id).get();
        return historyTotal;
    }

    public HistoryTotal findByPeriodAndEmployerCode(String period, String employerCode) {
        HistoryTotal historyTotal = historyTotalRepository.findByPeriodAndEmployerCode(period, employerCode);
        return historyTotal;
    }

    /**
     * 5条最近上传的
     *
     * @return
     */
    public Page<HistoryTotal> findNewUpload(int p) {
        Page<HistoryTotal> historyTotal = historyTotalRepository.findAll(PageRequest.of(p - 1, 20, Sort.by("id").descending()));
        return historyTotal;
    }
    @Transactional
    public HistoryTotal saveHistoryTotal(HistoryTotal historyTotal) {
        return historyTotalRepository.saveAndFlush(historyTotal);
    }

    //@Modifying
    @Transactional
    public void delHistoryTotal(Long id) {
        historyTotalRepository.deleteById(id);
        //HistoryTotal historyTotal = historyTotalRepository.findById(id).get();
        //historyTotalRepository.delete(historyTotal);
    }

    /**
     * 通过上传的excel文件，保存到数据库（按每月一份）；
     *
     * @param
     * @param
     * @param period
     * @return
     */
    //todo
    @Transactional
    public HistoryTotal excel2db(String jsonStr, HistoryTotal historyTotal, Map map, String period, String pdfViewDate, String employerCode) {


        //HistoryTotal historyTotal = new HistoryTotal();

        historyTotal.setPeriod(period);
        historyTotal.setPdfViewDate(pdfViewDate);
        historyTotal.setEmployerCode(employerCode);

        List<Contribution> contributionList = new ArrayList<Contribution>();

        //map 中取出所有list。 再组装entity
        for (Object key : map.keySet()) {
            for (PageDate pageDate : (List<PageDate>) map.get(key)) {
                Contribution contribution = new Contribution();
                //Contribution.setDip("-");

                contribution.setInsuranceNumber(pageDate.getNumber());
                contribution.setInsuredPerson(pageDate.getName());
                contribution.setNoofDaysWorked(pageDate.getDays());
                //log.info("1111{}",pageDate.getWages());
                //log.info("2222{}",Double.valueOf(pageDate.getWages()));

                //log.info("3333{}",contribution.getTotalMonthlyWages());
                contribution.setTotalMonthlyWages(pageDate.getWages());
                //log.info("4444{}",contribution.getTotalMonthlyWages());

                contribution.setIpContribution(pageDate.getContribution());
                contribution.setHistoryTotal(historyTotal);
                contributionList.add(contribution);

            }
        }
        historyTotal.setContribution(contributionList);

        //log.info("{},{}", historyTotal, historyTotal.getTotalContribution());
        historyTotalRepository.save(historyTotal);

        Challan challan = new Challan();
        challan.setPeriod(historyTotal.getPeriod());
        challan.setEmployerCode(historyTotal.getEmployerCode());
        String challanNo2Number = ExcelOperationHelp.getChallanNo();
        challan.setChallanNo(challanNo2Number);

        TransactionDetails transactionDetails = new TransactionDetails();
        //transactionDetails.setChallan(challan);
        transactionDetails.setEmployersCodeNo(historyTotal.getEmployerCode());
        transactionDetails.setChallanNumber(challanNo2Number);
        transactionDetails.setTransactionNumber(ExcelOperationHelp.getTransactionNumber());

        challan.setTransactionDetails(transactionDetails);

        challanRepository.save(challan);
        //ExcelUtil.readBySax(file.getInputStream(), 0, createRowHandler());

        PdfJson pdfJson = new PdfJson();
        //xls(htmlData,response);
        pdfJson.setJson(jsonStr);
        pdfJson.setHistoryTotal(historyTotal);
        pdfJsonRepository.save(pdfJson);
        historyTotal.setPdfJson(pdfJson);
        historyTotal.setChallan(challan);

        return historyTotal;
    }
}

package com.example.esi.service;

import com.example.esi.control.AdminControl;
import com.example.esi.entity.Contribution;
import com.example.esi.entity.HistoryTotal;
import com.example.esi.pojo.ViewContributionHistoryData;
import com.example.esi.repository.ContributionRepository;
import com.example.esi.repository.HistoryTotalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class ViewContributionHistoryService {
    static Logger log = LoggerFactory.getLogger(AdminControl.class);
    static int showLine = 30;
    HistoryTotalRepository historyTotalRepository;
    ContributionRepository contributionRepository;

    @Autowired
    public void setContributionRepository(ContributionRepository contributionRepository) {
        this.contributionRepository = contributionRepository;
    }

    @Autowired
    public void setHistoryTotalRepository(HistoryTotalRepository historyTotalRepository) {
        this.historyTotalRepository = historyTotalRepository;
    }

    //@Transactional
    public ViewContributionHistoryData getViewContributionHistory(String period, int p, String employerCode) {


        HistoryTotal historyTotal = historyTotalRepository.findByPeriodAndEmployerCode(period, employerCode);
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
        Page<HistoryTotal> historyTotal = historyTotalRepository.findAll(PageRequest.of(p-1, 20, Sort.by("id").descending()));
        return historyTotal;
    }

    public HistoryTotal saveHistoryTotal(HistoryTotal historyTotal) {
        return historyTotalRepository.saveAndFlush(historyTotal);
    }

    public void delHistoryTotal(Long id) {
        historyTotalRepository.deleteById(id);
    }

}

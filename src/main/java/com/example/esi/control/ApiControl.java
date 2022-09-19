package com.example.esi.control;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.esi.entity.Challan;
import com.example.esi.entity.HistoryTotal;
import com.example.esi.entity.TransactionDetails;
import com.example.esi.pojo.ViewContributionHistoryData;
import com.example.esi.repository.ChallanRepository;
import com.example.esi.repository.HistoryTotalRepository;
import com.example.esi.repository.TransactionDetailsRepository;
import com.example.esi.service.ViewContributionHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Controller
public class ApiControl {
    static Logger log = LoggerFactory.getLogger(ApiControl.class);

    public static void main(String[] args) {
        //Last Logged In Tuesday, September 06, 2022 at 12:37 PM
        SimpleDateFormat sdf = new SimpleDateFormat("'Last Logged In' EEEE, MMMM dd, yyyy 'at' HH:mm aaa", Locale.ENGLISH);
         //log.info("{}", sdf.format(new Date()));

         //log.info("{}", "06722117347790".length());
    }

    @RequestMapping("/esi-login")
    @ResponseBody
    public String pfLogin(HttpServletRequest request) {
        String user = request.getParameter("user");
        //String pwd = request.getParameter("pwd");
        if (StrUtil.isEmpty(user)) {
            return "fail";
        }
        HttpSession mySession = request.getSession();
        //Last Logged In Tuesday, September 06, 2022 at 12:37 PM
        SimpleDateFormat sdf = new SimpleDateFormat("'Last Logged In' EEEE, MMMM dd, yyyy 'at' HH:mm aaa", Locale.ENGLISH);
         //log.info("{}", sdf.format(new Date()));
        mySession.setAttribute("epfoNow", sdf.format(new Date()));
        if (user.indexOf("67") > -1) {
            user = "67000909350001099";
        }
        if (user.indexOf("22") > -1) {
            user = "22001290260001099";
        } else {
            user = "67000909350001099";
        }
        mySession.setAttribute("userName", user);
        mySession.setAttribute("employerCode", user);
        //mySession.setAttribute("UUID", IdUtil.simpleUUID().toUpperCase());
        //mySession.setAttribute("lin", "1867777921");
        return "success";
    }

    ViewContributionHistoryService viewContributionHistoryService;

    @Autowired
    public void setViewContributionHistoryService(ViewContributionHistoryService viewContributionHistoryService) {
        this.viewContributionHistoryService = viewContributionHistoryService;
    }

    /**
     * @param period
     * @param employerCode
     * @param p
     * @return
     */
    @RequestMapping("/getContributionHistory")
    @ResponseBody
    public ViewContributionHistoryData viewHistoryJson(
            @RequestParam(value = "period", required = true) String period,
            @RequestParam(value = "employerCode", required = true, defaultValue = "67000909350001099") String employerCode,
            @RequestParam(value = "p", required = false, defaultValue = "1") int p) {
        ViewContributionHistoryData viewContributionHistoryData = viewContributionHistoryService.getViewContributionHistory(period, p, employerCode);
        //JSONObject json2 = JSONUtil.parseObj(viewContributionHistoryData.getContribution());
        //JSONObject json1 = JSONUtil.parseObj(viewContributionHistoryData.getHistoryTotal());
        //

        return viewContributionHistoryData;
    }

    TransactionDetailsRepository transactionDetailsRepository;

    @Autowired
    public void setTransactionDetailsRepository(TransactionDetailsRepository transactionDetailsRepository) {
        this.transactionDetailsRepository = transactionDetailsRepository;
    }

    ChallanRepository challanRepository;

    @Autowired
    public void setChallanRepository(ChallanRepository challanRepository) {
        this.challanRepository = challanRepository;
    }

    /**
     * @param model
     * @param employerCode
     * @param period
     * @return
     */
    @RequestMapping("/getModifyChallan")
    @ResponseBody
    public Challan ModifyChallan(Model model,
                                 @RequestParam(value = "employerCode", required = true, defaultValue = "67000909350001099") String employerCode,
                                 @RequestParam(value = "period", required = true) String period) {

        //log.info("getModifyChallan------");
        Challan challan = challanRepository.findByPeriodAndEmployerCode(period, employerCode);
        //log.info("getModifyChallan------",challan);

        return challan;
    }

    /**
     * @param model
     * @param employerCode
     * @param challanNo
     * @return
     */
    @RequestMapping("/getOnlineChallanStatus")
    @ResponseBody
    public TransactionDetails onlineChallanStatus(Model model,
                                                  @RequestParam(value = "employerCode", required = true, defaultValue = "67000909350001099") String employerCode,
                                                  @RequestParam(value = "challanNo", required = true, defaultValue = "06722119367501") String challanNo) {

         //log.info("getOnlineChallanStatus1111{}, {}", challanNo, employerCode);

        TransactionDetails transactionDetails = transactionDetailsRepository.findByChallanNumberAndEmployersCodeNo(challanNo, employerCode);
         //log.info("getOnlineChallanStatus{},{},{}", transactionDetails, challanNo, employerCode);

        return transactionDetails;
    }

}

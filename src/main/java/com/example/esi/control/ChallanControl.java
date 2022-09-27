package com.example.esi.control;

import com.example.esi.entity.Challan;
import com.example.esi.entity.TransactionDetails;
import com.example.esi.repository.ChallanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class ChallanControl {
    static Logger log = LoggerFactory.getLogger(AdminControl.class);

    ChallanRepository challanRepository;

    @Autowired
    public void setChallanRepository(ChallanRepository challanRepository) {
        this.challanRepository = challanRepository;
    }


    @RequestMapping("/op2challan")
    public String op2challan(Model model,
                             @RequestParam(value = "period", required = true, defaultValue = "2022Jan") String period,
                             @RequestParam(value = "employerCode", required = true, defaultValue = "67000909350001099") String employerCode) {

        model.addAttribute("period", period);
        model.addAttribute("employerCode", employerCode);
        Challan challan = challanRepository.findByPeriodAndEmployerCode(period, employerCode);
         //log.info("{}", challan);
        model.addAttribute("challan", challan);
        return "operation-chall";
    }


    @RequestMapping("/s2challan")
    public String s2challan(Model model,
                            @RequestParam(value = "challanId", required = false, defaultValue = "1") Long challanId,
                            @RequestParam(value = "period", required = true, defaultValue = "2022Jan") String period,
                            @RequestParam(value = "employerCode", required = true, defaultValue = "67000909350001099") String employerCode) {

        model.addAttribute("period", period);
        model.addAttribute("employerCode", employerCode);

        if (challanId.intValue() > 0) {
            Challan challan = challanRepository.findById(challanId).get();
            model.addAttribute("challan", challan);
             //log.info("-----challan:{}", challan);
        }

        return "save-challan";
    }



    /**
     *
     * @param challan
     * @param transactionDetails
     * @param period
     * @param employerCode
     * @return
     */
    //todo 入参去除左右空格。
    @RequestMapping("/saveChallan")
    public String saveChallan(Challan challan,
                              TransactionDetails transactionDetails,
                              @RequestParam(value = "period", required = true, defaultValue = "2022Jan") String period,
                              @RequestParam(value = "employerCode", required = true, defaultValue = "67000909350001099") String employerCode) {

        //06722120591515	02222102596926
        //challan.setChallanNo("02222102596926");
        //challan.setChallanDate("16/06/2022");
        //challan.setRealizedDate("17/06/2022");
        //challan.setDepositedDate("17/06/2022");
        //challan.setAmount(145520d);
        //challan.setChallanType("Contribution");
        //
        //transactionDetails.setTransactionStatus("Transaction Completed Successfully");
        //transactionDetails.setEmployersCodeNo("67000909350001099");
        //transactionDetails.setEmployersName("PRIMEWINGS SERVICE PRIVATE LIMITED");
        //transactionDetails.setChallanPeriod(period);
        //transactionDetails.setChallanNumber("02222102596926");
        //transactionDetails.setChallanCreatedDate("16-06-2022 21:54:04");
        //transactionDetails.setChallanSubmittedDate("17-06-2022 10:10:19");
        //transactionDetails.setAmountPaid(145520.00);
        //transactionDetails.setTransactionNumber("81852503");
         //log.info("transactionDetails{}", transactionDetails);
         //log.info("challan{}", challan);

        if (challan != null && transactionDetails != null) {
            //transactionDetails.setChallan(challan);
            challan.setTransactionDetails(transactionDetails);
            //log.info(transactionDetails.getAmountPaid());
            challanRepository.save(challan);
        }


        return "redirect:/op2challan?employerCode="+employerCode+"&period="+period;
    }

}

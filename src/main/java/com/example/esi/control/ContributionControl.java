package com.example.esi.control;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.example.esi.entity.HistoryTotal;
import com.example.esi.entity.PdfJson;
import com.example.esi.pojo.ViewContributionHistoryData;
import com.example.esi.repository.PdfJsonRepository;
import com.example.esi.service.ViewContributionHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
public class ContributionControl {
    //todo
    //根据时间 ，查询列表。
    static Logger log = LoggerFactory.getLogger(AdminControl.class);

    ViewContributionHistoryService viewContributionHistoryService;

    @Autowired
    public void setViewContributionHistoryService(ViewContributionHistoryService viewContributionHistoryService) {
        this.viewContributionHistoryService = viewContributionHistoryService;
    }

    @RequestMapping("/op2contribution")
    public String op2contribution(Model model,
                                  @RequestParam(value = "period", required = false) String period,
                                  @RequestParam(value = "employerCode", required = true, defaultValue = "67000909350001099") String employerCode,
                                  @RequestParam(value = "p", required = false, defaultValue = "1") int p) {
        model.addAttribute("p", p);
        model.addAttribute("period", period);
        model.addAttribute("employerCode", employerCode);

        ViewContributionHistoryData viewContributionHistoryData = viewContributionHistoryService.getViewContributionHistory(period, p, employerCode);
        if (viewContributionHistoryData != null) {
            model.addAttribute("dataH", viewContributionHistoryData.getHistoryTotal());
            model.addAttribute("data", viewContributionHistoryData.getContribution());
        }
        return "operation-contribution";
    }

    @RequestMapping("/del")
    public String del(Long id) throws IOException {
        viewContributionHistoryService.delHistoryTotal(id);
        return "redirect:/op2contribution";
    }


    @PostMapping("/getEsihistoryTotalRepository")
    @ResponseBody
    public String getEsihistoryTotalRepository(String employerCode, String period) {
        HistoryTotal historyTotal = viewContributionHistoryService.findByPeriodAndEmployerCode(period, employerCode);

        //log.info("{}", historyTotal);
        if (historyTotal != null) {
            return "fail";
        }
        return "success";
    }

    @RequestMapping("/viewpdf")
    public String viewpdf() {
        return "viewpdf";
    }

    /**
     * 最新上传的5条
     *
     * @return
     */
    @GetMapping("/findNewUpload")
    @ResponseBody
    public Page<HistoryTotal> getNewUpload(@RequestParam(value = "p", required = false, defaultValue = "1") int p) {
        return viewContributionHistoryService.findNewUpload(p);
    }

    @PostMapping("/uploadEsiPdf")
    @ResponseBody
    public String uploadEsiPdf(HttpServletRequest request) {
        log.info("-----id:{}", request.getParameter("id"));
        List<MultipartFile> fileList = ((MultipartHttpServletRequest) request).getFiles("file");
        log.info("-----fileList0:{}", fileList);
        fileList.stream().filter(file -> !ObjectUtils.isEmpty(file)).forEach(filePdf -> uploadFile(filePdf, request.getParameter("id")));
        return "success";
    }

    @Value("${file.uploadFolder}")
    private String uploadFolder;
    PdfJsonRepository pdfJsonRepository;

    @Autowired
    public void setPdfJsonRepository(PdfJsonRepository pdfJsonRepository) {
        this.pdfJsonRepository = pdfJsonRepository;
    }

    private void uploadFile(MultipartFile file, String id) {

        String filename = file.getOriginalFilename();
        BufferedOutputStream out = FileUtil.getOutputStream(uploadFolder + filename);
        try {
            long copySize = IoUtil.copy(file.getInputStream(), out, IoUtil.DEFAULT_BUFFER_SIZE);
            if (copySize > 0 && !StrUtil.isEmpty(id)) {
                HistoryTotal historyTotal = viewContributionHistoryService.getHistoryTotal(Long.parseLong(id));
                historyTotal.setEsiPdfName(filename);
                //todo
                //这里上传pdf 的时候，自动产生excel的名字，在本地c:\\esixls中创建相同名字的xls
                Date date = DateUtil.date();
                String format = DateUtil.format(date, "ssmmHHddMMyyyy");
                historyTotal.setEsiExcelName(format + ".xls");
                viewContributionHistoryService.saveHistoryTotal(historyTotal);

                PdfJson pdfJson = pdfJsonRepository.findById(Long.parseLong(id)).get();
                pdfJson.setPdfName(filename);
                pdfJsonRepository.save(pdfJson);
            }
            //log.info("copySize:{}", copySize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Date date = DateUtil.date();
        String format = DateUtil.format(date, "ssmmHHddMMyyyy");
        System.out.println(format);
    }
}

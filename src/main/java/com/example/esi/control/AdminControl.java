package com.example.esi.control;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileReader;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

import com.example.esi.entity.*;
import com.example.esi.pojo.HtmlData;
import com.example.esi.pojo.PageDate;
import com.example.esi.pojo.TotalData;
import com.example.esi.service.ViewContributionHistoryService;
import com.example.esi.util.ExcelOperationHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class AdminControl {
    static Logger log = LoggerFactory.getLogger(AdminControl.class);

    ViewContributionHistoryService viewContributionHistoryService;

    @Autowired
    public void setViewContributionHistoryService(ViewContributionHistoryService viewContributionHistoryService) {
        this.viewContributionHistoryService = viewContributionHistoryService;
    }

    @RequestMapping("")
    public String index() {
        return "login-esi";
    }

    @RequestMapping("pdf")
    public String pdf() {
        return "pdf";
    }

    @RequestMapping("/e2xsl")
    public String e2txt() {
        return "e2xsl";
    }

    @RequestMapping("/e2pdf")
    public String e2pdf() {
        return "e2pdf";
    }

    @RequestMapping("/admin-login")
    public String adminLogin(String user, String pwd, HttpServletRequest request) {

        HttpSession mySession = request.getSession();
        mySession.setAttribute("userName", user);
        SimpleDateFormat sdf = new SimpleDateFormat("EE dd MMM yyyy", Locale.UK);
        mySession.setAttribute("epfoNow", sdf.format(new Date()));
        if (!pwd.equals("xq")) {
            return "/login-esi";
        }
        return "redirect:/op2contribution";
    }

    @RequestMapping("json")
    public String json(Model model) {
        //默认UTF-8编码，可以在构造中传入第二个参数做为编码
        FileReader fileReader = new FileReader("static/json.data");
        String result = fileReader.readString();
        JSONObject json = JSONUtil.parseObj(result);
        model.addAttribute("htmlData", json);
        return "json";
    }

    /**
     * 上传的excel，转换成带格式的 excel（带excel表头）；
     *
     * @param file
     * @param response
     * @throws IOException
     */
    @PostMapping("/excel2xsl")
    public void xls(MultipartFile file, HttpServletResponse response) throws IOException {
        List listCol = new ArrayList<>();
        double ipContribution = 0, employerContribution = 0, contribution = 0, monthlyWages = 0;
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        List<List<Object>> readAll = reader.read();

        for (int i = 1; i < readAll.size(); i++) {

            List list = readAll.get(i);

            Object obj0 = list.get(0);
            Object obj1 = list.get(1);
            Object obj2 = list.get(2);
            Object obj3 = list.get(3);
            //Object obj4 = list.get(4);

            //  total wage
            Double d2 = Double.valueOf(String.valueOf(obj2));
            Double d4 = ExcelOperationHelp.getRoundup(d2 * 0.0075, 0);
            String d2format = NumberUtil.decimalFormat("0.00", d2);
            String d4format = NumberUtil.decimalFormat("0.00", d4);


            //employerContribution += d2 * 0.0325;
            ipContribution += d4;

            monthlyWages += d2;

            listCol.add(CollUtil.newArrayList(i, '-', obj0, String.valueOf(obj1).toUpperCase(), obj3, d2format, d4format, '-'));

        }
        TotalData totalData = new TotalData();
        employerContribution = ExcelOperationHelp.getRoundup(monthlyWages * 0.0325, 0);

        contribution = ipContribution + employerContribution;

        totalData.setIpContribution(ExcelOperationHelp.lakhFormattedComma(ipContribution));
        totalData.setEmployerContribution(ExcelOperationHelp.lakhFormattedComma(employerContribution));
        totalData.setContribution(ExcelOperationHelp.lakhFormattedComma(contribution));
        totalData.setMonthlyWages(ExcelOperationHelp.lakhFormattedComma(monthlyWages));
        totalData.setGovernmentContribution("0.00");


        // 通过工具类创建writer，默认创建xls格式
        ExcelWriter writer = ExcelUtil.getWriter();
        //todo 表头应该是一行
        List<String> row1 = CollUtil.newArrayList("Employees' State Insurance Corporation", "Contribution History Of 22001290260001099 for Jan2022");
        writer.writeRow(row1);

        // 一次性写出内容，使用默认样式，强制输出标题
        writer.addHeaderAlias("ipContribution", "Total IP Contribution");
        writer.addHeaderAlias("employerContribution", "Total Employer Contribution");
        writer.addHeaderAlias("contribution", "Total Contribution");
        writer.addHeaderAlias("governmentContribution", "Total Government Contribution");
        writer.addHeaderAlias("monthlyWages", "Total Monthly Wages");
        List<TotalData> row2 = CollUtil.newArrayList(totalData);
        writer.write(row2, true);

        writer.passCurrentRow();
        writer.passCurrentRow();

        //List<List> rows = CollUtil.newArrayList(row1, row2);
        //todo
        writer.write(listCol);

        // 11:52:25 AM	Page -1 of 1	Printed On:	2022/9/22
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss aaa", Locale.ENGLISH);
        //System.out.printf(sdf.format(new Date()));
        String today= DateUtil.today();
        List<String> rowe = CollUtil.newArrayList(sdf.format(new Date()), "Page -1 of 1", "Printed On", today);
        writer.writeRow(rowe);

        //out为OutputStream，需要写出到的目标流

        //response为HttpServletResponse对象
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=output.xls");
        ServletOutputStream out = response.getOutputStream();

        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss aaa", Locale.ENGLISH);
        System.out.printf(sdf.format(new Date()));
    }

    /**
     * 上传的excel，转换成json；
     * 提供给html，待转换-pdf；
     *
     * @param model
     * @param file
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/excel2pdf")
    public String excel2pdf(Model model, MultipartFile file, HttpServletRequest request) throws IOException {
        if (file.isEmpty()) {
            return "fail";
        }

        HtmlData htmlData = new HtmlData();
        TotalData totalData = new TotalData();
        Map map = new HashMap<Integer, List<PageDate>>();
        List<PageDate> pageDateList = new ArrayList<>();
        int line = 29, page = 1, rowCount = 1, maxStrLen = 21;
        double ipContribution = 0, employerContribution = 0, contribution = 0, monthlyWages = 0;
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        List<List<Object>> readAll = reader.read();
        for (int i = 1; i < readAll.size(); i++) {

            List list = readAll.get(i);

            Object obj0 = list.get(0);
            Object obj1 = list.get(1);
            String str1 = String.valueOf(obj1).trim();

            Object obj2 = list.get(2);
            Object obj3 = list.get(3);

            //  total wage
            Double d2 = Double.valueOf(String.valueOf(obj2));
            Double d4 = ExcelOperationHelp.getRoundup(d2 * 0.0075, 0);

            String d2format = NumberUtil.decimalFormat("0.00", d2);
            String d4format = NumberUtil.decimalFormat("0.00", d4);


            PageDate pageDate = new PageDate();
            pageDate.setSNo(i);
            pageDate.setIsDisable("");
            pageDate.setNumber(String.valueOf(obj0));
            if (str1.length() >= maxStrLen) {
                str1 = ExcelOperationHelp.strTr2S(str1);
                //line = line - 1;
                rowCount = rowCount + 2;
                pageDate.setName(str1.trim());
            } else {
                pageDate.setName(String.valueOf(obj1).toUpperCase().trim());
            }
            pageDate.setDays(String.valueOf(obj3));
            pageDate.setWages(d2format);

            monthlyWages += d2;
            //employerContribution += d2 * 0.0325;
            ipContribution += d4;

            pageDate.setContribution(d4format);

            pageDate.setReason("");
            pageDateList.add(pageDate);
            rowCount = rowCount + 1;
            //如果字符超过21，最后空格换成回车，该页的表格行数为29-1=28行。

            if (page == 1 && i == 18) {
                map.put(page, pageDateList);
                pageDateList = new ArrayList<>();
                page = page + 1;
                rowCount = 0;
                //line =29;
            }
            if (page > 1 && rowCount >= line) {
                ////log.info("1111page{},rowCount{},line{}", page, rowCount, line);
                map.put(page, pageDateList);
                pageDateList = new ArrayList<>();
                page = page + 1;
                rowCount = 0;
                //line = 29;
                //todo add line =29,每页从29行开始，待测试
            }
        }
        employerContribution = ExcelOperationHelp.getRoundup(monthlyWages * 0.0325, 0);
        contribution = ipContribution + employerContribution;
        map.put(page, pageDateList);
        totalData.setIpContribution(ExcelOperationHelp.lakhFormattedComma(ipContribution));
        totalData.setEmployerContribution(ExcelOperationHelp.lakhFormattedComma(employerContribution));
        totalData.setContribution(ExcelOperationHelp.lakhFormattedComma(contribution));
        totalData.setMonthlyWages(ExcelOperationHelp.lakhFormattedComma(monthlyWages));
        totalData.setGovernmentContribution("0.00");

        htmlData.setTotalData(totalData);
        ////log.info("totalData:{}", totalData);

        htmlData.setPageCount(page);
        htmlData.setTotalNumber(readAll.size());
        htmlData.setPageDate(map);

        String period = request.getParameter("period");
        String pdfViewDate = request.getParameter("pdfViewDate");
        if (StrUtil.isEmpty(period)) {
            period = ExcelOperationHelp.getPeriodUK();
        }
        String employerCode = request.getParameter("employerCode");
        if (StrUtil.isEmpty(employerCode)) {
            employerCode = "67000909350001099";
        }
        htmlData.setPeriod(period);
        htmlData.setEmployerCode(employerCode);
        htmlData.setPdfViewDate(pdfViewDate);

        JSONObject json = JSONUtil.parseObj(htmlData);
        String jsonStr = JSONUtil.toJsonPrettyStr(htmlData);
        model.addAttribute("htmlData", jsonStr);
        //log.info("json:{}", json);
        //todo
        HistoryTotal historyTotal = new HistoryTotal();
        historyTotal.setTotalIpContribution(NumberUtil.decimalFormat("0.00", ipContribution));
        historyTotal.setTotalEmployerContribution(NumberUtil.decimalFormat("0.00", NumberUtil.round(employerContribution, 0).doubleValue()));
        historyTotal.setTotalContribution(NumberUtil.decimalFormat("0.00", contribution));
        historyTotal.setTotalMonthlywages(NumberUtil.decimalFormat("0.00", monthlyWages));
        historyTotal.setTotalGovernmentContribution("0.00");

        viewContributionHistoryService.excel2db(jsonStr, historyTotal, map, period, pdfViewDate, employerCode);
        //组装新的实体对象，保存到数据库。

        return "pdf";
    }


}

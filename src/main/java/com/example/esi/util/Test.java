package com.example.esi.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import cn.hutool.poi.excel.cell.CellSetter;
import com.example.esi.control.AdminControl;
import com.example.esi.pojo.TotalData;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Test {
    public static void xls() throws IOException {
        List listCol = new ArrayList<>();
        double ipContribution = 0, employerContribution = 0, contribution = 0, monthlyWages = 0;
        ExcelReader reader = ExcelUtil.getReader("/Users/apple/hasan/1September2022ESIC.xls");
        List<List<Object>> readAll = reader.read();

        for (int i = 1; i < readAll.size(); i++) {

            List list = readAll.get(i);

            Object obj0 = list.get(0);
            Object obj1 = list.get(1);
            Object obj2 = list.get(3);
            Object obj3 = list.get(2);
            //Object obj4 = list.get(4);

            //  total wage
            Double d2 = Double.valueOf(String.valueOf(obj2));
            Double d4 = ExcelOperationHelp.getRoundup(d2 * 0.0075, 0);
            String d2format = NumberUtil.decimalFormat("0.00", d2);
            String d4format = NumberUtil.decimalFormat("0.00", d4);


            //employerContribution += d2 * 0.0325;
            ipContribution += d4;

            monthlyWages += d2;

            String endRowStr = "-";
            if (Double.valueOf(obj3.toString()) == 0) {
                endRowStr = "On Leave";
            }
            listCol.add(CollUtil.newArrayList(i + "", '-', String.valueOf(obj0), obj1.toString().toUpperCase(), String.valueOf(obj3), d2format, d4format, endRowStr));

        }

        //reader.close();

        employerContribution = ExcelOperationHelp.getRoundup(monthlyWages * 0.0325, 0);

        contribution = ipContribution + employerContribution;


        // 通过工具类创建writer，默认创建xls格式
        ExcelWriter writer = ExcelUtil.getWriter("/Users/apple/hasan/345.xlsx");
        writer.getCellStyle().setAlignment(HorizontalAlignment.LEFT);
        writer.getHeadCellStyle().setAlignment(HorizontalAlignment.LEFT);

        Font font = writer.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        //writer.disableDefaultStyle();

        writer.getStyleSet().setFont(font, false);

        StyleSet style = writer.getStyleSet();
        CellStyle cellStyle = style.getHeadCellStyle();
        //cellStyle.setFillBackgroundColor((short)0);
        cellStyle.setBorderBottom(BorderStyle.NONE);
        cellStyle.setBorderTop(BorderStyle.NONE);
        cellStyle.setBorderRight(BorderStyle.NONE);
        cellStyle.setBorderLeft(BorderStyle.NONE);

        //style.setBackgroundColor(IndexedColors.AUTOMATIC, true);
        //style.getCellStyle().setShrinkToFit(true);
        style.getCellStyle().setBorderBottom(BorderStyle.NONE);
        style.getCellStyle().setBorderTop(BorderStyle.NONE);
        style.getCellStyle().setBorderRight(BorderStyle.NONE);
        style.getCellStyle().setBorderLeft(BorderStyle.NONE);

        //todo 表头应该是一行
        List<String> row1 = CollUtil.newArrayList("Employees' State Insurance Corporation", "Contribution History Of 22001290260001099 for Jan2022");
        writer.writeRow(row1);

        // 一次性写出内容，使用默认样式，强制输出标题
        List<String> row2 = CollUtil.newArrayList("Total IP Contribution", "Total Employer Contribution", "Total Contribution", "Total Government Contribution", "Total Monthly Wages");
        writer.writeRow(row2);
//
//        TotalData totalData = new TotalData();
//        totalData.setIpContribution(ExcelOperationHelp.lakhFormattedComma(ipContribution));
//        totalData.setEmployerContribution(ExcelOperationHelp.lakhFormattedComma(employerContribution));
//        totalData.setContribution(ExcelOperationHelp.lakhFormattedComma(contribution));
//        totalData.setMonthlyWages(ExcelOperationHelp.lakhFormattedComma(monthlyWages));
//        totalData.setGovernmentContribution("0.00");
        List<String> row3 = CollUtil.newArrayList(ExcelOperationHelp.lakhFormattedComma(ipContribution),
                ExcelOperationHelp.lakhFormattedComma(employerContribution),
                ExcelOperationHelp.lakhFormattedComma(contribution),
                ExcelOperationHelp.lakhFormattedComma(monthlyWages), "0.00"
        );
//        writer.createRowStyle(3).setAlignment(HorizontalAlignment.RIGHT);


        //writer.writeCellValue(0, 2, sdf.format(new Date()));
        //writer.getOrCreateRowStyle(2).setAlignment(HorizontalAlignment.RIGHT);
        writer.writeRow(row3);



        writer.passCurrentRow();
        writer.passCurrentRow();

        //List<List> rows = CollUtil.newArrayList(row1, row2);
        //todo
        writer.write(listCol);

        // 11:52:25 AM	Page -1 of 1	Printed On:	2022/9/22
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH);
        //System.out.printf(sdf.format(new Date()));
        SimpleDateFormat sdfD = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        List<String> rowe = CollUtil.newArrayList(sdf.format(new Date()), "Page -1 of 1", "Printed On", sdfD.format(new Date()));
        writer.writeRow(rowe);
        //out为OutputStream，需要写出到的目标流

        //response为HttpServletResponse对象
//        response.setContentType("application/vnd.ms-excel;charset=utf-8");
//        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
//        response.setHeader("Content-Disposition", "attachment;filename=output.xls");
//        ServletOutputStream out = response.getOutputStream();

        //writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        // IoUtil.close(out);
    }

    static Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) throws IOException {
        String strDays = String.valueOf(0);
        if (strDays.trim().equals("0")) {
            strDays = "OnLeave";
        }
        log.info("{}",strDays);
         //xls();

//        ExcelWriter writer = ExcelUtil.getWriter("/Users/apple/hasan/123/9.xlsx");
//
//
//        writer.writeCellValue(1, 0, "1111");
//        writer.getCell(1, 0).getCellStyle().setAlignment(HorizontalAlignment.RIGHT);
//
//        writer.writeCellValue(1, 1, "2222222");
//        writer.getCell(1, 0).getCellStyle().setAlignment(HorizontalAlignment.RIGHT);
//
//
//        writer.writeCellValue(1, 2, "333333");
//        writer.getCell(1, 0).getCellStyle().setAlignment(HorizontalAlignment.RIGHT);
//        writer.writeCellValue(1, 3, "44444");
//        writer.getCell(1, 0).getCellStyle().setAlignment(HorizontalAlignment.RIGHT);
//
//
//        writer.close();

    }
}

package com.example.esi;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.example.esi.pojo.HtmlData;
import com.example.esi.pojo.PageDate;
import com.example.esi.util.ExcelOperationHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Test {
    static Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        int rowCount = 0;
        rowCount++;

        String str = ExcelOperationHelp.lakhFormattedComma(3333332186983.250000002);


        log.info(" {}", NumberUtil.round("999.8", 2));

    }

    public static void mai2n(String[] args) {
        HtmlData htmlData = new HtmlData();
        Map map = new HashMap<Integer, List<PageDate>>();
        List<PageDate> pageDateList = new ArrayList<>();
        int line = 29;
        int page = 1;
        int maxStrLen = 21;
        ExcelReader reader = ExcelUtil.getReader("/Volumes/bak/esi/ESI input.xlsx");
        List<List<Object>> readAll = reader.read();
        for (int i = 1; i < readAll.size(); i++) {

            List list = readAll.get(i);

            Object obj0 = list.get(0);
            Object obj1 = list.get(1);
            String str1 = String.valueOf(obj1);

            //如果字符超过21，最后空格换成回车，该页的表格行数为29-1=28行。
            if (str1.length() >= maxStrLen) {
                str1 = ExcelOperationHelp.strTr2S(str1);
                line = 28;
            }

            Object obj2 = list.get(2);
            Object obj3 = list.get(3);
            String str2 = ExcelOperationHelp.isNumberForRound(String.valueOf(obj2));

            String formatStr2 = NumberUtil.decimalFormat(".00", Double.valueOf(str2));//299,792,458

            String str4 = NumberUtil.roundStr(Double.valueOf(str2) * 0.0075, 2);

            String formatStr4 = NumberUtil.decimalFormat(".00", Double.valueOf(str4));//299,792,458


            PageDate pageDate = new PageDate();
            pageDate.setSNo(i);
            pageDate.setIsDisable("");
            pageDate.setNumber(str1);
            pageDate.setName(String.valueOf(obj1));
            pageDate.setDays(String.valueOf(obj3));
            pageDate.setWages(formatStr2);
            pageDate.setContribution(formatStr4);
            pageDate.setReason("");
            pageDateList.add(pageDate);

            if (i / line == 0) {
                pageDateList = new ArrayList<>();
                page = page + 1;
            }
        }

        map.put(page, pageDateList);
        htmlData.setPageCount(page);
        htmlData.setTotalNumber(readAll.size());
        htmlData.setPageDate(map);

        log.info(" {}", htmlData);

    }


}

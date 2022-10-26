package com.example.esi.util;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author hs
 */
public class ExcelOperationHelp {
    /**
     * 判断字符串 是否为数字（小数），并四舍五入 保留2小数点
     *
     * @param str
     * @return
     */
    public static String isNumberForRound(String str) {
        boolean strResult = NumberUtil.isNumber(str);
        if (strResult) {
            return NumberUtil.toStr(NumberUtil.round(str, 2));
        }
        return str;
    }


    /**
     * 超过 21 个字符，
     * 最后一个空格替换成回车
     * 并行数 29-1 = 28
     *
     * @param str
     * @return
     */

    public static String strTr2S(String str) {
        String[] strArr = str.trim().replace("\n","").split(" ");
        String str_end = strArr[strArr.length - 1];
        String str_s = str.replaceAll(str_end, "<br/>" + str_end);
        return str_s.toUpperCase();
    }


    /**
     * #,##,###.00
     *
     * @param value
     * @return
     */
    public static String lakhFormattedComma(double value) {
        String dstr = NumberUtil.roundStr(value, 2);
        String[] dstrArr = dstr.split("\\.");

        value = Double.valueOf(dstrArr[0]);
        if (value < 1000) {
            return format("###", value) + "." + dstrArr[1];
        } else {
            double hundreds = value % 1000;
            int other = (int) (value / 1000);
            return format(",###", other) + ',' + format("000", hundreds) + "." + dstrArr[1];
        }
    }

    /**
     * 2022Jan
     *
     * @return
     */
    public static String getPeriodUK() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMM ", Locale.UK);
        String period = sdf.format(new Date());
        return period;
    }

    /**
     * 067221 17347790
     * 067221 20591515
     * 067221 24031024
     * 067221 27186130
     *
     * @return
     */
    public static String getChallanNo() {
        //RandomUtil.weightRandom()
        return "067221" + RandomUtil.randomNumbers(8);
    }

    /**
     * @param r
     * @return
     */
    public static double getRoundup(double r, int c) {
        BigDecimal bd = new BigDecimal(r);
        bd = bd.setScale(c, RoundingMode.UP);
        return bd.doubleValue();
    }

    public static void main(String[] args) {
        //String s = ExcelOperationHelp.lakhFormattedComma(ExcelOperationHelp.getRoundup(8.9, 0));
        ////System.out.printf(s);
        //String d4format = NumberUtil.decimalFormat("0", 1.0);
        ////System.out.printf(d4format);
        //double d =  6407.99 * 0.0325;
        //double employerContribution = ExcelOperationHelp.getRoundup(d, 0);
        //String employerContributionS = ExcelOperationHelp.lakhFormattedComma(employerContribution);
        ////System.out.printf("" + employerContribution
        //
        //
        //System.out.println(""+employerContribution);
        //System.out.println(employerContributionS);

        System.out.println(strTr2S("KOWSALYA KALIYAMOORTHY ").trim());


    }

    public static String getTransactionNumber() {
        //RandomUtil.weightRandom()
        return RandomUtil.randomNumbers(8);
    }

    private static String format(String pattern, Object value) {

        return new DecimalFormat(pattern).format(value);
    }
}

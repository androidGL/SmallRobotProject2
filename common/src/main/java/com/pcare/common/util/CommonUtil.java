package com.pcare.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: gl
 * @CreateDate: 2019/12/1
 * @Description: 一些通用类
 */
public class CommonUtil {
    public static String getDateStr(Date date, String format) {
        if(null == date){
            return "暂无数据";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd  HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }
    public static String getDateStr(Date date) {
        return getDateStr(date,null);
    }
}

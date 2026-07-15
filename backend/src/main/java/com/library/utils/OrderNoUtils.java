package com.library.utils;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * 订单号生成工具类
 */
public class OrderNoUtils {
    
    /**
     * 生成预约流水号
     * 格式：YYYYMMDD-XXXX
     */
    public static String generateOrderNo(int sequence) {
        String dateStr = DateUtil.format(new Date(), "yyyyMMdd");
        String seqStr = String.format("%04d", sequence);
        return dateStr + "-" + seqStr;
    }
}
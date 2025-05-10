package com.ruoyi.flowable.utils;

/**
 * 隐患区域工具类
 *
 * @author fengcheng
 */
public class RiskAreaUtils {

    /**
     * 根据隐患区域数据获取隐患区域名称
     *
     * @param riskArea 隐患区域
     * @return
     */
    public static String getHazardAreaNameByData(String riskArea) {
        switch (riskArea) {
            case "1":
                return "炼铁";
            case "2":
                return "炼钢";
            case "3":
                return "轧钢";
            case "4":
                return "能源";
            case "5":
                return "机加";
            case "6":
                return "原料";
            case "7":
                return "其他";
            default:
                return "未知";
        }
    }
}

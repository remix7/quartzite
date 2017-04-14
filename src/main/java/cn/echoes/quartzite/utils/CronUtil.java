package cn.echoes.quartzite.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * -------------------------------------
 * quartz cron 根据参数转换规则
 * -------------------------------------
 * Created by liutao on 2017/4/14 下午7:51.
 */
public class CronUtil {
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");


    /**
     * 获取从多少面开始多少秒执行一次的cron表达式
     *
     * @param second
     * @return
     */
    public static String getCronByEveryMinute(int start, int second) {
        if (second >= 60 || start >= 60) {
            throw new IllegalArgumentException("?");
        }
        return "0 " + start + "/" + second + " * * * ? *";
    }

    /**
     * 获取从多少面开始多少秒执行一次的cron表达式
     *
     * @param second
     * @return
     */
    public static String getCronByEverySecond(int start, int second) {
        if (second >= 60 || start >= 60) {
            throw new IllegalArgumentException("?");
        }
        return start + "/" + second + " * * * * ? *";
    }

    /**
     * 精确执行
     * 根据时间字符串生成 cron表达式
     *
     * @param str
     * @return
     */
    public static String getCronByDateStr(String str) {
        try {
            return getCronByDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("?");
    }

    /**
     * 精确执行
     * 根据输入的Date 返回cron 表达式
     *
     * @param date
     * @return
     */
    public static String getCronByDate(Date date) {
        String[] fileds = df.format(date).split("-");
        return fileds[5] + " " + fileds[4] + " " + fileds[3] + " " + fileds[2] + " " + fileds[1] + " ? " + fileds[0] + "-" + fileds[0];
    }

    public static void main(String[] args) {
        System.out.println(getCronByDateStr("2017-12-12 04:04:40"));
    }

}

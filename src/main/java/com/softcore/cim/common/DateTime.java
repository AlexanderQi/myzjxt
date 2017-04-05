/********************************************************************
 Copyright (C), 2010-2019, 南京软核科技有限公司
 文件: DateTime @ Softcore.Common
 作者: Fisher
 版本: 1.0
 日期: 2011-9-13 16:52:43
 描述:
 ----###
 历史:
 ----Fisher    2011-9-13    :build this moudle

 ********************************************************************/
package com.softcore.cim.common;

import java.text.*;
import java.util.*;

public class DateTime
{
    /** 时间比较，仅对时间部分做比较 */
    public static boolean TimeCompare(Date Min, Date Max)
    {
        return GetTimePart(Min).compareTo(GetTimePart(Max)) <= 0;
    }

    /** 获取距离当天零点的秒数 */
    public static int SecondOfTheDay(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
    }

    /** 获取时间间隔的秒数 */
    public static int SecondsBetween(Date Min, Date Max)
    {
        if (Min != null && Max != null)
        {
            return (int) Math.abs((Max.getTime() - Min.getTime()) / 1000);
        }
        else
        {
            return 0;
        }

    }

    /** 获取日期部分 */
    public static Date GetDatePart(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /** 获取时间部分 */
    public static Date GetTimePart(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, 1970);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /** 对日期做加减运算 */
    public static Date AddToDate(Date date, int Offset)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, Offset);
        return calendar.getTime();
    }

    /** 将字符串解析为日期 */
    public static Date StrToDate(String strdate)
    {
        if ("".equals(strdate) || strdate == null)
        {
            return null;
        }
        try
        {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strdate);
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        }
        return null;
    }

    public static String DateToStr(Date date)
    {
        return date != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) : "";
    }
}

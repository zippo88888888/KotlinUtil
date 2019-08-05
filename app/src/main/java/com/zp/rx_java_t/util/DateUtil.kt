package com.zp.rx_java_t.util

import com.zp.rx_java_t.content.IS_OFFICIAL
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    private const val DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss"

    /**
     * 获取当前系统时间
     * @param pattern   格式--->>> yyyy-MM-dd
     */
    fun getDate(pattern: String = DEFAULT_PATTERN) = SimpleDateFormat(pattern, Locale.CHINA).run {
        format(Date(System.currentTimeMillis()))
    }

    /**
     * 时间戳转日期格式
     */
    fun timeDateStamp(timestamp: Long, pattern: String = DEFAULT_PATTERN) =
            SimpleDateFormat(pattern, Locale.CHINA).run {
                format(Date(timestamp * 1000L))
            }

    /**
     * 日期格式字符串转换成时间戳
     * @param date_str 字符串日期
     */
    fun date2TimeStamp(date_str: String) = try {
        SimpleDateFormat(DEFAULT_PATTERN, Locale.CHINA).run {
            (parse(date_str).time / 1000).toString()
        }
    } catch (e: Exception) {
        if (!IS_OFFICIAL) e.printStackTrace()
        ""
    }

    /**
     * 时间格式化
     * @param time              字符串时间
     * @param isNeedShowHMS     是否显示时分秒
     */
    fun formatDataForDay(time: String, isNeedShowHMS: Boolean = false): String {
        val l = java.lang.Long.parseLong(date2TimeStamp(time)) * 1000
        val nowL = (System.currentTimeMillis() - l) / (60 * 1000)
        return when {
            nowL <= 1 -> "刚刚"
            nowL in 2..59 -> nowL.toString() + "分钟前"
            nowL < 60 * 24 && nowL >= 60 -> (nowL / 60).toString() + "小时前"
            nowL >= 60 * 24 && nowL < 60 * 24 * 7 -> {
                val day = nowL / (60 * 24)
                return when {
                    day <= 1 -> "昨天${SimpleDateFormat(" HH:mm", Locale.CHINA).run {
                        format(Date(l))
                    }}"
                    isNeedShowHMS -> time
                    else -> time.substring(0, time.indexOf(" ") + 1)
                }
            }
            else -> if (isNeedShowHMS) time else time.substring(0, time.indexOf(" ") + 1)
        }
    }

    /**
     * 根据 传入日期  与当前日期 计算 相隔 天数
     */
    fun getDayForDate(time: String): Double {
        val l = java.lang.Long.parseLong(date2TimeStamp(time)) * 1000
        // 分钟
        val nowL = (System.currentTimeMillis() - l) / (60 * 1000)
        return nowL / (60.0 * 24.0)
    }

    /**
     * 根据现在时间 计算 过去时间
     * @param day  与现在时间相差的天数
     */
    fun getDayForDay(day: Int): String {
        // 当前时间 秒
        val nowL = System.currentTimeMillis()
        // 结束时间 秒
        val endL = day * 24 * 60 * 60 * 1000L
        return SimpleDateFormat(DEFAULT_PATTERN, Locale.CHINA).run {
            format(Date(nowL - endL))
        }
    }

}